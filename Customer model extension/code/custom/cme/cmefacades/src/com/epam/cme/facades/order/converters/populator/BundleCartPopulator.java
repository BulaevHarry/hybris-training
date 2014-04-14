/*
 * [y] hybris Platform
 *
 * Copyright (c) 2000-2013 hybris AG
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of hybris
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with hybris.
 * 
 *  
 */
package com.epam.cme.facades.order.converters.populator;

import de.hybris.platform.commercefacades.order.data.AbstractOrderData;
import de.hybris.platform.commercefacades.order.data.CartData;
import de.hybris.platform.commercefacades.order.data.OrderEntryData;
import de.hybris.platform.configurablebundleservices.bundle.BundleCommerceCartService;
import de.hybris.platform.configurablebundleservices.model.BundleTemplateModel;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.promotions.result.PromotionOrderResults;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.servicelayer.i18n.L10NService;
import com.epam.cme.facades.data.BundleTemplateData;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.springframework.beans.factory.annotation.Required;


/**
 * Modify the cart converter to show all bundle components of a package in a cart. This means that even those components
 * are displayed for which there is no product in the cart yet. Also show the first invalid bundle component in the cart
 * (if it exists).
 */
public class BundleCartPopulator<S extends CartModel, T extends CartData> extends AbstractBundleOrderPopulator<S, T>
{
	private Converter<BundleTemplateModel, BundleTemplateData> bundleTemplateConverter;
	private BundleCommerceCartService bundleCommerceCartService;
	private L10NService l10NService;

	/**
	 * Modify populate method to set the first incomplete bundle component in the cart {@link CartModel}
	 */
	@Override
	public void populate(final S source, final T target)
	{
		if (source.getBillingTime() == null)
		{
			// compatibility mode: do not perform the bundling specific populator tasks
			return;
		}

		addEntries(source, target);
		addCartInvalidMessage(source, target);
		setFirstIncompleteComponent(target);
		addPromotions(source, target);
	}

	/**
	 * Modify addEntries method to add bundle components that belong to a bundle template but not added yet to cart
	 */
	@Override
	protected void addEntries(final AbstractOrderModel source, final AbstractOrderData target)
	{
		final Collection<OrderEntryData> entries = target.getEntries();
		final List<OrderEntryData> modifiedEntries = addEmptyBundleComponents(entries, (S) source);
		final List<OrderEntryData> sortedEntries = getSortedEntryListBasedOnBundleAndComponent(modifiedEntries);
		target.setEntries(sortedEntries);
	}

	/**
	 * Get first incomplete bundle component of each package
	 */
	protected void setFirstIncompleteComponent(final T target)
	{
		final Map<Integer, BundleTemplateData> firstInvalids = new HashMap<Integer, BundleTemplateData>();
		for (final OrderEntryData orderEntry : target.getEntries())
		{
			if (!orderEntry.isValid())
			{
				final Integer currentBundleNo = Integer.valueOf(orderEntry.getBundleNo());
				if (!firstInvalids.containsKey(currentBundleNo))
				{
					//get the first invalid bundle component and exit
					firstInvalids.put(currentBundleNo, orderEntry.getComponent());
					if (firstInvalids.size() >= 1)
					{
						break;
					}
				}
			}
		}
		target.setFirstIncompleteBundleComponentsMap(firstInvalids);
	}

	/**
	 * Method to add empty bundle components to cart entries {@link OrderEntryData}.
	 * 
	 * @param entries
	 * @param cart
	 * @return modified list of {@link OrderEntryData}
	 */
	protected List<OrderEntryData> addEmptyBundleComponents(final Collection<OrderEntryData> entries, final S cart)
	{
		// create a map with one entry per bundleno
		final Map<Integer, List<OrderEntryData>> entriesMap = new TreeMap<Integer, List<OrderEntryData>>();
		for (final OrderEntryData orderEntryData : entries)
		{
			final OrderEntryData entry = orderEntryData;
			final Integer bundleNo = Integer.valueOf(entry.getBundleNo());
			List<OrderEntryData> tempList = entriesMap.get(bundleNo);
			if (tempList == null)
			{
				tempList = new ArrayList<OrderEntryData>();
			}
			tempList.add(entry);
			entriesMap.put(bundleNo, tempList);
		}

		// extract standalone entries 
		final List<OrderEntryData> standalones = entriesMap.remove(Integer.valueOf(0));

		// merge bundle entries
		final List<OrderEntryData> modifiedEntriesList = new ArrayList<OrderEntryData>();
		for (final List<OrderEntryData> list : entriesMap.values())
		{
			modifiedEntriesList.addAll(mergeEntries(list, cart));
		}

		// add standalone entries at the very end
		if (standalones != null)
		{
			modifiedEntriesList.addAll(standalones);
		}

		return modifiedEntriesList;
	}

	/**
	 * Method to merge existing cart entries {@link OrderEntryData} and empty cart entries {@link OrderEntryData}. If
	 * cart entry is 1) a standalone product return orderentries because there is nothing to add. 2) a bundle product,
	 * get all the bundle components of the root bundle. Merge the retrieved bundle components with bundle components
	 * already added to the cart - while maintaining the relative order of bundle components
	 * 
	 * @param entries
	 *           {@link OrderEntryData} The bundle components added to the cart
	 * @param cart
	 *           {@link CartModel}
	 * @return merged list of {@link OrderEntryData}
	 */
	protected Collection<OrderEntryData> mergeEntries(final Collection<OrderEntryData> entries, final S cart)
	{
		if (entries.isEmpty())
		{
			return Collections.emptyList();
		}

		final OrderEntryData firstEntry = entries.iterator().next();

		final int bundleNo = firstEntry.getBundleNo();
		if (bundleNo == 0) //standalone nothing to add
		{
			return entries;
		}

		final Collection<OrderEntryData> mergedEntries = new ArrayList<OrderEntryData>();

		final BundleTemplateData rootTemplateData = firstEntry.getRootBundleTemplate();
		final BundleTemplateModel rootBundle = getBundleTemplateService().getBundleTemplateForCode(rootTemplateData.getId(),
				rootTemplateData.getVersion());

		for (final BundleTemplateModel childTemplate : rootBundle.getChildTemplates())
		{
			final Collection<OrderEntryData> entriesOfComponent = new ArrayList<OrderEntryData>();

			for (final OrderEntryData entry : entries)
			{
				if (entry.getComponent().getId().equals(childTemplate.getId()))
				{
					entriesOfComponent.add(entry);
				}
			}

			if (entriesOfComponent.isEmpty()
					&& getBundleCommerceCartService().checkIsComponentEditable(cart, childTemplate, bundleNo))
			{
				mergedEntries.add(createEmptyOrderEntryData(bundleNo, rootBundle, childTemplate, cart));
			}
			else
			{
				mergedEntries.addAll(entriesOfComponent);
			}
		}

		return mergedEntries;
	}

	/**
	 * Method to add an empty {@link OrderEntryData} to the bundle cart view. The quantity is currently being set to some
	 * invalid value like -1. And BillingTimeData is also being set to a default value.
	 * 
	 * @param bundleNo
	 *           The bundle No to which the empty row should be added
	 * @param rootBundle
	 *           The root {@link BundleTemplateModel} for the empty row
	 * @param childTemplate
	 *           The bundle template {@link BundleTemplateModel} the empty row represents
	 * @param masterCart
	 *           The {@link CartModel}
	 * @return The created {@link OrderEntryData}
	 */
	protected OrderEntryData createEmptyOrderEntryData(final int bundleNo, final BundleTemplateModel rootBundle,
			final BundleTemplateModel childTemplate, final S masterCart)
	{
		final OrderEntryData emptyOrderEntry = new OrderEntryData();
		emptyOrderEntry.setBundleNo(bundleNo);
		emptyOrderEntry.setRemoveable(false);
		emptyOrderEntry.setUpdateable(false);
		emptyOrderEntry.setRootBundleTemplate(getBundleTemplateConverter().convert(rootBundle));

		emptyOrderEntry.setEditable(true);
		emptyOrderEntry.setValid(getBundleCommerceCartService().checkIsComponentSelectionCriteriaMet(masterCart, childTemplate,
				bundleNo));
		emptyOrderEntry.setComponent(getBundleTemplateConverter().convert(childTemplate));
		emptyOrderEntry.setQuantity(Long.valueOf(-1));
		return emptyOrderEntry;
	}

	@Override
	protected void addPromotions(final AbstractOrderModel source, final AbstractOrderData target)
	{
		addPromotions(source, getPromotionsService().getPromotionResults(source), target);
	}

	@Override
	protected void addPromotions(final AbstractOrderModel source, final PromotionOrderResults promoOrderResults,
			final AbstractOrderData target)
	{
		if (promoOrderResults != null)
		{
			final CartData cartData = (CartData) target;
			cartData.setPotentialOrderPromotions(getPromotions(promoOrderResults.getPotentialOrderPromotions()));
			cartData.setPotentialProductPromotions(getPromotions(promoOrderResults.getPotentialProductPromotions()));
		}
	}

	protected void addCartInvalidMessage(final S source, final T target)
	{
		final BundleTemplateModel bundleTemplate = getBundleCommerceCartService().getFirstInvalidComponentInCart(source);
		if (bundleTemplate != null)
		{
			final String cartInvalidMessage = getL10NService().getLocalizedString(
					"bundleservices.validation.cartcomponentisinvalid", new Object[]
					{ bundleTemplate.getParentTemplate().getName(), bundleTemplate.getName() });
			target.setCartInvalidMessage(cartInvalidMessage);
		}
	}

	@Required
	public void setBundleCommerceCartService(final BundleCommerceCartService bundleCommerceCartService)
	{
		this.bundleCommerceCartService = bundleCommerceCartService;
	}

	protected BundleCommerceCartService getBundleCommerceCartService()
	{
		return bundleCommerceCartService;
	}

	@Required
	public void setL10NService(final L10NService l10NService)
	{
		this.l10NService = l10NService;
	}

	protected L10NService getL10NService()
	{
		return l10NService;
	}

	protected Converter<BundleTemplateModel, BundleTemplateData> getBundleTemplateConverter()
	{
		return bundleTemplateConverter;
	}

	@Required
	public void setBundleTemplateConverter(final Converter<BundleTemplateModel, BundleTemplateData> bundleTemplateConverter)
	{
		this.bundleTemplateConverter = bundleTemplateConverter;
	}

}

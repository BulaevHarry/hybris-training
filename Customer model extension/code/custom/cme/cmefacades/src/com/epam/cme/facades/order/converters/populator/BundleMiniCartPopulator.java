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
import de.hybris.platform.converters.Converters;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.servicelayer.dto.converter.Converter;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.util.Assert;


/**
 * Slightly changed behavior to display the last added item(s) instead of just the first x items in the cart, which does
 * not make any sense in terms of bundles.
 */
public class BundleMiniCartPopulator<S extends CartModel, T extends CartData> extends AbstractBundleOrderPopulator<S, T>
{
	private Converter<AbstractOrderEntryModel, OrderEntryData> orderEntryConverter;

	@Override
	public void populate(final S source, final T target)
	{
		Assert.notNull(target, "Parameter target cannot be null.");

		if (source == null)
		{
			target.setTotalPrice(createZeroPrice());
			target.setDeliveryCost(null);
			target.setSubTotal(createZeroPrice());
			target.setTotalItems(Integer.valueOf(0));
			target.setTotalUnitCount(Integer.valueOf(0));
		}
		else
		{
			if (source.getBillingTime() == null)
			{
				// compatibility mode: do not perform the bundling specific populator tasks
				return;
			}

			addEntries(source, target);

			if (CollectionUtils.isEmpty(source.getEntries()))
			{
				target.setAllEntriesCount(Integer.valueOf(0));
			}
			else
			{
				target.setAllEntriesCount(Integer.valueOf(source.getEntries().size()));
			}
		}
	}

	/**
	 * Add only those cart entries that will be displayed in the mini cart (= last modified records). Existing entries in
	 * the target are not replaced but filtered/sorted so that only the last modified records stay
	 * 
	 */
	@Override
	protected void addEntries(final AbstractOrderModel source, final AbstractOrderData target)
	{
		if (CollectionUtils.isEmpty(target.getEntries()))
		{
			final List<OrderEntryData> entries = Converters.convertAll(source.getEntries(), getOrderEntryConverter());
			target.setEntries(entries);
		}

		List<OrderEntryData> filteredEntries = new ArrayList<OrderEntryData>();
		List<AbstractOrderEntryModel> lastModifiedEntries = new ArrayList<AbstractOrderEntryModel>();

		if (source instanceof CartModel)
		{
			lastModifiedEntries = new ArrayList<AbstractOrderEntryModel>(((CartModel) source).getLastModifiedEntries());
			filteredEntries = extractModifiedEntriesFromEntryList(lastModifiedEntries, target.getEntries());
		}
		else
		{
			filteredEntries = target.getEntries();
		}

		final List<OrderEntryData> sortedEntries = getSortedEntryListBasedOnBundleAndComponent(filteredEntries);
		target.setEntries(sortedEntries);
	}

	protected List<OrderEntryData> extractModifiedEntriesFromEntryList(final List<AbstractOrderEntryModel> lastModifiedEntries,
			final List<OrderEntryData> allEntries)
	{
		final List<OrderEntryData> filteredEntries = new ArrayList<OrderEntryData>();

		final Collection<Integer> entryList = new ArrayList<Integer>();
		for (final AbstractOrderEntryModel modifiedEntry : lastModifiedEntries)
		{
			entryList.add(modifiedEntry.getEntryNumber());
		}

		for (final OrderEntryData entry : allEntries)
		{
			if (entryList.contains(entry.getEntryNumber()))
			{
				filteredEntries.add(entry);
			}
		}

		return filteredEntries;
	}

	@Override
	public Converter<AbstractOrderEntryModel, OrderEntryData> getOrderEntryConverter()
	{
		return orderEntryConverter;
	}

	@Override
	public void setOrderEntryConverter(final Converter<AbstractOrderEntryModel, OrderEntryData> orderEntryConverter)
	{
		this.orderEntryConverter = orderEntryConverter;
	}
}

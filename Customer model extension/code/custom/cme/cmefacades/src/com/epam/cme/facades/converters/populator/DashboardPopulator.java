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
package com.epam.cme.facades.converters.populator;

import de.hybris.platform.commercefacades.order.data.OrderEntryData;
import de.hybris.platform.configurablebundleservices.bundle.BundleCommerceCartService;
import de.hybris.platform.configurablebundleservices.model.AutoPickBundleSelectionCriteriaModel;
import de.hybris.platform.configurablebundleservices.model.BundleTemplateModel;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.CartEntryModel;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import com.epam.cme.facades.data.BundleTemplateData;
import com.epam.cme.facades.data.DashboardBoxData;
import com.epam.cme.facades.data.DashboardBoxEntryData;
import com.epam.cme.facades.data.DashboardData;
import com.epam.cme.facades.data.DashboardPopulatorParameters;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;


/**
 * Populates the {@link DashboardData} of extras for guided selling to be used in frontend.
 */
public class DashboardPopulator<SOURCE extends DashboardPopulatorParameters, TARGET extends DashboardData> implements
		Populator<SOURCE, TARGET>
{
	protected static final Logger LOG = Logger.getLogger(DashboardPopulator.class);

	private Converter<BundleTemplateModel, BundleTemplateData> bundleTemplateConverter;
	private BundleCommerceCartService bundleCommerceCartService;
	private Converter<AbstractOrderEntryModel, OrderEntryData> telcoOrderEntryConverter;

	@Override
	public void populate(final SOURCE source, final TARGET target) throws ConversionException
	{
		final CartModel cart = source.getCart();
		final BundleTemplateModel currentComponent = source.getCurrentComponent();
		final BundleTemplateModel parentTemplate = source.getCurrentComponent().getParentTemplate();
		final int bundleNo = source.getBundleNo();

		final List<DashboardBoxData> dashboardBoxes = new ArrayList<DashboardBoxData>();
		for (final BundleTemplateModel component : parentTemplate.getChildTemplates())
		{
			final BundleTemplateData componentData = getBundleTemplateConverter().convert(component);
			final DashboardBoxData dashboardBoxData = new DashboardBoxData();
			dashboardBoxData.setMainComponent(componentData);
			dashboardBoxData.setType(component.getName());
			if (component.equals(currentComponent))
			{
				dashboardBoxData.setActive(true);
			}

			final List<DashboardBoxEntryData> dashboardBoxEntries = new ArrayList<DashboardBoxEntryData>();
			if (component.getBundleSelectionCriteria() != null
					&& !(component.getBundleSelectionCriteria() instanceof AutoPickBundleSelectionCriteriaModel))
			{
				final List<CartEntryModel> cartEntriesForComponentInBundle = getBundleCommerceCartService()
						.getCartEntriesForComponentInBundle(cart, component, bundleNo);
				for (final CartEntryModel cartEntry : cartEntriesForComponentInBundle)
				{
					final DashboardBoxEntryData dashboardBoxEntryData = new DashboardBoxEntryData();
					final OrderEntryData cartEntryData = getTelcoOrderEntryConverter().convert(cartEntry);

					dashboardBoxEntryData.setOrderEntry(cartEntryData);
					dashboardBoxEntryData.setComponent(componentData);

					dashboardBoxEntries.add(dashboardBoxEntryData);
				}
			}
			dashboardBoxData.setDashboardBoxEntries(dashboardBoxEntries);

			dashboardBoxes.add(dashboardBoxData);
		}

		target.setBundleNo(bundleNo);
		target.setDashboardBoxes(mergeTelcoSpecificBoxes(dashboardBoxes));
	}

	/**
	 * The dashbord boxes that have been created in a generic way are refactored to telco needs. Please see inline
	 * comments for more information on the changes.
	 * 
	 * @param dashboardBoxes
	 *           the generic dashboard boxes, one box for each existing component
	 * @return the telco specific dashboard boxes, always 3 boxes, one for the device, one for the service plan and one
	 *         for all service addons.
	 */
	protected List<DashboardBoxData> mergeTelcoSpecificBoxes(final List<DashboardBoxData> dashboardBoxes)
	{
		final List<DashboardBoxData> mergedDashboardBoxes = new ArrayList<DashboardBoxData>();

		// collecting the different item types that are existing in the dashboard boxes
		final List<String> types = new ArrayList<String>();
		for (final DashboardBoxData dashboardBoxData : dashboardBoxes)
		{
			final String type = dashboardBoxData.getMainComponent().getType();
			if (!types.contains(type) && type != null)
			{
				types.add(type);
			}
		}

		// merging together all dashboard boxes of the same type 
		for (final String type : types)
		{
			final List<DashboardBoxEntryData> mergedEntries = new ArrayList<DashboardBoxEntryData>();
			DashboardBoxData mergedDashboardBoxData = null;
			for (final DashboardBoxData dashboardBoxData : dashboardBoxes)
			{
				if (type.equals(dashboardBoxData.getMainComponent().getType()))
				{
					if (mergedDashboardBoxData == null)
					{
						mergedDashboardBoxData = dashboardBoxData;
					}
					if (dashboardBoxData.isActive())
					{
						mergedDashboardBoxData.setActive(true);
					}
					mergedEntries.addAll(dashboardBoxData.getDashboardBoxEntries());
				}
			}
			mergedDashboardBoxData.setDashboardBoxEntries(mergedEntries);
			mergedDashboardBoxData.setType(type);
			mergedDashboardBoxes.add(mergedDashboardBoxData);
		}

		// if there is no device box - e.g. because of a sim only package - then we are adding one at the first position
		if (!types.contains("DeviceModel"))
		{
			final DashboardBoxData dashboardBoxData = new DashboardBoxData();
			dashboardBoxData.setType("DeviceModel");
			mergedDashboardBoxes.add(0, dashboardBoxData);
		}

		return mergedDashboardBoxes;
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

	protected BundleCommerceCartService getBundleCommerceCartService()
	{
		return bundleCommerceCartService;
	}

	@Required
	public void setBundleCommerceCartService(final BundleCommerceCartService bundleCommerceCartService)
	{
		this.bundleCommerceCartService = bundleCommerceCartService;
	}

	protected Converter<AbstractOrderEntryModel, OrderEntryData> getTelcoOrderEntryConverter()
	{
		return telcoOrderEntryConverter;
	}

	public void setTelcoOrderEntryConverter(final Converter<AbstractOrderEntryModel, OrderEntryData> telcoOrderEntryConverter)
	{
		this.telcoOrderEntryConverter = telcoOrderEntryConverter;
	}

}

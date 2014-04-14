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

import de.hybris.platform.commercefacades.product.PriceDataFactory;
import de.hybris.platform.commercefacades.product.data.PriceDataType;
import de.hybris.platform.commercefacades.product.data.ProductData;
import de.hybris.platform.commerceservices.search.resultdata.SearchResultValueData;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.servicelayer.i18n.CommonI18NService;
import de.hybris.platform.subscriptionfacades.data.BillingPlanData;
import de.hybris.platform.subscriptionfacades.data.BillingTimeData;
import de.hybris.platform.subscriptionfacades.data.SubscriptionTermData;
import de.hybris.platform.subscriptionfacades.data.TermOfServiceFrequencyData;
import de.hybris.platform.subscriptionservices.model.SubscriptionProductModel;

import java.math.BigDecimal;

import org.springframework.beans.factory.annotation.Required;


/**
 * SOLR Populator for {@link SubscriptionProductModel}
 */
public class SearchProductTelcoPopulator<SOURCE extends SearchResultValueData, TARGET extends ProductData> implements
		Populator<SOURCE, TARGET>
{
	private PriceDataFactory priceDataFactory;
	private CommonI18NService commonI18NService;

	@Override
	public void populate(final SOURCE source, final TARGET target)
	{
		final ProductData telcoProduct = target;

		final String billingTimeAsString = this.getValue(source, "billingTime");
		final BillingTimeData billingTime = new BillingTimeData();
		billingTime.setName(billingTimeAsString);

		if (telcoProduct.getSubscriptionTerm() == null)
		{
			telcoProduct.setSubscriptionTerm(new SubscriptionTermData());
			telcoProduct.getSubscriptionTerm().setBillingPlan(new BillingPlanData());
		}

		if (telcoProduct.getSubscriptionTerm().getBillingPlan() == null)
		{
			telcoProduct.getSubscriptionTerm().setBillingPlan(new BillingPlanData());
		}

		telcoProduct.getSubscriptionTerm().getBillingPlan().setBillingTime(billingTime);

		final Boolean soldIndividually = this.getValue(source, ProductModel.SOLDINDIVIDUALLY);
		telcoProduct.setSoldIndividually(soldIndividually == null ? true : soldIndividually.booleanValue());

		final String termOfServiceFrequencyAsString = this.getValue(source, "termLimit");
		final TermOfServiceFrequencyData termOfServiceFrequencyData = new TermOfServiceFrequencyData();
		termOfServiceFrequencyData.setName(termOfServiceFrequencyAsString);
		telcoProduct.getSubscriptionTerm().setTermOfServiceFrequency(termOfServiceFrequencyData);

		final Double lowestBundlePriceValue = this.getValue(source, "lowestBundlePriceValue");
		telcoProduct.setLowestBundlePrice(lowestBundlePriceValue == null ? null : getPriceDataFactory().create(PriceDataType.BUY,
				BigDecimal.valueOf(lowestBundlePriceValue.doubleValue()), getCommonI18NService().getCurrentCurrency().getIsocode()));
	}

	protected <T> T getValue(final SOURCE source, final String propertyName)
	{
		if (source.getValues() == null)
		{
			return null;
		}

		// DO NOT REMOVE the cast (T) below, while it should be unnecessary it is required by the javac compiler
		return (T) source.getValues().get(propertyName);
	}

	protected PriceDataFactory getPriceDataFactory()
	{
		return priceDataFactory;
	}

	@Required
	public void setPriceDataFactory(final PriceDataFactory priceDataFactory)
	{
		this.priceDataFactory = priceDataFactory;
	}

	protected CommonI18NService getCommonI18NService()
	{
		return commonI18NService;
	}

	@Required
	public void setCommonI18NService(final CommonI18NService commonI18NService)
	{
		this.commonI18NService = commonI18NService;
	}
}

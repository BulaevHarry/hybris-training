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

import de.hybris.platform.commercefacades.product.data.PriceDataType;
import de.hybris.platform.commercefacades.product.data.ProductData;
import de.hybris.platform.configurablebundleservices.model.BundleTemplateModel;
import de.hybris.platform.configurablebundleservices.model.ChangeProductPriceBundleRuleModel;
import de.hybris.platform.configurablebundleservices.model.DisableProductBundleRuleModel;
import de.hybris.platform.core.model.c2l.CurrencyModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.subscriptionfacades.data.RecurringChargeEntryData;
import de.hybris.platform.subscriptionfacades.data.SubscriptionPricePlanData;
import de.hybris.platform.subscriptionservices.model.SubscriptionProductModel;
import com.epam.cme.core.model.DeviceModel;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;


/**
 * Populates the {@link ProductData} with the device and plan for guided selling bundle information to be used by front
 * end.
 */
public class DeviceBundleTabsPopulator<SOURCEPRODUCT extends DeviceModel, TARGETPRODUCT extends ProductData, SOURCETEMPLATE extends BundleTemplateModel>
		extends AbstractProductBundleTabsPopulator<SOURCEPRODUCT, TARGETPRODUCT, SOURCETEMPLATE>
{
	/**
	 * Resolves the components to be used for populating the bundle tabs. For Devices the components where the device is
	 * configured for are used, e.g. "Smartphone-Handset" and "Pay as you go-Handset" in case of an iPhone.
	 * 
	 * @param productModel
	 *           the {@link ProductModel} for which the components should be searched for
	 * @return a list of component {@link BundleTemplateModel}s which are used to populating the bundle tabs
	 */
	@Override
	protected Collection<SOURCETEMPLATE> getComponents(final SOURCEPRODUCT productModel)
	{
		return (Collection<SOURCETEMPLATE>) productModel.getBundleTemplates();
	}

	/**
	 * Resolves the target component {@link BundleTemplateModel} for a source component {@link BundleTemplateModel}. In
	 * case of a Device, the target component is the plan component in the same package as the device component. E.g. the
	 * target component for the source component "Smartphone-Handset" would be "Smartphone-Plan".
	 * 
	 * @param sourceComponent
	 *           The source component {@link BundleTemplateModel} for which the target component should be found.
	 * @return the target component {@link BundleTemplateModel}
	 */
	@Override
	protected SOURCETEMPLATE getTargetComponent(final SOURCETEMPLATE sourceComponent)
	{
		return (SOURCETEMPLATE) getBundleTemplateService().getSubsequentBundleTemplate(sourceComponent);
	}

	/**
	 * Resolves the list of {@link ProductModel}s for the given <code>productModel</code>. For Devices it first simply
	 * resolves all products for the target component {@link BundleTemplateModel}. Then it removes all plans from that
	 * list which are invalid for the given <code>productModel</code> in the context of the given
	 * <code>sourceComponent</code> and <code>productModel</code>. A plan is considered invalid if a
	 * {@link DisableProductBundleRuleModel} exists which does not allow the combination of device and plan in a bundle.
	 * The corrected list of {@link ProductModel}s is returned.
	 * 
	 * @param productModel
	 *           the {@link ProductModel} for which the list of {@link ProductModel}s should be found
	 * @param sourceComponent
	 *           the source component {@link BundleTemplateModel} for which the list of {@link ProductModel}s should be
	 *           found
	 * @param targetComponent
	 *           the target component {@link BundleTemplateModel} for which the list of {@link ProductModel}s should be
	 *           found
	 * @return the list of {@link ProductModel}s matching the parameters
	 */
	@Override
	protected List<ProductModel> getProducts(final SOURCEPRODUCT productModel, final SOURCETEMPLATE sourceComponent,
			final SOURCETEMPLATE targetComponent)
	{
		final List<ProductModel> validPlans = new ArrayList<ProductModel>();

		for (final ProductModel plan : targetComponent.getProducts())
		{
			final DisableProductBundleRuleModel disableRule = getBundleRuleService().getDisableRuleForBundleProduct(sourceComponent,
					productModel, plan);

			if (disableRule == null)
			{
				validPlans.add(plan);
			}
		}

		return validPlans;
	}

	/**
	 * Hook to call additional populators for different purposes. For Devices we are populating the standard price of the
	 * according plan. Afterwards we are finding possible price rules for the device product as well as for the plan
	 * product and storing the cheapest ones in the plan DTO as <code>otherBundleProductPrice</code> for the cheapest
	 * device price and as <code>thisBundleProductPrice</code> for the cheapest plan price.
	 * 
	 * @param sourceComponent
	 *           in this case e.g. the "Smartphone-Handset" component
	 * @param targetComponent
	 *           in this case e.g. the "Smartphone-Plan" component
	 * @param productModel
	 *           in this case the Model of the device product
	 * @param productData
	 *           in this case the DTO of the device product
	 * @param subscriptionProductModel
	 *           in this case the Model of the plan product
	 * @param subscriptionProductData
	 *           in this case the DTO of the plan product
	 */
	@Override
	protected void callPopulators(final SOURCETEMPLATE sourceComponent, final SOURCETEMPLATE targetComponent,
			final SOURCEPRODUCT productModel, final TARGETPRODUCT productData,
			final SubscriptionProductModel subscriptionProductModel, final ProductData subscriptionProductData)
	{
		getProductPricePopulator().populate(subscriptionProductModel, subscriptionProductData);
		getSubscriptionProductPricePopulator().populate(subscriptionProductModel, subscriptionProductData);
		getProductDescriptionPopulator().populate(subscriptionProductModel, subscriptionProductData);
		getProductClassificationPopulator().populate(subscriptionProductModel, subscriptionProductData);

		final CurrencyModel currency = getCommonI18NService().getCurrentCurrency();

		final ChangeProductPriceBundleRuleModel devicePriceRule = getBundleRuleService().getChangePriceBundleRule(sourceComponent,
				productModel, subscriptionProductModel, currency);
		subscriptionProductData.setOtherBundleProductPrice(devicePriceRule == null ? productData.getPrice() : getPriceDataFactory()
				.create(PriceDataType.BUY, devicePriceRule.getPrice(), currency.getIsocode()));

		final ChangeProductPriceBundleRuleModel planPriceRule = getBundleRuleService().getChangePriceBundleRule(targetComponent,
				subscriptionProductModel, productModel, currency);
		final BigDecimal planPriceRulePrice = planPriceRule == null ? null : planPriceRule.getPrice();

		final SubscriptionPricePlanData pricePlan = (SubscriptionPricePlanData) subscriptionProductData.getPrice();
		BigDecimal pricePlanLowestPrice = BigDecimal.ZERO;
		BigDecimal discountPrice = BigDecimal.ZERO;

		if (pricePlan != null && CollectionUtils.isNotEmpty(pricePlan.getRecurringChargeEntries()))
		{
			final RecurringChargeEntryData lowestPriceChargeEntry = pricePlan.getRecurringChargeEntries().iterator().next();
			pricePlanLowestPrice = lowestPriceChargeEntry.getPrice().getValue();
		}

		if (planPriceRulePrice == null || pricePlanLowestPrice.doubleValue() < planPriceRulePrice.doubleValue())
		{
			discountPrice = pricePlanLowestPrice;
		}
		else
		{
			discountPrice = BigDecimal.valueOf(planPriceRulePrice.doubleValue());
		}

		subscriptionProductData.setThisBundleProductPrice(getPriceDataFactory().create(PriceDataType.BUY, discountPrice,
				currency.getIsocode()));
	}
}

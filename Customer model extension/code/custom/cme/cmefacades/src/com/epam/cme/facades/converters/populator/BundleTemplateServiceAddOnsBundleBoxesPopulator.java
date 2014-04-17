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
import de.hybris.platform.commercefacades.product.PriceDataFactory;
import de.hybris.platform.commercefacades.product.data.PriceData;
import de.hybris.platform.commercefacades.product.data.PriceDataType;
import de.hybris.platform.commercefacades.product.data.ProductData;
import de.hybris.platform.configurablebundleservices.bundle.BundleCommerceCartService;
import de.hybris.platform.configurablebundleservices.bundle.BundleRuleService;
import de.hybris.platform.configurablebundleservices.bundle.BundleTemplateService;
import de.hybris.platform.configurablebundleservices.model.BundleTemplateModel;
import de.hybris.platform.configurablebundleservices.model.ChangeProductPriceBundleRuleModel;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.CartEntryModel;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.product.ProductService;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.servicelayer.i18n.CommonI18NService;
import de.hybris.platform.subscriptionfacades.data.RecurringChargeEntryData;
import de.hybris.platform.subscriptionfacades.data.SubscriptionPricePlanData;
import com.epam.cme.core.bundle.GuidedSellingService;
import com.epam.cme.core.model.ServiceAddOnModel;
import com.epam.cme.facades.data.BundleBoxData;
import com.epam.cme.facades.data.BundleBoxEntryData;
import com.epam.cme.facades.data.BundleTemplateData;
import com.epam.cme.facades.data.BundleTemplateServiceAddOnsPopulatorParameters;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;

/**
 * Populates the {@link BundleTemplateData} of extras for guided selling to be used in frontend.
 */
public class BundleTemplateServiceAddOnsBundleBoxesPopulator<SOURCE extends BundleTemplateServiceAddOnsPopulatorParameters, TARGET extends BundleTemplateData>
        implements Populator<SOURCE, TARGET> {

    protected static final Logger LOG = Logger.getLogger(BundleTemplateServiceAddOnsBundleBoxesPopulator.class);

    private Converter<BundleTemplateModel, BundleBoxData> bundleBoxConverter;
    private Converter<BundleTemplateModel, BundleTemplateData> bundleTemplateConverter;
    private BundleCommerceCartService bundleCommerceCartService;
    private BundleTemplateService bundleTemplateService;
    private ProductService productService;
    private BundleRuleService bundleRuleService;
    private PriceDataFactory priceDataFactory;
    private CommonI18NService commonI18NService;
    private Converter<AbstractOrderEntryModel, OrderEntryData> telcoOrderEntryConverter;
    private GuidedSellingService guidedSellingService;

    @Override
    public void populate(final SOURCE source, final TARGET target) throws ConversionException {
        final CartModel cart = source.getCart();
        final BundleTemplateModel parentTemplate = source.getCurrentComponent().getParentTemplate();
        final int bundleNo = source.getBundleNo().intValue();
        final List<BundleTemplateModel> childBundleTemplates = parentTemplate.getChildTemplates();
        final BundleTemplateModel expandedComponent = findExpandedComponent(cart, source.getCurrentComponent(),
                childBundleTemplates, bundleNo);

        final List<BundleBoxData> bundleBoxDatas = new ArrayList<BundleBoxData>();
        boolean beforeExpanded = true;
        for (final BundleTemplateModel bundleTemplateModel : childBundleTemplates) {
            if (getGuidedSellingService().isComponentToBeDisplayedOnGuidedSellingSelectPage(cart, bundleTemplateModel,
                    bundleNo, ServiceAddOnModel.class)) {
                final BundleBoxData bundleBoxData = getBundleBoxConverter().convert(bundleTemplateModel);

                if (bundleTemplateModel.equals(expandedComponent)) {
                    bundleBoxData.setExpanded(true);
                    beforeExpanded = false;

                }
                bundleBoxData.setNextButton(bundleBoxData.isExpanded());
                bundleBoxData.setReviewButton(beforeExpanded);

                // create a target list which shall contain only selected references from the
                // original entries
                final List<BundleBoxEntryData> selectedBoxEntryDatas = new ArrayList<BundleBoxEntryData>();

                // Loop through the BoxEntryData and set order entry data if product exist in the
                // cart
                for (final BundleBoxEntryData boxEntryData : bundleBoxData.getBundleBoxEntries()) {
                    populateBundleBoxEntryForDisabledProducts(cart, source.getBundleNo().intValue(),
                            bundleTemplateModel, boxEntryData);

                    // if the product from BoxEntryData exists in the cart keep it
                    final CartEntryModel cartEntry = findMatchingCartEntry(cart, boxEntryData.getProduct().getCode(),
                            bundleTemplateModel, source.getBundleNo().intValue());
                    if (cartEntry == null) {
                        boxEntryData.getProduct().setThisBundleProductPrice(
                                calculateProvisionedPriceForNonCartEntry(cart, source.getBundleNo().intValue(),
                                        bundleTemplateModel, boxEntryData.getProduct()));
                        boxEntryData.getProduct().setPrice(calculatePriceForNonCartEntry(boxEntryData.getProduct()));
                    } else {
                        boxEntryData.setSelected(true);
                        populateBundleBoxEntryForNotRemovableProducts(cartEntry, boxEntryData);
                        boxEntryData.setOrderEntry(getTelcoOrderEntryConverter().convert(cartEntry));
                    }

                    if (bundleBoxData.isExpanded() || cartEntry != null) {
                        selectedBoxEntryDatas.add(boxEntryData);
                    }
                }

                bundleBoxData.setBundleBoxEntries(selectedBoxEntryDatas);
                bundleBoxDatas.add(bundleBoxData);
            }
        }

        target.setBundleBoxes(bundleBoxDatas);
    }

    protected PriceData calculateProvisionedPriceForNonCartEntry(final CartModel cart, final int bundleNo,
            final BundleTemplateModel bundleTemplateModel, final ProductData productData) {
        final ChangeProductPriceBundleRuleModel reducedPriceRule = getBundleRuleService().getChangePriceBundleRule(
                cart, bundleTemplateModel, getProductService().getProductForCode(productData.getCode()), bundleNo);

        if (reducedPriceRule == null) {
            if (productData.getPrice() instanceof SubscriptionPricePlanData) {
                final SubscriptionPricePlanData pricePlan = (SubscriptionPricePlanData) productData.getPrice();

                if (pricePlan != null && CollectionUtils.isNotEmpty(pricePlan.getRecurringChargeEntries())) {
                    final RecurringChargeEntryData firstChargeEntry = pricePlan.getRecurringChargeEntries().iterator()
                            .next();
                    return firstChargeEntry.getPrice();
                }
                return productData.getPrice();
            } else {
                return productData.getPrice();
            }
        } else {
            return getPriceDataFactory().create(PriceDataType.BUY, reducedPriceRule.getPrice(),
                    commonI18NService.getCurrentCurrency().getIsocode());
        }
    }

    protected PriceData calculatePriceForNonCartEntry(final ProductData productData) {
        if (productData.getPrice() instanceof SubscriptionPricePlanData) {
            final SubscriptionPricePlanData pricePlan = (SubscriptionPricePlanData) productData.getPrice();
            return pricePlan;
        } else {
            return productData.getPrice();
        }
    }

    protected void populateBundleBoxEntryForDisabledProducts(final CartModel cart, final int bundleNo,
            final BundleTemplateModel bundleTemplateModel, final BundleBoxEntryData boxEntryData) {
        final ProductModel productModel = getProductService().getProductForCode(boxEntryData.getProduct().getCode());
        final String disableMessage = getBundleCommerceCartService().checkAndGetReasonForDisabledProductInComponent(
                cart, productModel, bundleTemplateModel, bundleNo, false);
        if (disableMessage != null) {
            boxEntryData.setDisabled(true);
            boxEntryData.setDisabledMessage(disableMessage);
        }
    }

    protected void populateBundleBoxEntryForNotRemovableProducts(final CartEntryModel cartEntry,
            final BundleBoxEntryData boxEntryData) {
        final String disableMessage = getBundleCommerceCartService().checkAndGetReasonForNotRemovableEntry(cartEntry);
        if (disableMessage == null) {
            boxEntryData.setRemovable(true);
        } else {
            boxEntryData.setRemovable(false);
            boxEntryData.setDisabledMessage(disableMessage);
        }
    }

    protected BundleTemplateModel findExpandedComponent(final CartModel cart,
            final BundleTemplateModel sourceBundleTemplateModel, final List<BundleTemplateModel> childBundleTemplates,
            final int bundleNo) {
        BundleTemplateModel expandedComponent = null;
        BundleTemplateModel firstExpandedComponent = null;

        for (final BundleTemplateModel bundleTemplateModel : childBundleTemplates) {
            if (getGuidedSellingService().isComponentToBeDisplayedOnGuidedSellingSelectPage(cart, bundleTemplateModel,
                    bundleNo, ServiceAddOnModel.class)) {
                if (firstExpandedComponent == null) {
                    firstExpandedComponent = bundleTemplateModel;
                }

                if (bundleTemplateModel.equals(sourceBundleTemplateModel)) {
                    expandedComponent = bundleTemplateModel;
                }
            }
        }

        if (expandedComponent == null) {
            expandedComponent = firstExpandedComponent;

        }
        return expandedComponent;
    }

    protected CartEntryModel findMatchingCartEntry(final CartModel cart, final String productCodeToFind,
            final BundleTemplateModel bundleTemplateToFind, final int bundleNoToFind) {
        final List<CartEntryModel> cartEntriesForProductInBundle = getBundleCommerceCartService()
                .getCartEntriesForProductInBundle(cart, getProductService().getProductForCode(productCodeToFind),
                        bundleNoToFind);

        for (final CartEntryModel cartEntry : cartEntriesForProductInBundle) {
            if (cartEntry.getBundleTemplate().equals(bundleTemplateToFind)) {
                return cartEntry;
            }
        }

        return null;
    }

    protected Converter<BundleTemplateModel, BundleTemplateData> getBundleTemplateConverter() {
        return bundleTemplateConverter;
    }

    @Required
    public void setBundleTemplateConverter(
            final Converter<BundleTemplateModel, BundleTemplateData> bundleTemplateConverter) {
        this.bundleTemplateConverter = bundleTemplateConverter;
    }

    protected BundleCommerceCartService getBundleCommerceCartService() {
        return bundleCommerceCartService;
    }

    @Required
    public void setBundleCommerceCartService(final BundleCommerceCartService bundleCommerceCartService) {
        this.bundleCommerceCartService = bundleCommerceCartService;
    }

    protected ProductService getProductService() {
        return productService;
    }

    @Required
    public void setProductService(final ProductService productService) {
        this.productService = productService;
    }

    protected BundleRuleService getBundleRuleService() {
        return bundleRuleService;
    }

    @Required
    public void setBundleRuleService(final BundleRuleService bundleRuleService) {
        this.bundleRuleService = bundleRuleService;
    }

    protected PriceDataFactory getPriceDataFactory() {
        return priceDataFactory;
    }

    @Required
    public void setPriceDataFactory(final PriceDataFactory priceDataFactory) {
        this.priceDataFactory = priceDataFactory;
    }

    protected CommonI18NService getCommonI18NService() {
        return commonI18NService;
    }

    @Required
    public void setCommonI18NService(final CommonI18NService commonI18NService) {
        this.commonI18NService = commonI18NService;
    }

    protected Converter<AbstractOrderEntryModel, OrderEntryData> getTelcoOrderEntryConverter() {
        return telcoOrderEntryConverter;
    }

    public void setTelcoOrderEntryConverter(
            final Converter<AbstractOrderEntryModel, OrderEntryData> telcoOrderEntryConverter) {
        this.telcoOrderEntryConverter = telcoOrderEntryConverter;
    }

    @Required
    public void setBundleTemplateService(final BundleTemplateService bundleTemplateService) {
        this.bundleTemplateService = bundleTemplateService;
    }

    protected BundleTemplateService getBundleTemplateService() {
        return bundleTemplateService;
    }

    @Required
    public void setGuidedSellingService(final GuidedSellingService guidedSellingService) {
        this.guidedSellingService = guidedSellingService;
    }

    protected GuidedSellingService getGuidedSellingService() {
        return guidedSellingService;
    }

    protected Converter<BundleTemplateModel, BundleBoxData> getBundleBoxConverter() {
        return bundleBoxConverter;
    }

    @Required
    public void setBundleBoxConverter(final Converter<BundleTemplateModel, BundleBoxData> bundleBoxConverter) {
        this.bundleBoxConverter = bundleBoxConverter;
    }
}

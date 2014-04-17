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
import de.hybris.platform.commercefacades.product.converters.populator.ProductClassificationPopulator;
import de.hybris.platform.commercefacades.product.converters.populator.ProductDescriptionPopulator;
import de.hybris.platform.commercefacades.product.converters.populator.ProductPricePopulator;
import de.hybris.platform.commercefacades.product.data.ProductData;
import de.hybris.platform.configurablebundleservices.bundle.BundleRuleService;
import de.hybris.platform.configurablebundleservices.bundle.BundleTemplateService;
import de.hybris.platform.configurablebundleservices.model.BundleTemplateModel;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.servicelayer.i18n.CommonI18NService;
import de.hybris.platform.subscriptionfacades.data.TermOfServiceFrequencyData;
import de.hybris.platform.subscriptionfacades.product.converters.populator.SubscriptionProductBasicPopulator;
import de.hybris.platform.subscriptionfacades.product.converters.populator.SubscriptionProductEntitlementPopulator;
import de.hybris.platform.subscriptionfacades.product.converters.populator.SubscriptionProductPricePlanPopulator;
import de.hybris.platform.subscriptionservices.enums.TermOfServiceFrequency;
import de.hybris.platform.subscriptionservices.model.SubscriptionProductModel;
import de.hybris.platform.subscriptionservices.price.SubscriptionCommercePriceService;
import com.epam.cme.core.model.DeviceModel;
import com.epam.cme.facades.data.BundleTabData;
import com.epam.cme.facades.data.BundleTemplateData;
import com.epam.cme.facades.data.FrequencyTabData;
import com.epam.cme.facades.product.FrequencyTabDataComparator;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.BooleanUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;

/**
 * Populates the {@link ProductData} with the device and plan for guided selling bundle information
 * to be used by front end.
 */
public abstract class AbstractProductBundleTabsPopulator<SOURCEPRODUCT extends ProductModel, TARGETPRODUCT extends ProductData, SOURCETEMPLATE extends BundleTemplateModel>
        implements Populator<SOURCEPRODUCT, TARGETPRODUCT> {

    private static final Logger LOG = Logger.getLogger(AbstractProductBundleTabsPopulator.class);

    private CommonI18NService commonI18NService;
    private BundleTemplateService bundleTemplateService;
    private BundleRuleService bundleRuleService;
    private Converter<BundleTemplateModel, BundleTemplateData> bundleTemplateConverter;
    private Converter<ProductModel, ProductData> productConverter;
    private PriceDataFactory priceDataFactory;
    private ProductPricePopulator<ProductModel, ProductData> productPricePopulator;
    private Converter<TermOfServiceFrequency, TermOfServiceFrequencyData> termOfServiceFrequencyConverter;
    private ProductDescriptionPopulator<ProductModel, ProductData> productDescriptionPopulator;
    private ProductClassificationPopulator<ProductModel, ProductData> productClassificationPopulator;
    private SubscriptionProductBasicPopulator<ProductModel, ProductData> subscriptionProductBasicPopulator;
    private SubscriptionProductEntitlementPopulator<ProductModel, ProductData> subscriptionProductEntitlementPopulator;
    private SubscriptionCommercePriceService commercePriceService;
    private SubscriptionProductPricePlanPopulator<ProductModel, ProductData> subscriptionProductPricePopulator;

    @Override
    public void populate(final SOURCEPRODUCT productModel, final TARGETPRODUCT productData) throws ConversionException {
        // iterate over all components, which represent the package tabs in the frontend
        final Map<String, BundleTabData> bundleTabsMap = new HashMap<String, BundleTabData>();
        for (final SOURCETEMPLATE sourceComponent : getComponents(productModel)) {
            final SOURCETEMPLATE parentBundleTemplate = (SOURCETEMPLATE) sourceComponent.getParentTemplate();

            final SOURCETEMPLATE targetComponent = getTargetComponent(sourceComponent);

            final BundleTabData bundleTabData;
            if (bundleTabsMap.containsKey(parentBundleTemplate.getId())) {
                bundleTabData = bundleTabsMap.get(parentBundleTemplate.getId());
            } else {
                bundleTabData = new BundleTabData();
                bundleTabsMap.put(parentBundleTemplate.getId(), bundleTabData);
            }

            bundleTabData.setParentBundleTemplate(getBundleTemplateConverter().convert(parentBundleTemplate));
            bundleTabData.setSourceComponent(getBundleTemplateConverter().convert(sourceComponent));
            bundleTabData.setTargetComponent(getBundleTemplateConverter().convert(targetComponent));

            final Map<String, FrequencyTabData> frequencyTabsMap = new HashMap<String, FrequencyTabData>();
            final List<FrequencyTabData> frequencyTabList = bundleTabData.getFrequencyTabs();
            if (CollectionUtils.isNotEmpty(frequencyTabList)) {
                for (final FrequencyTabData frequencyTabData : frequencyTabList) {
                    frequencyTabsMap.put(frequencyTabData.getTermOfServiceFrequency().getCode() + ":"
                            + frequencyTabData.getTermOfServiceNumber(), frequencyTabData);
                }
            }

            // iterate over all products per bundle tab
            for (final ProductModel targetProductModel : getProducts(productModel, sourceComponent, targetComponent)) {
                if (targetProductModel instanceof SubscriptionProductModel) {
                    final SubscriptionProductModel subscriptionProductModel = (SubscriptionProductModel) targetProductModel;

                    final TermOfServiceFrequencyData termOfServiceFrequency = getTermOfServiceFrequencyConverter()
                            .convert(subscriptionProductModel.getSubscriptionTerm().getTermOfServiceFrequency());

                    final int termOfServiceNumber = subscriptionProductModel.getSubscriptionTerm()
                            .getTermOfServiceNumber() == null ? 0 : subscriptionProductModel.getSubscriptionTerm()
                            .getTermOfServiceNumber().intValue();

                    // The list of Plans is split by its terms and conditions number and frequency,
                    // which lead to the frequency tabs in the frontend
                    FrequencyTabData frequencyTab;
                    final String frequencyString = termOfServiceFrequency.getCode() + ":" + termOfServiceNumber;

                    if (frequencyTabsMap.containsKey(frequencyString)) {
                        frequencyTab = frequencyTabsMap.get(frequencyString);
                    } else {
                        frequencyTab = buildFrequencyTab(termOfServiceFrequency, termOfServiceNumber);
                        frequencyTabsMap.put(frequencyString, frequencyTab);
                    }

                    // the related product is populated with specific information
                    final ProductData subscriptionProductData = getProductConverter().convert(subscriptionProductModel);

                    getSubscriptionProductBasicPopulator().populate(subscriptionProductModel, subscriptionProductData);
                    getSubscriptionProductEntitlementPopulator().populate(subscriptionProductModel,
                            subscriptionProductData);

                    callPopulators(sourceComponent, targetComponent, productModel, productData,
                            subscriptionProductModel, subscriptionProductData);

                    frequencyTab.getProducts().add(subscriptionProductData);
                } else {
                    LOG.error("Product '" + targetProductModel.getCode()
                            + "' is not a SubscriptionProduct. Ignoring it.");
                }
            }

            final List<FrequencyTabData> sortedFrequencies = new ArrayList<FrequencyTabData>(frequencyTabsMap.values());
            Collections.sort(sortedFrequencies, new FrequencyTabDataComparator());
            Collections.reverse(sortedFrequencies);
            bundleTabData.setFrequencyTabs(sortedFrequencies);

        }
        final List<BundleTabData> bundleTabs = new ArrayList<BundleTabData>(bundleTabsMap.values());

        if (productModel instanceof DeviceModel) {
            productData.setSoldIndividually(BooleanUtils.toBoolean(productModel.getSoldIndividually()));
            productData.setBundleTabs(bundleTabs);
        } else if (productModel instanceof SubscriptionProductModel) {
            productData.setBundleTabs(bundleTabs);
        }

        // populate the information about pre-selected tabs
        changePreselectedFlags(bundleTabs, productModel.getCode());
    }

    /**
     * Resolves the components to be used for populating the bundle tabs.
     * 
     * @param productModel
     *            the {@link ProductModel} for which the components should be searched for
     * @return a list of component {@link BundleTemplateModel}s which are used to populating the
     *         bundle tabs
     */
    protected abstract Collection<SOURCETEMPLATE> getComponents(SOURCEPRODUCT productModel);

    /**
     * Resolves the target component {@link BundleTemplateModel} for a source component
     * {@link BundleTemplateModel}
     * 
     * @param sourceComponent
     *            The source component {@link BundleTemplateModel} for which the target component
     *            should be found.
     * @return the target component {@link BundleTemplateModel}
     */
    protected abstract SOURCETEMPLATE getTargetComponent(final SOURCETEMPLATE sourceComponent);

    /**
     * Resolves the list of {@link ProductModel}s for the given <code>productModel</code>.
     * 
     * @param productModel
     *            the {@link ProductModel} for which the list of {@link ProductModel}s should be
     *            found
     * @param sourceComponent
     *            the source component {@link BundleTemplateModel} for which the list of
     *            {@link ProductModel}s should be found
     * @param targetComponent
     *            the target component {@link BundleTemplateModel} for which the list of
     *            {@link ProductModel}s should be found
     * @return the list of {@link ProductModel}s matching the parameters
     */
    protected abstract List<ProductModel> getProducts(final SOURCEPRODUCT productModel,
            final SOURCETEMPLATE sourceComponent, final SOURCETEMPLATE targetComponent);

    /**
     * Hook to call additional populators for different purposes.
     */
    protected abstract void callPopulators(final SOURCETEMPLATE sourceComponent, final SOURCETEMPLATE targetComponent,
            final SOURCEPRODUCT productModel, final TARGETPRODUCT productData,
            final SubscriptionProductModel subscriptionProductModel, final ProductData subscriptionProductData);

    protected FrequencyTabData buildFrequencyTab(final TermOfServiceFrequencyData termOfServiceFrequency,
            final int termOfServiceNumber) {
        FrequencyTabData frequencyTab;
        frequencyTab = new FrequencyTabData();
        frequencyTab.setTermOfServiceFrequency(termOfServiceFrequency);
        frequencyTab.setTermOfServiceNumber(termOfServiceNumber);
        frequencyTab.setProducts(new ArrayList<ProductData>());
        return frequencyTab;
    }

    protected boolean changePreselectedFlags(final List<BundleTabData> bundleTabs, final String productCode) {
        boolean found = false;
        for (final BundleTabData bundleTab : bundleTabs) {
            for (final FrequencyTabData frequencyTab : bundleTab.getFrequencyTabs()) {
                for (final ProductData product : frequencyTab.getProducts()) {
                    if (productCode.equals(product.getCode())) {
                        if (!found) {
                            bundleTab.setPreselected(true);
                        }
                        frequencyTab.setPreselected(true);
                        product.setPreselected(true);
                        found = true;
                    }
                }
            }
        }

        return found;
    }

    protected CommonI18NService getCommonI18NService() {
        return commonI18NService;
    }

    @Required
    public void setCommonI18NService(final CommonI18NService commonI18NService) {
        this.commonI18NService = commonI18NService;
    }

    protected BundleTemplateService getBundleTemplateService() {
        return bundleTemplateService;
    }

    @Required
    public void setBundleTemplateService(final BundleTemplateService bundleTemplateService) {
        this.bundleTemplateService = bundleTemplateService;
    }

    protected BundleRuleService getBundleRuleService() {
        return bundleRuleService;
    }

    @Required
    public void setBundleRuleService(final BundleRuleService bundleRuleService) {
        this.bundleRuleService = bundleRuleService;
    }

    protected Converter<ProductModel, ProductData> getProductConverter() {
        return productConverter;
    }

    @Required
    public void setProductConverter(final Converter<ProductModel, ProductData> productConverter) {
        this.productConverter = productConverter;
    }

    protected PriceDataFactory getPriceDataFactory() {
        return priceDataFactory;
    }

    @Required
    public void setPriceDataFactory(final PriceDataFactory priceDataFactory) {
        this.priceDataFactory = priceDataFactory;
    }

    protected ProductPricePopulator<ProductModel, ProductData> getProductPricePopulator() {
        return productPricePopulator;
    }

    @Required
    public void setProductPricePopulator(final ProductPricePopulator<ProductModel, ProductData> productPricePopulator) {
        this.productPricePopulator = productPricePopulator;
    }

    protected Converter<TermOfServiceFrequency, TermOfServiceFrequencyData> getTermOfServiceFrequencyConverter() {
        return termOfServiceFrequencyConverter;
    }

    @Required
    public void setTermOfServiceFrequencyConverter(
            final Converter<TermOfServiceFrequency, TermOfServiceFrequencyData> termOfServiceFrequencyConverter) {
        this.termOfServiceFrequencyConverter = termOfServiceFrequencyConverter;
    }

    @Required
    public void setProductDescriptionPopulator(
            final ProductDescriptionPopulator<ProductModel, ProductData> productDescriptionPopulator) {
        this.productDescriptionPopulator = productDescriptionPopulator;
    }

    protected ProductDescriptionPopulator<ProductModel, ProductData> getProductDescriptionPopulator() {
        return productDescriptionPopulator;
    }

    @Required
    public void setProductClassificationPopulator(
            final ProductClassificationPopulator<ProductModel, ProductData> productClassificationPopulator) {
        this.productClassificationPopulator = productClassificationPopulator;
    }

    protected ProductClassificationPopulator<ProductModel, ProductData> getProductClassificationPopulator() {
        return productClassificationPopulator;
    }

    protected Converter<BundleTemplateModel, BundleTemplateData> getBundleTemplateConverter() {
        return bundleTemplateConverter;
    }

    @Required
    public void setBundleTemplateConverter(
            final Converter<BundleTemplateModel, BundleTemplateData> bundleTemplateConverter) {
        this.bundleTemplateConverter = bundleTemplateConverter;
    }

    protected SubscriptionProductBasicPopulator<ProductModel, ProductData> getSubscriptionProductBasicPopulator() {
        return subscriptionProductBasicPopulator;
    }

    @Required
    public void setSubscriptionProductBasicPopulator(
            final SubscriptionProductBasicPopulator<ProductModel, ProductData> subscriptionProductBasicPopulator) {
        this.subscriptionProductBasicPopulator = subscriptionProductBasicPopulator;
    }

    protected SubscriptionProductEntitlementPopulator<ProductModel, ProductData> getSubscriptionProductEntitlementPopulator() {
        return subscriptionProductEntitlementPopulator;
    }

    @Required
    public void setSubscriptionProductEntitlementPopulator(
            final SubscriptionProductEntitlementPopulator<ProductModel, ProductData> subscriptionProductEntitlementPopulator) {
        this.subscriptionProductEntitlementPopulator = subscriptionProductEntitlementPopulator;
    }

    protected SubscriptionCommercePriceService getCommercePriceService() {
        return commercePriceService;
    }

    @Required
    public void setCommercePriceService(final SubscriptionCommercePriceService commercePriceService) {
        this.commercePriceService = commercePriceService;
    }

    protected SubscriptionProductPricePlanPopulator<ProductModel, ProductData> getSubscriptionProductPricePopulator() {
        return subscriptionProductPricePopulator;
    }

    @Required
    public void setSubscriptionProductPricePopulator(
            final SubscriptionProductPricePlanPopulator<ProductModel, ProductData> subscriptionProductPricePopulator) {
        this.subscriptionProductPricePopulator = subscriptionProductPricePopulator;
    }

}
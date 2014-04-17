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
package com.epam.cme.facades.bundle.impl;

import de.hybris.platform.commercefacades.product.PriceDataFactory;
import de.hybris.platform.commercefacades.product.data.PriceData;
import de.hybris.platform.commercefacades.product.data.PriceDataType;
import de.hybris.platform.commercefacades.product.data.ProductData;
import de.hybris.platform.commercefacades.search.ProductSearchFacade;
import de.hybris.platform.commercefacades.search.data.SearchQueryData;
import de.hybris.platform.commercefacades.search.data.SearchStateData;
import de.hybris.platform.commerceservices.search.facetdata.BreadcrumbData;
import de.hybris.platform.commerceservices.search.facetdata.FacetData;
import de.hybris.platform.commerceservices.search.facetdata.FacetValueData;
import de.hybris.platform.commerceservices.search.facetdata.ProductSearchPageData;
import de.hybris.platform.commerceservices.search.pagedata.PageableData;
import de.hybris.platform.configurablebundleservices.bundle.BundleCommerceCartService;
import de.hybris.platform.configurablebundleservices.bundle.BundleRuleService;
import de.hybris.platform.configurablebundleservices.bundle.BundleTemplateService;
import de.hybris.platform.configurablebundleservices.model.BundleTemplateModel;
import de.hybris.platform.configurablebundleservices.model.ChangeProductPriceBundleRuleModel;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.core.model.order.CartEntryModel;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.order.CartService;
import de.hybris.platform.product.ProductService;
import de.hybris.platform.subscriptionservices.model.RecurringChargeEntryModel;
import de.hybris.platform.subscriptionservices.model.SubscriptionPricePlanModel;
import de.hybris.platform.subscriptionservices.model.SubscriptionProductModel;
import de.hybris.platform.subscriptionservices.price.SubscriptionCommercePriceService;
import com.epam.cme.core.bundle.GuidedSellingService;
import com.epam.cme.core.model.DeviceModel;
import com.epam.cme.core.model.ServiceAddOnModel;
import com.epam.cme.core.model.ServicePlanModel;
import com.epam.cme.facades.bundle.GuidedSellingFacade;
import com.epam.cme.facades.data.BundleTemplateData;
import com.epam.cme.facades.data.BundleTemplateServiceAddOnsPopulatorParameters;
import com.epam.cme.facades.data.DashboardData;
import com.epam.cme.facades.data.DashboardPopulatorParameters;

import java.math.BigDecimal;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Required;

/**
 * Default implementation for {@link GuidedSellingFacade}
 */
public class DefaultGuidedSellingFacade implements GuidedSellingFacade {
    private BundleTemplateService bundleTemplateService;
    private BundleRuleService bundleRuleService;
    private ProductService productService;
    private PriceDataFactory priceDataFactory;
    private Populator<BundleTemplateServiceAddOnsPopulatorParameters, BundleTemplateData> bundleTemplateAddOnsPopulator;
    private Populator<DashboardPopulatorParameters, DashboardData> dashboardPopulator;
    private BundleCommerceCartService bundleCommerceCartService;
    private CartService cartService;
    private ProductSearchFacade<ProductData> productSearchFacade;
    private GuidedSellingService guidedSellingService;
    private SubscriptionCommercePriceService commercePriceService;

    @Override
    public ProductType getComponentProductType(final String bundleTemplateId) {
        final BundleTemplateModel bundleModel = getBundleTemplateService().getBundleTemplateForCode(bundleTemplateId);
        if (CollectionUtils.isNotEmpty(bundleModel.getProducts())) {
            final ProductModel product = bundleModel.getProducts().iterator().next();

            if (product instanceof DeviceModel) {
                return ProductType.DEVICE;
            } else if (product instanceof ServicePlanModel) {
                return ProductType.SERVICEPLAN;
            } else if (product instanceof ServiceAddOnModel) {
                return ProductType.SERVICEADDON;
            }
        }

        return null;
    }

    @Override
    public String getRelativeComponentId(final String bundleNo, final String bundleTemplateId,
            final int relativeposition) {
        final BundleTemplateModel bundleModel = getBundleTemplateService().getBundleTemplateForCode(bundleTemplateId);
        final BundleTemplateModel subsequentBundleModel = getGuidedSellingService().getRelativeSelectionComponent(
                getCartService().getSessionCart(), bundleModel, Integer.parseInt(bundleNo), relativeposition);

        if (subsequentBundleModel == null) {
            return null;
        }

        return subsequentBundleModel.getId();
    }

    @Override
    public BundleTemplateData getComponentToEdit(final String bundleNo, final String bundleTemplateId) {
        final BundleTemplateServiceAddOnsPopulatorParameters populatorParams = new BundleTemplateServiceAddOnsPopulatorParameters();
        populatorParams.setBundleNo(Integer.valueOf(bundleNo));
        populatorParams.setCurrentComponent(getBundleTemplateService().getBundleTemplateForCode(bundleTemplateId));
        populatorParams.setCart(getCartService().getSessionCart());

        final BundleTemplateData bundleTemplateData = new BundleTemplateData();
        getBundleTemplateAddOnsPopulator().populate(populatorParams, bundleTemplateData);
        return bundleTemplateData;
    }

    @Override
    public boolean checkIsComponentSelectionCriteriaMet(final String bundleNo, final String bundleTemplateId) {
        final CartModel cartModel = getCartService().getSessionCart();

        final int bundleNoValue = Integer.parseInt(bundleNo);

        final List<CartEntryModel> entries = getBundleCommerceCartService().getCartEntriesForBundle(cartModel,
                bundleNoValue);
        final BundleTemplateModel parentModel = entries.get(0).getBundleTemplate().getParentTemplate();
        final BundleTemplateModel bundleModel = getBundleTemplateService().getBundleTemplateForCode(bundleTemplateId,
                parentModel.getVersion());

        return getBundleCommerceCartService().checkIsComponentSelectionCriteriaMet(cartModel, bundleModel,
                bundleNoValue);
    }

    protected ProductSearchPageData<SearchStateData, ProductData> enrichSearchResult(
            final ProductSearchPageData<SearchStateData, ProductData> searchPageData, final String componentId,
            final String componentVersion, final Integer bundleNo) {
        final CartModel cartModel = getCartService().getSessionCart();
        final BundleTemplateModel bundleModel = getBundleTemplateService().getBundleTemplateForCode(componentId,
                componentVersion);

        // bundle prices
        for (final ProductData productData : searchPageData.getResults()) {
            final ProductModel product = getProductService().getProductForCode(productData.getCode());
            final ChangeProductPriceBundleRuleModel priceRule = getBundleRuleService().getChangePriceBundleRule(
                    cartModel, bundleModel, product, bundleNo.intValue());
            SubscriptionPricePlanModel pricePlan = null;

            if (product instanceof SubscriptionProductModel) {
                pricePlan = getCommercePriceService().getSubscriptionPricePlanForProduct(
                        (SubscriptionProductModel) product);
            }

            final Double bestPrice = getMinPriceOfRuleAndPlan(pricePlan, priceRule);

            if (bestPrice != null) {
                final PriceData priceData = getPriceDataFactory().create(PriceDataType.BUY,
                        BigDecimal.valueOf(bestPrice.doubleValue()), cartModel.getCurrency().getIsocode());

                productData.setThisBundleProductPrice(priceData);
            }
        }

        // disable rules
        for (final ProductData productData : searchPageData.getResults()) {
            final ProductModel product = getProductService().getProductForCode(productData.getCode());
            final String disableMessage = getBundleCommerceCartService()
                    .checkAndGetReasonForDisabledProductInComponent(cartModel, product, bundleModel,
                            bundleNo.intValue(), true);
            if (disableMessage != null) {
                productData.setDisabled(true);
                productData.setDisabledMessage(disableMessage);
            }
        }

        return searchPageData;
    }

    protected Double getMinPriceOfRuleAndPlan(final SubscriptionPricePlanModel pricePlan,
            final ChangeProductPriceBundleRuleModel priceRule) {
        Double rulePrice = null;
        Double planPrice = null;

        if (pricePlan != null) {
            final RecurringChargeEntryModel chargeEntry = getCommercePriceService().getFirstRecurringPriceFromPlan(
                    pricePlan);
            planPrice = chargeEntry.getPrice();
        }

        if (priceRule != null) {
            rulePrice = Double.valueOf(priceRule.getPrice().doubleValue());
        }

        if (rulePrice == null && planPrice != null) {
            return planPrice;
        } else if (rulePrice != null && planPrice == null) {
            return rulePrice;
        } else if (rulePrice != null && planPrice != null) {
            return (Double.compare(rulePrice.doubleValue(), planPrice.doubleValue()) == -1) ? rulePrice : planPrice;
        }

        return null;
    }

    protected BundleTemplateService getBundleTemplateService() {
        return bundleTemplateService;
    }

    @Required
    public void setBundleTemplateService(final BundleTemplateService bundleTemplateService) {
        this.bundleTemplateService = bundleTemplateService;
    }

    public Populator<BundleTemplateServiceAddOnsPopulatorParameters, BundleTemplateData> getBundleTemplateAddOnsPopulator() {
        return bundleTemplateAddOnsPopulator;
    }

    @Required
    public void setBundleTemplateAddOnsPopulator(
            final Populator<BundleTemplateServiceAddOnsPopulatorParameters, BundleTemplateData> bundleTemplateAddOnsPopulator) {
        this.bundleTemplateAddOnsPopulator = bundleTemplateAddOnsPopulator;
    }

    protected BundleCommerceCartService getBundleCommerceCartService() {
        return bundleCommerceCartService;
    }

    @Required
    public void setBundleCommerceCartService(final BundleCommerceCartService bundleCommerceCartService) {
        this.bundleCommerceCartService = bundleCommerceCartService;
    }

    protected CartService getCartService() {
        return cartService;
    }

    @Required
    public void setCartService(final CartService cartService) {
        this.cartService = cartService;
    }

    protected ProductSearchFacade<ProductData> getProductSearchFacade() {
        return productSearchFacade;
    }

    @Required
    public void setProductSearchFacade(final ProductSearchFacade<ProductData> productSearchFacade) {
        this.productSearchFacade = productSearchFacade;
    }

    protected BundleRuleService getBundleRuleService() {
        return bundleRuleService;
    }

    @Required
    public void setBundleRuleService(final BundleRuleService bundleRuleService) {
        this.bundleRuleService = bundleRuleService;
    }

    protected ProductService getProductService() {
        return productService;
    }

    @Required
    public void setProductService(final ProductService productService) {
        this.productService = productService;
    }

    protected PriceDataFactory getPriceDataFactory() {
        return priceDataFactory;
    }

    @Required
    public void setPriceDataFactory(final PriceDataFactory priceDataFactory) {
        this.priceDataFactory = priceDataFactory;
    }

    @Override
    public ProductSearchPageData<SearchStateData, ProductData> bundleSearch(final PageableData pageableData,
            final String searchQuery, final String urlPrefix, final String componentId, final Integer bundleNo) {
        final SearchStateData searchState = new SearchStateData();

        final CartModel cartModel = getCartService().getSessionCart();
        final List<CartEntryModel> entries = getBundleCommerceCartService().getCartEntriesForBundle(cartModel,
                bundleNo.intValue());
        final BundleTemplateModel bundleModel = entries.get(0).getBundleTemplate().getParentTemplate();
        final String bundleVersion = bundleModel.getVersion();

        patchQuery(searchState, searchQuery, componentId, bundleVersion);

        ProductSearchPageData<SearchStateData, ProductData> searchPageData = productSearchFacade.textSearch(
                searchState, pageableData);

        searchPageData = enrichSearchResult(searchPageData, componentId, bundleVersion, bundleNo);

        patchURLs(urlPrefix, searchPageData);

        return searchPageData;
    }

    protected String patchURLs(final String urlPrefix,
            final ProductSearchPageData<SearchStateData, ProductData> searchPageData) {
        for (final BreadcrumbData<SearchStateData> breadcrumbData : searchPageData.getBreadcrumbs()) {
            breadcrumbData.getRemoveQuery().setUrl(urlPrefix + breadcrumbData.getRemoveQuery().getQuery().getValue());
        }
        for (final FacetData<SearchStateData> facetData : searchPageData.getFacets()) {
            if (facetData.getTopValues() != null) {
                for (final FacetValueData<SearchStateData> facetValuesData : facetData.getTopValues()) {
                    facetValuesData.getQuery().setUrl(urlPrefix + facetValuesData.getQuery().getQuery().getValue());
                }
            }
            for (final FacetValueData<SearchStateData> facetValuesData : facetData.getValues()) {
                facetValuesData.getQuery().setUrl(urlPrefix + facetValuesData.getQuery().getQuery().getValue());
            }
        }
        searchPageData.getCurrentQuery().setUrl(urlPrefix + searchPageData.getCurrentQuery().getQuery().getValue());
        return urlPrefix;
    }

    protected void patchQuery(final SearchStateData searchState, final String searchQuery, final String componentId,
            final String componentVersion) {
        final StringBuffer newSearchQuery = new StringBuffer(100);
        if (StringUtils.isEmpty(searchQuery)) {
            newSearchQuery.append(':');
        } else if (searchQuery.charAt(0) == ':') {
            newSearchQuery.append(searchQuery);
        }

        newSearchQuery.append(":bundleTemplates_string_mv:");
        newSearchQuery.append(componentId);
        newSearchQuery.append('|');
        newSearchQuery.append(componentVersion);

        final SearchQueryData searchQueryData = new SearchQueryData();
        searchQueryData.setValue(newSearchQuery.toString());
        searchState.setQuery(searchQueryData);
    }

    @Override
    public DashboardData getDashboard(final int bundleNo, final String currentComponent) {
        final DashboardData cartData = null;
        if (getCartService().hasSessionCart()) {
            final DashboardPopulatorParameters populatorParams = new DashboardPopulatorParameters();
            populatorParams.setCart(getCartService().getSessionCart());
            populatorParams.setBundleNo(bundleNo);
            populatorParams.setCurrentComponent(getBundleTemplateService().getBundleTemplateForCode(currentComponent));

            final DashboardData dashboardData = new DashboardData();
            getDashboardPopulator().populate(populatorParams, dashboardData);
            return dashboardData;
        }

        return cartData;
    }

    protected Populator<DashboardPopulatorParameters, DashboardData> getDashboardPopulator() {
        return dashboardPopulator;
    }

    @Required
    public void setDashboardPopulator(final Populator<DashboardPopulatorParameters, DashboardData> dashboardPopulator) {
        this.dashboardPopulator = dashboardPopulator;
    }

    protected GuidedSellingService getGuidedSellingService() {
        return guidedSellingService;
    }

    @Required
    public void setGuidedSellingService(final GuidedSellingService guidedSellingService) {
        this.guidedSellingService = guidedSellingService;
    }

    protected SubscriptionCommercePriceService getCommercePriceService() {
        return commercePriceService;
    }

    @Required
    public void setCommercePriceService(final SubscriptionCommercePriceService commercePriceService) {
        this.commercePriceService = commercePriceService;
    }

}

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

import de.hybris.platform.commercefacades.product.data.ProductData;
import de.hybris.platform.configurablebundleservices.model.BundleTemplateModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.subscriptionfacades.product.converters.populator.SubscriptionProductPricePlanPopulator;
import de.hybris.platform.subscriptionservices.model.SubscriptionProductModel;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;

/**
 * Populates the {@link ProductData} with the plans for guided selling bundle information to be used
 * by front end.
 */
public class SubscriptionProductBundleTabsPopulator<SOURCEPRODUCT extends SubscriptionProductModel, TARGETPRODUCT extends ProductData, SOURCETEMPLATE extends BundleTemplateModel>
        extends AbstractProductBundleTabsPopulator<SOURCEPRODUCT, TARGETPRODUCT, SOURCETEMPLATE> {
    private static final Logger LOG = Logger.getLogger(SubscriptionProductBundleTabsPopulator.class);

    private SubscriptionProductPricePlanPopulator<ProductModel, ProductData> subscriptionProductPricePopulator;

    private String typeClassName;

    /**
     * Resolves the components to be used for populating the bundle tabs. For Plans we are using all
     * existing components that have plans as products, no matter if they are assigned to this
     * <code>productModel</code> or not. So for any plan we will find all existing plan components.
     * 
     * @param productModel
     *            the {@link ProductModel} for which the components should be searched for
     * @return a list of component {@link BundleTemplateModel}s which are used to populating the
     *         bundle tabs
     */
    @Override
    protected Collection<SOURCETEMPLATE> getComponents(final SOURCEPRODUCT productModel) {
        // Get all existing Bundle Templates
        final Collection<SOURCETEMPLATE> bundleTemplateModels = (List<SOURCETEMPLATE>) getBundleTemplateService()
                .getAllRootBundleTemplates(productModel.getCatalogVersion());
        final Collection<SOURCETEMPLATE> componentModels = new ArrayList<SOURCETEMPLATE>();

        Class typeClass = null;
        try {
            typeClass = Class.forName(getTypeClassName());
        } catch (final ClassNotFoundException e) {
            LOG.error("Configuration problem with details page populator", e);
            return componentModels;
        }

        for (final SOURCETEMPLATE bundleTemplateModel : bundleTemplateModels) {
            for (final BundleTemplateModel component : getBundleTemplateService().getAllComponentsOfType(
                    bundleTemplateModel, typeClass)) {
                componentModels.add((SOURCETEMPLATE) component);
            }
        }

        return componentModels;
    }

    /**
     * Resolves the target component {@link BundleTemplateModel} for a source component
     * {@link BundleTemplateModel}. In case of a Plan, the target component the same as the source
     * component as it is not used.
     * 
     * @param sourceComponent
     *            The source component {@link BundleTemplateModel} for which the target component
     *            should be found.
     * @return the target component {@link BundleTemplateModel}
     */
    @Override
    protected SOURCETEMPLATE getTargetComponent(final SOURCETEMPLATE sourceComponent) {
        return sourceComponent;
    }

    /**
     * Resolves the list of {@link ProductModel}s for the given <code>sourceComponent</code>. For
     * plans it simply resolves all products for the source component {@link BundleTemplateModel}.
     * 
     * @param productModel
     *            the {@link ProductModel} for which the list of {@link ProductModel}s should be
     *            found
     * @param sourceComponent
     *            not used here
     * @param targetComponent
     *            not used here
     * @return the list of {@link ProductModel}s matching the parameters
     */
    @Override
    protected List<ProductModel> getProducts(final SOURCEPRODUCT productModel, final SOURCETEMPLATE sourceComponent,
            final SOURCETEMPLATE targetComponent) {
        return sourceComponent.getProducts();
    }

    /**
     * Hook to call additional populators for different purposes. For Plans we are populating the
     * standard price of the according plan. Afterwards we are populating the classification data of
     * the plan.
     * 
     * @param sourceComponent
     *            not used in this case
     * @param targetComponent
     *            not used in this case
     * @param productData
     *            not used in this case
     * @param productModel
     *            not used in this case
     * @param subscriptionProductModel
     *            in this case the DTO of the plan product
     * @param subscriptionProductData
     *            in this case the Model of the plan product
     */
    @Override
    protected void callPopulators(final SOURCETEMPLATE sourceComponent, final SOURCETEMPLATE targetComponent,
            final SOURCEPRODUCT productModel, final TARGETPRODUCT productData,
            final SubscriptionProductModel subscriptionProductModel, final ProductData subscriptionProductData) {
        getProductPricePopulator().populate(subscriptionProductModel, subscriptionProductData);
        getSubscriptionProductPricePopulator().populate(subscriptionProductModel, subscriptionProductData);
        getProductClassificationPopulator().populate(subscriptionProductModel, subscriptionProductData);
    }

    protected String getTypeClassName() {
        return typeClassName;
    }

    @Required
    public void setTypeClassName(final String typeClassName) {
        this.typeClassName = typeClassName;
    }

    @Override
    protected SubscriptionProductPricePlanPopulator<ProductModel, ProductData> getSubscriptionProductPricePopulator() {
        return subscriptionProductPricePopulator;
    }

    @Override
    @Required
    public void setSubscriptionProductPricePopulator(
            final SubscriptionProductPricePlanPopulator<ProductModel, ProductData> subscriptionProductPricePopulator) {
        this.subscriptionProductPricePopulator = subscriptionProductPricePopulator;
    }

}

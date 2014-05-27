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
package com.epam.cme.facades.order.impl;

import de.hybris.platform.commercefacades.order.data.CartData;
import de.hybris.platform.commercefacades.order.data.CartModificationData;
import de.hybris.platform.commerceservices.order.CommerceCartModification;
import de.hybris.platform.commerceservices.order.CommerceCartModificationException;
import de.hybris.platform.configurablebundleservices.bundle.BundleCommerceCartService;
import de.hybris.platform.configurablebundleservices.bundle.BundleTemplateService;
import de.hybris.platform.configurablebundleservices.model.BundleTemplateModel;
import de.hybris.platform.converters.Converters;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.CartEntryModel;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.subscriptionfacades.order.impl.DefaultSubscriptionCartFacade;
import de.hybris.platform.subscriptionservices.subscription.BillingTimeService;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Required;

import com.epam.cme.facades.order.BundleCartFacade;

/**
 * Default implementation for {@link BundleCartFacade}
 */
public class DefaultBundleCartFacade extends DefaultSubscriptionCartFacade implements BundleCartFacade {
    private BillingTimeService billingTimeService;
    private BundleCommerceCartService bundleCommerceCartService;
    private BundleTemplateService bundleTemplateService;
    private Converter<CartModel, CartData> bundleCartConverter;

    @Override
    public List<CartModificationData> addToCart(final String productCode, final long quantity, final int bundleNo,
            final String bundleTemplateId, final boolean removeCurrentProducts)
            throws CommerceCartModificationException {
        final CartModel cartModel = getCartService().getSessionCart();
        final ProductModel product = getProductService().getProductForCode(productCode);

        final String xml = getProductAsXML(product);

        BundleTemplateModel bundleTemplate = null;
        if (StringUtils.isNotEmpty(bundleTemplateId)) {
            if (bundleNo > 0) {
                final List<CartEntryModel> entries = getBundleCommerceCartService().getCartEntriesForBundle(cartModel,
                        bundleNo);
                final BundleTemplateModel parentModel = entries.get(0).getBundleTemplate().getParentTemplate();
                bundleTemplate = getBundleTemplateService().getBundleTemplateForCode(bundleTemplateId,
                        parentModel.getVersion());
            } else {
                bundleTemplate = getBundleTemplateService().getBundleTemplateForCode(bundleTemplateId);
            }
        }

        final List<CommerceCartModification> modifications = getBundleCommerceCartService().addToCart(cartModel,
                product, quantity, product.getUnit(), false, bundleNo, bundleTemplate, removeCurrentProducts, xml);
        return Converters.convertAll(modifications, getCartModificationConverter());
    }

    @Override
    public List<CartModificationData> addToCart(final String productCode1, final int bundleNo,
            final String bundleTemplateId1, final String productCode2, final String bundleTemplateId2)
            throws CommerceCartModificationException {
        final CartModel cartModel = getCartService().getSessionCart();
        final ProductModel product1 = getProductService().getProductForCode(productCode1);
        final ProductModel product2 = getProductService().getProductForCode(productCode2);

        final String xml1 = getProductAsXML(product1);
        final String xml2 = getProductAsXML(product2);

        BundleTemplateModel bundleTemplateModel1 = null;
        BundleTemplateModel bundleTemplateModel2 = null;
        if (StringUtils.isNotEmpty(bundleTemplateId1)) {
            bundleTemplateModel1 = getBundleTemplateService().getBundleTemplateForCode(bundleTemplateId1);
        }
        if (StringUtils.isNotEmpty(bundleTemplateId2)) {
            bundleTemplateModel2 = getBundleTemplateService().getBundleTemplateForCode(bundleTemplateId2);
        }

        final List<CommerceCartModification> modifications = getBundleCommerceCartService().addToCart(cartModel,
                product1.getUnit(), bundleNo, product1, bundleTemplateModel1, product2, bundleTemplateModel2, xml1,
                xml2);
        return Converters.convertAll(modifications, getCartModificationConverter());
    }

    @Override
    public void deleteCartBundle(final int bundleNo) throws CommerceCartModificationException {
        getBundleCommerceCartService().removeAllEntries(getCartService().getSessionCart(), bundleNo);
    }

    @Override
    public boolean isCartValid() {
        final BundleTemplateModel bundleTemplate = getBundleCommerceCartService().getFirstInvalidComponentInCart(
                getCartService().getSessionCart());

        if (bundleTemplate == null) {
            return true;
        }
        return false;
    }

    @Override
    public List<CartModificationData> addRestoredEntriesToCart(final List<AbstractOrderEntryModel> restoredEntries)
            throws CommerceCartModificationException {
        int prevBundleNo = -1;
        int newBundleNo = 0;
        List<CartModificationData> modifications = new ArrayList<>();
        for (AbstractOrderEntryModel e : restoredEntries) {
            if (e.getBundleNo() != prevBundleNo) {
                prevBundleNo = e.getBundleNo();
                modifications.addAll(addToCart(e.getProduct().getCode(), e.getQuantity(), -1, e.getBundleTemplate()
                        .getId(), false));
                newBundleNo = modifications.get(modifications.size() - 1).getEntry().getBundleNo();
            } else {
                addToCart(e.getProduct().getCode(), e.getQuantity(), newBundleNo, e.getBundleTemplate().getId(), false);
            }
        }
        return modifications;
    }

    protected BillingTimeService getBillingTimeService() {
        return billingTimeService;
    }

    @Required
    public void setBillingTimeService(final BillingTimeService billingTimeService) {
        this.billingTimeService = billingTimeService;
    }

    protected BundleCommerceCartService getBundleCommerceCartService() {
        return bundleCommerceCartService;
    }

    @Required
    public void setBundleCommerceCartService(final BundleCommerceCartService bundleCommerceCartService) {
        this.bundleCommerceCartService = bundleCommerceCartService;
    }

    protected BundleTemplateService getBundleTemplateService() {
        return bundleTemplateService;
    }

    @Required
    public void setBundleTemplateService(final BundleTemplateService bundleTemplateService) {
        this.bundleTemplateService = bundleTemplateService;
    }

    protected Converter<CartModel, CartData> getBundleCartConverter() {
        return bundleCartConverter;
    }

    @Required
    public void setBundleCartConverter(final Converter<CartModel, CartData> bundleCartConverter) {
        this.bundleCartConverter = bundleCartConverter;
    }
}

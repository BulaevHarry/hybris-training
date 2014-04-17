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

import de.hybris.platform.commercefacades.order.data.OrderEntryData;
import de.hybris.platform.configurablebundleservices.bundle.BundleCommerceCartService;
import de.hybris.platform.configurablebundleservices.model.BundleTemplateModel;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.core.model.order.CartEntryModel;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.subscriptionfacades.order.converters.populator.SubscriptionOrderEntryPopulator;
import com.epam.cme.facades.data.BundleTemplateData;

import org.springframework.beans.factory.annotation.Required;
import org.springframework.util.Assert;

/**
 * Converter for converting order / cart entries. It adds bundle specific data (bundle no,
 * component) and flags the entry as Valid/Updateable/Removable/Editable.
 */
public class BundleOrderEntryPopulator extends SubscriptionOrderEntryPopulator {
    private Converter<BundleTemplateModel, BundleTemplateData> bundleTemplateConverter;
    private BundleCommerceCartService bundleCommerceCartService;

    @Override
    public void populate(final AbstractOrderEntryModel source, final OrderEntryData target) {
        Assert.notNull(source, "Parameter source cannot be null.");
        Assert.notNull(target, "Parameter target cannot be null.");

        if (source.getOrder() != null && source.getOrder().getBillingTime() != null) {
            addCommon(source, target);
            target.setBundleNo(source.getBundleNo() == null ? 0 : source.getBundleNo().intValue());

            if (source.getBundleTemplate() != null) {
                target.setComponent(getBundleTemplateConverter().convert(source.getBundleTemplate()));
                if (source.getBundleTemplate().getParentTemplate() != null) {
                    target.setRootBundleTemplate(getBundleTemplateConverter().convert(
                            source.getBundleTemplate().getParentTemplate()));
                }
            }
        }
    }

    /**
     * Modify order entry: set flags for Updateable/Removable/Editable/IsValid
     */
    @Override
    protected void addCommon(final AbstractOrderEntryModel orderEntry, final OrderEntryData entry) {
        if (orderEntry instanceof CartEntryModel) {
            final CartModel cart = ((CartEntryModel) orderEntry).getOrder();
            adjustUpdateable(entry, orderEntry);
            adjustRemoveable(entry, (CartEntryModel) orderEntry);
            adjustEditable(entry, (CartEntryModel) orderEntry, cart);
            adjustIsValid(entry, (CartEntryModel) orderEntry, cart);
        }
    }

    /**
     * Modify method to call backend service to determine if an entry is updateable or not
     * 
     * @param entry
     * @param entryToUpdate
     *            the {@link AbstractOrderEntryModel}
     */
    @Override
    protected void adjustUpdateable(final OrderEntryData entry, final AbstractOrderEntryModel entryToUpdate) {

        final CartEntryModel orderEntry = (CartEntryModel) entryToUpdate;
        entry.setUpdateable(getBundleCommerceCartService().checkIsEntryUpdateable(orderEntry));

    }

    /**
     * 
     * Calls backend service to determine if entry is removable or not
     * 
     * @param entry
     * @param entryToUpdate
     */
    protected void adjustRemoveable(final OrderEntryData entry, final CartEntryModel entryToUpdate) {
        entry.setRemoveable(getBundleCommerceCartService().checkIsEntryRemovable(entryToUpdate));
    }

    /**
     * Calls backend service to determine if entry is editable or not. The backend service is for
     * cart items that are part of a bundle For standalone products return false
     * 
     * @param entry
     * @param entryToEdit
     * @param cart
     */
    protected void adjustEditable(final OrderEntryData entry, final CartEntryModel entryToEdit, final CartModel cart) {
        final boolean result;
        if (entryToEdit.getBundleNo() != null && entryToEdit.getBundleNo().intValue() > 0) {
            final CartModel masterCart = (CartModel) getMasterAbstractOrderFromOrder(cart);
            result = getBundleCommerceCartService().checkIsComponentEditable(masterCart,
                    entryToEdit.getBundleTemplate(), entryToEdit.getBundleNo().intValue());

        } else
        // standalone product
        {
            result = false;
        }
        entry.setEditable(result);
    }

    protected AbstractOrderModel getMasterAbstractOrderFromOrder(final AbstractOrderModel abstractOrder) {
        return abstractOrder.getParent() == null ? abstractOrder : abstractOrder.getParent();
    }

    /**
     * Calls backend service to determine whether the order entry is valid or not
     * 
     * @param entry
     * @param cartEntry
     * @param cart
     */
    protected void adjustIsValid(final OrderEntryData entry, final CartEntryModel cartEntry, final CartModel cart) {
        final CartModel masterCart = (CartModel) getMasterAbstractOrderFromOrder(cart);
        entry.setValid(getBundleCommerceCartService().checkIsComponentSelectionCriteriaMet(masterCart,
                cartEntry.getBundleTemplate(), cartEntry.getBundleNo().intValue()));
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

}

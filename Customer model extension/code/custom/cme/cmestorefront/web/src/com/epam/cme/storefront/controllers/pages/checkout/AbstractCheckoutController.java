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
package com.epam.cme.storefront.controllers.pages.checkout;

import de.hybris.platform.acceleratorfacades.order.AcceleratorCheckoutFacade;
import de.hybris.platform.commercefacades.order.data.CartData;
import de.hybris.platform.commercefacades.user.data.AddressData;
import com.epam.cme.storefront.controllers.pages.AbstractPageController;

import java.util.Collections;
import java.util.List;

import javax.annotation.Resource;

/**
 * Base controller for all page controllers. Provides common functionality for all page controllers.
 */
public abstract class AbstractCheckoutController extends AbstractPageController {
    @Resource(name = "acceleratorCheckoutFacade")
    private AcceleratorCheckoutFacade checkoutFacade;

    /**
     * Checks if there are any items in the cart.
     * 
     * @return returns true if items found in cart.
     */
    protected boolean hasItemsInCart() {
        final CartData cartData = getCheckoutFacade().getCheckoutCart();

        return (cartData.getEntries() != null && !cartData.getEntries().isEmpty());
    }

    protected List<? extends AddressData> getDeliveryAddresses(final AddressData selectedAddressData) {
        List<AddressData> deliveryAddresses = null;
        if (selectedAddressData != null) {
            deliveryAddresses = (List<AddressData>) getCheckoutFacade().getSupportedDeliveryAddresses(true);

            if (deliveryAddresses == null || deliveryAddresses.isEmpty()) {
                deliveryAddresses = Collections.singletonList(selectedAddressData);
            } else if (!isAddressOnList(deliveryAddresses, selectedAddressData)) {
                deliveryAddresses.add(selectedAddressData);
            }
        }

        return deliveryAddresses == null ? Collections.<AddressData>emptyList() : deliveryAddresses;
    }

    protected boolean isAddressOnList(final List<? extends AddressData> deliveryAddresses,
            final AddressData selectedAddressData) {
        if (deliveryAddresses == null || selectedAddressData == null) {
            return false;
        }

        for (final AddressData address : deliveryAddresses) {
            if (address.getId().equals(selectedAddressData.getId())) {
                return true;
            }
        }

        return false;
    }

    protected AcceleratorCheckoutFacade getCheckoutFacade() {
        return checkoutFacade;
    }
}

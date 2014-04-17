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
package com.epam.cme.facades.order;

import de.hybris.platform.commercefacades.order.data.CartModificationData;
import de.hybris.platform.commerceservices.order.CommerceCartModificationException;
import de.hybris.platform.subscriptionfacades.order.SubscriptionCartFacade;

import java.util.List;

/**
 * Bundle Cart facade interface. Service is responsible for getting and updating all necessary
 * information for a bundle cart.
 */
public interface BundleCartFacade extends SubscriptionCartFacade {
    /**
     * Adds the product with the productId and with the given <code>quantity</code> to the cart. If
     * an entry with the given product exists in the cart already, then the given
     * <code>quantity</code> is added to the quantity of this cart entry. Dependent on the parameter
     * <code>bundleNo</code> the product is added to an existing or new bundle or treated as a
     * standalone product. In case a new bundle is created the method also adds auto-pick products
     * to the cart if there are any setup for the bundle template. After this the multi-cart is
     * calculated.
     * 
     * 
     * @param productCode
     *            the product id that will be added to the cart
     * @param quantity
     *            the quantity of the product
     * @param bundleNo
     *            indicates to which bundle the product shall be added (-1=create new bundle;
     *            0=standalone product/no bundle; >0=number of existing bundle)
     * @param bundleTemplateId
     *            The bundletemplate id to add the product to
     * @param removeCurrentProducts
     *            whether to remove existing products in that component
     * @return List of CartModificationData {@link CartModificationData}
     * @throws CommerceCartModificationException
     */
    List<CartModificationData> addToCart(final String productCode, final long quantity, final int bundleNo,
            final String bundleTemplateId, final boolean removeCurrentProducts)
            throws CommerceCartModificationException;

    /**
     * Method to add the <code>productCode1<code> and <code>productcode2</code> to multi-cart as new
     * cart entries. As this method works for bundles only, both products must have a bundle
     * template and must be part of an existing bundle or a new bundle that is created during the
     * addToCart. In case a new bundle is created the method also adds auto-pick products to the
     * cart if there are any setup for the bundle template. After this the multi-cart is calculated.
     * 
     * @param productCode1
     *            the first product id that will be added to the cart
     * @param bundleNo
     *            indicates to which bundle the product model shall be added (-1=create new bundle;
     *            >0=number of existing bundle; 0=standalone product/no bundle is not allowed here)
     * @param bundleTemplateId1
     *            current version of the bundleTemplate id based on which the first product is added
     *            to the cart. It will be stored on the cart entry.
     * @param productCode2
     *            the second product id that will be added to the cart
     * @param bundleTemplateId2
     *            current version of the bundleTemplate id based on which the second product is
     *            added to the cart. It will be stored on the cart entry.
     * @return List the list of CartModificationData {@link CartModificationData}
     * @throws CommerceCartModificationException
     */
    List<CartModificationData> addToCart(String productCode1, int bundleNo, String bundleTemplateId1,
            String productCode2, String bundleTemplateId2) throws CommerceCartModificationException;

    /**
     * Method to delete all cart entries of a particular bundle
     * 
     * @param bundleNo
     *            bundleNo in cart
     * 
     * @throws CommerceCartModificationException
     */
    void deleteCartBundle(final int bundleNo) throws CommerceCartModificationException;

    /**
     * Checks if the session cart is valid (= does not contain any invalid components)
     * 
     * @return <code>true</code> if the session cart is valid, otherwise <code>false</code>
     */
    boolean isCartValid();
}

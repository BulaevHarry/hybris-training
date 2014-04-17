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
package com.epam.cme.storefront.controllers.misc;

import de.hybris.platform.commercefacades.order.data.CartData;
import de.hybris.platform.commercefacades.order.data.CartModificationData;
import de.hybris.platform.commerceservices.order.CommerceCartModificationException;
import com.epam.cme.storefront.controllers.AbstractController;
import com.epam.cme.storefront.controllers.ControllerConstants;
import com.epam.cme.storefront.controllers.util.GlobalMessages;
import com.epam.cme.facades.order.BundleCartFacade;

import java.util.List;

import javax.annotation.Resource;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * Controller for Add to Cart functionality which is not specific to a certain page.
 */
@Controller
public class TelcoAddToCartController extends AbstractController {
    protected static final Logger LOG = Logger.getLogger(TelcoAddToCartController.class);

    @Resource(name = "cartFacade")
    private BundleCartFacade cartFacade;

    @RequestMapping(value = "/cart/add", method = RequestMethod.POST, produces = "application/json")
    public String addToCart(@RequestParam("productCodePost") final String code, final Model model,
            @RequestParam(value = "qty", required = false, defaultValue = "1") final long qty,
            @RequestParam(value = "bundleNo", required = false) final Integer bundleNo,
            @RequestParam(value = "bundleTemplateId", required = false) final String bundleTemplateId) {
        if (qty <= 0) {
            model.addAttribute("errorMsg", "basket.error.quantity.invalid");
            return ControllerConstants.Views.Fragments.Cart.AddToCartPopup;
        }

        addProduct(code, model, qty, bundleNo, bundleTemplateId);

        return ControllerConstants.Views.Fragments.Cart.AddToCartPopup;
    }

    protected void addProduct(final String code, final Model model, final long qty, final Integer bundleNo,
            final String bundleTemplateId) {
        try {
            final List<CartModificationData> cartModifications = cartFacade.addToCart(code, qty, bundleNo.intValue(),
                    bundleTemplateId, false);
            model.addAttribute("modifiedCartData", cartModifications);

            for (final CartModificationData cartModification : cartModifications) {

                if (cartModification.getQuantityAdded() == 0L) {
                    GlobalMessages.addErrorMessage(model, "basket.information.quantity.noItemsAdded."
                            + cartModification.getStatusCode());
                    model.addAttribute("errorMsg",
                            "basket.information.quantity.noItemsAdded." + cartModification.getStatusCode());
                } else if (cartModification.getQuantityAdded() < qty) {
                    GlobalMessages.addErrorMessage(model, "basket.information.quantity.reducedNumberOfItemsAdded."
                            + cartModification.getStatusCode());
                    model.addAttribute("errorMsg", "basket.information.quantity.reducedNumberOfItemsAdded."
                            + cartModification.getStatusCode());
                }
            }
        } catch (final CommerceCartModificationException ex) {
            model.addAttribute("errorMsg", "basket.error.occurred");
            LOG.warn("Couldn't add product of code " + code + " to cart.", ex);
        }

        final CartData cartData = cartFacade.getSessionCart();
        model.addAttribute("cartData", cartData);
    }

    @RequestMapping(value = "/cart/addBundle", method = RequestMethod.POST)
    public String addToCartBundle(@RequestParam("productCode1") final String productCode1,
            @RequestParam("productCode2") final String productCode2, final Model model,
            @RequestParam(value = "bundleTemplateId1", required = false) final String bundleTemplateId1,
            @RequestParam(value = "bundleTemplateId2", required = false) final String bundleTemplateId2) {
        String bundleId = null;
        try {
            final List<CartModificationData> cartModifications = cartFacade.addToCart(productCode1, -1,
                    bundleTemplateId1, productCode2, bundleTemplateId2);
            model.addAttribute("modifiedCartData", cartModifications);

            for (final CartModificationData cartModification : cartModifications) {
                if (cartModification.getEntry() == null) {
                    GlobalMessages.addErrorMessage(model, "basket.information.quantity.noItemsAdded");
                    model.addAttribute("errorMsg", "basket.information.quantity.noItemsAdded");
                    throw new CommerceCartModificationException("Cart entry was not created. Reason: "
                            + cartModification.getStatusCode());
                } else {
                    bundleId = String.valueOf(cartModification.getEntry().getBundleNo());
                }
            }
        } catch (final CommerceCartModificationException ex) {
            model.addAttribute("errorMsg", "basket.error.occurred");
            LOG.error("Couldn't add products of code " + productCode1 + " and " + productCode2 + " to cart.", ex);
            // we have no bundleId here, so we cannot redirect to extras-page; go to cart page
            // instead.
            return REDIRECT_PREFIX + "/cart";
        }

        return REDIRECT_PREFIX + "/bundle/edit-component/" + bundleId + "/nextcomponent/" + bundleTemplateId2;
    }
}

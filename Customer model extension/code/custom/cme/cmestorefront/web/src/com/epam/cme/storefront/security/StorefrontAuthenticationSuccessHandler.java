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
package com.epam.cme.storefront.security;

import de.hybris.platform.acceleratorservices.enums.UiExperienceLevel;
import de.hybris.platform.acceleratorservices.uiexperience.UiExperienceService;
import de.hybris.platform.commercefacades.customer.CustomerFacade;
import de.hybris.platform.commercefacades.order.CartFacade;
import de.hybris.platform.commerceservices.order.CommerceCartRestorationException;
import de.hybris.platform.commerceservices.order.CommerceCartService;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.order.CartFactory;
import de.hybris.platform.order.CartService;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.session.SessionService;

import java.io.IOException;
import java.util.Collections;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;

import com.epam.cme.storefront.constants.WebConstants;

/**
 * Success handler initializing user settings and ensuring the cart is handled correctly
 */
public class StorefrontAuthenticationSuccessHandler extends SavedRequestAwareAuthenticationSuccessHandler {
    private CustomerFacade customerFacade;
    private UiExperienceService uiExperienceService;
    private CartFacade cartFacade;
    private SessionService sessionService;
    private BruteForceAttackCounter bruteForceAttackCounter;
    @Autowired
    private CartService cartService;
    @Autowired
    private CommerceCartService defaultCommerceCartService;
    @Autowired
    private CartFactory defaultCartFactory;
    @Autowired
    private ModelService modelService;

    private Map<UiExperienceLevel, Boolean> forceDefaultTargetForUiExperienceLevel;

    public UiExperienceService getUiExperienceService() {
        return uiExperienceService;
    }

    @Required
    public void setUiExperienceService(final UiExperienceService uiExperienceService) {
        this.uiExperienceService = uiExperienceService;
    }

    @Override
    public void onAuthenticationSuccess(final HttpServletRequest request, final HttpServletResponse response,
            final Authentication authentication) throws IOException, ServletException {

        final CartModel newCart = defaultCartFactory.createCart();
        cartService.appendToCart(cartService.getSessionCart(), newCart);
        cartService.removeSessionCart();

        if (!getCartFacade().hasSessionCart() || getCartFacade().getSessionCart().getEntries().isEmpty()) {
            try {
                getSessionService().setAttribute(WebConstants.CART_RESTORATION, getCartFacade().restoreSavedCart(null));
            } catch (final CommerceCartRestorationException e) {
                getSessionService().setAttribute(WebConstants.CART_RESTORATION, "basket.restoration.errorMsg");
            }
        }

        cartService.appendToCart(newCart, cartService.getSessionCart());
        newCart.setEntries(Collections.EMPTY_LIST);
        getCustomerFacade().loginSuccess();
        modelService.remove(newCart);

        getBruteForceAttackCounter().resetUserCounter(getCustomerFacade().getCurrentCustomer().getUid());
        super.onAuthenticationSuccess(request, response, authentication);
    }

    protected CartFacade getCartFacade() {
        return cartFacade;
    }

    @Required
    public void setCartFacade(final CartFacade cartFacade) {
        this.cartFacade = cartFacade;
    }

    protected SessionService getSessionService() {
        return sessionService;
    }

    @Required
    public void setSessionService(final SessionService sessionService) {
        this.sessionService = sessionService;
    }

    protected CustomerFacade getCustomerFacade() {
        return customerFacade;
    }

    @Required
    public void setCustomerFacade(final CustomerFacade customerFacade) {
        this.customerFacade = customerFacade;
    }

    /*
     * @see
     * org.springframework.security.web.authentication.AbstractAuthenticationTargetUrlRequestHandler
     * # isAlwaysUseDefaultTargetUrl()
     */
    @Override
    protected boolean isAlwaysUseDefaultTargetUrl() {
        final UiExperienceLevel uiExperienceLevel = getUiExperienceService().getUiExperienceLevel();
        if (getForceDefaultTargetForUiExperienceLevel().containsKey(uiExperienceLevel)) {
            return Boolean.TRUE.equals(getForceDefaultTargetForUiExperienceLevel().get(uiExperienceLevel));
        } else {
            return false;
        }
    }

    protected Map<UiExperienceLevel, Boolean> getForceDefaultTargetForUiExperienceLevel() {
        return forceDefaultTargetForUiExperienceLevel;
    }

    @Required
    public void setForceDefaultTargetForUiExperienceLevel(
            final Map<UiExperienceLevel, Boolean> forceDefaultTargetForUiExperienceLevel) {
        this.forceDefaultTargetForUiExperienceLevel = forceDefaultTargetForUiExperienceLevel;
    }

    protected BruteForceAttackCounter getBruteForceAttackCounter() {
        return bruteForceAttackCounter;
    }

    @Required
    public void setBruteForceAttackCounter(final BruteForceAttackCounter bruteForceAttackCounter) {
        this.bruteForceAttackCounter = bruteForceAttackCounter;
    }
}
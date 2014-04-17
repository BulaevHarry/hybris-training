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

import de.hybris.platform.cms2.exceptions.CMSItemNotFoundException;
import de.hybris.platform.cms2.model.pages.AbstractPageModel;
import com.epam.cme.storefront.controllers.ControllerConstants;
import com.epam.cme.storefront.controllers.pages.AbstractRegisterPageController;
import com.epam.cme.storefront.forms.RegisterForm;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * Checkout Register Controller. Handles login and register for the checkout flow.
 */
@Controller
@RequestMapping(value = "/register/checkout")
public class CheckoutRegisterController extends AbstractRegisterPageController {
    @Override
    protected AbstractPageModel getCmsPage() throws CMSItemNotFoundException {
        return getContentPageForLabelOrId("checkout-register");
    }

    @Override
    protected String getSuccessRedirect(final HttpServletRequest request, final HttpServletResponse response) {
        // Redirect to the main checkout controller to handle checkout.
        return "/checkout";
    }

    @Override
    protected String getView() {
        return ControllerConstants.Views.Pages.Checkout.CheckoutRegisterPage;
    }

    @RequestMapping(method = RequestMethod.GET)
    public String doCheckoutRegister(final Model model) throws CMSItemNotFoundException {
        return getDefaultRegistrationPage(model);
    }

    @RequestMapping(value = "/newcustomer", method = RequestMethod.POST)
    public String doCheckoutRegister(@Valid final RegisterForm form, final BindingResult bindingResult,
            final Model model, final HttpServletRequest request, final HttpServletResponse response)
            throws CMSItemNotFoundException {
        return processRegisterUserRequest(null, form, bindingResult, model, request, response);
    }
}

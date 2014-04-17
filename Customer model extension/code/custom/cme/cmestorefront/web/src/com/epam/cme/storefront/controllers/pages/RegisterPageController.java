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
package com.epam.cme.storefront.controllers.pages;

import de.hybris.platform.cms2.exceptions.CMSItemNotFoundException;
import de.hybris.platform.cms2.model.pages.AbstractPageModel;
import com.epam.cme.storefront.controllers.ControllerConstants;
import com.epam.cme.storefront.forms.RegisterForm;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.web.savedrequest.HttpSessionRequestCache;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * Register Controller for mobile. Handles login and register for the account flow.
 */
@Controller
@RequestMapping(value = "/register")
public class RegisterPageController extends AbstractRegisterPageController {
    @Override
    protected AbstractPageModel getCmsPage() throws CMSItemNotFoundException {
        return getContentPageForLabelOrId("register");
    }

    @Override
    protected String getSuccessRedirect(final HttpServletRequest request, final HttpServletResponse response) {
        if (httpSessionRequestCache.getRequest(request, response) != null) {
            return httpSessionRequestCache.getRequest(request, response).getRedirectUrl();
        }
        return "/my-account";
    }

    @Override
    protected String getView() {
        return ControllerConstants.Views.Pages.Account.AccountRegisterPage;
    }

    private HttpSessionRequestCache httpSessionRequestCache;

    @Autowired
    @Qualifier("httpSessionRequestCache")
    public void setHttpSessionRequestCache(final HttpSessionRequestCache accHttpSessionRequestCache) {
        this.httpSessionRequestCache = accHttpSessionRequestCache;
    }

    @RequestMapping(method = RequestMethod.GET)
    public String doRegister(final Model model, final HttpServletRequest request) throws CMSItemNotFoundException {
        return getDefaultRegistrationPage(model);
    }

    @RequestMapping(value = "/newcustomer", method = RequestMethod.POST)
    public String doRegister(@Valid final RegisterForm form, final BindingResult bindingResult, final Model model,
            final HttpServletRequest request, final HttpServletResponse response) throws CMSItemNotFoundException {
        return processRegisterUserRequest(null, form, bindingResult, model, request, response);
    }
}

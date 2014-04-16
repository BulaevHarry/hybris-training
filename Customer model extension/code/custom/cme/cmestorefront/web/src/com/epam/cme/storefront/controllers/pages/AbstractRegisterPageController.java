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
import de.hybris.platform.cms2.model.pages.ContentPageModel;
import de.hybris.platform.commercefacades.user.UserFacade;
import de.hybris.platform.commercefacades.user.data.TitleData;
import de.hybris.platform.commerceservices.customer.DuplicateUidException;
import de.hybris.platform.subscriptionfacades.SubscriptionFacade;
import de.hybris.platform.subscriptionfacades.exceptions.SubscriptionFacadeException;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.servlet.support.RequestContextUtils;

import com.epam.cme.facades.organization.OrganizationFacade;
import com.epam.cme.facades.organization.data.OrganizationData;
import com.epam.cme.facades.user.data.CmeRegisterData;
import com.epam.cme.storefront.breadcrumb.Breadcrumb;
import com.epam.cme.storefront.controllers.util.GlobalMessages;
import com.epam.cme.storefront.forms.LoginForm;
import com.epam.cme.storefront.forms.RegisterForm;
import com.epam.cme.storefront.security.AutoLoginStrategy;


public abstract class AbstractRegisterPageController extends AbstractPageController
{
	protected static final Logger LOG = Logger.getLogger(AbstractRegisterPageController.class);

	protected abstract AbstractPageModel getCmsPage() throws CMSItemNotFoundException;

	protected abstract String getSuccessRedirect(final HttpServletRequest request, final HttpServletResponse response);

	protected abstract String getView();


	@Resource(name = "autoLoginStrategy")
	private AutoLoginStrategy autoLoginStrategy;

	@Resource(name = "userFacade")
	private UserFacade userFacade;

	@Resource(name = "organizationFacade")
	private OrganizationFacade organizationFacade;

	@Resource(name = "subscriptionFacade")
	private SubscriptionFacade subscriptionFacade;


	/**
	 * @return the autoLoginStrategy
	 */
	protected AutoLoginStrategy getAutoLoginStrategy()
	{
		return autoLoginStrategy;
	}

	protected UserFacade getUserFacade()
	{
		return userFacade;
	}

	protected OrganizationFacade getOrganizationFacade()
	{
		return organizationFacade;
	}

	protected SubscriptionFacade getSubscriptionFacade()
	{
		return subscriptionFacade;
	}

	@ModelAttribute("titles")
	public Collection<TitleData> getTitles()
	{
		return getUserFacade().getTitles();
	}

	@ModelAttribute("organizations")
	public Collection<OrganizationData> getOrganizations()
	{
		return getOrganizationFacade().getOrganizations();
	}

	protected String getDefaultRegistrationPage(final Model model) throws CMSItemNotFoundException
	{
		storeCmsPageInModel(model, getCmsPage());
		setUpMetaDataForContentPage(model, (ContentPageModel) getCmsPage());
		final Breadcrumb loginBreadcrumbEntry = new Breadcrumb("#", getMessageSource().getMessage("header.link.login", null,
				getI18nService().getCurrentLocale()), null);
		model.addAttribute("breadcrumbs", Collections.singletonList(loginBreadcrumbEntry));
		model.addAttribute(new RegisterForm());
		return getView();
	}

	/**
	 * This method takes data from the registration form and create a new customer account and attempts to log in using
	 * the credentials of this new user.
	 * 
	 * @param referer
	 * @param form
	 * @param bindingResult
	 * @param model
	 * @param request
	 * @param response
	 * @return true if there are no binding errors or the account does not already exists.
	 * @throws CMSItemNotFoundException
	 */
	protected String processRegisterUserRequest(final String referer, final RegisterForm form, final BindingResult bindingResult,
			final Model model, final HttpServletRequest request, final HttpServletResponse response) throws CMSItemNotFoundException
	{
		if (bindingResult.hasErrors())
		{
			model.addAttribute(new LoginForm());
			GlobalMessages.addErrorMessage(model, "form.global.error");
			return handleRegistrationError(model);
		}
		final CmeRegisterData data = new CmeRegisterData();
		data.setFirstName(form.getFirstName());
		data.setLastName(form.getLastName());
		data.setOrganizationsIds(form.getOrganizations());
		data.setLogin(form.getEmail());
		data.setPassword(form.getPwd());
		data.setTitleCode(form.getTitleCode());
		try
		{
			getBlockableCustomerFacade().register(data);
			getAutoLoginStrategy().login(form.getEmail(), form.getPwd(), request, response);
			getSubscriptionFacade().updateProfile(new HashMap<String, String>());
			RequestContextUtils.getOutputFlashMap(request).put(GlobalMessages.CONF_MESSAGES_HOLDER,
					Collections.singletonList("registration.confirmation.message.title"));
		}
		catch (final DuplicateUidException e)
		{
			LOG.warn("registration failed: " + e);
			model.addAttribute(new LoginForm());
			bindingResult.rejectValue("email", "registration.error.account.exists.title");
			GlobalMessages.addErrorMessage(model, "form.global.error");
			return handleRegistrationError(model);
		}
		catch (final SubscriptionFacadeException e)
		{
			LOG.warn(String.format("Creating new subscription billing profile for user %s failed", form.getEmail()), e);
			model.addAttribute(new LoginForm());
			GlobalMessages.addErrorMessage(model, "registration.error.subscription.billing.profil");
			return handleRegistrationError(model);
		}
		return REDIRECT_PREFIX + getSuccessRedirect(request, response);
	}


	private String handleRegistrationError(final Model model) throws CMSItemNotFoundException
	{
		storeCmsPageInModel(model, getCmsPage());
		setUpMetaDataForContentPage(model, (ContentPageModel) getCmsPage());
		return getView();
	}

}

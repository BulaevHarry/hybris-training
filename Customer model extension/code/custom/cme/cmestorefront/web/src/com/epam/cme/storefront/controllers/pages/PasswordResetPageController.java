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
import de.hybris.platform.commercefacades.customer.CustomerFacade;
import de.hybris.platform.commerceservices.customer.TokenInvalidatedException;
import de.hybris.platform.servicelayer.exceptions.UnknownIdentifierException;
import com.epam.cme.storefront.breadcrumb.ResourceBreadcrumbBuilder;
import com.epam.cme.storefront.constants.WebConstants;
import com.epam.cme.storefront.controllers.ControllerConstants;
import com.epam.cme.storefront.controllers.util.GlobalMessages;
import com.epam.cme.storefront.forms.ForgottenPwdForm;
import com.epam.cme.storefront.forms.UpdatePwdForm;

import java.util.Collections;

import javax.annotation.Resource;
import javax.validation.Valid;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;


/**
 * Controller for the forgotten password pages. Supports requesting a password reset email as well as changing the
 * password once you have got the token that was sent via email.
 */
@Controller
@RequestMapping(value = "/login/pw")
public class PasswordResetPageController extends AbstractPageController
{
	private static final Logger LOG = Logger.getLogger(PasswordResetPageController.class);

	private static final String REDIRECT_LOGIN = "redirect:/login";
	private static final String REDIRECT_HOME = "redirect:/";

	private static final String UPDATE_PWD_CMS_PAGE = "updatePassword";
	private static final String FORGOT_PASSWORD_CMS_PAGE = "forgottenPassword";

	@Resource(name = "customerFacade")
	private CustomerFacade customerFacade;

	@Resource(name = "simpleBreadcrumbBuilder")
	private ResourceBreadcrumbBuilder resourceBreadcrumbBuilder;

	@RequestMapping(value = "/request", method = RequestMethod.GET)
	public String getPasswordRequest(final Model model) throws CMSItemNotFoundException
	{
		model.addAttribute(new ForgottenPwdForm());
		storeCmsPageInModel(model, getContentPageForLabelOrId(FORGOT_PASSWORD_CMS_PAGE));
		setUpMetaDataForContentPage(model, getContentPageForLabelOrId(FORGOT_PASSWORD_CMS_PAGE));
		model.addAttribute(WebConstants.BREADCRUMBS_KEY, resourceBreadcrumbBuilder.getBreadcrumbs("forgottenPwd.title"));
		return ControllerConstants.Views.Pages.Password.PasswordResetRequestPage;
	}

	@RequestMapping(value = "/request", method = RequestMethod.POST)
	public String passwordRequest(@Valid final ForgottenPwdForm form, final BindingResult bindingResult, final Model model,
			final RedirectAttributes redirectModel) throws CMSItemNotFoundException
	{
		if (bindingResult.hasErrors())
		{
			prepareErrorMessage(model, FORGOT_PASSWORD_CMS_PAGE);
		}
		else
		{
			try
			{
				customerFacade.forgottenPassword(form.getEmail());
				redirectModel.addFlashAttribute(GlobalMessages.CONF_MESSAGES_HOLDER,
						Collections.singletonList("account.confirmation.forgotten.password.link.sent"));
			}
			catch (final UnknownIdentifierException unknownIdentifierException)
			{
				redirectModel.addFlashAttribute(GlobalMessages.CONF_MESSAGES_HOLDER,
						Collections.singletonList("account.confirmation.forgotten.password.link.sent.onfail"));
			}
			return REDIRECT_LOGIN;
		}
		model.addAttribute(WebConstants.BREADCRUMBS_KEY, resourceBreadcrumbBuilder.getBreadcrumbs("forgottenPwd.title"));
		return ControllerConstants.Views.Pages.Password.PasswordResetRequestPage;
	}

	@RequestMapping(value = "/change", method = RequestMethod.GET)
	public String getChangePassword(@RequestParam(required = false) final String token, final Model model)
			throws CMSItemNotFoundException
	{
		if (StringUtils.isBlank(token))
		{
			return REDIRECT_HOME;
		}
		final UpdatePwdForm form = new UpdatePwdForm();
		form.setToken(token);
		model.addAttribute(form);
		storeCmsPageInModel(model, getContentPageForLabelOrId(UPDATE_PWD_CMS_PAGE));
		setUpMetaDataForContentPage(model, getContentPageForLabelOrId(UPDATE_PWD_CMS_PAGE));
		model.addAttribute(WebConstants.BREADCRUMBS_KEY, resourceBreadcrumbBuilder.getBreadcrumbs("updatePwd.title"));
		return ControllerConstants.Views.Pages.Password.PasswordResetChangePage;
	}

	@RequestMapping(value = "/change", method = RequestMethod.POST)
	public String changePassword(@Valid final UpdatePwdForm form, final BindingResult bindingResult, final Model model,
			final RedirectAttributes redirectModel) throws CMSItemNotFoundException
	{
		if (bindingResult.hasErrors())
		{
			prepareErrorMessage(model, UPDATE_PWD_CMS_PAGE);
			return ControllerConstants.Views.Pages.Password.PasswordResetChangePage;
		}
		if (!StringUtils.isBlank(form.getToken()))
		{
			try
			{
				customerFacade.updatePassword(form.getToken(), form.getPwd());
				redirectModel.addFlashAttribute(GlobalMessages.CONF_MESSAGES_HOLDER,
						Collections.singletonList("account.confirmation.password.updated"));
			}
			catch (final TokenInvalidatedException e)
			{
				redirectModel.addFlashAttribute(GlobalMessages.ERROR_MESSAGES_HOLDER,
						Collections.singletonList("updatePwd.token.invalidated"));
			}
			catch (final RuntimeException e)
			{
				redirectModel.addFlashAttribute(GlobalMessages.ERROR_MESSAGES_HOLDER,
						Collections.singletonList("updatePwd.token.invalid"));
			}
		}
		return REDIRECT_LOGIN;
	}

	/**
	 * Prepares the view to display an error message
	 * 
	 * @param model
	 * @param page
	 * @throws CMSItemNotFoundException
	 */
	protected void prepareErrorMessage(final Model model, final String page) throws CMSItemNotFoundException
	{
		GlobalMessages.addErrorMessage(model, "form.global.error");
		storeCmsPageInModel(model, getContentPageForLabelOrId(page));
		setUpMetaDataForContentPage(model, getContentPageForLabelOrId(page));
	}
}

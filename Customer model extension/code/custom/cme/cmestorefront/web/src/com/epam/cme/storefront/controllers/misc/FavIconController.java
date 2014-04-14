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

import de.hybris.platform.servicelayer.i18n.I18NService;
import com.epam.cme.storefront.controllers.AbstractController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.ui.context.ThemeSource;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ThemeResolver;


/**
 * Controller for evil clients that go for the favicon.ico directly in the root, redirect them to the real location
 */
@Controller
public class FavIconController extends AbstractController
{
	private static final String FAVICON_THEME_CODE = "img.favIcon";

	@Resource(name = "themeResolver")
	private ThemeResolver themeResolver;

	@Resource(name = "themeSource")
	private ThemeSource themeSource;

	@Resource(name = "i18nService")
	private I18NService i18nService;


	@RequestMapping(value = "/favicon.ico", method = RequestMethod.GET)
	public String getFavIcon(final HttpServletRequest request)
	{
		final String themeName = themeResolver.resolveThemeName(request);
		final String iconPath = themeSource.getTheme(themeName).getMessageSource()
				.getMessage(FAVICON_THEME_CODE, new Object[] {}, i18nService.getCurrentLocale());

		return REDIRECT_PREFIX + iconPath;
	}
}

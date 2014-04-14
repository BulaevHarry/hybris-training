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
package com.epam.cme.storefront.breadcrumb.impl;

import de.hybris.platform.servicelayer.i18n.I18NService;
import com.epam.cme.storefront.breadcrumb.Breadcrumb;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Required;
import org.springframework.context.MessageSource;


/**
 * StorefinderBreadcrumbBuilder implementation for store finder related pages
 */
public class StorefinderBreadcrumbBuilder
{
	private static final String LAST_LINK_CLASS = "active";

	private MessageSource messageSource;
	private I18NService i18nService;

	protected I18NService getI18nService()
	{
		return i18nService;
	}

	@Required
	public void setI18nService(final I18NService i18nService)
	{
		this.i18nService = i18nService;
	}

	protected MessageSource getMessageSource()
	{
		return messageSource;
	}

	@Required
	public void setMessageSource(final MessageSource messageSource)
	{
		this.messageSource = messageSource;
	}

	public List<Breadcrumb> getBreadcrumbs() throws IllegalArgumentException
	{
		final List<Breadcrumb> breadcrumbs = new ArrayList<Breadcrumb>();

		final String storeFinderLinkName = getMessageSource().getMessage("storeFinder.link", null, getI18nService().getCurrentLocale());
		breadcrumbs.add(new Breadcrumb("/store-finder", storeFinderLinkName, null));

		if (!breadcrumbs.isEmpty())
		{
			breadcrumbs.get(breadcrumbs.size() - 1).setLinkClass(LAST_LINK_CLASS);
		}

		return breadcrumbs;
	}

	public List<Breadcrumb> getBreadcrumbsForLocationSearch(final String locationSearch) throws IllegalArgumentException
	{
		final List<Breadcrumb> breadcrumbs = new ArrayList<Breadcrumb>();

		final String storeFinderLinkName = getMessageSource().getMessage("storeFinder.link", null, getI18nService().getCurrentLocale());
		breadcrumbs.add(new Breadcrumb("/store-finder", storeFinderLinkName, null));

		if (locationSearch != null && !locationSearch.isEmpty())
		{
			breadcrumbs.add(new Breadcrumb("#", locationSearch, null));
		}

		if (!breadcrumbs.isEmpty())
		{
			breadcrumbs.get(breadcrumbs.size() - 1).setLinkClass(LAST_LINK_CLASS);
		}

		return breadcrumbs;
	}

	public List<Breadcrumb> getBreadcrumbsForCurrentPositionSearch() throws IllegalArgumentException
	{
		final List<Breadcrumb> breadcrumbs = new ArrayList<Breadcrumb>();

		final String storeFinderLinkName = getMessageSource().getMessage("storeFinder.link", null, getI18nService().getCurrentLocale());
		breadcrumbs.add(new Breadcrumb("/store-finder", storeFinderLinkName, null));

		final String currentPositionLinkName = getMessageSource().getMessage("storeFinder.currentPosition", null, getI18nService().getCurrentLocale());
		breadcrumbs.add(new Breadcrumb("#", currentPositionLinkName, null));

		if (!breadcrumbs.isEmpty())
		{
			breadcrumbs.get(breadcrumbs.size() - 1).setLinkClass(LAST_LINK_CLASS);
		}

		return breadcrumbs;
	}
}

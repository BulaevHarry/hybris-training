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

import de.hybris.platform.cms2.model.pages.ContentPageModel;
import com.epam.cme.storefront.breadcrumb.Breadcrumb;

import java.util.Collections;
import java.util.List;


/**
 * Breadcrumb builder that uses page title in breadcrumb or page name as fallback when title is missing.
 */
public class ContentPageBreadcrumbBuilder
{
	private static final String LAST_LINK_CLASS = "active";

	/**
	 * @param page
	 * @return breadcrumb for given page
	 */
	public List<Breadcrumb> getBreadcrumbs(final ContentPageModel page)
	{
		String title = page.getTitle();
		if (title == null)
		{
			title = page.getName();
		}
		return Collections.singletonList(new Breadcrumb("#", title, LAST_LINK_CLASS));
	}

}

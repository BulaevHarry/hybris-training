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
package com.epam.cme.storefront.breadcrumb;

import java.util.List;

/**
 * ResourceBreadcrumbBuilder builds a list of breadcrumbs based on a resource key
 */
public interface ResourceBreadcrumbBuilder {
    public List<Breadcrumb> getBreadcrumbs(String resourceKey) throws IllegalArgumentException;
}

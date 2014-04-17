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
package com.epam.cme.storefront.controllers.util;

import javax.servlet.http.HttpServletRequest;

/**
 * Helper that contains product data related utility methods
 */
public class CategoryDataHelper {
    public static final String CURRENT_CATEGORY = "currentCategoryCode";

    public static String getCurrentCategory(final HttpServletRequest request) {
        return (String) request.getAttribute(CURRENT_CATEGORY);
    }

    public static void setCurrentCategory(final HttpServletRequest request, final String currentCategoryCode) {
        request.setAttribute(CURRENT_CATEGORY, currentCategoryCode);
    }
}

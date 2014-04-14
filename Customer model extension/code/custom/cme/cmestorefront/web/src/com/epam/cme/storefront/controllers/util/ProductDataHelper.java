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
public class ProductDataHelper
{
	public static final String CURRENT_PRODUCT = "currentProductCode";

	public static String getCurrentProduct(final HttpServletRequest request)
	{
		return (String) request.getAttribute(CURRENT_PRODUCT);
	}

	public static void setCurrentProduct(final HttpServletRequest request, final String currentProductCode)
	{
		request.setAttribute(CURRENT_PRODUCT, currentProductCode);
	}
}

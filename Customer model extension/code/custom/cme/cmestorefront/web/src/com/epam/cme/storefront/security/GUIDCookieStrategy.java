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
package com.epam.cme.storefront.security;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Strategy for setting and removing a GUID cookie
 */
public interface GUIDCookieStrategy {
    /**
     * Generates a UID and stores it as Cookie and session attribute
     * 
     * @param request
     * @param response
     */
    void setCookie(HttpServletRequest request, HttpServletResponse response);

    /**
     * Removes the GUID cookie
     * 
     * @param request
     * @param response
     */
    void deleteCookie(HttpServletRequest request, HttpServletResponse response);
}

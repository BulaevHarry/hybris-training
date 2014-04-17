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
package com.epam.cme.storefront.util;

/**
 * Filters given string to prevent cross-site scripting
 */
public class XSSFilterUtil {
    /**
     * 
     * @param value
     *            to be sanitized
     * @return sanitized content
     */
    public static String filter(final String value) {
        if (value == null) {
            return null;
        }
        String sanitized = value;
        sanitized = sanitized.replaceAll("<", "&lt;").replaceAll(">", "&gt;");
        sanitized = sanitized.replaceAll("\\(", "&#40;").replaceAll("\\)", "&#41;");
        sanitized = sanitized.replaceAll("'", "&#39;");
        sanitized = sanitized.replaceAll("eval\\((.*)\\)", "");
        sanitized = sanitized.replaceAll("[\\\"\\\'][\\s]*javascript:(.*)[\\\"\\\']", "\"\"");
        return sanitized;
    }
}

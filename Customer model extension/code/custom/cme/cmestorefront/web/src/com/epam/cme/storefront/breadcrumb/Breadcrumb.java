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

/**
 * Breadcrumb piece data object.
 */
public class Breadcrumb {
    private String url;
    private String name;
    private String linkClass;

    private String categoryCode;

    public Breadcrumb(final String url, final String name, final String linkClass) {
        this.url = url;
        this.name = name;
        this.linkClass = linkClass;
    }

    public Breadcrumb(final String url, final String name, final String linkClass, final String categoryCode) {
        this(url, name, linkClass);

        this.categoryCode = categoryCode;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(final String url) {
        this.url = url;
    }

    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public String getLinkClass() {
        return linkClass;
    }

    public void setLinkClass(final String linkClass) {
        this.linkClass = linkClass;
    }

    public String getCategoryCode() {
        return categoryCode;
    }

    public void setCategoryCode(final String categoryCode) {
        this.categoryCode = categoryCode;
    }
}

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
package com.epam.cme.storefront.security.cookie;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;

import org.springframework.web.util.CookieGenerator;

/**
 * Enhanced {@link CookieGenerator} sets additionally header attribute {@value #HEADER_COOKIE}
 * 
 */
public class EnhancedCookieGenerator extends CookieGenerator {
    public static final String HEADER_COOKIE = "Set-Cookie";
    public static final boolean DEFAULT_HTTP_ONLY = false;
    public static final boolean DEFAULT_COOKIE_PATH = true;

    private boolean useDefaultPath = DEFAULT_COOKIE_PATH;
    private boolean httpOnly = DEFAULT_HTTP_ONLY;

    /**
     * Marker to choose between only cookie based session and http header as addition
     * 
     */
    public void setHttpOnly(final boolean httpOnly) {
        this.httpOnly = httpOnly;
    }

    /**
     * 
     * Adjusts either dynamic {@link Cookie#setPath(String)} or static assignment. If true a cookie
     * path is calculated by {@link #setEnhancedCookiePath(HttpServletRequest, Cookie)} method.
     */
    public void setUseDefaultPath(final boolean useDefaultPath) {
        this.useDefaultPath = useDefaultPath;
    }

    public void addCookie(final HttpServletRequest request, final HttpServletResponse response, final String cookieValue) {
        final CookieDecorator cookieDecorator = getCookieDecorator(request, response);
        super.addCookie(new HttpServletResponseWrapper(response) {
            @Override
            public void addCookie(final Cookie cookie) {
                super.addCookie(cookieDecorator.decorate(cookie));
            }
        }, cookieValue);

    }

    private CookieDecorator getCookieDecorator(final HttpServletRequest request, final HttpServletResponse response) {
        return isHttpOnly() ? new HttpHeaderCookieDecorator(request, response) : new RequestPathCookieDecorator(
                request, response);
    }

    protected boolean canUseDefaultPath() {
        return useDefaultPath;
    }

    protected boolean isHttpOnly() {
        return httpOnly;
    }

    /**
     * 
     * Sets dynamically the {@link Cookie#setPath(String)} value using available
     * {@link HttpServletRequest#getContextPath()}.
     */
    protected void setEnhancedCookiePath(final HttpServletRequest request, final Cookie cookie) {
        if (!canUseDefaultPath()) {
            cookie.setPath(request.getContextPath());
        }
    }

    interface CookieDecorator {
        Cookie decorate(Cookie cookie);
    }

    class RequestPathCookieDecorator implements CookieDecorator {

        protected final HttpServletRequest request;
        protected final HttpServletResponse response;

        RequestPathCookieDecorator(final HttpServletRequest request, final HttpServletResponse response) {
            this.request = request;
            this.response = response;
        }

        @Override
        public Cookie decorate(final Cookie cookieIn) {
            setEnhancedCookiePath(request, cookieIn);
            return cookieIn;
        }

    }

    class HttpHeaderCookieDecorator extends RequestPathCookieDecorator {

        HttpHeaderCookieDecorator(final HttpServletRequest request, final HttpServletResponse response) {
            super(request, response);
        }

        @Override
        public Cookie decorate(final Cookie cookieIn) {
            final StringBuffer headerBuffer = new StringBuffer(100);
            ServerCookie.appendCookieValue(headerBuffer, cookieIn.getVersion(), cookieIn.getName(),
                    cookieIn.getValue(), cookieIn.getPath(), cookieIn.getDomain(), cookieIn.getComment(),
                    cookieIn.getMaxAge(), cookieIn.getSecure(), isHttpOnly());
            response.addHeader(HEADER_COOKIE, headerBuffer.toString());
            return super.decorate(cookieIn);
        }

    }

}

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
package com.epam.cme.storefront.interceptors.beforecontroller;

import java.util.Set;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.security.web.RedirectStrategy;
import org.springframework.web.util.CookieGenerator;
import org.springframework.web.util.UrlPathHelper;

/**
 * Interceptor to additionally secure HTTPS calls. If no secure cookie is found the interceptor will
 * redirect to the configured loginUrl. The secure cookie is first generated after successful login
 * on an HTTPS channel and sent with every following secure request.
 */
public class SecureRequestCookieCheckBeforeControllerHandler implements BeforeControllerHandler {
    private static final Logger LOG = Logger.getLogger(SecureRequestCookieCheckBeforeControllerHandler.class);

    public static final String SECURE_GUID_SESSION_KEY = "acceleratorSecureGUID";

    private Set<String> excludeUrls;
    private String loginUrl;
    private RedirectStrategy redirectStrategy;
    private CookieGenerator cookieGenerator;
    private UrlPathHelper urlPathHelper;

    @Override
    public boolean beforeController(final HttpServletRequest request, final HttpServletResponse response)
            throws Exception // NOPMD
    {
        final String path = getUrlPathHelper().getServletPath(request);
        if (request.isSecure() && !getExcludeUrls().contains(path)) {
            boolean redirect = true;
            final String guid = (String) request.getSession().getAttribute(SECURE_GUID_SESSION_KEY);
            if (guid != null && request.getCookies() != null) {
                final String guidCookieName = getCookieGenerator().getCookieName();
                if (guidCookieName != null) {
                    for (final Cookie cookie : request.getCookies()) {
                        if (guidCookieName.equals(cookie.getName())) {
                            if (guid.equals(cookie.getValue())) {
                                redirect = false;
                                break;
                            } else {
                                LOG.info("Found secure cookie with invalid value. expected [" + guid + "] actual ["
                                        + cookie.getValue() + "]. removing.");
                                getCookieGenerator().removeCookie(response);
                            }
                        }
                    }
                }
            }
            if (redirect) {
                LOG.warn((guid == null ? "missing secure token in session" : "no matching guid cookie")
                        + ", redirecting");
                getRedirectStrategy().sendRedirect(request, response, getLoginUrl());
                return false;
            }
        }

        return true;
    }

    protected Set<String> getExcludeUrls() {
        return excludeUrls;
    }

    @Required
    public void setExcludeUrls(final Set<String> excludeUrls) {
        this.excludeUrls = excludeUrls;
    }

    protected String getLoginUrl() {
        return loginUrl;
    }

    @Required
    public void setLoginUrl(final String loginUrl) {
        this.loginUrl = loginUrl;
    }

    protected RedirectStrategy getRedirectStrategy() {
        return redirectStrategy;
    }

    @Required
    public void setRedirectStrategy(final RedirectStrategy redirectStrategy) {
        this.redirectStrategy = redirectStrategy;
    }

    protected CookieGenerator getCookieGenerator() {
        return cookieGenerator;
    }

    @Required
    public void setCookieGenerator(final CookieGenerator cookieGenerator) {
        this.cookieGenerator = cookieGenerator;
    }

    protected UrlPathHelper getUrlPathHelper() {
        return urlPathHelper;
    }

    @Required
    public void setUrlPathHelper(final UrlPathHelper urlPathHelper) {
        this.urlPathHelper = urlPathHelper;
    }
}

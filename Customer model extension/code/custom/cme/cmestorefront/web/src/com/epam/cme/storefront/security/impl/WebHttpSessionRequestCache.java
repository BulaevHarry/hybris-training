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
package com.epam.cme.storefront.security.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.PortResolver;
import org.springframework.security.web.PortResolverImpl;
import org.springframework.security.web.savedrequest.DefaultSavedRequest;
import org.springframework.security.web.savedrequest.HttpSessionRequestCache;
import org.springframework.security.web.savedrequest.SavedRequest;
import org.springframework.security.web.util.AnyRequestMatcher;
import org.springframework.security.web.util.RequestMatcher;
import org.springframework.security.web.util.UrlUtils;

/**
 * Extension of HttpSessionRequestCache that allows pass through of cookies from the current
 * request. This is required to allow the GUIDInterceptor to see the secure cookie written during
 * authentication.
 * 
 * The <tt>RequestCache</tt> stores the <tt>SavedRequest</tt> in the HttpSession, this is then
 * restored perfectly. Unfortunately the saved request also hides new cookies that have been written
 * since the saved request was created. This implementation allows the current request's cookie
 * values to override the cookies within the saved request.
 */
public class WebHttpSessionRequestCache extends HttpSessionRequestCache {
    private static final String REFERER = "referer";

    static final String SAVED_REQUEST = "SPRING_SECURITY_SAVED_REQUEST";

    private PortResolver portResolver = new PortResolverImpl();
    private RequestMatcher requestMatcher = new AnyRequestMatcher();
    private boolean createSessionAllowed = true;

    @Override
    public void setRequestMatcher(final RequestMatcher requestMatcher) {
        this.requestMatcher = requestMatcher;
        super.setRequestMatcher(requestMatcher);
    }

    @Override
    public void setPortResolver(final PortResolver portResolver) {
        this.portResolver = portResolver;
        super.setPortResolver(portResolver);
    }

    @Override
    public void setCreateSessionAllowed(final boolean createSessionAllowed) {
        this.createSessionAllowed = createSessionAllowed;
    }

    @Override
    public void saveRequest(final HttpServletRequest request, final HttpServletResponse response) {
        // this might be called while in ExceptionTranslationFilter#handleSpringSecurityException in
        // this case base implementation
        if (SecurityContextHolder.getContext().getAuthentication() == null) {
            super.saveRequest(request, response);
        } else {
            final SavedRequest savedBefore = getRequest(request, response);
            if (savedBefore != null)// to not override request saved by
                                    // ExceptionTranslationFilter#handleSpringSecurityException
            {
                return;
            }

            if (getRequestMatcher().matches(request)) {
                final DefaultSavedRequest savedRequest = new DefaultSavedRequest(request, getPortResolver()) {
                    private final String referer = request.getHeader(REFERER);
                    private final String contextPath = request.getContextPath();

                    @Override
                    public String getRedirectUrl() {
                        return calculateRelativeRedirectUrl(contextPath, referer);
                    }
                };

                if (isCreateSessionAllowed() || request.getSession(false) != null) {
                    request.getSession().setAttribute(SAVED_REQUEST, savedRequest);
                    logger.debug("DefaultSavedRequest added to Session: " + savedRequest);
                }
            } else {
                logger.debug("Request not saved as configured RequestMatcher did not match");
            }
        }
    }

    /**
     * Converts the given url into a relative url
     * 
     * @param contextPath
     * @param url
     * @return relative url
     */
    protected String calculateRelativeRedirectUrl(final String contextPath, final String url) {
        if (UrlUtils.isAbsoluteUrl(url)) {
            String relUrl = url.substring(url.indexOf("://") + 3);
            relUrl = relUrl.substring(relUrl.indexOf(contextPath) + contextPath.length());
            return relUrl;
        } else {
            return url;
        }
    }

    protected boolean isCreateSessionAllowed() {
        return createSessionAllowed;
    }

    protected PortResolver getPortResolver() {
        return portResolver;
    }

    protected RequestMatcher getRequestMatcher() {
        return requestMatcher;
    }

    @Override
    public HttpServletRequest getMatchingRequest(final HttpServletRequest request, final HttpServletResponse response) {
        HttpServletRequest result = super.getMatchingRequest(request, response);
        if (result != null) {
            result = new CookieMergingHttpServletRequestWrapper(result, request);
        }
        return result;
    }

    /**
     * Request wrapper that wraps an innerRequest, and overlays on top of it the cookies from the
     * outerRequest.
     */
    public static class CookieMergingHttpServletRequestWrapper extends HttpServletRequestWrapper {
        private final HttpServletRequest outerRequest;

        public CookieMergingHttpServletRequestWrapper(final HttpServletRequest innerRequest,
                final HttpServletRequest outerRequest) {
            super(innerRequest);
            this.outerRequest = outerRequest;
        }

        @Override
        public Cookie[] getCookies() {
            return mergeCookies(super.getCookies(), outerRequest.getCookies());
        }

        protected Cookie[] mergeCookies(final Cookie[] savedCookies, final Cookie[] currentCookies) {
            // Start with the cookies from the current request as these will be more up to date
            final List<Cookie> cookies = new ArrayList<Cookie>(Arrays.asList(currentCookies));

            // Add any missing ones from the saved request
            for (final Cookie savedCookie : savedCookies) {
                if (!containsCookie(cookies, savedCookie.getName())) {
                    cookies.add(savedCookie);
                }
            }

            return cookies.toArray(new Cookie[cookies.size()]);
        }

        protected boolean containsCookie(final List<Cookie> cookies, final String cookieName) {
            if (cookies != null && !cookies.isEmpty() && cookieName != null) {
                for (final Cookie cookie : cookies) {
                    if (cookieName.equals(cookie.getName())) {
                        return true;
                    }
                }
            }
            return false;
        }
    }

}

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
package com.epam.cme.storefront.interceptors.beforeview;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

public class SeoRobotsFollowBeforeViewHandler implements BeforeViewHandler {
    @Override
    public void beforeView(final HttpServletRequest request, final HttpServletResponse response,
            final ModelAndView modelAndView) {
        // Check to see if the controller has specified a Index/Follow directive for robots
        if (modelAndView != null && !modelAndView.getModel().containsKey("metaRobots")) {
            // Build a default directive
            String robotsValue = "no-index,no-follow";

            if (RequestMethod.GET.name().equalsIgnoreCase(request.getMethod())) {
                if (request.isSecure()) {
                    robotsValue = "no-index,follow";
                } else {
                    robotsValue = "index,follow";
                }
            } else if (RequestMethod.POST.name().equalsIgnoreCase(request.getMethod())) {
                robotsValue = "no-index,no-follow";
            }

            modelAndView.addObject("metaRobots", robotsValue);
        }
    }
}

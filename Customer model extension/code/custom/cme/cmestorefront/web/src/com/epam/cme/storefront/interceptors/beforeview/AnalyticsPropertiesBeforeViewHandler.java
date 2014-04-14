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

import de.hybris.platform.acceleratorservices.config.HostConfigService;
import de.hybris.platform.servicelayer.i18n.CommonI18NService;
import com.epam.cme.storefront.controllers.ThirdPartyConstants;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.ModelAndView;


public class AnalyticsPropertiesBeforeViewHandler implements BeforeViewHandler
{
	@Resource(name = "hostConfigService")
	private HostConfigService hostConfigService;

	@Resource(name = "commonI18NService")
	private CommonI18NService commonI18NService;

	@Override
	public void beforeView(final HttpServletRequest request, final HttpServletResponse response, final ModelAndView modelAndView)
	{
		final String serverName = request.getServerName();

		// Add config properties for google analytics
		addHostProperty(serverName, modelAndView, ThirdPartyConstants.Google.ANALYTICS_TRACKING_ID, "googleAnalyticsTrackingId");

		// Add config properties for jirafe analytics
		addHostProperty(serverName, modelAndView, ThirdPartyConstants.Jirafe.API_URL, "jirafeApiUrl");
		addHostProperty(serverName, modelAndView, ThirdPartyConstants.Jirafe.API_TOKEN, "jirafeApiToken");
		addHostProperty(serverName, modelAndView, ThirdPartyConstants.Jirafe.APPLICATION_ID, "jirafeApplicationId");
		addHostProperty(serverName, modelAndView, ThirdPartyConstants.Jirafe.VERSION, "jirafeVersion");
		addHostProperty(serverName, modelAndView, ThirdPartyConstants.Jirafe.DATA_URL, "jirafeDataUrl");

		// Lookup a currency specific jirafe site id first, and only if it is missing fallback to the default site id
		final String currencyIso = commonI18NService.getCurrentCurrency().getIsocode().toLowerCase();
		final String currencySpecificJirafeSiteId = hostConfigService.getProperty(ThirdPartyConstants.Jirafe.SITE_ID + "." + currencyIso, serverName);
		if (org.apache.commons.lang.StringUtils.isNotBlank(currencySpecificJirafeSiteId))
		{
			modelAndView.addObject("jirafeSiteId", currencySpecificJirafeSiteId);
		}
		else
		{
			// Fallback to the non-currency specific value
			final String jirafeSiteId = hostConfigService.getProperty(ThirdPartyConstants.Jirafe.SITE_ID, serverName);
			modelAndView.addObject("jirafeSiteId", jirafeSiteId);
		}
	}

	protected void addHostProperty(final String serverName, final ModelAndView modelAndView, final String configKey, final String modelKey)
	{
		modelAndView.addObject(modelKey, hostConfigService.getProperty(configKey, serverName));
	}
}

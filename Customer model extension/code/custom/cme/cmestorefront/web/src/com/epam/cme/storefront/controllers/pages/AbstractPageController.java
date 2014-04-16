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
package com.epam.cme.storefront.controllers.pages;

import de.hybris.platform.acceleratorservices.config.HostConfigService;
import de.hybris.platform.acceleratorservices.config.SiteConfigService;
import de.hybris.platform.acceleratorservices.storefront.util.PageTitleResolver;
import de.hybris.platform.cms2.exceptions.CMSItemNotFoundException;
import de.hybris.platform.cms2.model.pages.AbstractPageModel;
import de.hybris.platform.cms2.model.pages.ContentPageModel;
import de.hybris.platform.cms2.model.pages.PageTemplateModel;
import de.hybris.platform.cms2.model.site.CMSSiteModel;
import de.hybris.platform.cms2.servicelayer.services.CMSPageService;
import de.hybris.platform.cms2.servicelayer.services.CMSSiteService;
import de.hybris.platform.commercefacades.customer.CustomerFacade;
import de.hybris.platform.commercefacades.order.CartFacade;
import de.hybris.platform.commercefacades.order.data.CartData;
import de.hybris.platform.commercefacades.storesession.StoreSessionFacade;
import de.hybris.platform.commercefacades.storesession.data.CurrencyData;
import de.hybris.platform.commercefacades.storesession.data.LanguageData;
import de.hybris.platform.commercefacades.user.data.CustomerData;
import de.hybris.platform.servicelayer.i18n.I18NService;
import de.hybris.platform.servicelayer.session.SessionService;

import java.io.UnsupportedEncodingException;
import java.util.Collection;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.httpclient.URIException;
import org.apache.commons.httpclient.util.URIUtil;
import org.apache.commons.lang.StringUtils;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.servlet.View;

import com.epam.cme.facades.blockablecustomer.BlockableCustomerFacade;
import com.epam.cme.storefront.constants.WebConstants;
import com.epam.cme.storefront.controllers.AbstractController;


/**
 * Base controller for all page controllers. Provides common functionality for all page controllers.
 */
public abstract class AbstractPageController extends AbstractController
{
	public static final String PAGE_ROOT = "pages/";

	public static final String CMS_PAGE_MODEL = "cmsPage";
	public static final String CMS_PAGE_TITLE = "pageTitle";
	public static final String COMPRESSION_CONFIG_KEY_PREFIX = "enable.compression.css.js";

	public static enum PageType
	{
		ProductSearch("ProductSearch"), //
		Category("Category"), //
		Product("Product"), //
		Cart("Cart"), //
		OrderConfirmation("OrderConfirmation"); //

		private final String value;

		private PageType(final String value)
		{
			this.value = value;
		}

		public String getValue()
		{
			return value;
		}
	}

	@Resource(name = "cmsSiteService")
	private CMSSiteService cmsSiteService;

	@Resource(name = "cmsPageService")
	private CMSPageService cmsPageService;

	@Resource(name = "storeSessionFacade")
	private StoreSessionFacade storeSessionFacade;

	@Resource(name = "customerFacade")
	private CustomerFacade customerFacade;

	@Resource(name = "blockableCustomerFacade")
	private BlockableCustomerFacade blockableCustomerFacade;

	@Resource(name = "pageTitleResolver")
	private PageTitleResolver pageTitleResolver;

	@Resource(name = "sessionService")
	private SessionService sessionService;

	@Resource(name = "hostConfigService")
	private HostConfigService hostConfigService;

	@Resource(name = "messageSource")
	private MessageSource messageSource;

	@Resource(name = "i18nService")
	private I18NService i18nService;

	@Resource(name = "siteConfigService")
	private SiteConfigService siteConfigService;

	@Resource(name = "cartFacade")
	private CartFacade cartFacade;

	protected SiteConfigService getSiteConfigService()
	{
		return siteConfigService;
	}

	protected CMSSiteService getCmsSiteService()
	{
		return cmsSiteService;
	}

	protected CMSPageService getCmsPageService()
	{
		return cmsPageService;
	}

	protected StoreSessionFacade getStoreSessionFacade()
	{
		return storeSessionFacade;
	}

	protected BlockableCustomerFacade getBlockableCustomerFacade()
	{
		return blockableCustomerFacade;
	}

	protected SessionService getSessionService()
	{
		return sessionService;
	}

	protected HostConfigService getHostConfigService()
	{
		return hostConfigService;
	}

	protected MessageSource getMessageSource()
	{
		return messageSource;
	}

	protected I18NService getI18nService()
	{
		return i18nService;
	}

	@ModelAttribute("languages")
	public Collection<LanguageData> getLanguages()
	{
		return storeSessionFacade.getAllLanguages();
	}

	@ModelAttribute("currencies")
	public Collection<CurrencyData> getCurrencies()
	{
		return storeSessionFacade.getAllCurrencies();
	}

	@ModelAttribute("currentLanguage")
	public LanguageData getCurrentLanguage()
	{
		return storeSessionFacade.getCurrentLanguage();
	}

	@ModelAttribute("currentCurrency")
	public CurrencyData getCurrentCurrency()
	{
		return storeSessionFacade.getCurrentCurrency();
	}

	@ModelAttribute("miniCartData")
	public CartData getCartData()
	{
		return cartFacade.getMiniCart();
	}

	@ModelAttribute("siteName")
	public String getSiteName()
	{
		final CMSSiteModel site = cmsSiteService.getCurrentSite();
		return site != null ? site.getName() : "";
	}

	@ModelAttribute("user")
	public CustomerData getUser()
	{
		return customerFacade.getCurrentCustomer();
	}

	@ModelAttribute("cssCompression")
	public Boolean isCssCompressionEnabled()
	{
		final String compressionEnabled = siteConfigService.getProperty(COMPRESSION_CONFIG_KEY_PREFIX);
		return Boolean.valueOf(compressionEnabled);
	}

	protected void storeCmsPageInModel(final Model model, final AbstractPageModel cmsPage)
	{
		if (model != null && cmsPage != null)
		{
			model.addAttribute(CMS_PAGE_MODEL, cmsPage);
			if (cmsPage instanceof ContentPageModel)
			{
				storeContentPageTitleInModel(model, getPageTitleResolver().resolveContentPageTitle(cmsPage.getTitle()));
			}
		}
	}

	protected void storeContentPageTitleInModel(final Model model, final String title)
	{
		model.addAttribute(CMS_PAGE_TITLE, title);
	}

	protected String getViewForPage(final Model model)
	{
		if (model.containsAttribute(CMS_PAGE_MODEL))
		{
			final AbstractPageModel page = (AbstractPageModel) model.asMap().get(CMS_PAGE_MODEL);
			if (page != null)
			{
				return getViewForPage(page);
			}
		}
		return null;
	}

	protected String getViewForPage(final AbstractPageModel page)
	{
		if (page != null)
		{
			final PageTemplateModel masterTemplate = page.getMasterTemplate();
			if (masterTemplate != null)
			{
				final String targetPage = cmsPageService.getFrontendTemplateName(masterTemplate);
				if (targetPage != null && !targetPage.isEmpty())
				{
					return PAGE_ROOT + targetPage;
				}
			}
		}
		return null;
	}

	/**
	 * Checks request URL against properly resolved URL and returns null if url is proper or redirection string if not.
	 * 
	 * @param request
	 *           - request that contains current URL
	 * @param response
	 *           - response to write "301 Moved Permanently" status to if redirected
	 * @param resolvedUrl
	 *           - properly resolved URL
	 * @return null if url is properly resolved or redirection string if not
	 * @throws UnsupportedEncodingException
	 */
	protected String checkRequestUrl(final HttpServletRequest request, final HttpServletResponse response, final String resolvedUrl)
			throws UnsupportedEncodingException
	{
		try
		{
			final String requestURI = URIUtil.decode(request.getRequestURI(), "utf-8");
			final String decoded = URIUtil.decode(resolvedUrl, "utf-8");
			if (StringUtils.isNotEmpty(requestURI) && requestURI.endsWith(decoded))
			{
				return null;
			}
			else
			{
				request.setAttribute(View.RESPONSE_STATUS_ATTRIBUTE, HttpStatus.MOVED_PERMANENTLY);
				final String queryString = request.getQueryString();
				if (queryString != null && !queryString.isEmpty())
				{
					return "redirect:" + resolvedUrl + "?" + queryString;
				}
				return "redirect:" + resolvedUrl;
			}
		}
		catch (final URIException e)
		{
			throw new UnsupportedEncodingException();
		}
	}

	protected ContentPageModel getContentPageForLabelOrId(final String labelOrId) throws CMSItemNotFoundException
	{
		String key = labelOrId;
		if (StringUtils.isEmpty(labelOrId))
		{
			// Fallback to site home page
			final ContentPageModel homePage = cmsPageService.getHomepage();
			if (homePage != null)
			{
				key = cmsPageService.getLabelOrId(homePage);
			}
			else
			{
				// Fallback to site start page label
				final CMSSiteModel site = cmsSiteService.getCurrentSite();
				if (site != null)
				{
					key = cmsSiteService.getStartPageLabelOrId(site);
				}
			}
		}

		// Actually resolve the label or id - running cms restrictions
		return cmsPageService.getPageForLabelOrId(key);
	}

	protected PageTitleResolver getPageTitleResolver()
	{
		return pageTitleResolver;
	}

	protected void storeContinueUrl(final HttpServletRequest request)
	{
		final StringBuilder url = new StringBuilder();
		url.append(request.getServletPath());
		final String queryString = request.getQueryString();
		if (queryString != null && !queryString.isEmpty())
		{
			url.append('?').append(queryString);
		}
		getSessionService().setAttribute(WebConstants.CONTINUE_URL, url.toString());
	}

	protected void setUpMetaData(final Model model, final String metaKeywords, final String metaDescription)
	{
		model.addAttribute("metaKeywords", metaKeywords);
		model.addAttribute("metaDescription", metaDescription);
	}

	protected void setUpMetaDataForContentPage(final Model model, final ContentPageModel contentPage)
	{
		setUpMetaData(model, contentPage.getKeywords(), contentPage.getDescription());
	}
}

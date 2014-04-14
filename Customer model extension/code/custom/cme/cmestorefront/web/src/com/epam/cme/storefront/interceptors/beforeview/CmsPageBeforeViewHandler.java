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

import com.epam.cme.storefront.controllers.pages.AbstractPageController;
import com.epam.cme.storefront.filters.cms.CMSSiteFilter;
import de.hybris.platform.cms2.jalo.preview.PreviewData;
import de.hybris.platform.cms2.model.contents.contentslot.ContentSlotModel;
import de.hybris.platform.cms2.model.pages.AbstractPageModel;
import de.hybris.platform.cms2.model.pages.PageTemplateModel;
import de.hybris.platform.cms2.model.preview.CMSPreviewTicketModel;
import de.hybris.platform.cms2.model.preview.PreviewDataModel;
import de.hybris.platform.cms2.servicelayer.data.ContentSlotData;
import de.hybris.platform.cms2.servicelayer.services.CMSPageService;
import de.hybris.platform.cms2.servicelayer.services.CMSPreviewService;
import de.hybris.platform.cms2.servicelayer.services.CMSSiteService;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.springframework.web.servlet.ModelAndView;


/**
 * Filter to load the appropriate Cms page slots into the model
 */
public class CmsPageBeforeViewHandler implements BeforeViewHandler
{
	private static final Logger LOG = Logger.getLogger(CmsPageBeforeViewHandler.class);

	public static final String SLOTS_MODEL = "slots";

	@Resource(name = "cmsSiteService")
	private CMSSiteService cmsSiteService;

	@Resource(name = "cmsPageService")
	private CMSPageService cmsPageService;

	@Resource(name = "cmsPreviewService")
	private CMSPreviewService cmsPreviewService;

	@Override
	public void beforeView(final HttpServletRequest request, final HttpServletResponse response, final ModelAndView modelAndView)
	{
		modelAndView.addObject("cmsSite", cmsSiteService.getCurrentSite());

		// Look for the page in the model
		AbstractPageModel page = (AbstractPageModel) modelAndView.getModel().get(AbstractPageController.CMS_PAGE_MODEL);
		if (page != null)
		{
			final AbstractPageModel previewPage = lookupPreviewPage(request);
			if (previewPage != null && !previewPage.equals(page))
			{
				// Have a preview page that overrides the current page

				// Check that the preview page is the same type as the expected page
				if (!page.getClass().isInstance(previewPage))
				{
					LOG.error("Preview page is of type [" + previewPage.getClass().getName() + "] expected page of type ["
							+ page.getClass().getName() + "]");
				}
				else
				{
					// Push the preview page into the model
					LOG.info("Replaced page [" + page + "] with preview page [" + previewPage + "]");
					modelAndView.addObject(AbstractPageController.CMS_PAGE_MODEL, previewPage);

					// Check to see if we are using the default view for the page
					if (modelAndView.getViewName() != null && modelAndView.getViewName().equals(getViewForPage(page)))
					{
						final String viewForPreviewPage = getViewForPage(previewPage);
						if (viewForPreviewPage != null && !viewForPreviewPage.equals(modelAndView.getViewName()))
						{
							// Change the view name
							LOG.info("Changing view from [" + modelAndView.getViewName() + "] to preview view [" + viewForPreviewPage
									+ "]");
							modelAndView.setViewName(viewForPreviewPage);
						}
					}

					page = previewPage;
				}
			}

			// Add the page to the request attribute "currentPage" which is then picked up by the CMSBodyTag, and then passed over to the CMS Cockpit
			request.setAttribute("currentPage", page);

			// Add the content slots to the page as a map of slots - keyed by position
			modelAndView.addObject(SLOTS_MODEL, getContentSlotsForPageAsMap(page));
		}
	}

	/**
	 * Retrieve all content slots for the page and return them in a map
	 * 
	 * @param page
	 * @return map with content slots assigned to positions
	 */
	protected Map<String, ContentSlotModel> getContentSlotsForPageAsMap(final AbstractPageModel page)
	{
		if (page == null)
		{
			throw new IllegalArgumentException("page must not be null");
		}

		final Collection<ContentSlotData> slotModels = cmsPageService.getContentSlotsForPage(page);

		final Map<String, ContentSlotModel> slots = new HashMap<String, ContentSlotModel>(slotModels.size());
		for (final ContentSlotData contentSlot : slotModels)
		{
			slots.put(contentSlot.getPosition(), contentSlot.getContentSlot());
		}

		return slots;
	}

	/**
	 * Retrieves a preview ticket, if available and retrieves the preview page from the {@link PreviewData}
	 * 
	 * @param request
	 * @return preview page
	 */
	protected AbstractPageModel lookupPreviewPage(final HttpServletRequest request)
	{
		final String previewTicketId = request.getParameter(CMSSiteFilter.PREVIEW_TICKET_ID_PARAM);
		if (previewTicketId != null && !previewTicketId.isEmpty())
		{
			final CMSPreviewTicketModel previewTicket = cmsPreviewService.getPreviewTicket(previewTicketId);
			if (previewTicket != null)
			{
				final PreviewDataModel previewData = previewTicket.getPreviewData();
				if (previewData != null)
				{
					return previewData.getPage();
				}
			}
		}
		return null;
	}

	/**
	 * Returns ths view name for the page by retrieving the frontendTemplateName from the masterTemplate of the page
	 * 
	 * @param page
	 * @return view name or null, if the view name cannot retrieved from the masterTemplate
	 */
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
					return AbstractPageController.PAGE_ROOT + targetPage;
				}
			}
		}
		return null;
	}
}

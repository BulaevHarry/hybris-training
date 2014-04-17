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
package com.epam.cme.storefront.controllers.cms;

import de.hybris.platform.cms2.exceptions.CMSItemNotFoundException;
import de.hybris.platform.cms2.model.contents.components.AbstractCMSComponentModel;
import de.hybris.platform.cms2.servicelayer.services.CMSComponentService;
import com.epam.cme.storefront.controllers.AbstractController;
import com.epam.cme.storefront.controllers.ControllerConstants;
import com.epam.cme.storefront.controllers.pages.AbstractPageController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Abstract Controller for CMS Components
 */
public abstract class AbstractCMSComponentController<T extends AbstractCMSComponentModel> extends AbstractController {
    protected static final Logger LOG = Logger.getLogger(AbstractCMSComponentController.class);

    protected static final String COMPONENT_UID = "componentUid";

    @Resource(name = "cmsComponentService")
    private CMSComponentService cmsComponentService;

    // Setter required for UnitTests
    public void setCmsComponentService(final CMSComponentService cmsComponentService) {
        this.cmsComponentService = cmsComponentService;
    }

    @RequestMapping
    public String handleGet(final HttpServletRequest request, final HttpServletResponse response, final Model model)
            throws Exception {
        String componentUid = (String) request.getAttribute(COMPONENT_UID);
        if (StringUtils.isEmpty(componentUid)) {
            componentUid = request.getParameter(COMPONENT_UID);
        }

        if (StringUtils.isEmpty(componentUid)) {
            LOG.error("No component specified in [" + COMPONENT_UID + "]");
            throw new AbstractPageController.HttpNotFoundException();
        }

        try {
            final T component = (T) cmsComponentService.getSimpleCMSComponent(componentUid);
            if (component == null) {
                LOG.error("Component with UID [" + componentUid + "] is null");
                throw new AbstractPageController.HttpNotFoundException();
            } else {
                // Add the component to the model
                model.addAttribute("component", component);

                // Allow subclasses to handle the component
                return handleComponent(request, response, model, component);
            }
        } catch (final CMSItemNotFoundException e) {
            LOG.error("Could not find component with UID [" + componentUid + "]");
            throw new AbstractPageController.HttpNotFoundException(e);
        }
    }

    protected String handleComponent(final HttpServletRequest request, final HttpServletResponse response,
            final Model model, final T component) throws Exception {
        fillModel(request, model, component);
        return getView(component);
    }

    protected abstract void fillModel(final HttpServletRequest request, final Model model, final T component);

    protected String getView(final T component) {
        // build a jsp response based on the component type
        return ControllerConstants.Views.Cms.ComponentPrefix + StringUtils.lowerCase(getTypeCode(component));
    }

    protected String getTypeCode(final T component) {
        return component.getItemtype();
    }

    protected CMSComponentService getCmsComponentService() {
        return cmsComponentService;
    }
}

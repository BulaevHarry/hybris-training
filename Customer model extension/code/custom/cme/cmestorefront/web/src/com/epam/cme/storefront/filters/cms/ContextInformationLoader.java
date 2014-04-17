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
package com.epam.cme.storefront.filters.cms;

import de.hybris.platform.acceleratorservices.uiexperience.UiExperienceService;
import de.hybris.platform.catalog.CatalogVersionService;
import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.cms2.constants.Cms2Constants;
import de.hybris.platform.cms2.exceptions.CMSItemNotFoundException;
import de.hybris.platform.cms2.misc.CMSFilter;
import de.hybris.platform.cms2.model.preview.PreviewDataModel;
import de.hybris.platform.cms2.model.site.CMSSiteModel;
import de.hybris.platform.cms2.servicelayer.services.CMSSiteService;
import de.hybris.platform.core.model.c2l.LanguageModel;
import de.hybris.platform.core.model.security.PrincipalModel;
import de.hybris.platform.core.model.user.UserGroupModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.jalo.JaloSession;
import de.hybris.platform.servicelayer.i18n.I18NService;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.time.TimeService;
import de.hybris.platform.servicelayer.user.UserService;
import de.hybris.platform.site.BaseSiteService;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collection;
import java.util.Date;
import java.util.Locale;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;

/**
 * Default context information loader
 */
public class ContextInformationLoader {
    private static final Logger LOG = Logger.getLogger(ContextInformationLoader.class);

    private CMSSiteService cmsSiteService;
    private BaseSiteService baseSiteService;
    private CatalogVersionService catalogVersionService;
    private UserService userService;
    private I18NService i18NService;
    private ModelService modelService;
    private TimeService timeService;
    private UiExperienceService uiExperienceService;

    public CMSSiteModel getCurrentSite() {
        return getCMSSiteService().getCurrentSite();
    }

    public void setCatalogVersions() {
        try {
            final CMSSiteModel currentSiteModel = getCurrentSite();
            if (currentSiteModel != null) {
                getCMSSiteService().setCurrentSiteAndCatalogVersions(currentSiteModel, true);
            }
        } catch (final CMSItemNotFoundException e) {
            if (LOG.isDebugEnabled()) {
                LOG.debug("Catalog has no active catalog version!", e);
            }
        }
    }

    public CMSSiteModel initializeSiteFromRequest(final String absoluteURL) {
        try {
            final URL currentURL = new URL(absoluteURL);
            final CMSSiteService cmsSiteService = getCMSSiteService();
            final CMSSiteModel cmsSiteModel = cmsSiteService.getSiteForURL(currentURL);
            if (cmsSiteModel != null) {
                getBaseSiteService().setCurrentBaseSite(cmsSiteModel, true);
                return cmsSiteModel;
            }
        } catch (final MalformedURLException e) {
            if (LOG.isDebugEnabled()) {
                LOG.debug("Cannot find CMSSite associated with current URL ( " + absoluteURL
                        + " - check whether this is correct URL) !");
            }
        } catch (final CMSItemNotFoundException e) {
            LOG.warn("Cannot find CMSSite associated with current URL (" + absoluteURL + ")!");
            if (LOG.isDebugEnabled()) {
                LOG.debug(e);
            }
        }
        return null;
    }

    public void initializePreviewRequest(final PreviewDataModel previewDataModel) {
        loadActiveBaseSite(previewDataModel);
        loadCatalogVersions(previewDataModel.getCatalogVersions());
        loadUiExperienceLevel(previewDataModel);
    }

    public void loadActiveBaseSite(final PreviewDataModel previewDataModel) {
        final BaseSiteService baseSiteService = getBaseSiteService();
        if (previewDataModel.getActiveSite() == null) {
            LOG.warn("Could not set active site. Reason: No active site was selected!");
        } else {
            baseSiteService.setCurrentBaseSite(previewDataModel.getActiveSite(), true);
        }
    }

    public void loadCatalogVersions(final Collection<CatalogVersionModel> catalogVersions) {
        getCatalogVersionService().setSessionCatalogVersions(catalogVersions);
    }

    public void loadUiExperienceLevel(final PreviewDataModel previewDataModel) {
        getUiExperienceService().setDetectedUiExperienceLevel(previewDataModel.getUiExperience());
    }

    protected void loadFakeUser(final UserModel fakeUser) {
        final UserService userService = getUserService();
        final UserModel currentUser = userService.getCurrentUser();
        if (fakeUser != null && !fakeUser.equals(currentUser)) {
            userService.setCurrentUser(fakeUser);
        }
    }

    protected void loadFakeUserGroup(final PreviewDataModel previewDataModel) {
        if (previewDataModel.getUser() == null && previewDataModel.getUserGroup() != null) {
            UserModel userWithinDesiredGroup = null;
            final UserGroupModel fakeUserGroup = previewDataModel.getUserGroup();
            for (final PrincipalModel principalModel : fakeUserGroup.getMembers()) {
                if (principalModel instanceof UserModel) {
                    userWithinDesiredGroup = (UserModel) principalModel;
                    break;
                }
            }
            if (userWithinDesiredGroup != null) {
                loadFakeUser(userWithinDesiredGroup);
            }
        }
    }

    protected void loadFakeLanguage(final LanguageModel languageModel) {
        if (languageModel != null) {
            getI18NService().setCurrentLocale(new Locale(languageModel.getIsocode()));
        }
    }

    protected void storePreviewTicketIDWithinSession(final HttpServletRequest httpRequest) {
        final String ticketId = httpRequest.getParameter(CMSFilter.PREVIEW_TICKET_ID_PARAM);
        if (StringUtils.isNotBlank(ticketId)) {
            JaloSession.getCurrentSession().setAttribute(CMSFilter.PREVIEW_TICKET_ID_PARAM, ticketId);
        }
    }

    protected void loadFakeDate(final Date fakeDate) {
        if (fakeDate != null) {
            getTimeService().setCurrentTime(fakeDate);
            JaloSession.getCurrentSession().setAttribute(Cms2Constants.PREVIEW_TIME, fakeDate);
        }
    }

    public void loadFakeContextInformation(final HttpServletRequest httpRequest, final PreviewDataModel previewData) {
        // set fake user
        loadFakeUser(previewData.getUser());
        // set fake user group
        loadFakeUserGroup(previewData);
        // set fake language
        loadFakeLanguage(previewData.getLanguage());
        // set fake date
        loadFakeDate(previewData.getTime());

        storePreviewTicketIDWithinSession(httpRequest);
    }

    public void storePreviewData(final PreviewDataModel previewData) {
        final ModelService modelService = getModelService();
        if (previewData == null) {
            LOG.warn("Could not store preview data. Reason: Preview data was null.");
        } else {
            if (modelService == null) {
                LOG.warn("Could not store preview data. Reason: Model service was null.");
            } else {
                modelService.save(previewData);
            }
        }
    }

    @Required
    public void setBaseSiteService(final BaseSiteService baseSiteService) {
        this.baseSiteService = baseSiteService;
    }

    @Required
    public void setCatalogVersionService(final CatalogVersionService catalogVersionService) {
        this.catalogVersionService = catalogVersionService;
    }

    @Required
    public void setCmsSiteService(final CMSSiteService cmsSiteService) {
        this.cmsSiteService = cmsSiteService;
    }

    @Required
    public void setI18NService(final I18NService i18nService) {
        i18NService = i18nService;
    }

    @Required
    public void setModelService(final ModelService modelService) {
        this.modelService = modelService;
    }

    @Required
    public void setTimeService(final TimeService timeService) {
        this.timeService = timeService;
    }

    @Required
    public void setUserService(final UserService userService) {
        this.userService = userService;
    }

    @Required
    public void setUiExperienceService(final UiExperienceService uiExperienceService) {
        this.uiExperienceService = uiExperienceService;
    }

    protected CMSSiteService getCMSSiteService() {
        return cmsSiteService;
    }

    protected BaseSiteService getBaseSiteService() {
        return baseSiteService;
    }

    protected CatalogVersionService getCatalogVersionService() {
        return catalogVersionService;
    }

    protected UserService getUserService() {
        return userService;
    }

    protected I18NService getI18NService() {
        return i18NService;
    }

    protected ModelService getModelService() {
        return modelService;
    }

    protected TimeService getTimeService() {
        return timeService;
    }

    protected UiExperienceService getUiExperienceService() {
        return uiExperienceService;
    }
}

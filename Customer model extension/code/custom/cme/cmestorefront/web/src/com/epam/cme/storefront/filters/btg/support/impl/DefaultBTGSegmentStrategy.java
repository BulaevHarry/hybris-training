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
package com.epam.cme.storefront.filters.btg.support.impl;

import de.hybris.platform.btg.enums.BTGConditionEvaluationScope;
import de.hybris.platform.btg.enums.BTGEvaluationMethod;
import de.hybris.platform.btg.enums.BTGResultScope;
import de.hybris.platform.btg.segment.SegmentEvaluationException;
import de.hybris.platform.btg.servicelayer.services.evaluator.impl.SessionBTGEvaluationContextProvider;
import de.hybris.platform.btg.services.BTGEvaluationService;
import de.hybris.platform.btg.services.BTGResultService;
import de.hybris.platform.btg.services.impl.BTGEvaluationContext;
import de.hybris.platform.cms2.misc.CMSFilter;
import de.hybris.platform.cms2.model.preview.CMSPreviewTicketModel;
import de.hybris.platform.cms2.model.preview.PreviewDataModel;
import de.hybris.platform.cms2.model.site.CMSSiteModel;
import de.hybris.platform.cms2.servicelayer.services.CMSPreviewService;
import de.hybris.platform.cms2.servicelayer.services.CMSSiteService;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.servicelayer.session.SessionService;
import de.hybris.platform.servicelayer.user.UserService;
import com.epam.cme.storefront.filters.btg.support.BTGSegmentStrategy;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;

/**
 * Evaluate the BTG segments. Only evaluated once per request.
 */
public class DefaultBTGSegmentStrategy implements BTGSegmentStrategy {
    private static final String PROCEED_LOGOUT = "proceedLogout";
    private static final String BTG_SEGMENT_EVALUATED = "btgSegmentEvaluated";

    private static final Logger LOG = Logger.getLogger(DefaultBTGSegmentStrategy.class);

    private CMSSiteService cmsSiteService;
    private SessionService sessionService;
    private UserService userService;
    private CMSPreviewService cmsPreviewService;
    private BTGEvaluationService btgEvaluationService;
    private BTGResultService btgResultService;
    private boolean evaluateAnonymousSessions;

    @Override
    public void evaluateSegment(final HttpServletRequest httpRequest) throws ServletException, IOException {
        if (shouldEvaluateAnonymousSession(userService.getCurrentUser(), canEvaluateAnonymousSessions())) {
            final Object btgSegmentEvaluated = httpRequest.getAttribute(BTG_SEGMENT_EVALUATED);
            if (!Boolean.TRUE.equals(btgSegmentEvaluated)) {
                if (!isDuringLogout(httpRequest)) {
                    final BTGEvaluationService btgEvaluationService = getBtgEvaluationService();
                    final BTGResultService btgResultService = getBtgResultService();
                    final UserService userService = getUserService();
                    final UserModel currentUser = userService.getCurrentUser();
                    final CMSSiteService cmsSiteService = getCmsSiteService();
                    final CMSSiteModel currentSite = cmsSiteService.getCurrentSite();

                    try {
                        final BTGEvaluationContext context;
                        if (isPreviewDataModelValid(httpRequest)) {
                            // preview for BTGCockpit
                            // always invoke FULL evaluation method and store results per session
                            context = new BTGEvaluationContext(BTGConditionEvaluationScope.ONLINE,
                                    BTGEvaluationMethod.FULL, BTGResultScope.SESSION);
                        } else {
                            // process normal request (i.e. normal browser non-btgcockpit request)
                            // the evaluation method will be taken from segment!
                            context = new BTGEvaluationContext(BTGConditionEvaluationScope.ONLINE, null);
                        }
                        // right now we basically invalidate all results, because we don't specify
                        // BTGRuleType
                        // i.e. when user would like to invalidate only some type of rules he should
                        // specify this parameter
                        btgResultService.invalidateEvaluationResults(currentSite, currentUser, context, null);
                        btgEvaluationService.evaluateAllSegments(currentUser, currentSite, context);

                        getSessionService().setAttribute(
                                SessionBTGEvaluationContextProvider.BTG_CURRENT_EVALUATION_CONTEXT, context);
                    } catch (final SegmentEvaluationException e) {
                        // Log the exception but do not 'fail' the request
                        LOG.error("Failed to evaluate BTG Segments", e);
                    }
                }
                httpRequest.setAttribute(BTG_SEGMENT_EVALUATED, Boolean.TRUE);
            }
        }
    }

    /**
     * @param currentUser
     *            The user in the session
     * @param anonymousSessionCheckFlag
     *            If you want to always evaluate anonymous sessions set this to false
     * @return true if the user is anonymous else false, if false was passed for
     *         <param>anonymousSessionCheckFlag</param> a true is returned.
     */
    protected boolean shouldEvaluateAnonymousSession(final UserModel currentUser,
            final boolean anonymousSessionCheckFlag) {
        return !anonymousSessionCheckFlag || !userService.isAnonymousUser(currentUser);
    }

    protected boolean isDuringLogout(final HttpServletRequest httpRequest) {
        return Boolean.parseBoolean(httpRequest.getParameter(PROCEED_LOGOUT));
    }

    /**
     * Retrieves {@link CMSFilter#PREVIEW_TICKET_ID_PARAM} from current request
     * 
     * @param httpRequest
     *            current request
     * @return current ticket id
     */
    protected String getPreviewTicketId(final HttpServletRequest httpRequest) {
        String id = httpRequest.getParameter(CMSFilter.PREVIEW_TICKET_ID_PARAM);
        if (StringUtils.isBlank(id)) {
            id = getSessionService().getAttribute(CMSFilter.PREVIEW_TICKET_ID_PARAM);
        }
        return id;
    }

    /**
     * Checks whether current Preview Data is valid (not removed)
     * 
     * @param httpRequest
     *            current request
     * @return true whether is valid otherwise false
     */
    protected boolean isPreviewDataModelValid(final HttpServletRequest httpRequest) {
        return getPreviewData(getPreviewTicketId(httpRequest)) != null;
    }

    /**
     * Retrieves current Preview Data according to given ticked id
     * 
     * @param ticketId
     *            current ticket id
     * @return current Preview Data attached to given ticket if any otherwise null
     */
    protected PreviewDataModel getPreviewData(final String ticketId) {
        PreviewDataModel ret = null;
        final CMSPreviewTicketModel previewTicket = getCmsPreviewService().getPreviewTicket(ticketId);
        if (previewTicket != null) {
            ret = previewTicket.getPreviewData();
        }
        return ret;
    }

    protected CMSSiteService getCmsSiteService() {
        return cmsSiteService;
    }

    @Required
    public void setCmsSiteService(final CMSSiteService cmsSiteService) {
        this.cmsSiteService = cmsSiteService;
    }

    protected SessionService getSessionService() {
        return sessionService;
    }

    @Required
    public void setSessionService(final SessionService sessionService) {
        this.sessionService = sessionService;
    }

    protected UserService getUserService() {
        return userService;
    }

    @Required
    public void setUserService(final UserService userService) {
        this.userService = userService;
    }

    protected CMSPreviewService getCmsPreviewService() {
        return cmsPreviewService;
    }

    @Required
    public void setCmsPreviewService(final CMSPreviewService cmsPreviewService) {
        this.cmsPreviewService = cmsPreviewService;
    }

    protected BTGEvaluationService getBtgEvaluationService() {
        return btgEvaluationService;
    }

    @Required
    public void setBtgEvaluationService(final BTGEvaluationService btgEvaluationService) {
        this.btgEvaluationService = btgEvaluationService;
    }

    protected BTGResultService getBtgResultService() {
        return btgResultService;
    }

    @Required
    public void setBtgResultService(final BTGResultService btgResultService) {
        this.btgResultService = btgResultService;
    }

    protected boolean canEvaluateAnonymousSessions() {
        return evaluateAnonymousSessions;
    }

    @Required
    public void setEvaluateAnonymousSessions(final boolean evaluateAnonymousSessions) {
        this.evaluateAnonymousSessions = evaluateAnonymousSessions;
    }
}

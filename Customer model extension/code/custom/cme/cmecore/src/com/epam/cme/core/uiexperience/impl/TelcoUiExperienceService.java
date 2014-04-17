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
package com.epam.cme.core.uiexperience.impl;

import de.hybris.platform.acceleratorservices.enums.UiExperienceLevel;
import de.hybris.platform.acceleratorservices.uiexperience.impl.DefaultUiExperienceService;
import de.hybris.platform.store.BaseStoreModel;
import de.hybris.platform.store.services.BaseStoreService;

import org.springframework.beans.factory.annotation.Required;

/**
 * Telco specific implementation of the UiExperienceService. It makes sure that the session
 * attribute OVERRIDE_UI_EXPERIENCE_LEVEL always returns "Desktop" as UI experience level for the
 * telco storefront so that all requests use the desktop pages no matter if they come from a desktop
 * browser or a mobile device.
 */
public class TelcoUiExperienceService extends DefaultUiExperienceService {
    private BaseStoreService baseStoreService;

    @Override
    public UiExperienceLevel getOverrideUiExperienceLevel() {
        final BaseStoreModel baseStore = getBaseStoreService().getCurrentBaseStore();

        if (baseStore != null && "telco".equals(baseStore.getUid())) {
            return UiExperienceLevel.DESKTOP;
        } else {
            return super.getOverrideUiExperienceLevel();
        }
    }

    protected BaseStoreService getBaseStoreService() {
        return baseStoreService;
    }

    @Required
    public void setBaseStoreService(final BaseStoreService baseStoreService) {
        this.baseStoreService = baseStoreService;
    }
}

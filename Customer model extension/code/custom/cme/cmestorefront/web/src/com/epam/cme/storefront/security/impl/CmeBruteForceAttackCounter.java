package com.epam.cme.storefront.security.impl;

import de.hybris.platform.servicelayer.exceptions.UnknownIdentifierException;
import de.hybris.platform.servicelayer.model.ModelService;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;

import com.epam.cme.core.model.BlockableCustomerModel;
import com.epam.cme.services.blockablecustomer.BlockableCustomerService;
import com.epam.cme.storefront.security.BruteForceAttackCounter;

public class CmeBruteForceAttackCounter implements BruteForceAttackCounter {

    private final Integer maxFailedLogins;
    private ModelService modelService;
    private BlockableCustomerService blockableCustomerService;
    private static final Logger LOG = Logger.getLogger(CmeBruteForceAttackCounter.class);

    public CmeBruteForceAttackCounter(final Integer maxFailedLogins) {
        this.maxFailedLogins = maxFailedLogins;
    }

    @Override
    public void registerLoginFailure(final String uid) {
        final BlockableCustomerModel model;
        if (StringUtils.isNotEmpty(uid)) {
            try {
                model = blockableCustomerService.getBlockableCustomerByUid(uid);
            } catch (final UnknownIdentifierException ue) {
                return;
            }
            model.setAttemptCount(Math.min(model.getAttemptCount() + 1, maxFailedLogins + 1));
            modelService.save(model);
        }
    }

    @Override
    public boolean isAttack(final String uid) {
        if (StringUtils.isNotEmpty(uid)) {
            return maxFailedLogins.compareTo(blockableCustomerService.getBlockableCustomerByUid(uid).getAttemptCount()) <= 0;
        } else {
            return false;
        }
    }

    @Override
    public void resetUserCounter(final String uid) {
        if (StringUtils.isNotEmpty(uid)) {
            final BlockableCustomerModel model = blockableCustomerService.getBlockableCustomerByUid(uid);
            model.setAttemptCount(0);
            modelService.save(model);
        }
    }

    @Override
    public int getUserFailedLogins(final String uid) {
        if (StringUtils.isNotEmpty(uid)) {
            final BlockableCustomerModel model = blockableCustomerService.getBlockableCustomerByUid(uid);
            return model.getAttemptCount();
        }
        return 0;
    }

    @Required
    public void setBlockableCustomerService(final BlockableCustomerService blockableCustomerService) {
        this.blockableCustomerService = blockableCustomerService;
    }

    @Required
    public void setModelService(final ModelService modelService) {
        this.modelService = modelService;
    }

}

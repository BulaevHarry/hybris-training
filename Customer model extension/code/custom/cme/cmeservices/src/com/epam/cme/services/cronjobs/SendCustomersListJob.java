package com.epam.cme.services.cronjobs;

import de.hybris.platform.cronjob.model.CronJobModel;
import de.hybris.platform.servicelayer.cronjob.AbstractJobPerformable;
import de.hybris.platform.servicelayer.cronjob.PerformResult;

public class SendCustomersListJob extends AbstractJobPerformable<CronJobModel> {

    @Override
    public PerformResult perform(final CronJobModel cronJob) {
        // Add implementation
        return null;
    }

}

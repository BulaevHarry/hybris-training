package com.epam.cme.services.cronjobs;

import de.hybris.platform.cronjob.enums.CronJobResult;
import de.hybris.platform.cronjob.enums.CronJobStatus;
import de.hybris.platform.cronjob.model.CronJobModel;
import de.hybris.platform.servicelayer.cronjob.AbstractJobPerformable;
import de.hybris.platform.servicelayer.cronjob.PerformResult;

import java.util.List;

import javax.annotation.Resource;

import org.apache.log4j.Logger;

import com.epam.cme.core.model.OrganizationModel;
import com.epam.cme.services.mail.MailService;
import com.epam.cme.services.organization.OrganizationService;

public class SendCustomersListJob extends AbstractJobPerformable<CronJobModel> {

    private static final Logger LOG = Logger.getLogger(SendCustomersListJob.class);

    @Resource
    private OrganizationService organizationService;
    @Resource
    private MailService mailService;

    @Override
    public PerformResult perform(final CronJobModel cronJob) {
        LOG.info("Sending customer list mails");
        final List<OrganizationModel> organizations = organizationService.getOrganizations();

        if (organizations.isEmpty()) {
            LOG.info("No organizations are defined, skipping send of mails");
            return new PerformResult(CronJobResult.SUCCESS, CronJobStatus.FINISHED);
        }

        for (final OrganizationModel organization : organizations) {
            if (!organization.getCustomers().isEmpty()) {
                mailService.sendCustomersListMail(organization);
            }
        }
        return new PerformResult(CronJobResult.SUCCESS, CronJobStatus.FINISHED);
    }

}

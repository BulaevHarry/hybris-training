package com.epam.cme.services.cronjobs;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import de.hybris.platform.cronjob.model.CronJobModel;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.runners.MockitoJUnitRunner;

import com.epam.cme.core.model.BlockableCustomerModel;
import com.epam.cme.core.model.OrganizationModel;
import com.epam.cme.services.mail.MailService;
import com.epam.cme.services.organization.OrganizationService;

@RunWith(MockitoJUnitRunner.class)
public class SendCustomersListJobUnitTest {

    private final List<OrganizationModel> organizations = new ArrayList<>();
    private final OrganizationModel orgModel1 = new OrganizationModel();
    private final OrganizationModel orgModel2 = new OrganizationModel();

    @Mock
    private OrganizationService organizationService;
    @Mock
    private MailService mailService;

    @InjectMocks
    SendCustomersListJob job = new SendCustomersListJob();

    @Before
    public void init() {
        MockitoAnnotations.initMocks(this);
        final List<BlockableCustomerModel> customers = new ArrayList<>();
        customers.add(new BlockableCustomerModel());
        orgModel1.setCustomers(customers);
        orgModel2.setCustomers(new ArrayList<BlockableCustomerModel>());
        when(organizationService.getOrganizations()).thenReturn(organizations);
    }

    @Test
    public void testPerformOrganizationWithCustomersMailServiceSendInvoked() {
        organizations.add(orgModel1);
        job.perform(mock(CronJobModel.class));
        verify(mailService).sendCustomersListMail(any(OrganizationModel.class));
    }

    @Test
    public void testPerformOrganizationWithoutCustomersMailServiceSendNotInvoked() {
        organizations.add(orgModel2);
        job.perform(mock(CronJobModel.class));
        verify(mailService, never()).sendCustomersListMail(any(OrganizationModel.class));
    }

}

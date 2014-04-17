package com.epam.cme.services.organization.impl;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.servicelayer.ServicelayerTransactionalTest;
import de.hybris.platform.servicelayer.exceptions.UnknownIdentifierException;

import javax.annotation.Resource;

import org.junit.Test;

import com.epam.cme.services.organization.OrganizationService;

@IntegrationTest
public class DefaultOrganizationServiceIntegrationTest extends ServicelayerTransactionalTest {

    @Resource
    private OrganizationService organizationService;

    @Test(expected = UnknownIdentifierException.class)
    public void testGetOrganizationByIdNotExistingOrganization() {
        organizationService.getOrganizationById(Integer.valueOf(1));
    }

}

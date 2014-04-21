package com.epam.cme.services.organization.impl;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.servicelayer.ServicelayerTransactionalTest;
import de.hybris.platform.servicelayer.exceptions.UnknownIdentifierException;

import javax.annotation.Resource;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import com.epam.cme.services.organization.OrganizationService;

@IntegrationTest
public class DefaultOrganizationServiceIntegrationTest extends ServicelayerTransactionalTest {

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Resource
    private OrganizationService organizationService;

    @Test
    public void testGetOrganizationByIdNotExistingOrganization() {
        thrown.expect(UnknownIdentifierException.class);
        organizationService.getOrganizationById(Integer.valueOf(1));
    }

}

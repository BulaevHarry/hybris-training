package com.epam.cme.facades.organization.impl;

import static org.junit.Assert.assertEquals;

import de.hybris.platform.servicelayer.ServicelayerTransactionalTest;
import de.hybris.platform.servicelayer.model.ModelService;

import java.util.List;

import javax.annotation.Resource;

import org.junit.Before;
import org.junit.Test;

import com.epam.cme.core.model.OrganizationModel;
import com.epam.cme.facades.organization.OrganizationFacade;
import com.epam.cme.facades.organization.data.OrganizationData;

public class DefaultOrganizationFacadeIntegrationTest extends ServicelayerTransactionalTest {

    @Resource
    private OrganizationFacade organizationFacade;

    @Resource
    private ModelService modelService;

    private final OrganizationModel organizationModel = new OrganizationModel();
    private static final Integer id = new Integer(1);
    private static final String name = "Apple";
    private static final String phone = "111-222-333";

    @Before
    public void setUp() {
        organizationModel.setId(id);
        organizationModel.setName(name);
        organizationModel.setPhone(phone);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testNullParameter() {
        organizationFacade.getOrganizationById(null);
    }

    @Test
    public void testGetOrganizationsCorrectReturn() {
        List<OrganizationData> organizations = organizationFacade.getOrganizations();
        final int size = organizations.size();
        modelService.save(organizationModel);
        organizations = organizationFacade.getOrganizations();
        assertEquals(size + 1, organizations.size());
        final OrganizationData data = organizations.get(0);
        assertEquals(id, data.getCode());
        assertEquals(name, data.getName());
        assertEquals(phone, data.getPhone());
    }

    @Test
    public void testGetOrganizationByIdCorrectReturn() {
        modelService.save(organizationModel);
        final OrganizationData data = organizationFacade.getOrganizationById(id);
        assertEquals(id, data.getCode());
        assertEquals(name, data.getName());
        assertEquals(phone, data.getPhone());
    }

}

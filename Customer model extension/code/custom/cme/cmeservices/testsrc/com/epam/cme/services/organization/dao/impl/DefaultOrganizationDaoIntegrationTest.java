/**
 * 
 */
package com.epam.cme.services.organization.dao.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import de.hybris.platform.servicelayer.ServicelayerTransactionalTest;
import de.hybris.platform.servicelayer.model.ModelService;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.junit.Before;
import org.junit.Test;

import com.epam.cme.core.model.OrganizationModel;
import com.epam.cme.services.organization.dao.OrganizationDao;

public class DefaultOrganizationDaoIntegrationTest extends ServicelayerTransactionalTest {

    @Resource
    private OrganizationDao organizationDao;

    @Resource
    private ModelService modelService;

    private final OrganizationModel organizationModel1 = new OrganizationModel();
    private final OrganizationModel organizationModel2 = new OrganizationModel();
    private static final Integer organizationModel1Id = Integer.valueOf(1);
    private static final Integer organizationModel2Id = Integer.valueOf(2);
    private static final String organizationModel1Name = "Apple";
    private static final String organizationModel2Name = "Samsung";
    private static final String organizationModel1Phone = "111-222-333";
    private static final String organizationModel2Phone = "222-333-444";
    private static final String organizationModel1Email = "apple@a.a";
    private static final String organizationModel2Email = "samsung@s.s";

    @Before
    public void setUp() {
        organizationModel1.setId(organizationModel1Id);
        organizationModel2.setId(organizationModel2Id);
        organizationModel1.setName(organizationModel1Name);
        organizationModel2.setName(organizationModel2Name);
        organizationModel1.setPhone(organizationModel1Phone);
        organizationModel2.setPhone(organizationModel2Phone);
        organizationModel1.setEmail(organizationModel1Email);
        organizationModel2.setEmail(organizationModel2Email);
        modelService.save(organizationModel1);
    }

    @Test
    public void testFindOrganizationsReturnCorrectOrganizationCount() {
        List<OrganizationModel> organizations = organizationDao.findOrganizations();
        final int size = organizations.size();
        modelService.save(organizationModel2);
        organizations = organizationDao.findOrganizations();
        assertEquals(size + 1, organizations.size());
    }

    @Test
    public void testFindOrganizationByIdNotExistingIdEmptyResult() {
        final List<OrganizationModel> organizations = organizationDao.findOrganizationById(organizationModel2Id);
        assertTrue(organizations.isEmpty());
    }

    @Test
    public void testFindOrganizationByIdCorrectObjectReturned() {
        final OrganizationModel organization = organizationDao.findOrganizationById(organizationModel1Id).get(0);
        assertEquals(organizationModel1Id, organization.getId());
        assertEquals(organizationModel1Name, organization.getName());
        assertEquals(organizationModel1Phone, organization.getPhone());
        assertEquals(organizationModel1Email, organization.getEmail());
    }

    @Test
    public void testFindOrganizationsByIdsReturnCorrectOrganizationCount() {
        modelService.save(organizationModel2);
        final List<Integer> organizationsIds = new ArrayList<>();
        organizationsIds.add(organizationModel1Id);
        List<OrganizationModel> organizations = organizationDao.findOrganizationsByIds(organizationsIds);
        final int size = organizations.size();
        organizationsIds.add(organizationModel2Id);
        organizations = organizationDao.findOrganizationsByIds(organizationsIds);
        assertEquals(size + 1, organizations.size());
    }

}

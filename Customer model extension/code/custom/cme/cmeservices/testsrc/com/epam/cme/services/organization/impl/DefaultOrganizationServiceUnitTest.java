/**
 * 
 */
package com.epam.cme.services.organization.impl;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import com.epam.cme.core.model.OrganizationModel;
import com.epam.cme.services.organization.dao.OrganizationDao;

@UnitTest
public class DefaultOrganizationServiceUnitTest {

    private DefaultOrganizationService organizationService;
    private OrganizationDao organizationDao;
    private OrganizationModel organizationModel;
    private static final Integer id = new Integer(1);
    private static final String name = "Apple";
    private static final String phone = "111-222-333";
    private static final String email = "apple@a.a";

    @Before
    public void setUp() {
        organizationService = new DefaultOrganizationService();
        organizationDao = mock(OrganizationDao.class);
        organizationService.setOrganizationDao(organizationDao);
        organizationModel = new OrganizationModel();
        organizationModel.setName(name);
        organizationModel.setPhone(phone);
        organizationModel.setEmail(email);
    }

    @Test
    public void testGetOrganizationsCorrectReturn() {
        final List<OrganizationModel> organizationModels = Arrays.asList(organizationModel);
        when(organizationDao.findOrganizations()).thenReturn(organizationModels);
        organizationService.setOrganizationDao(organizationDao);
        final List<OrganizationModel> result = organizationService.getOrganizations();
        assertEquals(1, result.size());
        assertEquals(organizationModel, result.get(0));
    }

    @Test
    public void testGetOrganizationsByIdsCorrectReturn() {
        final List<OrganizationModel> organizationModels = Arrays.asList(organizationModel);
        final List<Integer> ids = Arrays.asList(id);
        when(organizationDao.findOrganizationsByIds(ids)).thenReturn(organizationModels);
        organizationService.setOrganizationDao(organizationDao);
        final List<OrganizationModel> result = organizationService.getOrganizationsByIds(ids);
        assertEquals(1, result.size());
        assertEquals(organizationModel, result.get(0));
    }

    @Test
    public void testGetOrganizationByIdCorrectReturn() {
        when(organizationDao.findOrganizationById(id)).thenReturn(Collections.singletonList(organizationModel));
        final OrganizationModel result = organizationService.getOrganizationById(id);
        assertEquals(organizationModel, result);
    }

}

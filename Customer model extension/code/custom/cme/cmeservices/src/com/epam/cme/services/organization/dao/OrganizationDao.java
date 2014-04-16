/**
 * 
 */
package com.epam.cme.services.organization.dao;

import java.util.List;

import com.epam.cme.core.model.OrganizationModel;


public interface OrganizationDao
{

	List<OrganizationModel> findOrganizations();

	List<OrganizationModel> findOrganizationById(Integer id);

	List<OrganizationModel> findOrganizationsByIds(List<Integer> ids);

}

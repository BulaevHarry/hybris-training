package com.epam.cme.facades.organization;

import java.util.List;

import com.epam.cme.facades.organization.data.OrganizationData;


public interface OrganizationFacade
{

	List<OrganizationData> getOrganizations();

	OrganizationData getOrganizationById(Integer id);

}

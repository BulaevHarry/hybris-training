/**
 * 
 */
package com.epam.cme.services.organization;

import java.util.List;

import com.epam.cme.core.model.OrganizationModel;

public interface OrganizationService {

    List<OrganizationModel> getOrganizations();

    OrganizationModel getOrganizationById(Integer id);

    List<OrganizationModel> getOrganizationsByIds(List<Integer> ids);

}

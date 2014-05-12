/**
 * 
 */
package com.epam.cme.services.organization.dao.impl;

import de.hybris.platform.servicelayer.search.FlexibleSearchQuery;
import de.hybris.platform.servicelayer.search.FlexibleSearchService;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.epam.cme.core.model.OrganizationModel;
import com.epam.cme.services.organization.dao.OrganizationDao;

@Component("organizationDao")
public class DefaultOrganizationDao implements OrganizationDao {

    @Autowired
    private FlexibleSearchService flexibleSearchService;

    private static final String FIND_ORGANIZATIONS_QUERY = "SELECT {p:" + OrganizationModel.PK + "} " + "FROM {"
            + OrganizationModel._TYPECODE + " AS p} ";
    private static final String FIND_ORGANIZATION_BY_ID_QUERY = "SELECT {p:" + OrganizationModel.PK + "} " + "FROM {"
            + OrganizationModel._TYPECODE + " AS p} " + "WHERE " + "{p:" + OrganizationModel.ID + "}=?id ";
    private static final String FIND_ORGANIZATIONS_BY_IDS_QUERY = "SELECT {p:" + OrganizationModel.PK + "} " + "FROM {"
            + OrganizationModel._TYPECODE + " AS p} " + "WHERE " + "{p:" + OrganizationModel.ID + "} IN ";

    @Override
    public List<OrganizationModel> findOrganizations() {
        final FlexibleSearchQuery query = new FlexibleSearchQuery(FIND_ORGANIZATIONS_QUERY);
        return flexibleSearchService.<OrganizationModel>search(query).getResult();
    }

    @Override
    public List<OrganizationModel> findOrganizationById(final Integer id) {
        final FlexibleSearchQuery query = new FlexibleSearchQuery(FIND_ORGANIZATION_BY_ID_QUERY);
        query.addQueryParameter("id", id);
        return flexibleSearchService.<OrganizationModel>search(query).getResult();
    }

    @Override
    public List<OrganizationModel> findOrganizationsByIds(final List<Integer> ids) {
        String queryString = FIND_ORGANIZATIONS_BY_IDS_QUERY;
        String idsCommaSep = ids.toString();
        idsCommaSep = idsCommaSep.substring(1, idsCommaSep.length() - 1);
        queryString += "(" + idsCommaSep + ")";
        final FlexibleSearchQuery query = new FlexibleSearchQuery(queryString);
        return flexibleSearchService.<OrganizationModel>search(query).getResult();
    }

}

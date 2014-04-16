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


@Component(value = "organizationDao")
public class DefaultOrganizationDao implements OrganizationDao
{

	@Autowired
	private FlexibleSearchService flexibleSearchService;

	@Override
	public List<OrganizationModel> findOrganizations()
	{
		final String queryString = "SELECT {p:" + OrganizationModel.PK + "} " + "FROM {" + OrganizationModel._TYPECODE + " AS p} ";
		final FlexibleSearchQuery query = new FlexibleSearchQuery(queryString);
		return flexibleSearchService.<OrganizationModel> search(query).getResult();
	}

	@Override
	public List<OrganizationModel> findOrganizationById(final Integer id)
	{
		final String queryString = "SELECT {p:" + OrganizationModel.PK + "} " + "FROM {" + OrganizationModel._TYPECODE + " AS p} "
				+ "WHERE " + "{p:" + OrganizationModel.ID + "}=?id ";
		final FlexibleSearchQuery query = new FlexibleSearchQuery(queryString);
		query.addQueryParameter("id", id);
		return flexibleSearchService.<OrganizationModel> search(query).getResult();
	}

	@Override
	public List<OrganizationModel> findOrganizationsByIds(final List<Integer> ids)
	{
		String queryString = "SELECT {p:" + OrganizationModel.PK + "} " + "FROM {" + OrganizationModel._TYPECODE + " AS p} "
				+ "WHERE " + "{p:" + OrganizationModel.ID + "} IN ";
		String idsCommaSep = ids.toString();
		idsCommaSep = idsCommaSep.substring(1, idsCommaSep.length() - 1);
		queryString += "(" + idsCommaSep + ")";
		final FlexibleSearchQuery query = new FlexibleSearchQuery(queryString);
		return flexibleSearchService.<OrganizationModel> search(query).getResult();
	}

}

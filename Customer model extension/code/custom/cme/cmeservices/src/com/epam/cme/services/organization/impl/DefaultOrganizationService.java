package com.epam.cme.services.organization.impl;

import de.hybris.platform.servicelayer.exceptions.AmbiguousIdentifierException;
import de.hybris.platform.servicelayer.exceptions.UnknownIdentifierException;

import java.util.List;

import org.springframework.beans.factory.annotation.Required;

import com.epam.cme.core.model.OrganizationModel;
import com.epam.cme.services.organization.OrganizationService;
import com.epam.cme.services.organization.dao.OrganizationDao;


public class DefaultOrganizationService implements OrganizationService
{

	private OrganizationDao organizationDao;

	@Override
	public List<OrganizationModel> getOrganizations()
	{
		return organizationDao.findOrganizations();
	}

	@Override
	public OrganizationModel getOrganizationById(final Integer id)
	{
		final List<OrganizationModel> result = organizationDao.findOrganizationById(id);
		if (result.isEmpty())
		{
			throw new UnknownIdentifierException("Organization with code '" + id + "' not found!");
		}
		else if (result.size() > 1)
		{
			throw new AmbiguousIdentifierException("Organization code '" + id + "' is not unique, " + result.size()
					+ " Organizations found!");
		}
		return result.get(0);
	}

	@Override
	public List<OrganizationModel> getOrganizationsByIds(final List<Integer> ids)
	{
		return organizationDao.findOrganizationsByIds(ids);
	}

	@Required
	public void setOrganizationDao(final OrganizationDao organizationDao)
	{
		this.organizationDao = organizationDao;
	}

}

/*
 * [y] hybris Platform
 *
 * Copyright (c) 2000-2014 hybris AG
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of hybris
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with hybris.
 *
 *
 */
package com.epam.cme.facades.blockablecustomer.impl;

import static de.hybris.platform.servicelayer.util.ServicesUtil.validateParameterNotNullStandardMessage;

import de.hybris.platform.commercefacades.customer.impl.DefaultCustomerFacade;
import de.hybris.platform.commerceservices.customer.DuplicateUidException;
import de.hybris.platform.core.model.user.TitleModel;
import de.hybris.platform.servicelayer.exceptions.UnknownIdentifierException;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.util.Assert;

import com.epam.cme.core.model.BlockableCustomerModel;
import com.epam.cme.facades.blockablecustomer.BlockableCustomerFacade;
import com.epam.cme.facades.user.data.CmeRegisterData;
import com.epam.cme.services.organization.OrganizationService;


public class DefaultBlockableCustomerFacade extends DefaultCustomerFacade implements BlockableCustomerFacade
{

	private OrganizationService organizationService;

	@Override
	public void register(final CmeRegisterData cmeRegisterData) throws DuplicateUidException, UnknownIdentifierException,
			IllegalArgumentException
	{
		validateParameterNotNullStandardMessage("cmeRegisterData", cmeRegisterData);
		Assert.hasText(cmeRegisterData.getFirstName(), "The field [FirstName] cannot be empty");
		Assert.hasText(cmeRegisterData.getLastName(), "The field [LastName] cannot be empty");
		Assert.hasText(cmeRegisterData.getLogin(), "The field [Login] cannot be empty");
		Assert.notNull(cmeRegisterData.getOrganizationsIds(), "The field [Organizations] cannot be empty");
		final BlockableCustomerModel newCustomer = getModelService().create(BlockableCustomerModel.class);
		newCustomer.setName(getCustomerNameStrategy().getName(cmeRegisterData.getFirstName(), cmeRegisterData.getLastName()));
		newCustomer.setOrganizations(organizationService.getOrganizationsByIds(cmeRegisterData.getOrganizationsIds()));
		if (StringUtils.isNotBlank(cmeRegisterData.getFirstName()) && StringUtils.isNotBlank(cmeRegisterData.getLastName()))
		{
			newCustomer.setName(getCustomerNameStrategy().getName(cmeRegisterData.getFirstName(), cmeRegisterData.getLastName()));
		}
		final TitleModel title = getUserService().getTitleForCode(cmeRegisterData.getTitleCode());
		newCustomer.setTitle(title);
		setUidForRegister(cmeRegisterData, newCustomer);
		newCustomer.setSessionLanguage(getCommonI18NService().getCurrentLanguage());
		newCustomer.setSessionCurrency(getCommonI18NService().getCurrentCurrency());
		getCustomerAccountService().register(newCustomer, cmeRegisterData.getPassword());
	}

	@Required
	public void setOrganizationService(final OrganizationService organizationService)
	{
		this.organizationService = organizationService;
	}

}

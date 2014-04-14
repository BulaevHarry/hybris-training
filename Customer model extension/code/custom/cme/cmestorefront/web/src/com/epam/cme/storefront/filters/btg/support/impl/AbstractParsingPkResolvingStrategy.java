/*
 * [y] hybris Platform
 *
 * Copyright (c) 2000-2013 hybris AG
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of hybris
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with hybris.
 * 
 *  
 */
package com.epam.cme.storefront.filters.btg.support.impl;

import com.epam.cme.storefront.filters.btg.support.PkResolvingStrategy;
import com.epam.cme.storefront.filters.btg.support.UrlParsingStrategy;
import de.hybris.platform.core.model.ItemModel;
import de.hybris.platform.servicelayer.exceptions.AmbiguousIdentifierException;
import de.hybris.platform.servicelayer.exceptions.SystemException;
import de.hybris.platform.servicelayer.exceptions.UnknownIdentifierException;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;


/**
 * Abstract Implementation of {@link PkResolvingStrategy} that retrieves a product pk from the request
 */
public abstract class AbstractParsingPkResolvingStrategy implements PkResolvingStrategy
{
	private static final Logger LOG = Logger.getLogger(AbstractParsingPkResolvingStrategy.class);
	private UrlParsingStrategy urlParsingStrategy;

	/**
	 * @param urlParsingStrategy
	 *           the urlParsingStrategy to set
	 */
	public void setUrlParsingStrategy(final UrlParsingStrategy urlParsingStrategy)
	{
		this.urlParsingStrategy = urlParsingStrategy;
	}

	@Override
	public String resolvePrimaryKey(final HttpServletRequest request)
	{
		String result = null;
		final String key = urlParsingStrategy.parse(request);
		if (!StringUtils.isBlank(key))
		{
			try
			{
				final ItemModel model = retrieveModel(key);
				result = model.getPk().getLongValueAsString();
			}
			catch (final SystemException e)
			{
				LOG.warn("Could not retrieve category for " + key + ": " + e.toString());
			}
		}
		return result;
	}

	/**
	 * Retrieves the model searching by key
	 * 
	 * @param key the key
	 * @return model
	 * @throws UnknownIdentifierException
	 *            if the model could not be found
	 * @throws AmbiguousIdentifierException
	 *            if the model cannot be uniquely identified
	 */
	protected abstract ItemModel retrieveModel(String key);
}

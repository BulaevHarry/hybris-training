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
package com.epam.cme.facades.converters.populator;

import de.hybris.platform.configurablebundleservices.model.BundleTemplateModel;
import de.hybris.platform.converters.Populator;
import com.epam.cme.facades.data.BundleTemplateData;

import org.apache.commons.collections.CollectionUtils;


/**
 * 
 * Populator implementation for {@link BundleTemplateModel} as source and {@link BundleTemplateData} as target type.
 */

public class BundleTemplatePopulator<SOURCE extends BundleTemplateModel, TARGET extends BundleTemplateData> implements
		Populator<SOURCE, TARGET>
{

	@Override
	public void populate(final SOURCE source, final TARGET target)
	{
		target.setId(source.getId());
		target.setName(source.getName());
		target.setVersion(source.getVersion());

		if (CollectionUtils.isNotEmpty(source.getProducts()))
		{
			target.setType(source.getProducts().iterator().next().getClass().getSimpleName());
		}
	}
}

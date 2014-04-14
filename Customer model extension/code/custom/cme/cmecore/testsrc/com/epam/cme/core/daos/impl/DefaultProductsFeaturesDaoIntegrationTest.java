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
package com.epam.cme.core.daos.impl;

import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.jalo.CoreBasicDataCreator;
import de.hybris.platform.servicelayer.ServicelayerTest;
import com.epam.cme.core.daos.ProductsFeaturesDao;

import java.util.Collections;
import java.util.List;

import javax.annotation.Resource;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;

import org.apache.log4j.Logger;


/**
 * Integration test to Data access Object DefaultProductsFeaturesDao
 */
public class DefaultProductsFeaturesDaoIntegrationTest extends ServicelayerTest
{
	private static final Logger LOG = Logger.getLogger(DefaultProductsFeaturesDaoIntegrationTest.class);

	@Resource
	private ProductsFeaturesDao productsFeaturesDao;


	@Before
	public void setUp() throws Exception
	{
		new CoreBasicDataCreator().createEssentialData(Collections.EMPTY_MAP, null);
		importCsv("/test/impex/test_feature-compatibility.impex", "utf-8");
		LOG.info("Finsihed data setup");
	}

	@Test
	public void testVendorCompatibleCodeProducts()
	{
		final List<ProductModel> vendorCompatibleProducts = productsFeaturesDao.findAccessoriesByVendorCompatibility("Apple",
				"accessoryclassification", "vendorcompatibility", "Accessory");
		Assert.assertEquals(1, vendorCompatibleProducts.size());
	}
}

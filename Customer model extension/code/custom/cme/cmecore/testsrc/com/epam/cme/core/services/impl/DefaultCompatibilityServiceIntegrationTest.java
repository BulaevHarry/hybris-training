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
package com.epam.cme.core.services.impl;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.catalog.model.classification.ClassAttributeAssignmentModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.core.model.type.ComposedTypeModel;
import de.hybris.platform.jalo.CoreBasicDataCreator;
import de.hybris.platform.servicelayer.ServicelayerTest;
import de.hybris.platform.servicelayer.search.FlexibleSearchQuery;
import de.hybris.platform.servicelayer.search.FlexibleSearchService;
import com.epam.cme.core.services.CompatibilityService;

import java.util.Collections;
import java.util.List;
import java.util.Set;

import javax.annotation.Resource;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Transformer;
import org.apache.log4j.Logger;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.google.common.collect.Sets;


/**
 * Integration test for feature and vendor compatibility service
 */
@IntegrationTest
public class DefaultCompatibilityServiceIntegrationTest extends ServicelayerTest
{

	private static final Logger LOG = Logger.getLogger(DefaultCompatibilityServiceIntegrationTest.class);
	@Resource
	private CompatibilityService compatibilityService;

	@Resource
	private FlexibleSearchService flexibleSearchService;

	private ComposedTypeModel targetItemType;

	@Before
	public void setUp() throws Exception
	{
		new CoreBasicDataCreator().createEssentialData(Collections.EMPTY_MAP, null);
		importCsv("/test/impex/test_feature-compatibility.impex", "utf-8");
		LOG.info("Finsihed data setup");
	}

	/**
	 * should return only those devices that match 1 or more featurecompatibility
	 */
	@Test
	public void testFindFeatureCompatibleDevice()
	{
		targetItemType = new ComposedTypeModel();
		targetItemType.setCode("Device");
		final List<ProductModel> fcDevices = compatibilityService.getFeatureCompatibleProducts("109059",
				getClassAttributeAssignment(), targetItemType);
		this.assertCompatibileListIsCorrect(fcDevices, Sets.newHashSet("109058", "009058", "Device2", "Device3"));
	}

	/**
	 * Should return accessories that are only vendor compatible and not featurecompatible
	 */
	@Test
	public void testFindVendorCompatibleAccessories()
	{
		final ComposedTypeModel searchType = new ComposedTypeModel();
		searchType.setCode("Accessory");
		targetItemType = flexibleSearchService.getModelsByExample(searchType).get(0);

		final List<ProductModel> vcAccessories = compatibilityService.getAccessoriesForVendorCompatibility("109058", "Accessory");
		this.assertCompatibileListIsCorrect(vcAccessories, Sets.newHashSet("109060"));
	}

	/**
	 * There are 3 accessories and 199919 should not have any compatible devices
	 */
	@Test
	public void testShouldNotFindIncompatibleFeatureAccessories()
	{
		targetItemType = new ComposedTypeModel();
		targetItemType.setCode("Device");
		final List<ProductModel> featureCompatibleDevices = compatibilityService.getFeatureCompatibleProducts("199919",
				getClassAttributeAssignment(), targetItemType);

		Assert.assertTrue(featureCompatibleDevices.isEmpty());
		final Set<String> emptySet = Sets.newHashSet();
		this.assertCompatibileListIsCorrect(featureCompatibleDevices, emptySet);
	}

	/**
	 * 0090508 has Samsung as Vendor and accessories have Apple as Vendor
	 */
	@Test
	public void testShouldFindFeatureCompatibleAccessoriesOfDiffVendor()
	{
		final ComposedTypeModel searchType = new ComposedTypeModel();
		searchType.setCode("Accessory");
		targetItemType = flexibleSearchService.getModelsByExample(searchType).get(0);

		final List<ProductModel> fcAccessories = compatibilityService.getFeatureCompatibleProducts("009058",
				getClassAttributeAssignment(), targetItemType);

		// Set<String> expectedCodes = Sets.newHashSet<()
		assertCompatibileListIsCorrect(fcAccessories, Sets.newHashSet("109059", "Accessory1", "Accessory4", "Accessory5"));

	}

	private ClassAttributeAssignmentModel getClassAttributeAssignment()
	{

		final String queryStr = "select {caa:pk} from {ClassAttributeAssignment as caa JOIN ClassificationAttribute as ca on {ca:pk} = {caa:classificationattribute}}"
				+ " where {ca:code} = 'featurecompatibility'";

		final FlexibleSearchQuery query = new FlexibleSearchQuery(queryStr);

		final ClassAttributeAssignmentModel classificationAttributeAssignment = flexibleSearchService
				.<ClassAttributeAssignmentModel> searchUnique(query);
		return classificationAttributeAssignment;
	}

	protected void assertCompatibileListIsCorrect(final List<? extends ProductModel> returnedProducts,
			final Set<String> expectedCodes)
	{
		Assert.assertEquals(expectedCodes.size(), returnedProducts.size());
		final List<String> returnedCodes = (List<String>) CollectionUtils.collect(returnedProducts, new Transformer()
		{

			@Override
			public Object transform(final Object product)
			{
				final String productCode = ((ProductModel) product).getCode();
				return productCode;
			}

		});

		Assert.assertTrue(CollectionUtils.isEqualCollection(expectedCodes, returnedCodes));
	}
}

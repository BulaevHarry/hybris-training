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
package com.epam.cme.core.services;

import de.hybris.platform.catalog.model.classification.ClassAttributeAssignmentModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.core.model.type.ComposedTypeModel;

import java.util.List;


/**
 * Service which returns products based on feature compatibility and vendor compatibility
 */
public interface CompatibilityService
{

	/**
	 * Return list of compatible products based on featureCompatiblity classification feature
	 * 
	 * @param code
	 *           product Code
	 * @param classAttributeAssignment
	 * @param targetItemType
	 *           the itemType that is to be searched
	 * @return list of matching products
	 */
	List<ProductModel> getFeatureCompatibleProducts(String code, ClassAttributeAssignmentModel classAttributeAssignment,
			ComposedTypeModel targetItemType);

	/**
	 * 
	 * Return list of compatible products based where product's manufacturer name = compatible products vendor
	 * compatibility classification feature
	 * 
	 * @param code
	 *           product Code
	 * @param productTypeCode
	 *           type of the searched products (e.g. Accessory)
	 * @return list of matching products
	 */
	List<ProductModel> getAccessoriesForVendorCompatibility(String code, final String productTypeCode);

}

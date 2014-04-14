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
package com.epam.cme.core.daos;

import de.hybris.platform.core.model.product.ProductModel;

import java.util.List;


/**
 * Data Access Object for looking up items related to {@link ProductModel}
 * 
 * @spring.bean productsFeaturesDao
 */
public interface ProductsFeaturesDao
{
	/**
	 * Returns list of vendor compatible accessories based on products manufacturer name and vendorcompatibility code
	 * 
	 * @param manufacturerName
	 * @param classificationClassCode
	 * @param classificationAttributeCode
	 * @param productTypeCode
	 *           type of the searched products (e.g. Accessory)
	 * @throws IllegalArgumentException
	 *            if targetItemType is of an illegal type
	 * @return {@link List} of {@link ProductModel}s or empty {@link List}.
	 */
	List<ProductModel> findAccessoriesByVendorCompatibility(final String manufacturerName, final String classificationClassCode,
			final String classificationAttributeCode, final String productTypeCode);
}

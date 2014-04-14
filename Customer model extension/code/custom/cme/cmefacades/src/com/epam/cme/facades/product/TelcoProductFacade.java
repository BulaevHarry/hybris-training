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
package com.epam.cme.facades.product;

import de.hybris.platform.catalog.enums.ProductReferenceTypeEnum;
import de.hybris.platform.catalog.model.classification.ClassAttributeAssignmentModel;
import de.hybris.platform.commercefacades.product.ProductFacade;
import de.hybris.platform.commercefacades.product.ProductOption;
import de.hybris.platform.commercefacades.product.data.ProductData;
import de.hybris.platform.core.model.type.ComposedTypeModel;

import java.util.List;


/**
 * Telco product facade that provides functionality to return products that a) are 1 to 1 product references and feature
 * compatible products b) are 1 to 1 product references and feature and vendor compatible products
 */
public interface TelcoProductFacade extends ProductFacade
{

	/**
	 * Get unique list of product references and feature compatible products with a max limit set by maximunNumber Of
	 * Products First list of product references is returned and then feature compatible products are returned
	 * 
	 * @param code
	 * @param referenceTypes
	 *           {@link List} of {@link ProductReferenceTypeEnum}s
	 * @param options
	 *           {@link List} of {@link ProductOption}s
	 * @param limit
	 * @param classAttributeAssignment
	 * @param targetItemType
	 *           item type that is expected back in service
	 * @return {@link List} of {@link ProductData}
	 */
	List<ProductData> getProductReferencesAndFeatureCompatibleProductsForCode(final String code,
			List<ProductReferenceTypeEnum> referenceTypes, final List<ProductOption> options, final Integer limit,
			final ClassAttributeAssignmentModel classAttributeAssignment, final ComposedTypeModel targetItemType);

	/**
	 * Get unique list of product references ,feature compatible and vendor compatible products with a max limit set by
	 * maximunNumber Of Products First list of product references is returned and then feature compatible products and
	 * then vendor compatible products are returned
	 * 
	 * @param code
	 * @param referenceTypes
	 *           {@link List} of {@link ProductReferenceTypeEnum}s
	 * @param options
	 *           {@link List} of {@link ProductOption}s
	 * @param limit
	 * @param classAttributeAssigment
	 * @param targetItemType
	 *           item type that is expected back in service
	 * @return {@link List} of {@link ProductData}
	 */
	List<ProductData> getProductReferencesAndFeatureCompatibleAndVendorCompatibleProductsForCode(final String code,
			List<ProductReferenceTypeEnum> referenceTypes, final List<ProductOption> options, final Integer limit,
			final ClassAttributeAssignmentModel classAttributeAssigment, final ComposedTypeModel targetItemType);

}

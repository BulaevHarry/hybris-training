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

import de.hybris.platform.catalog.jalo.classification.ClassAttributeAssignment;
import de.hybris.platform.catalog.jalo.classification.ClassificationAttributeValue;
import de.hybris.platform.catalog.jalo.classification.util.FeatureValueCondition;
import de.hybris.platform.catalog.model.classification.ClassAttributeAssignmentModel;
import de.hybris.platform.catalog.model.classification.ClassificationAttributeValueModel;
import de.hybris.platform.classification.ClassificationService;
import de.hybris.platform.classification.features.Feature;
import de.hybris.platform.classification.features.FeatureValue;
import de.hybris.platform.core.GenericQuery;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.core.model.type.ComposedTypeModel;
import de.hybris.platform.genericsearch.GenericSearchService;
import de.hybris.platform.product.ProductService;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.search.SearchResult;
import com.epam.cme.core.daos.ProductsFeaturesDao;
import com.epam.cme.core.services.CompatibilityService;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Required;


/**
 * Default implementation of Compatibility Service {@link CompatibilityService}
 */
public class DefaultCompatibilityService implements CompatibilityService
{
	private ClassificationService classificationService;

	private ProductService productService;

	private GenericSearchService genericSearchService;

	private String classificationAttributeCode;
	private String classificationClassCode;

	private ModelService modelService;

	private ProductsFeaturesDao productsFeaturesDao;

	@Override
	public List<ProductModel> getFeatureCompatibleProducts(final String code,
			final ClassAttributeAssignmentModel classificationAttributeAssignment, final ComposedTypeModel targetItemType)
	{
		final ProductModel product = getProductService().getProductForCode(code);
		final Set<ProductModel> relatedProducts = new HashSet<ProductModel>();

		final Feature modelFeature = getClassificationService().getFeature(product, classificationAttributeAssignment);

		final List<ProductModel> selectedProducts = new ArrayList<ProductModel>();
		if (modelFeature != null)
		{
			relatedProducts.addAll(getProductsWithFeature(modelFeature, targetItemType));
		}
		CollectionUtils.addAll(selectedProducts, relatedProducts.iterator());
		return selectedProducts;
	}

	@Override
	public List<ProductModel> getAccessoriesForVendorCompatibility(final String code, final String productTypeCode)
	{
		final ProductModel product = getProductService().getProductForCode(code);

		if (product.getManufacturerName() != null)
		{
			return getProductsFeaturesDao().findAccessoriesByVendorCompatibility(product.getManufacturerName(),
					getClassificationClassCode(), getClassificationAttributeCode(), productTypeCode);
		}

		return Collections.emptyList();
	}

	/**
	 * Search list of products of a particular item and a particular classification feature value
	 * 
	 * @return list of matching products
	 */
	protected List<ProductModel> getProductsWithFeature(final Feature modelFeature, final ComposedTypeModel targetItemType)
	{
		final List<FeatureValue> featureValues = modelFeature.getValues();
		final List<ProductModel> featureCompatibleProducts = new ArrayList<ProductModel>();

		for (final FeatureValue featureValue : featureValues)
		{
			if (featureValue != null)
			{
				featureCompatibleProducts.addAll(getCompatibleProductsForFeature(featureValue, modelFeature, targetItemType));
			}
		}
		return featureCompatibleProducts;
	}

	/**
	 * Use generic search service to list of products based on feature compatibilty values
	 * 
	 * @param targetItemType
	 *           device
	 * @return collection of matching products
	 */
	protected Collection<ProductModel> getCompatibleProductsForFeature(final FeatureValue featureValue,
			final Feature modelFeature, final ComposedTypeModel targetItemType)
	{
		ClassificationAttributeValue classAttributeValue;
		if (featureValue.getValue() instanceof ClassificationAttributeValueModel)
		{
			classAttributeValue = getModelService().getSource(featureValue.getValue());
		}
		else
		{
			classAttributeValue = (ClassificationAttributeValue) featureValue.getValue();
		}
		final ClassAttributeAssignment classAttributeAssignment = getModelService().getSource(
				modelFeature.getClassAttributeAssignment());

		final SearchResult<ProductModel> prodsResult = getGenericSearchService().search(
				generateQuery(classAttributeAssignment, classAttributeValue, targetItemType.getCode()));

		return prodsResult.getResult();
	}

	/**
	 * Sample code given in hybris wiki https://wiki.hybris.com/display/release4/Classification+Feature+Value+API uses
	 * GenericQuery and jalo classes. This should be replaced by servicelayer code as soon as wiki page us updated.
	 */
	protected GenericQuery generateQuery(final ClassAttributeAssignment classAttributeAssignment,
			final ClassificationAttributeValue classAttributeValue, final String code)
	{
		final GenericQuery query = new GenericQuery(code, FeatureValueCondition.equals(classAttributeAssignment,
				classAttributeValue));
		return query;
	}

	protected ProductsFeaturesDao getProductsFeaturesDao()
	{
		return productsFeaturesDao;
	}

	@Required
	public void setProductsFeaturesDao(final ProductsFeaturesDao productsFeaturesDao)
	{
		this.productsFeaturesDao = productsFeaturesDao;
	}

	protected ModelService getModelService()
	{
		return modelService;
	}

	@Required
	public void setModelService(final ModelService modelService)
	{
		this.modelService = modelService;
	}

	protected GenericSearchService getGenericSearchService()
	{
		return genericSearchService;
	}

	@Required
	public void setGenericSearchService(final GenericSearchService genericSearchService)
	{
		this.genericSearchService = genericSearchService;
	}

	protected ClassificationService getClassificationService()
	{
		return classificationService;
	}

	@Required
	public void setClassificationService(final ClassificationService classificationService)
	{
		this.classificationService = classificationService;
	}

	protected ProductService getProductService()
	{
		return productService;
	}

	@Required
	public void setProductService(final ProductService productService)
	{
		this.productService = productService;
	}

	protected String getClassificationAttributeCode()
	{
		return classificationAttributeCode;
	}

	@Required
	public void setClassificationAttributeCode(final String classificationAttributeCode)
	{
		this.classificationAttributeCode = classificationAttributeCode;
	}

	protected String getClassificationClassCode()
	{
		return classificationClassCode;
	}

	@Required
	public void setClassificationClassCode(final String classificationClassCode)
	{
		this.classificationClassCode = classificationClassCode;
	}
}

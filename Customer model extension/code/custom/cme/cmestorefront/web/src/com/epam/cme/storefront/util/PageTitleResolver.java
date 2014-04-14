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
package com.epam.cme.storefront.util;

import de.hybris.platform.catalog.model.classification.ClassificationClassModel;
import de.hybris.platform.category.model.CategoryModel;
import de.hybris.platform.cms2.model.site.CMSSiteModel;
import de.hybris.platform.cms2.servicelayer.services.CMSSiteService;
import de.hybris.platform.commerceservices.category.CommerceCategoryService;
import de.hybris.platform.commerceservices.search.facetdata.BreadcrumbData;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.product.ProductService;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Required;


/**
 * Resolves page title according to page, search text, current category or product
 */
@Deprecated
class PageTitleResolver
{
	protected final String SEPARATOR = " | ";

	private ProductService productService;
	private CommerceCategoryService commerceCategoryService;
	private CMSSiteService cmsSiteService;

	protected CommerceCategoryService getCommerceCategoryService()
	{
		return commerceCategoryService;
	}

	@Required
	public void setCommerceCategoryService(final CommerceCategoryService commerceCategoryService)
	{
		this.commerceCategoryService = commerceCategoryService;
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

	protected CMSSiteService getCmsSiteService()
	{
		return cmsSiteService;
	}

	@Required
	public void setCmsSiteService(final CMSSiteService cmsSiteService)
	{
		this.cmsSiteService = cmsSiteService;
	}

	public String resolveContentPageTitle(final String title)
	{
		final CMSSiteModel currentSite = getCmsSiteService().getCurrentSite();

		final StringBuilder builder = new StringBuilder();
		if (!StringUtils.isEmpty(title))
		{
			builder.append(title).append(SEPARATOR);
		}
		builder.append(currentSite.getName());
		return StringEscapeUtils.escapeHtml(builder.toString());
	}

	public String resolveHomePageTitle(final String title)
	{
		final CMSSiteModel currentSite = getCmsSiteService().getCurrentSite();
		final StringBuilder builder = new StringBuilder();
		builder.append(currentSite.getName());

		if (!StringUtils.isEmpty(title))
		{
			builder.append(SEPARATOR).append(title);
		}

		return StringEscapeUtils.escapeHtml(builder.toString());
	}

	public <STATE> String resolveSearchPageTitle(final String searchText, final List<BreadcrumbData<STATE>> appliedFacets)
	{
		final CMSSiteModel currentSite = getCmsSiteService().getCurrentSite();

		final StringBuilder builder = new StringBuilder();
		if (!StringUtils.isEmpty(searchText))
		{
			builder.append(searchText).append(SEPARATOR);
		}
		for (final BreadcrumbData pathElement : appliedFacets)
		{
			builder.append(pathElement.getFacetValueName()).append(SEPARATOR);
		}
		builder.append(currentSite.getName());
		return StringEscapeUtils.escapeHtml(builder.toString());
	}

	public String resolveCategoryPageTitle(final CategoryModel category)
	{
		final StringBuilder sb = new StringBuilder();
		final List<CategoryModel> categories = this.getCategoryPath(category);
		for (final CategoryModel c : categories)
		{
			sb.append(c.getName()).append(SEPARATOR);
		}

		final CMSSiteModel currentSite = getCmsSiteService().getCurrentSite();
		if (currentSite != null)
		{
			sb.append(currentSite.getName());
		}

		return StringEscapeUtils.escapeHtml(sb.toString());
	}

	/**
	 * creates page title for given code and facets
	 */
	public <STATE> String resolveCategoryPageTitle(final CategoryModel category, final List<BreadcrumbData<STATE>> appliedFacets)
	{
		final CMSSiteModel currentSite = getCmsSiteService().getCurrentSite();

		final String name = category.getName();
		final StringBuilder builder = new StringBuilder();
		if (CollectionUtils.isEmpty(appliedFacets))
		{
			if (!StringUtils.isEmpty(name))
			{
				builder.append(name).append(SEPARATOR);
			}
			builder.append(currentSite.getName());
		}
		else
		{
			for (final BreadcrumbData pathElement : appliedFacets)
			{
				builder.append(pathElement.getFacetValueName()).append(SEPARATOR);
			}
			builder.append(currentSite.getName());
		}

		return StringEscapeUtils.escapeHtml(builder.toString());
	}

	/**
	 * creates page title for given code and facets
	 */
	public <STATE> String resolveCategoryPageTitle(final String categoryCode, final List<BreadcrumbData<STATE>> appliedFacets)
	{
		final CategoryModel category = getCommerceCategoryService().getCategoryForCode(categoryCode);
		return resolveCategoryPageTitle(category, appliedFacets);
	}

	/**
	 * creates page title for given code
	 */
	public String resolveProductPageTitle(final ProductModel product)
	{
		// Lookup categories
		final List<CategoryModel> path = getCategoryPath(product);
		// Lookup site (or store)
		final CMSSiteModel currentSite = getCmsSiteService().getCurrentSite();

		// Construct page title
		final String identifier = product.getName();
		final String articleNumber = product.getCode();
		final String productName = StringUtils.isEmpty(identifier) ? articleNumber : identifier;
		final StringBuilder builder = new StringBuilder(productName);

		for (final CategoryModel pathElement : path)
		{
			builder.append(SEPARATOR).append(pathElement.getName());
		}

		if (currentSite != null)
		{
			builder.append(SEPARATOR).append(currentSite.getName());
		}

		return StringEscapeUtils.escapeHtml(builder.toString());
	}

	public String resolveProductPageTitle(final String productCode)
	{
		// Lookup the product
		final ProductModel product = getProductService().getProductForCode(productCode);
		return resolveProductPageTitle(product);
	}

	protected List<CategoryModel> getCategoryPath(final ProductModel product)
	{
		final CategoryModel category = getPrimaryCategoryForProduct(product);
		if (category != null)
		{
			return getCategoryPath(category);
		}
		return Collections.emptyList();
	}

	protected List<CategoryModel> getCategoryPath(final CategoryModel category)
	{
		final Collection<List<CategoryModel>> paths = getCommerceCategoryService().getPathsForCategory(category);
		// Return first - there will always be at least 1
		final List<CategoryModel> cat2ret = paths.iterator().next();
		Collections.reverse(cat2ret);
		return cat2ret;
	}

	protected CategoryModel getPrimaryCategoryForProduct(final ProductModel product)
	{
		// Get the first super-category from the product that isn't a classification category
		for (final CategoryModel category : product.getSupercategories())
		{
			if (!(category instanceof ClassificationClassModel))
			{
				return category;
			}
		}
		return null;
	}
}

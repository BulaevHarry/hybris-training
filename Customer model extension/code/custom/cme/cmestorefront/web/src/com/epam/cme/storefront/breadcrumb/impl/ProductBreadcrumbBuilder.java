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
package com.epam.cme.storefront.breadcrumb.impl;

import de.hybris.platform.catalog.model.classification.ClassificationClassModel;
import de.hybris.platform.category.model.CategoryModel;
import de.hybris.platform.commercefacades.product.data.CategoryData;
import de.hybris.platform.commercefacades.product.data.ProductData;
import de.hybris.platform.commerceservices.url.UrlResolver;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.variants.model.VariantProductModel;
import com.epam.cme.storefront.breadcrumb.Breadcrumb;
import com.epam.cme.storefront.history.BrowseHistory;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Required;

/**
 * ProductBreadcrumbBuilder implementation for {@link ProductData}
 */
public class ProductBreadcrumbBuilder {
    private static final String LAST_LINK_CLASS = "active";

    private Converter<CategoryModel, CategoryData> categoryConverter;
    private UrlResolver<ProductModel> productModelUrlResolver;
    private UrlResolver<CategoryData> categoryDataUrlResolver;
    private BrowseHistory browseHistory;

    public List<Breadcrumb> getBreadcrumbs(final ProductModel productModel) throws IllegalArgumentException {
        final List<Breadcrumb> breadcrumbs = new ArrayList<Breadcrumb>();

        final Collection<CategoryModel> categoryModels = new ArrayList<CategoryModel>();
        final Breadcrumb last;

        final ProductModel baseProductModel = getBaseProduct(productModel);
        last = getProductBreadcrumb(baseProductModel);
        categoryModels.addAll(baseProductModel.getSupercategories());
        last.setLinkClass(LAST_LINK_CLASS);

        breadcrumbs.add(last);

        while (!categoryModels.isEmpty()) {
            CategoryModel toDisplay = null;
            for (final CategoryModel categoryModel : categoryModels) {
                if (!(categoryModel instanceof ClassificationClassModel)) {
                    if (toDisplay == null) {
                        toDisplay = categoryModel;
                    }
                    if (getBrowseHistory().findEntryMatchUrlEndsWith(categoryModel.getCode()) != null) {
                        break;
                    }
                }
            }
            categoryModels.clear();
            if (toDisplay != null) {
                breadcrumbs.add(getCategoryBreadcrumb(getCategoryConverter().convert(toDisplay)));
                categoryModels.addAll(toDisplay.getSupercategories());
            }
        }
        Collections.reverse(breadcrumbs);
        return breadcrumbs;
    }

    protected ProductModel getBaseProduct(final ProductModel product) {
        if (product instanceof VariantProductModel) {
            return getBaseProduct(((VariantProductModel) product).getBaseProduct());
        }
        return product;
    }

    protected Breadcrumb getProductBreadcrumb(final ProductModel product) {
        final String productUrl = getProductModelUrlResolver().resolve(product);
        return new Breadcrumb(productUrl, product.getName(), null);
    }

    protected Breadcrumb getCategoryBreadcrumb(final CategoryData categoryData) {
        final String categoryUrl = getCategoryDataUrlResolver().resolve(categoryData);
        return new Breadcrumb(categoryUrl, categoryData.getName(), null, categoryData.getCode());
    }

    protected Converter<CategoryModel, CategoryData> getCategoryConverter() {
        return categoryConverter;
    }

    @Required
    public void setCategoryConverter(final Converter<CategoryModel, CategoryData> categoryConverter) {
        this.categoryConverter = categoryConverter;
    }

    protected UrlResolver<ProductModel> getProductModelUrlResolver() {
        return productModelUrlResolver;
    }

    @Required
    public void setProductModelUrlResolver(final UrlResolver<ProductModel> productModelUrlResolver) {
        this.productModelUrlResolver = productModelUrlResolver;
    }

    protected UrlResolver<CategoryData> getCategoryDataUrlResolver() {
        return categoryDataUrlResolver;
    }

    @Required
    public void setCategoryDataUrlResolver(final UrlResolver<CategoryData> categoryDataUrlResolver) {
        this.categoryDataUrlResolver = categoryDataUrlResolver;
    }

    protected BrowseHistory getBrowseHistory() {
        return browseHistory;
    }

    @Required
    public void setBrowseHistory(final BrowseHistory browseHistory) {
        this.browseHistory = browseHistory;
    }
}

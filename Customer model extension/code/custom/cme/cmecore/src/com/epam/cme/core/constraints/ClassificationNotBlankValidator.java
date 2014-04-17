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
package com.epam.cme.core.constraints;

import de.hybris.platform.catalog.model.ProductFeatureModel;
import de.hybris.platform.category.CategoryService;
import de.hybris.platform.category.model.CategoryModel;
import de.hybris.platform.classification.ClassificationService;
import de.hybris.platform.classification.features.Feature;
import de.hybris.platform.classification.features.FeatureValue;
import de.hybris.platform.core.Registry;
import de.hybris.platform.core.model.product.ProductModel;
import com.epam.cme.core.jalo.ClassificationNotBlankConstraint;

import java.util.List;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

/**
 * Validates that if the given {@link ProductModel} has a certain category then the assigned
 * classification attribute must not be empty or null. The product's category and classification
 * combination to be checked is stored in the constraint definition
 * {@link ClassificationNotBlankConstraint}.
 */
public class ClassificationNotBlankValidator implements ConstraintValidator<ClassificationNotBlank, Object> {

    private static final Logger LOG = Logger.getLogger(ClassificationNotBlankValidator.class.getName());
    private CategoryService categoryService;
    private ClassificationService classificationService;

    private String categoryCode;
    private String classificationCode;

    @Override
    public void initialize(final ClassificationNotBlank constraintAnnotation) {
        this.categoryService = (CategoryService) Registry.getApplicationContext().getBean("categoryService");
        this.classificationService = (ClassificationService) Registry.getApplicationContext().getBean(
                "classificationService");
        this.classificationCode = constraintAnnotation.classification();
        this.categoryCode = constraintAnnotation.category();

        if (StringUtils.isEmpty(this.classificationCode) || StringUtils.isBlank(this.classificationCode)) {
            throw new IllegalArgumentException("parameter 'classification' must not be empty");
        }
        if (StringUtils.isEmpty(this.categoryCode) || StringUtils.isBlank(this.categoryCode)) {
            throw new IllegalArgumentException("parameter 'category' must not be empty");
        }
    }

    @Override
    public boolean isValid(final Object value, final ConstraintValidatorContext context) {
        ProductModel productModel = null;
        CategoryModel categoryModel = null;

        if (value instanceof ProductModel) {
            productModel = (ProductModel) value;
        } else {
            LOG.error("Provided object is not an instance of ProductModel: " + value.getClass());
            return false;
        }

        final List<CategoryModel> categories = categoryService.getCategoryPathForProduct(productModel);

        for (final CategoryModel curCategoryModel : categories) {
            if (this.categoryCode.equalsIgnoreCase(curCategoryModel.getCode())) {
                categoryModel = curCategoryModel;
                break;
            }
        }

        if (categoryModel == null) {
            // if product does not have the category to be checked, classifications do not need to
            // be validated and
            // the check is ok
            return true;
        }

        final List<ProductFeatureModel> productFeatures = productModel.getFeatures();
        for (final ProductFeatureModel productFeature : productFeatures) {
            if (this.classificationCode.equalsIgnoreCase(productFeature.getClassificationAttributeAssignment()
                    .getClassificationAttribute().getCode())) {
                final Feature feature = classificationService.getFeature(productModel,
                        productFeature.getClassificationAttributeAssignment());

                final List<FeatureValue> featureValues = feature.getValues();
                if (featureValues != null && !featureValues.isEmpty()) {
                    return true;
                }
                return false;

            }
        }

        return false;
    }

}

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

import static de.hybris.platform.servicelayer.util.ServicesUtil.validateParameterNotNullStandardMessage;

import de.hybris.platform.catalog.model.ProductFeatureModel;
import de.hybris.platform.catalog.model.classification.ClassAttributeAssignmentModel;
import de.hybris.platform.catalog.model.classification.ClassificationAttributeModel;
import de.hybris.platform.catalog.model.classification.ClassificationClassModel;
import de.hybris.platform.core.PK;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.core.model.type.TypeModel;
import de.hybris.platform.servicelayer.internal.dao.AbstractItemDao;
import de.hybris.platform.servicelayer.search.FlexibleSearchQuery;
import de.hybris.platform.servicelayer.type.TypeService;
import com.epam.cme.core.daos.ProductsFeaturesDao;

import java.util.List;

import org.springframework.beans.factory.annotation.Required;

/**
 * Default implementation of ProductsFeaturesDao {@link ProductsFeaturesDao}
 */
public class DefaultProductsFeaturesDao extends AbstractItemDao implements ProductsFeaturesDao {

    private TypeService typeService;

    private static final String FIND_PRODUCTS_BY_VENDORCDOE_QUERY = "SELECT {a:" + ProductFeatureModel.PK + "} FROM {"
            + ProductFeatureModel._TYPECODE + " AS pf JOIN " + ProductModel._TYPECODE + " AS a ON {a:pk} = {pf:"
            + ProductFeatureModel.PRODUCT + "} " + "	JOIN " + ClassAttributeAssignmentModel._TYPECODE
            + " AS caa ON {caa:pk} = {pf:" + ProductFeatureModel.CLASSIFICATIONATTRIBUTEASSIGNMENT + "}" + "  JOIN "
            + ClassificationAttributeModel._TYPECODE + " AS ca ON {ca:pk} = {caa: "
            + ClassAttributeAssignmentModel.CLASSIFICATIONATTRIBUTE + "}" + "  JOIN "
            + ClassificationClassModel._TYPECODE + " AS cc ON {cc:pk} = {caa:"
            + ClassAttributeAssignmentModel.CLASSIFICATIONCLASS + "}" + "}" + " WHERE {cc:"
            + ClassificationClassModel.CODE + "} = ?classificationClassCode AND {ca:"
            + ClassificationAttributeModel.CODE
            + "} = ?classificationAttributeCode AND {pf:stringvalue} LIKE ?brand and {a:" + ProductModel.ITEMTYPE
            + "}=?itemType";

    @Override
    public List<ProductModel> findAccessoriesByVendorCompatibility(final String manufacturerName,
            final String classificationClassCode, final String classificationAttributeCode, final String productTypeCode) {
        validateParameterNotNullStandardMessage("classificationClassCode", classificationClassCode);
        validateParameterNotNullStandardMessage("classificationAttributeCode", classificationAttributeCode);
        validateParameterNotNullStandardMessage("manufacturerName", manufacturerName);
        validateParameterNotNullStandardMessage("productTypeCode", productTypeCode);

        final FlexibleSearchQuery query = new FlexibleSearchQuery(FIND_PRODUCTS_BY_VENDORCDOE_QUERY);
        query.addQueryParameter("brand", manufacturerName);
        query.addQueryParameter("classificationClassCode", classificationClassCode);
        query.addQueryParameter("classificationAttributeCode", classificationAttributeCode);
        query.addQueryParameter("itemType", getItemType(productTypeCode));
        return this.getFlexibleSearchService().<ProductModel>search(query).getResult();
    }

    private PK getItemType(final String productTypeCode) {
        final TypeModel typeModel = getTypeService().getTypeForCode(productTypeCode);
        return typeModel.getPk();
    }

    @Required
    public void setTypeService(final TypeService typeService) {
        this.typeService = typeService;
    }

    protected TypeService getTypeService() {
        return typeService;
    }
}

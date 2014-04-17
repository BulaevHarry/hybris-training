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
package com.epam.cme.facades.product.impl;

import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.catalog.enums.ProductReferenceTypeEnum;
import de.hybris.platform.catalog.model.ProductReferenceModel;
import de.hybris.platform.catalog.model.classification.ClassAttributeAssignmentModel;
import de.hybris.platform.commercefacades.product.ProductOption;
import de.hybris.platform.commercefacades.product.data.ProductData;
import de.hybris.platform.commerceservices.product.CommerceProductReferenceService;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.core.model.type.ComposedTypeModel;
import de.hybris.platform.product.ProductService;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.servicelayer.model.ModelService;
import com.epam.cme.core.model.AccessoryModel;
import com.epam.cme.core.services.CompatibilityService;
import com.epam.cme.facades.product.impl.DefaultTelcoProductFacade;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import org.apache.log4j.Logger;

@IntegrationTest
public class DefaultTelcoProductFacadeTest {
    private static final Logger LOG = Logger.getLogger(DefaultTelcoProductFacadeTest.class);

    private final DefaultTelcoProductFacade telcoProductFacade = new DefaultTelcoProductFacade();

    @Mock
    private CompatibilityService compatibilityService;

    @Mock
    private ProductService productService;

    @Mock
    private ModelService modelService;

    @Mock
    private Converter<ProductModel, ProductData> productConverter;

    @Mock
    private CommerceProductReferenceService commerceProductReferenceService;

    private ProductModel demoProduct;

    @Before
    public void mockSetup() {
        MockitoAnnotations.initMocks(this);
        telcoProductFacade.setCompatibilityService(compatibilityService);
        telcoProductFacade.setProductService(productService);
        telcoProductFacade.setModelService(modelService);
        telcoProductFacade.setProductConverter(productConverter);
        telcoProductFacade.setCommerceProductReferenceService(commerceProductReferenceService);
        demoProduct = new ProductModel();
        demoProduct.setCode("democode");

        demoProduct.setProductReferences(Collections.<ProductReferenceModel>emptyList());
    }

    @Test
    public void testRefsAndFeatureCCompatibleLimitsSizeToMaxAmount() {
        final Integer limit = Integer.valueOf(5);
        final ClassAttributeAssignmentModel classAttributeAssignmentModel = new ClassAttributeAssignmentModel();
        final ComposedTypeModel targetItemType = new ComposedTypeModel();
        final List<ProductModel> mockProducts = createRandomProducts(9);
        when(productService.getProductForCode("democode")).thenReturn(demoProduct);
        when(productConverter.convert((ProductModel) Matchers.any())).thenReturn(createProductData("democode"));
        when(modelService.getAttributeValue(demoProduct, ProductModel.PRODUCTREFERENCES)).thenReturn(
                Collections.<ProductReferenceModel>emptyList());
        when(
                compatibilityService.getFeatureCompatibleProducts("democode", classAttributeAssignmentModel,
                        targetItemType)).thenReturn(mockProducts);
        final List<ProductOption> options = null;

        final List<ProductData> productData = telcoProductFacade
                .getProductReferencesAndFeatureCompatibleProductsForCode("democode",
                        Collections.singletonList(ProductReferenceTypeEnum.ACCESSORIES), options, limit,
                        classAttributeAssignmentModel, targetItemType);

        LOG.info(productData.size() + " is size of returned data");
        Assert.assertTrue(productData.size() <= limit.intValue());
    }

    @Test
    public void testRefsFeatureCompatibleandVendorCompatibleSizeToMaxAmount() {
        final Integer limit = Integer.valueOf(5);
        final ClassAttributeAssignmentModel classAttributeAssignmentModel = new ClassAttributeAssignmentModel();
        final ComposedTypeModel targetItemType = new ComposedTypeModel();
        final List<ProductModel> mockProducts = createRandomAccessories(9);
        when(productService.getProductForCode("democode")).thenReturn(demoProduct);
        when(productConverter.convert((ProductModel) Matchers.any())).thenReturn(createProductData("docde"));
        when(modelService.getAttributeValue(demoProduct, ProductModel.PRODUCTREFERENCES)).thenReturn(
                Collections.<ProductReferenceModel>emptyList());
        when(
                compatibilityService.getFeatureCompatibleProducts("democode", classAttributeAssignmentModel,
                        targetItemType)).thenReturn(Collections.<ProductModel>emptyList());
        when(compatibilityService.getAccessoriesForVendorCompatibility("democode", AccessoryModel._TYPECODE))
                .thenReturn(mockProducts);
        final List<ProductOption> options = null;

        final List<ProductData> productData = telcoProductFacade
                .getProductReferencesAndFeatureCompatibleAndVendorCompatibleProductsForCode("democode",
                        Collections.singletonList(ProductReferenceTypeEnum.ACCESSORIES), options, limit,
                        classAttributeAssignmentModel, targetItemType);

        LOG.info(productData.size() + " is size of returned data");
        Assert.assertTrue(productData.size() <= limit.intValue());
    }

    private List<ProductModel> createRandomProducts(final int count) {
        final List<ProductModel> products = new ArrayList<ProductModel>();
        for (int k = 0; k < count; k++) {
            products.add(new ProductModel());
        }
        return products;
    }

    private List<ProductModel> createRandomAccessories(final int count) {
        final List<ProductModel> products = new ArrayList<ProductModel>();
        for (int k = 0; k < count; k++) {
            products.add(new ProductModel());
        }
        return products;
    }

    private ProductData createProductData(final String code) {
        final ProductData productData = new ProductData();
        productData.setCode(code);
        return productData;
    }

    @Test
    public void testDuplicateProductsDontGetAddedTwice() {
        final List<ProductData> products = new ArrayList<ProductData>();
        final ProductData product1 = new ProductData();
        product1.setCode("jokd");
        final ProductData product2 = new ProductData();
        product2.setCode("kodfkk");
        final ProductData newProduct = new ProductData();
        newProduct.setCode("jokd");
        final ProductData brandnewProduct = new ProductData();
        brandnewProduct.setCode("mitthi");
        products.add(product1);
        products.add(product2);
        Assert.assertTrue(telcoProductFacade.contains(products, newProduct));

        Assert.assertFalse(telcoProductFacade.contains(products, brandnewProduct));
    }
}

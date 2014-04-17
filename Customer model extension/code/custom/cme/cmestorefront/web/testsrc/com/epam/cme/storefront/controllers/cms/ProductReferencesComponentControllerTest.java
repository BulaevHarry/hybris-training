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
package com.epam.cme.storefront.controllers.cms;

import static org.hamcrest.Matchers.hasProperty;
import static org.hamcrest.Matchers.instanceOf;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.acceleratorcms.model.components.ProductReferencesComponentModel;
import de.hybris.platform.catalog.enums.ProductReferenceTypeEnum;
import de.hybris.platform.cms2.exceptions.CMSItemNotFoundException;
import de.hybris.platform.cms2.servicelayer.services.impl.DefaultCMSComponentService;
import de.hybris.platform.commercefacades.product.ProductFacade;
import de.hybris.platform.commercefacades.product.data.ProductReferenceData;
import de.hybris.platform.core.model.type.ComposedTypeModel;
import de.hybris.platform.servicelayer.type.TypeService;
import com.epam.cme.storefront.controllers.ControllerConstants;
import com.epam.cme.storefront.controllers.cms.ProductReferencesComponentController;
import com.epam.cme.storefront.controllers.pages.AbstractPageController;
import com.epam.cme.storefront.controllers.util.ProductDataHelper;

import java.util.Collections;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import junit.framework.Assert;

import org.apache.commons.lang.StringUtils;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.ui.Model;

/**
 * Unit test for {@link ProductReferencesComponentController}
 */
@UnitTest
public class ProductReferencesComponentControllerTest {
    @Rule
    public ExpectedException thrown = ExpectedException.none();

    private static final String COMPONENT_UID = "componentUid";
    private static final String TEST_COMPONENT_UID = "componentUID";
    private static final String TEST_TYPE_CODE = "myTypeCode";
    private static final String TEST_TYPE_VIEW = ControllerConstants.Views.Cms.ComponentPrefix
            + StringUtils.lowerCase(TEST_TYPE_CODE);
    private static final String TEST_PRODUCT_CODE = "productCode";
    private static final String TITLE = "title";
    private static final String TITLE_VALUE = "Accessories";
    private static final String PRODUCT_REFERENCES = "productReferences";
    private static final String COMPONENT = "component";

    private ProductReferencesComponentController productReferencesComponentController;

    @Mock
    private ProductReferencesComponentModel productReferencesComponentModel;
    @Mock
    private Model model;
    @Mock
    private DefaultCMSComponentService cmsComponentService;
    @Mock
    private ProductFacade productFacade;
    @Mock
    private HttpServletRequest request;
    @Mock
    private HttpServletResponse response;
    @Mock
    private TypeService typeService;
    @Mock
    private ComposedTypeModel composedTypeModel;
    @Mock
    private ProductReferenceData productReferenceData;

    private final List<ProductReferenceData> productReferenceDataList = Collections.singletonList(productReferenceData);

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);

        productReferencesComponentController = new ProductReferencesComponentController();
        productReferencesComponentController.setCmsComponentService(cmsComponentService);
        ReflectionTestUtils.setField(productReferencesComponentController, "productFacade", productFacade);
    }

    @Test
    public void testRenderComponent() throws Exception {
        given(productReferencesComponentModel.getMaximumNumberProducts()).willReturn(Integer.valueOf(1));
        given(productReferencesComponentModel.getTitle()).willReturn(TITLE_VALUE);
        given(productReferencesComponentModel.getProductReferenceTypes()).willReturn(
                Collections.singletonList(ProductReferenceTypeEnum.ACCESSORIES));

        given(typeService.getComposedTypeForClass(Mockito.<Class>any())).willReturn(composedTypeModel);
        given(composedTypeModel.getCode()).willReturn(TEST_TYPE_CODE);
        given(request.getAttribute(ProductDataHelper.CURRENT_PRODUCT)).willReturn(TEST_PRODUCT_CODE);
        given(
                productFacade.getProductReferencesForCode(Mockito.anyString(),
                        Mockito.any(ProductReferenceTypeEnum.class), Mockito.any(List.class), Mockito.<Integer>any()))
                .willReturn(productReferenceDataList);
    }

    @Test
    public void testRenderComponentUid() throws Exception {
        given(request.getAttribute(COMPONENT_UID)).willReturn(TEST_COMPONENT_UID);
        given(cmsComponentService.getSimpleCMSComponent(TEST_COMPONENT_UID))
                .willReturn(productReferencesComponentModel);
        given(productReferencesComponentModel.getMaximumNumberProducts()).willReturn(Integer.valueOf(1));
        given(productReferencesComponentModel.getTitle()).willReturn(TITLE_VALUE);
        given(productReferencesComponentModel.getProductReferenceTypes()).willReturn(
                Collections.singletonList(ProductReferenceTypeEnum.ACCESSORIES));

        given(typeService.getComposedTypeForClass(Mockito.<Class>any())).willReturn(composedTypeModel);
        given(composedTypeModel.getCode()).willReturn(TEST_TYPE_CODE);
        given(request.getAttribute(ProductDataHelper.CURRENT_PRODUCT)).willReturn(TEST_PRODUCT_CODE);
        given(
                productFacade.getProductReferencesForCode(Mockito.anyString(),
                        Mockito.any(ProductReferenceTypeEnum.class), Mockito.any(List.class), Mockito.<Integer>any()))
                .willReturn(productReferenceDataList);

        final String viewName = productReferencesComponentController.handleGet(request, response, model);
        verify(model, Mockito.times(1)).addAttribute(COMPONENT, productReferencesComponentModel);
        verify(model, Mockito.times(1)).addAttribute(TITLE, TITLE_VALUE);
        verify(model, Mockito.times(1)).addAttribute(PRODUCT_REFERENCES, productReferenceDataList);
        Assert.assertEquals(TEST_TYPE_VIEW, viewName);
    }

    @Test
    public void testRenderComponentNotFound() throws Exception {
        given(request.getAttribute(COMPONENT_UID)).willReturn(null);
        given(request.getParameter(COMPONENT_UID)).willReturn(null);

        thrown.expect(AbstractPageController.HttpNotFoundException.class);

        productReferencesComponentController.handleGet(request, response, model);
    }

    @Test
    public void testRenderComponentNotFound2() throws Exception {
        given(request.getAttribute(COMPONENT_UID)).willReturn(null);
        given(request.getParameter(COMPONENT_UID)).willReturn(TEST_COMPONENT_UID);
        given(cmsComponentService.getSimpleCMSComponent(TEST_COMPONENT_UID)).willReturn(null);

        thrown.expect(AbstractPageController.HttpNotFoundException.class);

        productReferencesComponentController.handleGet(request, response, model);
    }

    @Test
    public void testRenderComponentNotFound3() throws Exception {
        given(request.getAttribute(COMPONENT_UID)).willReturn(TEST_COMPONENT_UID);
        given(cmsComponentService.getSimpleCMSComponent(TEST_COMPONENT_UID)).willReturn(null);
        given(cmsComponentService.getSimpleCMSComponent(TEST_COMPONENT_UID))
                .willThrow(new CMSItemNotFoundException(""));

        thrown.expect(AbstractPageController.HttpNotFoundException.class);
        thrown.expect(hasProperty("cause", instanceOf(CMSItemNotFoundException.class)));

        productReferencesComponentController.handleGet(request, response, model);
    }

}

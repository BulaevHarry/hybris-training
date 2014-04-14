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

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

import de.hybris.bootstrap.annotations.UnitTest;
import com.epam.cme.storefront.controllers.ControllerConstants;
import com.epam.cme.storefront.controllers.cms.CMSLinkComponentController;
import com.epam.cme.storefront.controllers.pages.AbstractPageController;
import de.hybris.platform.category.model.CategoryModel;
import de.hybris.platform.cms2.exceptions.CMSItemNotFoundException;
import de.hybris.platform.cms2.model.contents.components.CMSLinkComponentModel;
import de.hybris.platform.cms2.model.pages.ContentPageModel;
import de.hybris.platform.cms2.servicelayer.services.impl.DefaultCMSComponentService;
import de.hybris.platform.commercefacades.product.data.CategoryData;
import de.hybris.platform.commercefacades.product.data.ProductData;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.core.model.type.ComposedTypeModel;
import de.hybris.platform.servicelayer.dto.converter.Converter;

import org.apache.commons.lang.StringUtils;

import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.ui.Model;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;

import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

/**
 * Unit test for {@link CMSLinkComponentControllerTest}
 */
@UnitTest
public class CMSLinkComponentControllerTest
{

	private static final String COMPONENT_UID = "componentUid";
	private static final String TEST_COMPONENT_UID = "componentUID";
	private static final String TEST_TYPE_CODE = "myTypeCode";
	private static final String TEST_TYPE_VIEW = ControllerConstants.Views.Cms.ComponentPrefix + StringUtils.lowerCase(TEST_TYPE_CODE);
	private static final String COMPONENT = "component";
	private static final String TEST_COMPONENT_URL = "TestComponentUrl";
	private static final String TEST_CONTENT_PAGE_LABEL = "TestContentPageLabel";
	private static final String TEST_PRODUCT_URL = "TestProductUrl";
	private static final String TEST_CATEGORY_URL = "TestCategoryUrl";
	private static final String URL = "url";

	private CMSLinkComponentController cmsLinkComponentController;

	@Mock
	private Model model;
	@Mock
	private DefaultCMSComponentService cmsComponentService;
	@Mock
	private HttpServletRequest request;
	@Mock
	private HttpServletResponse response;
	@Mock
	private ComposedTypeModel composedTypeModel;
	@Mock
	private CMSLinkComponentModel cmsLinkComponentModel;
	@Mock
	private Converter<ProductModel, ProductData> productUrlConverter;
	@Mock
	private ProductModel productModel;
	@Mock
	private ProductData productData;
	@Mock
	private Converter<CategoryModel, CategoryData> categoryUrlConverter;
	@Mock
	private CategoryModel categoryModel;
	@Mock
	private CategoryData categoryData;
	@Mock
	private ContentPageModel contentPageModel;

	@Before
	public void setUp()
	{
		MockitoAnnotations.initMocks(this);

		cmsLinkComponentController = new CMSLinkComponentController();
		cmsLinkComponentController.setCmsComponentService(cmsComponentService);
		ReflectionTestUtils.setField(cmsLinkComponentController, "productUrlConverter", productUrlConverter);
		ReflectionTestUtils.setField(cmsLinkComponentController, "categoryUrlConverter", categoryUrlConverter);
	}

	@Test
	public void testRenderComponentWithComponentUrl() throws Exception
	{
		given(cmsLinkComponentModel.getUrl()).willReturn(TEST_COMPONENT_URL);
		given(cmsLinkComponentModel.getItemtype()).willReturn(TEST_TYPE_CODE);

		final String viewName = cmsLinkComponentController.handleComponent(request, response, model, cmsLinkComponentModel);
		verify(model, Mockito.times(1)).addAttribute(URL, TEST_COMPONENT_URL);
		Assert.assertEquals(TEST_TYPE_VIEW, viewName);
	}

	@Test
	public void testRenderComponentWithContentPage() throws Exception
	{
		given(cmsLinkComponentModel.getContentPage()).willReturn(contentPageModel);
		given(contentPageModel.getLabel()).willReturn(TEST_CONTENT_PAGE_LABEL);
		given(cmsLinkComponentModel.getItemtype()).willReturn(TEST_TYPE_CODE);

		final String viewName = cmsLinkComponentController.handleComponent(request, response, model, cmsLinkComponentModel);
		verify(model, Mockito.times(1)).addAttribute(URL, TEST_CONTENT_PAGE_LABEL);
		Assert.assertEquals(TEST_TYPE_VIEW, viewName);
	}

	@Test
	public void testRenderComponentWithProduct() throws Exception
	{
		given(cmsLinkComponentModel.getProduct()).willReturn(productModel);
		given(productUrlConverter.convert(productModel)).willReturn(productData);
		given(productData.getUrl()).willReturn(TEST_PRODUCT_URL);
		given(cmsLinkComponentModel.getItemtype()).willReturn(TEST_TYPE_CODE);

		final String viewName = cmsLinkComponentController.handleComponent(request, response, model, cmsLinkComponentModel);
		verify(model, Mockito.times(1)).addAttribute(URL, TEST_PRODUCT_URL);
		Assert.assertEquals(TEST_TYPE_VIEW, viewName);
	}

	@Test
	public void testRenderComponentWithCategory() throws Exception
	{
		given(cmsLinkComponentModel.getCategory()).willReturn(categoryModel);
		given(categoryUrlConverter.convert(categoryModel)).willReturn(categoryData);
		given(categoryData.getUrl()).willReturn(TEST_CATEGORY_URL);
		given(cmsLinkComponentModel.getItemtype()).willReturn(TEST_TYPE_CODE);

		final String viewName = cmsLinkComponentController.handleComponent(request, response, model, cmsLinkComponentModel);
		verify(model, Mockito.times(1)).addAttribute(URL, TEST_CATEGORY_URL);
		Assert.assertEquals(TEST_TYPE_VIEW, viewName);
	}

	@Test
	public void testRenderComponentUid() throws Exception
	{
		given(request.getAttribute(COMPONENT_UID)).willReturn(TEST_COMPONENT_UID);
		given(cmsComponentService.getSimpleCMSComponent(TEST_COMPONENT_UID)).willReturn(cmsLinkComponentModel);
		given(cmsLinkComponentModel.getProduct()).willReturn(productModel);
		given(productUrlConverter.convert(productModel)).willReturn(productData);
		given(productData.getUrl()).willReturn(TEST_PRODUCT_URL);
		given(cmsLinkComponentModel.getItemtype()).willReturn(TEST_TYPE_CODE);

		final String viewName = cmsLinkComponentController.handleGet(request, response, model);
		verify(model, Mockito.times(1)).addAttribute(COMPONENT, cmsLinkComponentModel);
		verify(model, Mockito.times(1)).addAttribute(URL, TEST_PRODUCT_URL);
		Assert.assertEquals(TEST_TYPE_VIEW, viewName);
	}

	@Test(expected = AbstractPageController.HttpNotFoundException.class)
	public void testRenderComponentNotFound() throws Exception
	{
		given(request.getAttribute(COMPONENT_UID)).willReturn(null);
		given(request.getParameter(COMPONENT_UID)).willReturn(null);
		cmsLinkComponentController.handleGet(request, response, model);
	}

	@Test(expected = AbstractPageController.HttpNotFoundException.class)
	public void testRenderComponentNotFound2() throws Exception
	{
		given(request.getAttribute(COMPONENT_UID)).willReturn(null);
		given(request.getParameter(COMPONENT_UID)).willReturn(TEST_COMPONENT_UID);
		given(cmsComponentService.getSimpleCMSComponent(TEST_COMPONENT_UID)).willReturn(null);
		cmsLinkComponentController.handleGet(request, response, model);
	}

	@Test(expected = AbstractPageController.HttpNotFoundException.class)
	public void testRenderComponentNotFound3() throws Exception
	{
		given(request.getAttribute(COMPONENT_UID)).willReturn(TEST_COMPONENT_UID);
		given(cmsComponentService.getSimpleCMSComponent(TEST_COMPONENT_UID)).willReturn(null);
		given(cmsComponentService.getSimpleCMSComponent(TEST_COMPONENT_UID)).willThrow(new CMSItemNotFoundException(""));
		cmsLinkComponentController.handleGet(request, response, model);
	}

}

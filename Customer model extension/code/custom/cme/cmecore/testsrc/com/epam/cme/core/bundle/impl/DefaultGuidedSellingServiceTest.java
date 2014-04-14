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
package com.epam.cme.core.bundle.impl;

import static junit.framework.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.configurablebundleservices.bundle.AbstractBundleComponentEditableChecker;
import de.hybris.platform.configurablebundleservices.bundle.impl.DefaultBundleTemplateService;
import de.hybris.platform.configurablebundleservices.model.BundleTemplateModel;
import com.epam.cme.core.model.DeviceModel;
import com.epam.cme.core.model.ServiceAddOnModel;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;


/**
 * JUnit test suite for {@link DefaultGuidedSellingService}
 */
public class DefaultGuidedSellingServiceTest
{

	@Rule
	public ExpectedException thrown = ExpectedException.none();

	@Mock
	private AbstractBundleComponentEditableChecker<CartModel> bundleComponentEditableChecker;

	@Mock
	private DefaultBundleTemplateService bundleTemplateService;

	private DefaultGuidedSellingService guidedSellingService;

	@Before
	public void setUp()
	{
		MockitoAnnotations.initMocks(this);
		guidedSellingService = new DefaultGuidedSellingService();
		guidedSellingService.setBundleComponentEditableChecker(bundleComponentEditableChecker);
		guidedSellingService.setBundleTemplateService(bundleTemplateService);
	}

	@Test
	public void testGetSubsequentExtraComponentWhenNoCart()
	{
		thrown.expect(IllegalArgumentException.class);
		thrown.expectMessage("masterAbstractOrder can not be null");

		guidedSellingService.getRelativeSelectionComponent(null, new BundleTemplateModel(), 1, 1);
	}

	@Test
	public void testGetSubsequentExtraComponentWhenNoTemplate()
	{
		thrown.expect(IllegalArgumentException.class);
		thrown.expectMessage("bundleTemplate can not be null");

		guidedSellingService.getRelativeSelectionComponent(new CartModel(), null, 1, 1);
	}

	@Test
	public void testGetSubsequentExtraComponent()
	{
		final CartModel masterCart = mock(CartModel.class);
		final BundleTemplateModel parentTemplateModel = mock(BundleTemplateModel.class);
		final ServiceAddOnModel addOnProduct = new ServiceAddOnModel();
		final List<ProductModel> products = new ArrayList<ProductModel>();
		products.add(addOnProduct);

		final BundleTemplateModel deviceTemplateModel = mock(BundleTemplateModel.class);
		given(deviceTemplateModel.getParentTemplate()).willReturn(parentTemplateModel);
		given(Integer.valueOf(bundleTemplateService.getPositionInParent(deviceTemplateModel))).willReturn(Integer.valueOf(0));

		final BundleTemplateModel planTemplateModel = mock(BundleTemplateModel.class);
		given(planTemplateModel.getParentTemplate()).willReturn(parentTemplateModel);
		given(Integer.valueOf(bundleTemplateService.getPositionInParent(planTemplateModel))).willReturn(Integer.valueOf(1));

		final List<BundleTemplateModel> childTemplates = new ArrayList<BundleTemplateModel>();
		childTemplates.add(deviceTemplateModel);
		childTemplates.add(planTemplateModel);
		given(parentTemplateModel.getChildTemplates()).willReturn(childTemplates);

		// Plan component is the last child component
		given(bundleTemplateService.getSubsequentBundleTemplate(planTemplateModel)).willReturn(null);
		BundleTemplateModel component = guidedSellingService.getRelativeSelectionComponent(masterCart, planTemplateModel, 1, 1);
		assertNull("Plan_Component should not have a subsequent component", component);

		// AddOn component comes after plan component and its dependencies are met
		final BundleTemplateModel addOnTemplateModel = mock(BundleTemplateModel.class);
		given(bundleTemplateService.getSubsequentBundleTemplate(planTemplateModel)).willReturn(addOnTemplateModel);
		given(bundleTemplateService.getSubsequentBundleTemplate(addOnTemplateModel)).willReturn(null);
		given(addOnTemplateModel.getParentTemplate()).willReturn(parentTemplateModel);
		given(Integer.valueOf(bundleTemplateService.getPositionInParent(addOnTemplateModel))).willReturn(Integer.valueOf(2));

		given(addOnTemplateModel.getProducts()).willReturn(products);
		childTemplates.add(addOnTemplateModel);
		given(Boolean.valueOf(bundleComponentEditableChecker.isAutoPickComponent(addOnTemplateModel))).willReturn(Boolean.FALSE);
		given(Boolean.valueOf(bundleComponentEditableChecker.canEdit(masterCart, addOnTemplateModel, 1))).willReturn(Boolean.TRUE);
		given(bundleTemplateService.getRelativeBundleTemplate(planTemplateModel, 1)).willReturn(addOnTemplateModel);
		given(
				Boolean.valueOf(bundleTemplateService.containsComponenentProductsOfType(addOnTemplateModel, DeviceModel.class,
						ServiceAddOnModel.class))).willReturn(Boolean.TRUE);
		component = guidedSellingService.getRelativeSelectionComponent(masterCart, planTemplateModel, 1, 1);
		assertEquals("", addOnTemplateModel, component);

		// AddOn component comes after plan component and its dependencies are not met and it's the last child component
		given(Boolean.valueOf(bundleComponentEditableChecker.canEdit(masterCart, addOnTemplateModel, 1))).willReturn(Boolean.FALSE);
		component = guidedSellingService.getRelativeSelectionComponent(masterCart, planTemplateModel, 1, 1);
		assertNull("Plan_Component should not have a subsequent component", component);

		// Tv component comes after addon component and its dependencies are not met and its an auto-pick and it does not have add-on proudcts
		final BundleTemplateModel tvTemplateModel = mock(BundleTemplateModel.class);
		given(bundleTemplateService.getRelativeBundleTemplate(planTemplateModel, 2)).willReturn(tvTemplateModel);
		given(bundleTemplateService.getSubsequentBundleTemplate(addOnTemplateModel)).willReturn(tvTemplateModel);
		given(bundleTemplateService.getSubsequentBundleTemplate(tvTemplateModel)).willReturn(null);
		given(tvTemplateModel.getParentTemplate()).willReturn(parentTemplateModel);
		given(Integer.valueOf(bundleTemplateService.getPositionInParent(tvTemplateModel))).willReturn(Integer.valueOf(3));

		given(Boolean.valueOf(bundleComponentEditableChecker.isAutoPickComponent(tvTemplateModel))).willReturn(Boolean.TRUE);
		given(Boolean.valueOf(bundleComponentEditableChecker.canEdit(masterCart, tvTemplateModel, 1))).willReturn(Boolean.FALSE);
		given(
				Boolean.valueOf(bundleTemplateService.containsComponenentProductsOfType(tvTemplateModel, DeviceModel.class,
						ServiceAddOnModel.class))).willReturn(Boolean.FALSE);
		component = guidedSellingService.getRelativeSelectionComponent(masterCart, planTemplateModel, 1, 1);
		assertNull("Plan_Component should not have a subsequent component", component);

		// Tv component comes after addon component and its dependencies are met and its an auto-pick and it does not have add-on proudcts
		given(Boolean.valueOf(bundleComponentEditableChecker.isAutoPickComponent(tvTemplateModel))).willReturn(Boolean.TRUE);
		given(Boolean.valueOf(bundleComponentEditableChecker.canEdit(masterCart, tvTemplateModel, 1))).willReturn(Boolean.TRUE);
		component = guidedSellingService.getRelativeSelectionComponent(masterCart, planTemplateModel, 1, 1);
		assertNull("Plan_Component should not have a subsequent component", component);

		// Tv component comes after addon component and its dependencies are met and its not an auto-pick and it does not have add-on proudcts
		given(Boolean.valueOf(bundleComponentEditableChecker.isAutoPickComponent(tvTemplateModel))).willReturn(Boolean.FALSE);
		given(Boolean.valueOf(bundleComponentEditableChecker.canEdit(masterCart, tvTemplateModel, 1))).willReturn(Boolean.TRUE);
		component = guidedSellingService.getRelativeSelectionComponent(masterCart, planTemplateModel, 1, 1);
		assertNull("Plan_Component should not have a subsequent component", component);

		// Tv component comes after addon component and its dependencies are met and its not an auto-pick and it has add-on proudcts
		given(tvTemplateModel.getProducts()).willReturn(products);
		childTemplates.add(tvTemplateModel);
		given(
				Boolean.valueOf(bundleTemplateService.containsComponenentProductsOfType(tvTemplateModel, DeviceModel.class,
						ServiceAddOnModel.class))).willReturn(Boolean.TRUE);
		component = guidedSellingService.getRelativeSelectionComponent(masterCart, planTemplateModel, 1, 1);
		assertEquals(tvTemplateModel, component);

	}

	@Test
	public void testIsComponentToBeDisplayedOnExtrasPageWhenNoCart()
	{
		thrown.expect(IllegalArgumentException.class);
		thrown.expectMessage("masterAbstractOrder can not be null");

		guidedSellingService.isComponentToBeDisplayedOnGuidedSellingSelectPage(null, new BundleTemplateModel(), 1,
				ServiceAddOnModel.class);
	}

	@Test
	public void testIsComponentToBeDisplayedOnExtrasPageWhenNoTemplate()
	{
		thrown.expect(IllegalArgumentException.class);
		thrown.expectMessage("bundleTemplate can not be null");

		guidedSellingService.isComponentToBeDisplayedOnGuidedSellingSelectPage(new CartModel(), null, 1, ServiceAddOnModel.class);
	}
}

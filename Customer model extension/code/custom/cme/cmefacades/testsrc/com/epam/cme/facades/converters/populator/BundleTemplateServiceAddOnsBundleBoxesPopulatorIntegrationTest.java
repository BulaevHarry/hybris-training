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
package com.epam.cme.facades.converters.populator;

import static de.hybris.platform.configurablebundleservices.bundle.impl.DefaultBundleCommerceCartService.NEW_BUNDLE;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import de.hybris.platform.catalog.CatalogVersionService;
import de.hybris.platform.commerceservices.order.CommerceCartModification;
import de.hybris.platform.commerceservices.order.CommerceCartModificationException;
import de.hybris.platform.configurablebundleservices.bundle.BundleCommerceCartService;
import de.hybris.platform.configurablebundleservices.bundle.BundleTemplateService;
import de.hybris.platform.configurablebundleservices.model.BundleTemplateModel;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.core.model.product.UnitModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.impex.constants.ImpExConstants;
import de.hybris.platform.jalo.CoreBasicDataCreator;
import de.hybris.platform.order.CartService;
import de.hybris.platform.product.ProductService;
import de.hybris.platform.servicelayer.ServicelayerTest;
import de.hybris.platform.servicelayer.i18n.CommonI18NService;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.user.UserService;
import de.hybris.platform.site.BaseSiteService;
import de.hybris.platform.util.Config;
import com.epam.cme.core.model.DeviceModel;
import com.epam.cme.core.model.ServiceAddOnModel;
import com.epam.cme.core.model.ServicePlanModel;
import com.epam.cme.facades.data.BundleBoxData;
import com.epam.cme.facades.data.BundleBoxEntryData;
import com.epam.cme.facades.data.BundleTemplateData;
import com.epam.cme.facades.data.BundleTemplateServiceAddOnsPopulatorParameters;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import javax.annotation.Resource;

import org.apache.log4j.Logger;
import org.junit.Before;
import org.junit.Test;


/**
 * Integration test suite for {@link BundleTemplateServiceAddOnsBundleBoxesPopulator}
 */
public class BundleTemplateServiceAddOnsBundleBoxesPopulatorIntegrationTest extends ServicelayerTest
{
	private static final Logger LOG = Logger.getLogger(BundleTemplateServiceAddOnsBundleBoxesPopulatorIntegrationTest.class);
	private static final String TEST_BASESITE_UID = "testSite";

	@Resource
	private BundleTemplateServiceAddOnsBundleBoxesPopulator bundleTemplateAddOnsPopulator;

	@Resource
	private UserService userService;

	@Resource
	private CatalogVersionService catalogVersionService;

	@Resource
	private ProductService productService;

	@Resource
	private BundleCommerceCartService bundleCommerceCartService;

	@Resource
	private BundleTemplateService bundleTemplateService;

	@Resource
	private CartService cartService;

	@Resource
	private BaseSiteService baseSiteService;

	@Resource
	private CommonI18NService commonI18NService;

	@Resource
	private ModelService modelService;

	private UnitModel unitModel;
	private CartModel telcoMasterCart;
	private DeviceModel galaxynexus;
	private ServicePlanModel smartStandard1Y;
	private ServiceAddOnModel internetPackMax;
	private ServiceAddOnModel weekender;
	private ServiceAddOnModel tvMobileBasic;
	private ServiceAddOnModel tvMobileExtrasDocu;
	private BundleTemplateModel smartPhoneDeviceSelection;
	private BundleTemplateModel smartPhonePlanSelection;
	private BundleTemplateModel smartPhoneInternetSelection;
	private BundleTemplateModel smartPhoneAddonSelection;
	private BundleTemplateModel smartPhoneTvBasicSelection;
	private BundleTemplateModel smartPhoneTvExtrasSelection;

	@Before
	public void setUp() throws Exception
	{
		// final Create data for tests
		LOG.info("Creating data for BundleTemplateServiceAddOnsBundleBoxesPopulatorIntegrationTest ...");
		userService.setCurrentUser(userService.getAdminUser());
		final long startTime = System.currentTimeMillis();
		new CoreBasicDataCreator().createEssentialData(Collections.EMPTY_MAP, null);

		// importing test csv
		final String legacyModeBackup = Config.getParameter(ImpExConstants.Params.LEGACY_MODE_KEY);
		LOG.info("Existing value for " + ImpExConstants.Params.LEGACY_MODE_KEY + " :" + legacyModeBackup);
		Config.setParameter(ImpExConstants.Params.LEGACY_MODE_KEY, "true");
		importCsv("/commerceservices/test/testCommerceCart.csv", "utf-8");
		Config.setParameter(ImpExConstants.Params.LEGACY_MODE_KEY, "false");
		importCsv("/cmefacades/test/testBundleTemplateExtrasBundleBoxesPopulator.impex", "utf-8");
		Config.setParameter(ImpExConstants.Params.LEGACY_MODE_KEY, legacyModeBackup);

		commonI18NService.setCurrentCurrency(commonI18NService.getCurrency("USD"));
		baseSiteService.setCurrentBaseSite(baseSiteService.getBaseSiteForUID(TEST_BASESITE_UID), false);
		catalogVersionService.setSessionCatalogVersion("testCatalog", "Online");

		galaxynexus = (DeviceModel) productService.getProductForCode("GALAXY_NEXUS");
		smartStandard1Y = (ServicePlanModel) productService.getProductForCode("SMART_STANDARD_1Y");
		internetPackMax = (ServiceAddOnModel) productService.getProductForCode("INTERNET_PACK_MAX");
		weekender = (ServiceAddOnModel) productService.getProductForCode("WEEKENDER");
		tvMobileBasic = (ServiceAddOnModel) productService.getProductForCode("TV_MOBILE_BASIC");
		tvMobileExtrasDocu = (ServiceAddOnModel) productService.getProductForCode("TV_MOBILE_EXTRAS_DOCU");

		smartPhoneDeviceSelection = bundleTemplateService.getBundleTemplateForCode("SmartPhoneDeviceSelection");
		smartPhonePlanSelection = bundleTemplateService.getBundleTemplateForCode("SmartPhonePlanSelection");
		smartPhoneInternetSelection = bundleTemplateService.getBundleTemplateForCode("SmartPhoneInternetSelection");
		smartPhoneAddonSelection = bundleTemplateService.getBundleTemplateForCode("SmartPhoneAddonSelection");
		smartPhoneTvBasicSelection = bundleTemplateService.getBundleTemplateForCode("SmartPhoneTvBasicSelection");
		smartPhoneTvExtrasSelection = bundleTemplateService.getBundleTemplateForCode("SmartPhoneTvExtrasSelection");

		final UserModel telco = userService.getUserForUID("telco");
		final Collection<CartModel> cartModels = telco.getCarts();
		assertEquals("", 1, cartModels.size());
		telcoMasterCart = cartModels.iterator().next();
		cartService.setSessionCart(telcoMasterCart);

		LOG.info("Finished data for BundleTemplateServiceAddOnsBundleBoxesPopulatorIntegrationTest."
				+ (System.currentTimeMillis() - startTime) + "ms");
		modelService.detachAll();
	}

	@Test
	public void testPopulate() throws CommerceCartModificationException
	{
		createAndCheckComplexExtrasPage();
		// create a second bundle to see if the bundles somehow interact with each other
		createAndCheckComplexExtrasPage();
	}

	private void createAndCheckComplexExtrasPage() throws CommerceCartModificationException
	{
		//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
		// device, plan, activation fee are in the cart; internet component should be expanded
		//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
		List<CommerceCartModification> mods = bundleCommerceCartService.addToCart(telcoMasterCart, unitModel, NEW_BUNDLE,
				galaxynexus, smartPhoneDeviceSelection, smartStandard1Y, smartPhonePlanSelection, "<product></product>",
				"<product></product>");
		assertEquals("Cart modifications != 3", 3, mods.size());
		final int bundleNo = mods.iterator().next().getEntry().getBundleNo().intValue();

		BundleTemplateData bundleTemplateData = createNewBundleTemplateData(telcoMasterCart, smartPhonePlanSelection, bundleNo);

		checkExtrasBundleBoxes(bundleTemplateData, 1, smartPhoneInternetSelection.getId(), 3);

		checkExtrasBundleBoxEntry(bundleTemplateData, 1, smartPhoneInternetSelection.getId(), 0, 0);
		checkExtrasBundleBoxEntry(bundleTemplateData, 2, smartPhoneAddonSelection.getId(), 0, 0);
		checkExtrasBundleBoxEntry(bundleTemplateData, 3, smartPhoneTvBasicSelection.getId(), 0, 0);

		//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
		// device, plan, activation fee, INTERNET_PACK_MAX are in the cart; internet component should be expanded
		//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
		mods = bundleCommerceCartService.addToCart(telcoMasterCart, internetPackMax, 1, unitModel, true, bundleNo,
				smartPhoneInternetSelection, false, "<product></product>");
		assertEquals("Cart modifications != 1", 1, mods.size());

		bundleTemplateData = createNewBundleTemplateData(telcoMasterCart, smartPhoneInternetSelection, bundleNo);

		checkExtrasBundleBoxes(bundleTemplateData, 1, smartPhoneInternetSelection.getId(), 3);

		// INTERNET_PACK_MAX is selected; INTERNET_PACK_MAX and INTERNET_PACK_1G are both disabled as upper limit of selection criteria 0To1 
		// is reached -> disabled = 2
		checkExtrasBundleBoxEntry(bundleTemplateData, 1, smartPhoneInternetSelection.getId(), 1, 2);
		checkExtrasBundleBoxEntry(bundleTemplateData, 2, smartPhoneAddonSelection.getId(), 0, 0);
		checkExtrasBundleBoxEntry(bundleTemplateData, 3, smartPhoneTvBasicSelection.getId(), 0, 0);

		//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
		// device, plan, activation fee, INTERNET_PACK_MAX are in the cart; AddOn component should be expanded
		//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
		bundleTemplateData = createNewBundleTemplateData(telcoMasterCart, smartPhoneAddonSelection, bundleNo);

		checkExtrasBundleBoxes(bundleTemplateData, 2, smartPhoneAddonSelection.getId(), 3);

		// INTERNET_PACK_MAX is selected; INTERNET_PACK_MAX and INTERNET_PACK_1G are both disabled as upper limit of selection criteria 0To1 
		// is reached, but only the selected INTERNET_PACK_MAX is displayed -> disabled = 1
		checkExtrasBundleBoxEntry(bundleTemplateData, 1, smartPhoneInternetSelection.getId(), 1, 1);
		checkExtrasBundleBoxEntry(bundleTemplateData, 2, smartPhoneAddonSelection.getId(), 0, 0);
		checkExtrasBundleBoxEntry(bundleTemplateData, 3, smartPhoneTvBasicSelection.getId(), 0, 0);

		//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
		// device, plan, activation fee, INTERNET_PACK_MAX, WEEKENDER are in the cart; AddOn component should be expanded
		//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
		mods = bundleCommerceCartService.addToCart(telcoMasterCart, weekender, 1, unitModel, true, bundleNo,
				smartPhoneAddonSelection, false, "<product></product>");
		assertEquals("Cart modifications != 1", 1, mods.size());

		bundleTemplateData = createNewBundleTemplateData(telcoMasterCart, smartPhoneAddonSelection, bundleNo);

		checkExtrasBundleBoxes(bundleTemplateData, 2, smartPhoneAddonSelection.getId(), 3);

		// INTERNET_PACK_MAX is selected; INTERNET_PACK_MAX and INTERNET_PACK_1G are both disabled as upper limit of selection criteria 0To1 
		// is reached, but only the selected INTERNET_PACK_MAX is displayed -> disabled = 1
		checkExtrasBundleBoxEntry(bundleTemplateData, 1, smartPhoneInternetSelection.getId(), 1, 1);
		// WEEKENDER is selected, FRIENDS_AND_FAMILY is disabled because of disable rule
		checkExtrasBundleBoxEntry(bundleTemplateData, 2, smartPhoneAddonSelection.getId(), 1, 1);
		checkExtrasBundleBoxEntry(bundleTemplateData, 3, smartPhoneTvBasicSelection.getId(), 0, 0);

		//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
		// device, plan, activation fee, INTERNET_PACK_MAX, WEEKENDER are in the cart; TV basic component should be expanded
		//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
		bundleTemplateData = createNewBundleTemplateData(telcoMasterCart, smartPhoneTvBasicSelection, bundleNo);

		checkExtrasBundleBoxes(bundleTemplateData, 3, smartPhoneTvBasicSelection.getId(), 3);

		// INTERNET_PACK_MAX is selected; INTERNET_PACK_MAX and INTERNET_PACK_1G are both disabled as upper limit of selection criteria 0To1 
		// is reached, but only the selected INTERNET_PACK_MAX is displayed -> disabled = 1
		checkExtrasBundleBoxEntry(bundleTemplateData, 1, smartPhoneInternetSelection.getId(), 1, 1);
		// WEEKENDER is selected; FRIENDS_AND_FAMILY is disabled because of disable rule but not displayed -> disabled = 0
		checkExtrasBundleBoxEntry(bundleTemplateData, 2, smartPhoneAddonSelection.getId(), 1, 0);
		checkExtrasBundleBoxEntry(bundleTemplateData, 3, smartPhoneTvBasicSelection.getId(), 0, 0);

		//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
		// device, plan, activation fee, INTERNET_PACK_MAX, WEEKENDER, TV_MOBILE_BASIC are in the cart; TV basic component should be expanded
		//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
		mods = bundleCommerceCartService.addToCart(telcoMasterCart, tvMobileBasic, 1, unitModel, true, bundleNo,
				smartPhoneTvBasicSelection, false, "<product></product>");
		assertEquals("Cart modifications != 1", 1, mods.size());

		bundleTemplateData = createNewBundleTemplateData(telcoMasterCart, smartPhoneTvBasicSelection, bundleNo);

		checkExtrasBundleBoxes(bundleTemplateData, 3, smartPhoneTvBasicSelection.getId(), 4);

		// INTERNET_PACK_MAX is selected; INTERNET_PACK_MAX and INTERNET_PACK_1G are both disabled as upper limit of selection criteria 0To1 
		// is reached, but only the selected INTERNET_PACK_MAX is displayed -> disabled = 1
		checkExtrasBundleBoxEntry(bundleTemplateData, 1, smartPhoneInternetSelection.getId(), 1, 1);
		// WEEKENDER is selected; FRIENDS_AND_FAMILY is disabled because of disable rule but not displayed -> disabled = 0
		checkExtrasBundleBoxEntry(bundleTemplateData, 2, smartPhoneAddonSelection.getId(), 1, 0);
		// TV_MOBILE_BASIC is selected and disabled as upper limit of selection criteria 0To1 is reached
		checkExtrasBundleBoxEntry(bundleTemplateData, 3, smartPhoneTvBasicSelection.getId(), 1, 1);
		checkExtrasBundleBoxEntry(bundleTemplateData, 4, smartPhoneTvExtrasSelection.getId(), 0, 0);

		//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
		// device, plan, activation fee, INTERNET_PACK_MAX, WEEKENDER, TV_MOBILE_BASIC are in the cart; TV extras component should be expanded
		//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
		bundleTemplateData = createNewBundleTemplateData(telcoMasterCart, smartPhoneTvExtrasSelection, bundleNo);

		checkExtrasBundleBoxes(bundleTemplateData, 4, smartPhoneTvExtrasSelection.getId(), 4);

		// INTERNET_PACK_MAX is selected; INTERNET_PACK_MAX and INTERNET_PACK_1G are both disabled as upper limit of selection criteria 0To1 
		// is reached, but only the selected INTERNET_PACK_MAX is displayed -> disabled = 1
		checkExtrasBundleBoxEntry(bundleTemplateData, 1, smartPhoneInternetSelection.getId(), 1, 1);
		// WEEKENDER is selected; FRIENDS_AND_FAMILY is disabled because of disable rule but not displayed -> disabled = 0
		checkExtrasBundleBoxEntry(bundleTemplateData, 2, smartPhoneAddonSelection.getId(), 1, 0);
		// TV_MOBILE_BASIC is selected and disabled as upper limit of selection criteria 0To1 is reached
		checkExtrasBundleBoxEntry(bundleTemplateData, 3, smartPhoneTvBasicSelection.getId(), 1, 1);
		checkExtrasBundleBoxEntry(bundleTemplateData, 4, smartPhoneTvExtrasSelection.getId(), 0, 0);

		//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
		// device, plan, activation fee, INTERNET_PACK_MAX, WEEKENDER, TV_MOBILE_BASIC, TV_MOBILE_EXTRAS_DOCU are in the cart; TV extras component should be expanded
		//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
		mods = bundleCommerceCartService.addToCart(telcoMasterCart, tvMobileExtrasDocu, 1, unitModel, true, bundleNo,
				smartPhoneTvExtrasSelection, false, "<product></product>");
		assertEquals("Cart modifications != 1", 1, mods.size());

		bundleTemplateData = createNewBundleTemplateData(telcoMasterCart, smartPhoneTvExtrasSelection, bundleNo);

		checkExtrasBundleBoxes(bundleTemplateData, 4, smartPhoneTvExtrasSelection.getId(), 4);

		// INTERNET_PACK_MAX is selected; INTERNET_PACK_MAX and INTERNET_PACK_1G are both disabled as upper limit of selection criteria 0To1 
		// is reached, but only the selected INTERNET_PACK_MAX is displayed -> disabled = 1
		checkExtrasBundleBoxEntry(bundleTemplateData, 1, smartPhoneInternetSelection.getId(), 1, 1);
		// WEEKENDER is selected; FRIENDS_AND_FAMILY is disabled because of disable rule but not displayed -> disabled = 0
		checkExtrasBundleBoxEntry(bundleTemplateData, 2, smartPhoneAddonSelection.getId(), 1, 0);
		// TV_MOBILE_BASIC is selected and disabled as upper limit of selection criteria 0To1 is reached
		checkExtrasBundleBoxEntry(bundleTemplateData, 3, smartPhoneTvBasicSelection.getId(), 1, 1);
		// TV_MOBILE_EXTRAS_DOCU is selected, nothing is disabled
		checkExtrasBundleBoxEntry(bundleTemplateData, 4, smartPhoneTvExtrasSelection.getId(), 1, 0);
	}

	private void checkExtrasBundleBoxes(final BundleTemplateData bundleTemplateData, final int expandedStepNo,
			final String expectedExpandedBoxId, final int totalSteps)
	{
		BundleBoxData expandedComponent = null;
		int stepCounter = 0;

		// root component
		compareSourceToTargetComponent(bundleTemplateData);

		// child components
		for (final BundleBoxData bundleBox : bundleTemplateData.getBundleBoxes())
		{
			stepCounter++;
			assertNotNull("", bundleBox.getComponent());

			compareSourceToTargetComponent(bundleBox.getComponent());

			assertEquals("next button and expanded flag do not match", Boolean.valueOf(bundleBox.isExpanded()),
					Boolean.valueOf(bundleBox.isNextButton()));

			if (bundleBox.isExpanded())
			{
				if (expectedExpandedBoxId.equals(bundleBox.getComponent().getId()))
				{
					expandedComponent = bundleBox;
					assertEquals("", expandedStepNo, stepCounter);
				}
				else
				{
					fail("Wrong component ('" + bundleBox.getComponent().getId() + "') is expanded; expected component '"
							+ expectedExpandedBoxId + "'");
				}
			}
		}

		if (expandedComponent == null)
		{
			fail("No component is expanded; expected component '" + expectedExpandedBoxId + "'");
		}

		assertEquals("count of steps is incorrect", totalSteps, stepCounter);

	}

	private void compareSourceToTargetComponent(final BundleTemplateData targetData)
	{
		final BundleTemplateModel sourceModel = bundleTemplateService.getBundleTemplateForCode(targetData.getId());

		assertEquals("", sourceModel.getId(), targetData.getId());
		assertEquals("", sourceModel.getName(), targetData.getName());

	}

	private void checkExtrasBundleBoxEntry(final BundleTemplateData bundleTemplateData, final int stepNo, final String stepId,
			final int expSelectedCount, final int expDisabledCount)
	{
		final BundleTemplateModel templateModel = bundleTemplateService.getBundleTemplateForCode(stepId);
		final List<ProductModel> bundleProducts = templateModel.getProducts();
		final List<ProductModel> boxProducts = new ArrayList<ProductModel>();
		int actSelectedCount = 0;
		int actDisabledCount = 0;

		final BundleBoxData bundleBox = bundleTemplateData.getBundleBoxes().get(stepNo - 1);
		assertEquals("stepId and stepNo belong to different components, sequence of components mixed up?", stepId, bundleBox
				.getComponent().getId());

		for (final BundleBoxEntryData entryData : bundleBox.getBundleBoxEntries())
		{
			final ProductModel boxProduct = productService.getProductForCode(entryData.getProduct().getCode());
			assertTrue("boxProducts must be instances of type ServiceAddOnModel", boxProduct instanceof ServiceAddOnModel);
			if (entryData.isSelected())
			{
				actSelectedCount++;
			}
			if (entryData.isDisabled())
			{
				actDisabledCount++;
			}

			boxProducts.add(boxProduct);
		}

		assertEquals("", expSelectedCount, actSelectedCount);
		assertEquals("", expDisabledCount, actDisabledCount);

		if (bundleBox.isExpanded())
		{
			assertTrue("", boxProducts.containsAll(bundleProducts));
			assertTrue("", bundleProducts.containsAll(boxProducts));
		}
		else
		{
			assertEquals("", expSelectedCount, boxProducts.size());
		}
	}

	private BundleTemplateData createNewBundleTemplateData(final CartModel masterCart, final BundleTemplateModel bundleTemplate,
			final int bundleNo)
	{
		final BundleTemplateData bundleTemplateData = new BundleTemplateData();
		bundleTemplateData.setId(bundleTemplate.getId());

		final BundleTemplateServiceAddOnsPopulatorParameters populatorParams = new BundleTemplateServiceAddOnsPopulatorParameters();
		populatorParams.setCart(masterCart);
		populatorParams.setBundleNo(Integer.valueOf(bundleNo));
		populatorParams.setCurrentComponent(bundleTemplate);

		bundleTemplateAddOnsPopulator.populate(populatorParams, bundleTemplateData);
		return bundleTemplateData;
	}
}

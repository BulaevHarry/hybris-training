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

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertNull;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import de.hybris.platform.catalog.CatalogVersionService;
import de.hybris.platform.commercefacades.product.data.ProductData;
import de.hybris.platform.commerceservices.order.CommerceCartModificationException;
import de.hybris.platform.core.model.c2l.CurrencyModel;
import de.hybris.platform.impex.constants.ImpExConstants;
import de.hybris.platform.jalo.CoreBasicDataCreator;
import de.hybris.platform.product.ProductService;
import de.hybris.platform.servicelayer.ServicelayerTest;
import de.hybris.platform.servicelayer.i18n.CommonI18NService;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.user.UserService;
import de.hybris.platform.site.BaseSiteService;
import de.hybris.platform.subscriptionservices.enums.TermOfServiceFrequency;
import de.hybris.platform.util.Config;
import com.epam.cme.core.model.DeviceModel;
import com.epam.cme.facades.data.BundleTabData;
import com.epam.cme.facades.data.FrequencyTabData;

import java.util.Collections;
import java.util.List;

import javax.annotation.Resource;

import org.apache.log4j.Logger;
import org.junit.Before;
import org.junit.Test;

/**
 * Integration test suite for {@link DeviceBundleTabsPopulator}
 */
public class DeviceBundleTabsPopulatorIntegrationTest extends ServicelayerTest {
    private static final Logger LOG = Logger.getLogger(DeviceBundleTabsPopulatorIntegrationTest.class);
    private static final String TEST_BASESITE_UID = "testSite";

    @Resource
    private DeviceBundleTabsPopulator deviceBundleTabsPopulator;

    @Resource
    private UserService userService;

    @Resource
    private CatalogVersionService catalogVersionService;

    @Resource
    private ProductService productService;

    @Resource
    private CommonI18NService commonI18NService;

    @Resource
    private BaseSiteService baseSiteService;

    @Resource
    private ModelService modelService;

    private CurrencyModel currencyUSD;
    private DeviceModel iPhone4s32gb;

    @Before
    public void setUp() throws Exception {
        // Create data for tests
        LOG.info("Creating data for DeviceBundleTabsPopulatorIntegrationTest ...");
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

        baseSiteService.setCurrentBaseSite(baseSiteService.getBaseSiteForUID(TEST_BASESITE_UID), false);
        catalogVersionService.setSessionCatalogVersion("testCatalog", "Online");
        iPhone4s32gb = (DeviceModel) productService.getProductForCode("APPLE_IPHONE_4S_32GB");
        currencyUSD = commonI18NService.getCurrency("USD");
        commonI18NService.setCurrentCurrency(currencyUSD);

        LOG.info("Finished data for DeviceBundleTabsPopulatorIntegrationTest."
                + (System.currentTimeMillis() - startTime) + "ms");
        modelService.detachAll();
    }

    @Test
    public void testPopulate() throws CommerceCartModificationException {
        final ProductData productData = new ProductData();
        deviceBundleTabsPopulator.populate(iPhone4s32gb, productData);

        assertTrue("", productData.isSoldIndividually());
        assertEquals("", 1, productData.getBundleTabs().size());

        final BundleTabData bundleTabData = productData.getBundleTabs().iterator().next();

        assertEquals("", "SmartPhonePackage", bundleTabData.getParentBundleTemplate().getId());
        assertEquals("", "SmartPhoneDeviceSelection", bundleTabData.getSourceComponent().getId());
        assertEquals("", "SmartPhonePlanSelection", bundleTabData.getTargetComponent().getId());
        assertFalse("", bundleTabData.isPreselected());
        assertEquals("", 3, bundleTabData.getFrequencyTabs().size());

        // freqTab1: 12 months
        checkTabAndPlanData(bundleTabData.getFrequencyTabs(), "SMART_ADVANCED_2000_12M",
                TermOfServiceFrequency.MONTHLY.getCode(), 12, Double.valueOf(60), Double.valueOf(60),
                Double.valueOf(350));
        checkTabAndPlanData(bundleTabData.getFrequencyTabs(), "SMART_ADVANCED_5000_12M",
                TermOfServiceFrequency.MONTHLY.getCode(), 12, Double.valueOf(70), Double.valueOf(70),
                Double.valueOf(330));
        checkTabAndPlanData(bundleTabData.getFrequencyTabs(), "SMART_ADVANCED_NOLIM_12M",
                TermOfServiceFrequency.MONTHLY.getCode(), 12, Double.valueOf(80), Double.valueOf(80),
                Double.valueOf(310));
        // freqTab2: 24 months
        checkTabAndPlanData(bundleTabData.getFrequencyTabs(), "SMART_ADVANCED_2000_24M",
                TermOfServiceFrequency.MONTHLY.getCode(), 24, Double.valueOf(60), Double.valueOf(60), null);
        checkTabAndPlanData(bundleTabData.getFrequencyTabs(), "SMART_ADVANCED_5000_24M",
                TermOfServiceFrequency.MONTHLY.getCode(), 24, Double.valueOf(70), Double.valueOf(70), null);
        checkTabAndPlanData(bundleTabData.getFrequencyTabs(), "SMART_ADVANCED_NOLIM_24M",
                TermOfServiceFrequency.MONTHLY.getCode(), 24, Double.valueOf(80), Double.valueOf(80), null);
        // freqTab3: 36 months
        checkTabAndPlanData(bundleTabData.getFrequencyTabs(), "SMART_ADVANCED_2000_36M",
                TermOfServiceFrequency.MONTHLY.getCode(), 36, Double.valueOf(60), Double.valueOf(60), null);
        checkTabAndPlanData(bundleTabData.getFrequencyTabs(), "SMART_ADVANCED_5000_36M",
                TermOfServiceFrequency.MONTHLY.getCode(), 36, Double.valueOf(70), Double.valueOf(70), null);
        checkTabAndPlanData(bundleTabData.getFrequencyTabs(), "SMART_ADVANCED_NOLIM_36M",
                TermOfServiceFrequency.MONTHLY.getCode(), 36, Double.valueOf(80), Double.valueOf(80), null);
    }

    private void checkTabAndPlanData(final List<FrequencyTabData> freqTabData, final String planCode,
            final String freqCode, final int serviceTermNo, final Double expPrice,
            final Double expThisBundleProductPrice, final Double expOtherBundleProductPrice) {
        int planCounter = 0;
        int freqTabCounter = 0;

        for (final FrequencyTabData freqTab : freqTabData) {
            if (freqCode.equals(freqTab.getTermOfServiceFrequency().getCode())
                    && serviceTermNo == freqTab.getTermOfServiceNumber()) {
                freqTabCounter++;
                assertEquals("", false, freqTab.isPreselected());

                for (final ProductData plan : freqTab.getProducts()) {
                    if (planCode.equals(plan.getCode())) {
                        planCounter++;
                        assertEquals("", planCode, plan.getCode());
                        assertEquals("", "monthly", plan.getSubscriptionTerm().getBillingPlan().getBillingTime()
                                .getCode());
                        assertNotNull("", plan.getUrl());
                        assertTrue("", plan.getUrl().indexOf(planCode) > 0);
                        // YTODO special pricePopulator for subscriptions needed: assertEquals("",
                        // expPrice,
                        // Double.valueOf(plan.getThisBundleProductPrice().getValue().doubleValue()));
                        assertEquals("", currencyUSD.getIsocode(), plan.getThisBundleProductPrice().getCurrencyIso());
                        // YTODO special pricePopulator for subscriptions needed: assertEquals("",
                        // expThisBundleProductPrice,
                        // Double.valueOf(plan.getPrice().getValue().doubleValue()));
                        assertEquals("", currencyUSD.getIsocode(), plan.getPrice().getCurrencyIso());
                        if (expOtherBundleProductPrice == null) {
                            assertNull("", plan.getOtherBundleProductPrice());
                        } else {
                            assertEquals("", expOtherBundleProductPrice,
                                    Double.valueOf(plan.getOtherBundleProductPrice().getValue().doubleValue()));
                            assertEquals("", currencyUSD.getIsocode(), plan.getOtherBundleProductPrice()
                                    .getCurrencyIso());
                        }
                        assertEquals("", false, plan.isPreselected());
                    }
                }
            }
        }

        assertEquals("", 1, freqTabCounter);
        assertEquals("", 1, planCounter);
    }

}

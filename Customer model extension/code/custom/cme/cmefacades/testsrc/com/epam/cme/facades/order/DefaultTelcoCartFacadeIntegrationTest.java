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
package com.epam.cme.facades.order;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.commercefacades.order.data.CartData;
import de.hybris.platform.commercefacades.order.data.CartModificationData;
import de.hybris.platform.commercefacades.order.data.OrderEntryData;
import de.hybris.platform.commerceservices.order.CommerceCartModificationException;
import de.hybris.platform.commerceservices.order.CommerceCartModificationStatus;
import de.hybris.platform.core.model.c2l.CurrencyModel;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.impex.constants.ImpExConstants;
import de.hybris.platform.impex.jalo.ImpExException;
import de.hybris.platform.jalo.c2l.Currency;
import de.hybris.platform.order.CartService;
import de.hybris.platform.servicelayer.ServicelayerTest;
import de.hybris.platform.servicelayer.i18n.CommonI18NService;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.site.BaseSiteService;
import de.hybris.platform.util.Config;
import com.epam.cme.facades.order.impl.DefaultBundleCartFacade;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;

@IntegrationTest
public class DefaultTelcoCartFacadeIntegrationTest extends ServicelayerTest {
    private static final String TEST_BASESITE_UID = "testSite";

    @Resource
    private DefaultBundleCartFacade defaultBundleCartFacade;

    @Resource
    private CartService cartService;

    @Resource
    private ModelService modelService;

    @Resource
    private CommonI18NService commonI18NService;

    @Resource
    private BaseSiteService baseSiteService;

    private static final String PRODUCT_MODEL_CODE_1 = "3417123";
    private static final String PRODUCT_MODEL_CODE_2 = "3417124";

    private static final String SUBSCRIPTION_PRODUCT_CODE_MONTHLY = "Y_STARTER_100_1Y";
    private static final String SUBSCRIPTION_PRODUCT_CODE_QUARTERLY = "Y_STARTER_200_2Y";
    private static final String SUBSCRIPTION_PRODUCT_CODE_YEARLY = "Y_STARTER_300_2Y";
    private static final String SUBSCRIPTION_PRODUCT_CODE_PAYNOW = "Y_STARTER_100_2Y";

    @Before
    public void setUp() throws ImpExException, CommerceCartModificationException {
        // importing test csv
        final String legacyModeBackup = Config.getParameter(ImpExConstants.Params.LEGACY_MODE_KEY);
        Config.setParameter(ImpExConstants.Params.LEGACY_MODE_KEY, "true");
        importCsv("/commerceservices/test/testCommerceCart.csv", "utf-8");
        Config.setParameter(ImpExConstants.Params.LEGACY_MODE_KEY, "false");
        importCsv("/subscriptionservices/test/testSubscriptionCommerceCartService.impex", "utf-8");
        importCsv("/configurablebundleservices/test/testBundleCommerceCartService.impex", "utf-8");
        importCsv("/configurablebundleservices/test/testApproveAllBundleTemplates.impex", "utf-8");
        Config.setParameter(ImpExConstants.Params.LEGACY_MODE_KEY, legacyModeBackup);

        baseSiteService.setCurrentBaseSite(baseSiteService.getBaseSiteForUID(TEST_BASESITE_UID), false);
        final CurrencyModel currency = commonI18NService.getCurrency("USD");
        final CustomerModel customer = modelService.create(CustomerModel.class);
        customer.setUid("customer");
        modelService.save(customer);
        final CartModel cartModel = modelService.create(CartModel.class);
        cartModel.setCurrency(currency);
        cartModel.setDate(new Date());
        cartModel.setNet(Boolean.TRUE);
        cartModel.setUser(customer);
        modelService.save(cartModel);
        cartService.setSessionCart(cartModel);
        jaloSession.getSessionContext().setCurrency((Currency) modelService.getSource(currency));

        defaultBundleCartFacade.addToCart(PRODUCT_MODEL_CODE_1, 1);
        defaultBundleCartFacade.addToCart(PRODUCT_MODEL_CODE_2, 1);
        defaultBundleCartFacade.addToCart(SUBSCRIPTION_PRODUCT_CODE_MONTHLY, 1);
        defaultBundleCartFacade.addToCart(SUBSCRIPTION_PRODUCT_CODE_QUARTERLY, 1);
        defaultBundleCartFacade.addToCart(SUBSCRIPTION_PRODUCT_CODE_YEARLY, 1);
        defaultBundleCartFacade.addToCart(SUBSCRIPTION_PRODUCT_CODE_PAYNOW, 1);

        modelService.detachAll();
    }

    @Test
    public void testUpdateCartEntry() throws CommerceCartModificationException {
        defaultBundleCartFacade.updateCartEntry(0, 2);
        final CartData cartData = defaultBundleCartFacade.getSessionCart();

        final List<OrderEntryData> entryDatas = new ArrayList<OrderEntryData>(cartData.getEntries());
        for (final OrderEntryData entryData : entryDatas) {
            if (PRODUCT_MODEL_CODE_1.equals(entryData.getProduct().getCode())) {
                Assert.assertEquals(2, entryData.getQuantity().intValue());
            }
            if (PRODUCT_MODEL_CODE_2.equals(entryData.getProduct().getCode())) {
                Assert.assertEquals(1, entryData.getQuantity().intValue());
            }
        }
    }

    @Test
    public void testaddToCartBundle() throws CommerceCartModificationException {
        final List<CartModificationData> results = defaultBundleCartFacade.addToCart("GALAXY_NEXUS", -1,
                "SmartPhoneDeviceSelection", "Y_STARTER_100_1Y", "SmartPhonePlanSelection");

        for (final CartModificationData mod : results) {
            assertNotNull("", mod.getEntry());
            assertEquals("", Long.valueOf(1), mod.getEntry().getQuantity());
            assertEquals("", 1, mod.getEntry().getQuantity().longValue());
            assertEquals("", CommerceCartModificationStatus.SUCCESS, mod.getStatusCode());
        }
    }

    @Test
    public void testaddToCartAsBundle() throws CommerceCartModificationException {
        defaultBundleCartFacade.addToCart("GALAXY_NEXUS", -1, "SmartPhoneDeviceSelection", "Y_STARTER_100_1Y",
                "SmartPhonePlanSelection");

        final CartData cartData = defaultBundleCartFacade.getSessionCart();
        final OrderEntryData orderEntry = cartData.getEntries().iterator().next();

        Assert.assertTrue((orderEntry.getBasePrice().getValue().compareTo(orderEntry.getTotalPrice().getValue()) < 0));

    }
}

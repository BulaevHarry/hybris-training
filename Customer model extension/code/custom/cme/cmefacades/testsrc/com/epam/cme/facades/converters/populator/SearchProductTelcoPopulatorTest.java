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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.BDDMockito.given;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commercefacades.product.PriceDataFactory;
import de.hybris.platform.commercefacades.product.data.PriceData;
import de.hybris.platform.commercefacades.product.data.PriceDataType;
import de.hybris.platform.commercefacades.product.data.ProductData;
import de.hybris.platform.commerceservices.search.resultdata.SearchResultValueData;
import de.hybris.platform.core.model.c2l.CurrencyModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.servicelayer.i18n.CommonI18NService;
import de.hybris.platform.subscriptionservices.model.BillingPlanModel;
import de.hybris.platform.subscriptionservices.model.SubscriptionTermModel;
import com.epam.cme.facades.converters.populator.SearchProductTelcoPopulator;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

/**
 * Test suite for {@link SearchProductTelcoPopulator}
 */
@UnitTest
public class SearchProductTelcoPopulatorTest {
    private SearchProductTelcoPopulator searchProductTelcoPopulator;
    @Mock
    private PriceDataFactory priceDataFactory;
    @Mock
    private CommonI18NService commonI18NService;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        searchProductTelcoPopulator = new SearchProductTelcoPopulator();
        searchProductTelcoPopulator.setCommonI18NService(commonI18NService);
        searchProductTelcoPopulator.setPriceDataFactory(priceDataFactory);
    }

    @Test
    public void testPopulate() {
        // create search result values
        final SearchResultValueData searchResultValueData = new SearchResultValueData();
        final Map<String, Object> searchValueMap = new HashMap<String, Object>();
        searchValueMap.put(BillingPlanModel.BILLINGFREQUENCY, "monthly");
        searchValueMap.put(ProductModel.SOLDINDIVIDUALLY, Boolean.TRUE);
        searchValueMap.put(SubscriptionTermModel.TERMOFSERVICERENEWAL, "yearly");
        searchValueMap.put("termLimit", "18 months");
        searchValueMap.put("lowestBundlePriceValue", Double.valueOf(1.99));
        searchResultValueData.setValues(searchValueMap);

        final CurrencyModel currency = new CurrencyModel();
        currency.setIsocode("USD");
        final PriceData priceData = new PriceData();
        priceData.setValue(BigDecimal.valueOf(1.99));
        priceData.setCurrencyIso(currency.getIsocode());
        given(commonI18NService.getCurrentCurrency()).willReturn(currency);
        given(priceDataFactory.create(PriceDataType.BUY, BigDecimal.valueOf(1.99), currency.getIsocode())).willReturn(
                priceData);

        final ProductData productData = new ProductData();
        searchProductTelcoPopulator.populate(searchResultValueData, productData);

        assertNotNull("", productData.getSubscriptionTerm());
        assertNotNull("", productData.getSubscriptionTerm().getBillingPlan());
        assertNotNull("", productData.getSubscriptionTerm().getBillingPlan().getBillingTime());
        assertEquals("", searchProductTelcoPopulator.getValue(searchResultValueData, "billingTime"), productData
                .getSubscriptionTerm().getBillingPlan().getBillingTime().getName());
        assertEquals("", searchProductTelcoPopulator.getValue(searchResultValueData, ProductModel.SOLDINDIVIDUALLY),
                Boolean.valueOf(productData.isSoldIndividually()));
        assertNotNull("", productData.getSubscriptionTerm().getTermOfServiceFrequency());
        assertEquals("", searchProductTelcoPopulator.getValue(searchResultValueData, "termLimit"), productData
                .getSubscriptionTerm().getTermOfServiceFrequency().getName());
        assertNotNull("", productData.getLowestBundlePrice());
        assertEquals("", BigDecimal.valueOf(((Double) searchProductTelcoPopulator.getValue(searchResultValueData,
                "lowestBundlePriceValue")).doubleValue()), productData.getLowestBundlePrice().getValue());
    }
}

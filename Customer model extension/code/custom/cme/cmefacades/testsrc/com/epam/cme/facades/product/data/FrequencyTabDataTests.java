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
package com.epam.cme.facades.product.data;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.subscriptionfacades.data.TermOfServiceFrequencyData;
import de.hybris.platform.subscriptionservices.enums.TermOfServiceFrequency;
import com.epam.cme.facades.data.FrequencyTabData;
import com.epam.cme.facades.product.FrequencyTabDataComparator;

import org.junit.Assert;
import org.junit.Test;

/**
 * Test to check the sorting of FrequencyTabData
 */

@UnitTest
@SuppressWarnings("deprecation")
public class FrequencyTabDataTests {
    @Test
    public void testFrequencyTabDataSortSameFrequencyDiffFreqNumber() {

        assertOrder(TermOfServiceFrequency.DAILY.getCode(), 5, TermOfServiceFrequency.DAILY.getCode(), 6);
    }

    @Test
    public void testFrequencyTabDataSortDiffFrequency() {
        assertOrder(TermOfServiceFrequency.NONE.getCode(), 0, TermOfServiceFrequency.DAILY.getCode(), 6);

    }

    @Test
    public void testFrequencyTabDataMonthVsAnnualFrequency() {
        assertOrder(TermOfServiceFrequency.MONTHLY.getCode(), 20, TermOfServiceFrequency.ANNUALLY.getCode(), 1);
    }

    @Test
    public void testFrequencyTabDataNoneFrequency() {
        assertEquals(TermOfServiceFrequency.NONE.getCode(), 0, TermOfServiceFrequency.NONE.getCode(), 0);

    }

    private void assertOrder(final String frequency1, final int number1, final String frequency2, final int number2) {
        final FrequencyTabData container1 = new FrequencyTabData();
        container1.setTermOfServiceNumber(number1);
        final TermOfServiceFrequencyData termOfServiceFrequency1 = new TermOfServiceFrequencyData();
        termOfServiceFrequency1.setCode(frequency1);
        container1.setTermOfServiceFrequency(termOfServiceFrequency1);

        final FrequencyTabData container2 = new FrequencyTabData();
        container2.setTermOfServiceNumber(number2);
        final TermOfServiceFrequencyData termOfServiceFrequency2 = new TermOfServiceFrequencyData();
        termOfServiceFrequency2.setCode(frequency2);
        container2.setTermOfServiceFrequency(termOfServiceFrequency2);
        Assert.assertTrue(new FrequencyTabDataComparator().compare(container1, container2) < 0);
    }

    private void assertEquals(final String frequency1, final int number1, final String frequency2, final int number2) {
        final FrequencyTabData container1 = new FrequencyTabData();
        container1.setTermOfServiceNumber(number1);
        final TermOfServiceFrequencyData termOfServiceFrequency1 = new TermOfServiceFrequencyData();
        termOfServiceFrequency1.setCode(frequency1);
        container1.setTermOfServiceFrequency(termOfServiceFrequency1);

        final FrequencyTabData container2 = new FrequencyTabData();
        container2.setTermOfServiceNumber(number2);
        final TermOfServiceFrequencyData termOfServiceFrequency2 = new TermOfServiceFrequencyData();
        termOfServiceFrequency2.setCode(frequency2);
        container2.setTermOfServiceFrequency(termOfServiceFrequency2);
        Assert.assertTrue(new FrequencyTabDataComparator().compare(container1, container2) == 0);
    }
}

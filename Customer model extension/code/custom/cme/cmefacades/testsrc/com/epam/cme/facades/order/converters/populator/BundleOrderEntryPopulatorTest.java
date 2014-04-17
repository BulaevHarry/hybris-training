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
package com.epam.cme.facades.order.converters.populator;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commercefacades.order.data.OrderEntryData;
import de.hybris.platform.core.model.order.CartEntryModel;
import de.hybris.platform.core.model.order.OrderEntryModel;
import com.epam.cme.facades.order.converters.populator.BundleOrderEntryPopulator;

import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

@UnitTest
public class BundleOrderEntryPopulatorTest {

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    private final BundleOrderEntryPopulator bundlingOrderEntryPopulator = new BundleOrderEntryPopulator();

    @Test
    public void testSourceParamCannotBeNull() {
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("source cannot be null");

        bundlingOrderEntryPopulator.populate(null, new OrderEntryData());
    }

    @Test
    public void testTargetParamCannotBeNull() {
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("target cannot be null");

        bundlingOrderEntryPopulator.populate(new OrderEntryModel(), null);
    }

    @Test
    public void testStandAloneProductsNotEditable() {
        final OrderEntryData entry = new OrderEntryData();
        final CartEntryModel cartEntry = new CartEntryModel();
        cartEntry.setBundleNo(Integer.valueOf(0));
        bundlingOrderEntryPopulator.adjustEditable(entry, cartEntry, null);
        Assert.assertFalse(entry.isEditable());
    }
}

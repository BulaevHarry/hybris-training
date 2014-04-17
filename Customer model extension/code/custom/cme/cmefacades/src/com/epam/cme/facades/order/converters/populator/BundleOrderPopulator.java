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

import de.hybris.platform.commercefacades.order.data.OrderData;
import de.hybris.platform.core.model.order.OrderModel;

import org.springframework.util.Assert;

/**
 * Bundling specific converter implementation for {@link OrderModel} as source and {@link OrderData}
 * as target type.
 */
public class BundleOrderPopulator<S extends OrderModel, T extends OrderData> extends AbstractBundleOrderPopulator<S, T> {
    @Override
    public void populate(final S source, final T target) {
        Assert.notNull(source, "Parameter source cannot be null.");
        Assert.notNull(target, "Parameter target cannot be null.");

        if (source.getBillingTime() == null) {
            // compatibility mode: do not perform the bundling specific populator tasks
            return;
        }

    }
}

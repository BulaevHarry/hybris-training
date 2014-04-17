/*
 * [y] hybris Platform
 *
 * Copyright (c) 2000-2012 hybris AG
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of hybris
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with hybris.
 *
 *
 */
package com.epam.cme.facades.product;

import de.hybris.platform.commerceservices.util.AbstractComparator;
import de.hybris.platform.subscriptionservices.enums.TermOfServiceFrequency;
import com.epam.cme.facades.data.FrequencyTabData;

import org.apache.commons.lang.StringUtils;

public class FrequencyTabDataComparator extends AbstractComparator<FrequencyTabData> {
    @Override
    protected int compareInstances(final FrequencyTabData thisFrequencyData, final FrequencyTabData thatFrequencyData) {
        final TermOfServiceFrequency thisToSFrequency = TermOfServiceFrequency.valueOf(StringUtils
                .upperCase(thisFrequencyData.getTermOfServiceFrequency().getCode()));
        final TermOfServiceFrequency thatToSFrequency = TermOfServiceFrequency.valueOf(StringUtils
                .upperCase(thatFrequencyData.getTermOfServiceFrequency().getCode()));
        if (thisToSFrequency.equals(thatToSFrequency) && !thisToSFrequency.equals(TermOfServiceFrequency.NONE)) {
            return thisFrequencyData.getTermOfServiceNumber() - thatFrequencyData.getTermOfServiceNumber();
        } else {
            return thisToSFrequency.compareTo(thatToSFrequency);
        }
    }
}

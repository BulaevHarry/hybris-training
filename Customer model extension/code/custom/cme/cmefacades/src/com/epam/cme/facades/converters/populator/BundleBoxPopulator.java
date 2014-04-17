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

import de.hybris.platform.configurablebundleservices.model.BundleTemplateModel;
import de.hybris.platform.converters.Converters;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.subscriptionservices.model.SubscriptionProductModel;
import com.epam.cme.facades.data.BundleBoxData;
import com.epam.cme.facades.data.BundleBoxEntryData;
import com.epam.cme.facades.data.BundleTemplateData;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;

/**
 * It converts the {@link BundleTemplateModel} for extra in guided selling to {@link BundleBoxData}
 */
public class BundleBoxPopulator<SOURCE extends BundleTemplateModel, TARGET extends BundleBoxData> implements
        Populator<SOURCE, TARGET> {
    private static final Logger LOG = Logger.getLogger(BundleBoxPopulator.class);
    private Converter<BundleTemplateModel, BundleTemplateData> bundleTemplateConverter;
    private Converter<ProductModel, BundleBoxEntryData> bundleBoxEntryConverter;

    @Override
    public void populate(final SOURCE source, final TARGET target) throws ConversionException {
        final BundleTemplateData bundleTemplateData = getBundleTemplateConverter().convert(source);
        target.setComponent(bundleTemplateData);

        final List<ProductModel> subscriptionProducts = new ArrayList<ProductModel>();
        for (final ProductModel product : source.getProducts()) {
            if (product instanceof SubscriptionProductModel) {
                subscriptionProducts.add(product);
            } else {
                LOG.error("Product '" + product.getCode() + "' is not a SubscriptionProduct. Ignoring it.");
            }
        }

        final List<BundleBoxEntryData> boxEntryDatas = Converters.convertAll(subscriptionProducts,
                getBundleBoxEntryConverter());
        target.setBundleBoxEntries(boxEntryDatas);
        target.setNextButton(true);
        target.setReviewButton(false);
    }

    protected Converter<BundleTemplateModel, BundleTemplateData> getBundleTemplateConverter() {
        return bundleTemplateConverter;
    }

    @Required
    public void setBundleTemplateConverter(
            final Converter<BundleTemplateModel, BundleTemplateData> bundleTemplateConverter) {
        this.bundleTemplateConverter = bundleTemplateConverter;
    }

    protected Converter<ProductModel, BundleBoxEntryData> getBundleBoxEntryConverter() {
        return bundleBoxEntryConverter;
    }

    @Required
    public void setBundleBoxEntryConverter(final Converter<ProductModel, BundleBoxEntryData> bundleBoxEntryConverter) {
        this.bundleBoxEntryConverter = bundleBoxEntryConverter;
    }

}

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

import de.hybris.platform.commercefacades.order.data.AbstractOrderData;
import de.hybris.platform.commercefacades.order.data.OrderEntryData;
import de.hybris.platform.configurablebundleservices.bundle.BundleTemplateService;
import de.hybris.platform.configurablebundleservices.model.BundleTemplateModel;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.subscriptionfacades.order.converters.populator.AbstractSubscriptionOrderPopulator;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.springframework.beans.factory.annotation.Required;

/**
 * Abstract class for order converters for bundles. This class is responsible for sorting the order
 * entries by the bundle template they are assigned to.
 * 
 */
public abstract class AbstractBundleOrderPopulator<SOURCE extends AbstractOrderModel, TARGET extends AbstractOrderData>
        extends AbstractSubscriptionOrderPopulator<SOURCE, TARGET> {
    private BundleTemplateService bundleTemplateService;

    /**
     * This method returns the given order entries sorted by the bundle number and component's
     * position.
     * 
     * @param entries
     *            {@link List} of {@link AbstractOrderEntryModel}s to be sorted
     * @return {@link List} of {@link AbstractOrderEntryModel}s ordered by the bundleNo and
     *         component.
     */
    protected List<OrderEntryData> getSortedEntryListBasedOnBundleAndComponent(final List<OrderEntryData> entries) {
        Collections.sort(entries, new OrderComparator());
        return entries;
    }

    class OrderComparator implements Comparator<OrderEntryData> {
        @Override
        public int compare(final OrderEntryData arg0, final OrderEntryData arg1) {
            // sort standalone items as last items
            if (arg0.getBundleNo() == 0) {
                return 1;
            }
            if (arg1.getBundleNo() == 0) {
                return -1;
            }

            // first comparing based on the bundleNo
            final int compare = Integer.valueOf(arg0.getBundleNo()).compareTo(Integer.valueOf(arg1.getBundleNo()));
            if (compare != 0) {
                return compare;
            }

            // second comparing based on the sort position of the bundletemplate
            Integer arg0pos = null;
            Integer arg1pos = null;
            if (arg0.getComponent() != null) {
                final BundleTemplateModel component0 = getBundleTemplateService().getBundleTemplateForCode(
                        arg0.getComponent().getId(), arg0.getComponent().getVersion());
                arg0pos = Integer.valueOf(getBundleTemplateService().getPositionInParent(component0));
            }
            if (arg1.getComponent() != null) {
                final BundleTemplateModel component1 = getBundleTemplateService().getBundleTemplateForCode(
                        arg1.getComponent().getId(), arg1.getComponent().getVersion());
                arg1pos = Integer.valueOf(getBundleTemplateService().getPositionInParent(component1));
            }
            if (arg0pos != null && arg1pos != null) {
                return arg0pos.compareTo(arg1pos);
            }

            return 0;
        }
    }

    protected BundleTemplateService getBundleTemplateService() {
        return bundleTemplateService;
    }

    @Required
    public void setBundleTemplateService(final BundleTemplateService bundleTemplateService) {
        this.bundleTemplateService = bundleTemplateService;
    }

}

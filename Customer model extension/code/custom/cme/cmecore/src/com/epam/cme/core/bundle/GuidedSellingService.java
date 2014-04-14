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
package com.epam.cme.core.bundle;

import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.configurablebundleservices.model.BundleTemplateModel;


/**
 * Guided Selling service that exposes methods to find the position of a component within a package and to determine
 * whether a component has to be displayed in the frontend.
 * 
 * @spring.bean guidedSellingService
 */
public interface GuidedSellingService
{
	/**
	 * Returns the next child bundle template (component) that shall be displayed in the extras page. A component is
	 * defined as subsequent extra component if it comes after the given <code>bundleTemplate</code> and if its selection
	 * dependencies are fulfilled and if it is not an auto-pick component and if it contains add-on products.
	 * 
	 * @param masterAbstractOrder
	 *           the master cart/order
	 * @param bundleTemplate
	 *           the component for which the subsequent component is searched
	 * @param bundleNo
	 *           the number of the bundle the <code>bundleTemplate<code> belongs to
	 * @param relativeposition
	 * @return the subsequent {@link BundleTemplateModel} if there is one, otherwise <code>null</code>
	 */
	BundleTemplateModel getRelativeSelectionComponent(final AbstractOrderModel masterAbstractOrder,
			final BundleTemplateModel bundleTemplate, final int bundleNo, final int relativeposition);

	/**
	 * Checks if the given <code>bundleTemplate</code> (component) fulfills the criteria to be displayed on the extras
	 * page. A component can be displayed on the extras page if its selection dependencies are fulfilled and if it is not
	 * an auto-pick component and if it contains products that have the given <code>clazz</code>.
	 * 
	 * @param masterAbstractOrder
	 *           the master cart/order
	 * @param bundleTemplate
	 *           the component which is checked
	 * @param bundleNo
	 *           the number of the bundle the <code>bundleTemplate<code> belongs to
	 * @param clazzes
	 *           classes of the products that are displayed on the extras page (e.g. ServiceAddOnModel.class)
	 * @return <code>true</code> if the given <code>bundleTemplate</code> can be displayed on the extras page, otherwise
	 *         <code>false</code>
	 */
	boolean isComponentToBeDisplayedOnGuidedSellingSelectPage(AbstractOrderModel masterAbstractOrder,
			BundleTemplateModel bundleTemplate, int bundleNo, final Class<? extends ProductModel>... clazzes);
}

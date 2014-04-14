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
package com.epam.cme.storefront.controllers;

import com.epam.cme.storefront.controllers.ControllerConstants;
import com.epam.cme.core.model.ProductReferencesAndClassificationsComponentModel;
import com.epam.cme.core.model.ProductReferencesAndClassificationsForDevicesComponentModel;


public interface TelcoControllerConstants extends ControllerConstants
{

	/**
	 * Class with action name constants
	 */
	interface Actions
	{
		interface Cms
		{
			String _Prefix = "/view/";
			String _Suffix = "Controller";



			/**
			 * CMS components that have specific handlers
			 */

			String ProductReferencesAndClassificationsComponent = _Prefix
					+ ProductReferencesAndClassificationsComponentModel._TYPECODE + _Suffix;
			String ProductReferencesAndClassificationsForDevicesComponent = _Prefix
					+ ProductReferencesAndClassificationsForDevicesComponentModel._TYPECODE + _Suffix;
		}
	}

	interface Views
	{
		interface Pages
		{
			interface GuidedSelling
			{
				String editComponentSolrStylePage = "pages/telco/guidedselling/editComponentSolrStylePage";
				String editComponentAccordeonStylePage = "pages/telco/guidedselling/editComponentAccordeonStylePage";
				String viewAllServicePlansPage = "pages/telco/guidedselling/viewAllServicePlansPage";
			}
		}
	}
}

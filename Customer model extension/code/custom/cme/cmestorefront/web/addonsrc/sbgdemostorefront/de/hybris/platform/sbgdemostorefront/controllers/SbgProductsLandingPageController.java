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
package de.hybris.platform.sbgdemostorefront.controllers;

import de.hybris.platform.addonsupport.controllers.AbstractAddOnController;
import de.hybris.platform.commercefacades.order.CartFacade;
import de.hybris.platform.commerceservices.order.CommerceCartModificationException;
import de.hybris.platform.order.InvalidCartException;

import javax.annotation.Resource;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;


@Controller("SbgProductsLandingPageController")
@RequestMapping(value = "/sbgproducts")
public class SbgProductsLandingPageController extends AbstractAddOnController
{
	protected static final Logger LOG = Logger.getLogger(SbgProductsLandingPageController.class);

	@Resource(name = "cartFacade")
	private CartFacade cartFacade;

	@RequestMapping(value = "/sbgaddtocart", method = RequestMethod.POST)
	public String postAddToCart(@RequestParam("productCodePost") final String code) throws CommerceCartModificationException,
			InvalidCartException
	{
		LOG.debug("Posting addToCart for " + code);
		cartFacade.addToCart(code, 1);

		return REDIRECT_PREFIX + "/cart/checkout";
	}

	protected CartFacade getCartFacade()
	{
		return cartFacade;
	}

}

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
package com.epam.cme.storefront.controllers.pages;


import de.hybris.platform.acceleratorservices.config.SiteConfigService;
import de.hybris.platform.cms2.exceptions.CMSItemNotFoundException;
import de.hybris.platform.commercefacades.order.data.CartData;
import de.hybris.platform.commercefacades.order.data.CartModificationData;
import de.hybris.platform.commercefacades.order.data.OrderEntryData;
import de.hybris.platform.commerceservices.order.CommerceCartModificationException;
import de.hybris.platform.servicelayer.session.SessionService;
import com.epam.cme.storefront.breadcrumb.ResourceBreadcrumbBuilder;
import com.epam.cme.storefront.constants.WebConstants;
import com.epam.cme.storefront.controllers.ControllerConstants;
import com.epam.cme.storefront.controllers.pages.AbstractPageController;
import com.epam.cme.storefront.controllers.util.GlobalMessages;
import com.epam.cme.storefront.forms.UpdateQuantityForm;
import com.epam.cme.facades.order.BundleCartFacade;
import com.epam.cme.storefront.forms.DeleteBundleForm;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.annotation.Resource;
import javax.validation.Valid;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;


/**
 * Controller for cart page
 */
@Controller
@RequestMapping(value = "/cart")
public class TelcoCartPageController extends AbstractPageController
{
	protected static final Logger LOG = Logger.getLogger(TelcoCartPageController.class);

	public static final String SHOW_CHECKOUT_STRATEGY_OPTIONS = "storefront.show.checkout.flows";

	private static final String CART_CMS_PAGE_LABEL = "cart";
	private static final String CONTINUE_URL = "continueUrl";

	@Resource(name = "cartFacade")
	private BundleCartFacade cartFacade;

	@Resource(name = "siteConfigService")
	private SiteConfigService siteConfigService;

	@Resource(name = "sessionService")
	private SessionService sessionService;

	@Resource(name = "simpleBreadcrumbBuilder")
	private ResourceBreadcrumbBuilder resourceBreadcrumbBuilder;

	// Public getter used in a test
	@Override
	public SiteConfigService getSiteConfigService()
	{
		return siteConfigService;
	}

	@ModelAttribute("showCheckoutStrategies")
	public boolean isCheckoutStrategyVisible()
	{
		final String property = getSiteConfigService().getProperty(SHOW_CHECKOUT_STRATEGY_OPTIONS);
		return !StringUtils.isBlank(property) && Boolean.parseBoolean(property);
	}

	/*
	 * Display the cart page
	 */
	@RequestMapping(method = RequestMethod.GET)
	public String showCart(final Model model) throws CMSItemNotFoundException
	{
		prepareDataForPage(model);
		return ControllerConstants.Views.Pages.Cart.CartPage;
	}

	/**
	 * Handle the '/cart/checkout' request url. This method checks to see if the cart is valid before allowing the
	 * checkout to begin. Note that this method does not require the user to be authenticated and therefore allows us to
	 * validate that the cart is valid without first forcing the user to login. The cart will be checked again once the
	 * user has logged in.
	 * 
	 * @return The page to redirect to
	 */
	@RequestMapping(value = "/checkout", method = RequestMethod.GET)
	public String cartCheck()
	{
		if (!cartFacade.hasSessionCart() || cartFacade.getSessionCart().getEntries().isEmpty())
		{
			LOG.info("Missing or empty cart");

			// No session cart or empty session cart. Bounce back to the cart page.
			return REDIRECT_PREFIX + "/cart";
		}

		if (!cartFacade.isCartValid())
		{
			LOG.info("Cart contains invalid component(s)");
			return REDIRECT_PREFIX + "/cart";
		}

		// Redirect to the start of the checkout flow to begin the checkout process
		// We just redirect to the generic '/checkout' page which will actually select the checkout flow
		// to use. The customer is not necessarily logged in on this request, but will be forced to login
		// when they arrive on the '/checkout' page.
		return REDIRECT_PREFIX + "/checkout";
	}

	// This controller method is used to allow the site to force the visitor through a specified checkout flow.
	// If you only have a static configured checkout flow then you can remove this method.
	@RequestMapping(value = "/checkout/select-flow", method = RequestMethod.GET)
	public String initCheck()
	{
		if (!cartFacade.hasSessionCart() || cartFacade.getSessionCart().getEntries().isEmpty())
		{
			LOG.info("Missing or empty cart");

			// No session cart or empty session cart. Bounce back to the cart page.
			return REDIRECT_PREFIX + "/cart";
		}

		// Redirect to the start of the checkout flow to begin the checkout process
		// We just redirect to the generic '/checkout' page which will actually select the checkout flow
		// to use. The customer is not necessarily logged in on this request, but will be forced to login
		// when they arrive on the '/checkout' page.
		return REDIRECT_PREFIX + "/checkout";
	}

	/**
	 * Handle the delete of a bundle
	 * 
	 * @param form
	 *           bundle delete form, indicating the bundle No
	 * @param model
	 * @param bindingResult
	 * @param redirectModel
	 * @return redirect url
	 * @throws CMSItemNotFoundException
	 */
	@RequestMapping(value = "/delete", method = RequestMethod.POST)
	public String deleteCartBundle(@Valid final DeleteBundleForm form, final Model model, final BindingResult bindingResult,
			final RedirectAttributes redirectModel) throws CMSItemNotFoundException
	{
		if (bindingResult.hasErrors())
		{
			for (final ObjectError error : bindingResult.getAllErrors())
			{
				GlobalMessages.addErrorMessage(model, error.getDefaultMessage());

			}
		}

		try
		{
			cartFacade.deleteCartBundle(form.getBundleNo());

			// Success in removing entry
			redirectModel.addFlashAttribute(GlobalMessages.CONF_MESSAGES_HOLDER,
					Collections.singletonList("basket.page.bundle.message.remove"));

		}
		catch (final CommerceCartModificationException e)
		{

			LOG.info("Could not delete bundle No" + form.getBundleNo());
			redirectModel.addFlashAttribute(GlobalMessages.CONF_MESSAGES_HOLDER,
					Collections.singletonList("basket.page.bundle.message.remove.error"));
		}

		prepareDataForPage(model);
		return REDIRECT_PREFIX + "/cart";

	}

	@RequestMapping(value = "/update", method = RequestMethod.POST)
	public String updateCartQuantities(@RequestParam("entryNumber") final long entryNumber, final Model model,
			@Valid final UpdateQuantityForm form, final BindingResult bindingResult, final RedirectAttributes redirectModel)
			throws CMSItemNotFoundException
	{
		if (bindingResult.hasErrors())
		{
			for (final ObjectError error : bindingResult.getAllErrors())
			{
				if (error.getCode().equals("typeMismatch"))
				{
					GlobalMessages.addErrorMessage(model, "basket.error.quantity.invalid");
				}
				else
				{
					GlobalMessages.addErrorMessage(model, error.getDefaultMessage());
				}
			}
		}
		else if (cartFacade.getSessionCart().getEntries() != null)
		{
			try
			{
				final CartModificationData cartModification = cartFacade.updateCartEntry(entryNumber, form.getQuantity().longValue());
				if (cartModification.getQuantity() == form.getQuantity().longValue())
				{
					// Success

					if (cartModification.getQuantity() == 0)
					{
						// Success in removing entry
						redirectModel.addFlashAttribute(GlobalMessages.CONF_MESSAGES_HOLDER,
								Collections.singletonList("basket.page.message.remove"));
					}
					else
					{
						// Success in update quantity
						redirectModel.addFlashAttribute(GlobalMessages.CONF_MESSAGES_HOLDER,
								Collections.singletonList("basket.page.message.update"));
					}
				}
				else
				{
					// Less than successful

					if (form.getQuantity().longValue() == 0)
					{
						// Failed to remove entry
						redirectModel.addFlashAttribute(
								GlobalMessages.ERROR_MESSAGES_HOLDER,
								Collections.singletonList("basket.information.quantity.reducedNumberOfItemsAdded."
										+ cartModification.getStatusCode()));
					}
					else
					{
						// Failed to update quantity
						redirectModel.addFlashAttribute(
								GlobalMessages.ERROR_MESSAGES_HOLDER,
								Collections.singletonList("basket.information.quantity.reducedNumberOfItemsAdded."
										+ cartModification.getStatusCode()));
					}
				}

				// Redirect to the cart page on update success so that the browser doesn't re-post again
				return REDIRECT_PREFIX + "/cart";
			}
			catch (final CommerceCartModificationException ex)
			{
				LOG.warn("Couldn't update product with the entry number: " + entryNumber + ".", ex);
			}
		}

		prepareDataForPage(model);
		return ControllerConstants.Views.Pages.Cart.CartPage;
	}


	protected void createProductList(final Model model) throws CMSItemNotFoundException
	{
		final CartData cartData = cartFacade.getSessionCart();

		if (cartData.getEntries() != null && !cartData.getEntries().isEmpty())
		{
			for (final OrderEntryData entry : cartData.getEntries())
			{
				if (entry.getProduct() != null)
				{ // this is not an empty cart entry 
					final UpdateQuantityForm uqf = new UpdateQuantityForm();
					uqf.setQuantity(entry.getQuantity());
					model.addAttribute("updateQuantityForm" + entry.getEntryNumber(), uqf);
					model.addAttribute("quantities", getNumberRange(0, 10));
				}
			}
		}

		model.addAttribute("cartData", cartData);

		storeCmsPageInModel(model, getContentPageForLabelOrId(CART_CMS_PAGE_LABEL));
		setUpMetaDataForContentPage(model, getContentPageForLabelOrId(CART_CMS_PAGE_LABEL));
	}

	protected void prepareDataForPage(final Model model) throws CMSItemNotFoundException
	{
		final String continueUrl = (String) sessionService.getAttribute(WebConstants.CONTINUE_URL);
		model.addAttribute(CONTINUE_URL, (continueUrl != null && !continueUrl.isEmpty()) ? continueUrl : ROOT);

		createProductList(model);
		model.addAttribute(WebConstants.BREADCRUMBS_KEY, resourceBreadcrumbBuilder.getBreadcrumbs("breadcrumb.cart"));
		model.addAttribute("pageType", PageType.Cart);
	}

	/**
	 * TODO:Need to move out of controller and place in facade with business logic to populate max number
	 * 
	 */
	protected List<String> getNumberRange(final int startNumber, final int endNumber)
	{
		final List<String> numbers = new ArrayList<String>();
		for (int number = startNumber; number <= endNumber; number++)
		{
			numbers.add(String.valueOf(number));
		}
		return numbers;
	}
}

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

import de.hybris.platform.cms2.exceptions.CMSItemNotFoundException;
import de.hybris.platform.commercefacades.order.data.CartModificationData;
import de.hybris.platform.commercefacades.product.ProductOption;
import de.hybris.platform.commercefacades.product.data.ProductData;
import de.hybris.platform.commercefacades.search.data.SearchStateData;
import de.hybris.platform.commerceservices.order.CommerceCartModificationException;
import de.hybris.platform.commerceservices.search.facetdata.ProductSearchPageData;
import de.hybris.platform.commerceservices.search.pagedata.PageableData;
import de.hybris.platform.configurablebundleservices.bundle.BundleTemplateService;
import de.hybris.platform.configurablebundleservices.model.BundleTemplateModel;
import com.epam.cme.storefront.controllers.pages.AbstractSearchPageController;
import com.epam.cme.storefront.controllers.util.GlobalMessages;
import com.epam.cme.storefront.forms.UpdateQuantityForm;
import com.epam.cme.core.model.ServicePlanModel;
import com.epam.cme.facades.bundle.GuidedSellingFacade;
import com.epam.cme.facades.bundle.GuidedSellingFacade.ProductType;
import com.epam.cme.facades.data.BundleTabData;
import com.epam.cme.facades.order.BundleCartFacade;
import com.epam.cme.facades.product.TelcoProductFacade;
import com.epam.cme.storefront.controllers.TelcoControllerConstants;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * Controller for guided selling flows like the extras page.
 */
@Controller
@RequestMapping(value = "/bundle")
public class TelcoGuidedSellingController extends AbstractSearchPageController {
    private static final String GUIDEDSELLING_SELECT_ADDONS = "bundleselection-extra";
    private static final String GUIDEDSELLING_SELECT_DEVICE = "guidedselling-select-device";
    private static final String GUIDEDSELLING_BUNDLE_PLAN = "bundleselection-plan";

    protected static final Logger LOG = Logger.getLogger(TelcoGuidedSellingController.class);

    public static enum ComponentNavigation {
        CURRENT(""),
        NEXT("next"),
        PREVIOUS("prev");

        final String code;

        private ComponentNavigation(final String code) {
            this.code = code;
        }
    }

    @Resource(name = "bundleTemplateService")
    private BundleTemplateService bundleTemplateService;

    @Resource(name = "guidedSellingFacade")
    private GuidedSellingFacade guidedSellingFacade;

    @Resource(name = "cartFacade")
    private BundleCartFacade cartFacade;

    @Resource(name = "telcoProductFacade")
    private TelcoProductFacade productFacade;

    @RequestMapping(value = "/view-plans/{bundleTemplateId}", method = RequestMethod.GET)
    public String viewPlans(@PathVariable("bundleTemplateId") final String bundleTemplateId, final Model model)
            throws CMSItemNotFoundException {
        final BundleTemplateModel bundleTemplateModel = bundleTemplateService
                .getBundleTemplateForCode(bundleTemplateId);
        final BundleTemplateModel firstComponentModel = bundleTemplateService
                .getAllComponentsOfType(bundleTemplateModel, ServicePlanModel.class).iterator().next();
        final ServicePlanModel firstPlan = (ServicePlanModel) firstComponentModel.getProducts().iterator().next();

        final ProductData productData = productFacade.getProductForOptions(firstPlan, Arrays.asList(
                ProductOption.BASIC, ProductOption.PRICE, ProductOption.SUMMARY, ProductOption.DESCRIPTION,
                ProductOption.GALLERY, ProductOption.CATEGORIES, ProductOption.REVIEW, ProductOption.PROMOTIONS,
                ProductOption.CLASSIFICATION, ProductOption.VARIANT_FULL, ProductOption.STOCK,
                ProductOption.SERVICE_PLAN_BUNDLE_TABS));

        // change selected package to the one that is passed as a parameter
        final List<BundleTabData> bundleTabs = productData.getBundleTabs();

        for (final BundleTabData bundleTab : bundleTabs) {
            bundleTab.setPreselected(bundleTemplateId.equals(bundleTab.getParentBundleTemplate().getId()));
        }
        model.addAttribute("bundleTabs", bundleTabs);

        storeCmsPageInModel(model, getContentPageForLabelOrId(GUIDEDSELLING_BUNDLE_PLAN));
        setUpMetaDataForContentPage(model, getContentPageForLabelOrId(GUIDEDSELLING_BUNDLE_PLAN));
        return TelcoControllerConstants.Views.Pages.GuidedSelling.viewAllServicePlansPage;
    }

    @RequestMapping(value = "/edit-component/{bundleNo}/component/{componentId}", method = RequestMethod.GET)
    public String editComponent(@PathVariable("bundleNo") final String bundleNo,
            @PathVariable("componentId") final String componentId,
            @RequestParam(value = "q", required = false) final String searchQuery,
            @RequestParam(value = "page", defaultValue = "0") final int page,
            @RequestParam(value = "show", defaultValue = "Page") final ShowMode showMode,
            @RequestParam(value = "sort", required = false) final String sortCode, final HttpServletRequest request,
            final Model model) throws CMSItemNotFoundException {
        return internalEditComponent(bundleNo, componentId, searchQuery, page, showMode, sortCode, request, model);
    }

    @RequestMapping(value = "/edit-component/{bundleNo}/nextcomponent/{componentId}", method = RequestMethod.GET)
    public String editNextComponent(@PathVariable("bundleNo") final String bundleNo,
            @PathVariable("componentId") final String componentId,
            @RequestParam(value = "q", required = false) final String searchQuery,
            @RequestParam(value = "page", defaultValue = "0") final int page,
            @RequestParam(value = "show", defaultValue = "Page") final ShowMode showMode,
            @RequestParam(value = "sort", required = false) final String sortCode, final HttpServletRequest request,
            final Model model) throws CMSItemNotFoundException {
        return editPositionalComponent(bundleNo, componentId, searchQuery, page, showMode, sortCode, request, model, 1);
    }

    @RequestMapping(value = "/edit-component/{bundleNo}/prevcomponent/{componentId}", method = RequestMethod.GET)
    public String editPreviousComponent(@PathVariable("bundleNo") final String bundleNo,
            @PathVariable("componentId") final String componentId,
            @RequestParam(value = "q", required = false) final String searchQuery,
            @RequestParam(value = "page", defaultValue = "0") final int page,
            @RequestParam(value = "show", defaultValue = "Page") final ShowMode showMode,
            @RequestParam(value = "sort", required = false) final String sortCode, final HttpServletRequest request,
            final Model model) throws CMSItemNotFoundException {
        return editPositionalComponent(bundleNo, componentId, searchQuery, page, showMode, sortCode, request, model, -1);
    }

    /**
     * Generic method to edit a component at a relative position. It resolves the relative component
     * and checks if it is valid to navigate there.
     */
    protected String editPositionalComponent(final String bundleNo, final String componentId, final String searchQuery,
            final int page, final ShowMode showMode, final String sortCode, final HttpServletRequest request,
            final Model model, final int relativeposition) throws CMSItemNotFoundException {
        String componentIdToNavigateTo = null;

        if (guidedSellingFacade.checkIsComponentSelectionCriteriaMet(bundleNo, componentId)) {
            componentIdToNavigateTo = guidedSellingFacade.getRelativeComponentId(bundleNo, componentId,
                    relativeposition);
        } else {
            GlobalMessages.addErrorMessage(model, "basket.next.selection.criteria.not.met");
            componentIdToNavigateTo = componentId;
        }

        if (componentIdToNavigateTo == null) {
            return REDIRECT_PREFIX + "/cart";
        }

        return internalEditComponent(bundleNo, componentIdToNavigateTo, searchQuery, page, showMode, sortCode, request,
                model);
    }

    /**
     * Populates the dashboard and the appropriate step of the guided selling flow.
     */
    protected String internalEditComponent(final String bundleNo, final String componentId, final String searchQuery,
            final int page, final ShowMode showMode, final String sortCode, final HttpServletRequest request,
            final Model model) throws CMSItemNotFoundException {
        model.addAttribute("dashboard",
                guidedSellingFacade.getDashboard(Integer.valueOf(bundleNo).intValue(), componentId));

        final ProductType productType = guidedSellingFacade.getComponentProductType(componentId);

        if (productType == null) {
            // we have an invalid (empty?) component here: do not try to edit it but go to cart
            return REDIRECT_PREFIX + "/cart";
        }

        model.addAttribute("productType", productType);
        switch (productType) {
        case DEVICE:
        case SERVICEPLAN:
            final String urlPrefix = "/bundle/edit-component/" + bundleNo + "/component/" + componentId + "?q=";
            final PageableData pageableData = createPageableData(page, getSearchPageSize(), sortCode, showMode);
            final ProductSearchPageData<SearchStateData, ProductData> searchPageData = guidedSellingFacade
                    .bundleSearch(pageableData, searchQuery, urlPrefix, componentId, Integer.valueOf(bundleNo));

            model.addAttribute("urlPrefix", urlPrefix);
            model.addAttribute("searchPageData", searchPageData);
            model.addAttribute("isShowAllAllowed", Boolean.valueOf(isShowAllAllowed(searchPageData)));
            model.addAttribute("pageType", PageType.ProductSearch);
            model.addAttribute("componentId", componentId);

            storeCmsPageInModel(model, getContentPageForLabelOrId(GUIDEDSELLING_SELECT_DEVICE));
            setUpMetaDataForContentPage(model, getContentPageForLabelOrId(GUIDEDSELLING_SELECT_DEVICE));
            return TelcoControllerConstants.Views.Pages.GuidedSelling.editComponentSolrStylePage;
        case SERVICEADDON:
            model.addAttribute("bundleTemplateData", guidedSellingFacade.getComponentToEdit(bundleNo, componentId));
            storeCmsPageInModel(model, getContentPageForLabelOrId(GUIDEDSELLING_SELECT_ADDONS));
            setUpMetaDataForContentPage(model, getContentPageForLabelOrId(GUIDEDSELLING_SELECT_ADDONS));
            return TelcoControllerConstants.Views.Pages.GuidedSelling.editComponentAccordeonStylePage;
        }

        return null;
    }

    @RequestMapping(value = "/addEntry", method = RequestMethod.POST)
    public String addToCart(
            @RequestParam("productCodePost") final String code,
            final Model model,
            @Valid final UpdateQuantityForm form,
            @RequestParam(value = "bundleNo", required = false) final Integer bundleNo,
            @RequestParam(value = "bundleTemplateId", required = false) final String bundleTemplateId,
            @RequestParam(value = "removeCurrentProducts", required = false, defaultValue = "false") final boolean removeCurrentProducts,
            @RequestParam("navigation") final ComponentNavigation navigation, final BindingResult bindingResult,
            final RedirectAttributes redirectModel) {
        if (bindingResult.hasErrors()) {
            for (final ObjectError error : bindingResult.getAllErrors()) {
                if (error.getCode().equals("typeMismatch")) {
                    redirectModel.addFlashAttribute(GlobalMessages.ERROR_MESSAGES_HOLDER,
                            Collections.singletonList("basket.error.quantity.invalid"));
                } else {
                    redirectModel.addFlashAttribute(GlobalMessages.ERROR_MESSAGES_HOLDER,
                            Collections.singletonList(error.getDefaultMessage()));
                }
            }
        }

        int finalBundleNo = addProduct(code, model, form.getQuantity().intValue(), bundleNo, bundleTemplateId,
                removeCurrentProducts, redirectModel);

        if (finalBundleNo < 0) {
            finalBundleNo = bundleNo == null ? 0 : bundleNo.intValue();
        }

        return REDIRECT_PREFIX + "/bundle/edit-component/" + finalBundleNo + "/" + navigation.code + "component/"
                + bundleTemplateId;
    }

    protected int addProduct(final String code, final Model model, final long qty, final Integer bundleNo,
            final String bundleTemplateId, final boolean removeCurrentProducts, final RedirectAttributes redirectModel) {

        try {
            final List<CartModificationData> cartModifications = cartFacade.addToCart(code, qty, bundleNo.intValue(),
                    bundleTemplateId, removeCurrentProducts);
            model.addAttribute("modifiedCartData", cartModifications);

            for (final CartModificationData cartModification : cartModifications) {

                if (cartModification.getQuantityAdded() == 0L) {
                    redirectModel.addFlashAttribute(
                            GlobalMessages.ERROR_MESSAGES_HOLDER,
                            Collections.singletonList("basket.information.quantity.noItemsAdded."
                                    + cartModification.getStatusCode()));
                } else if (cartModification.getQuantityAdded() < qty) {
                    redirectModel.addFlashAttribute(
                            GlobalMessages.ERROR_MESSAGES_HOLDER,
                            Collections.singletonList("basket.information.quantity.reducedNumberOfItemsAdded."
                                    + cartModification.getStatusCode()));
                } else if (cartModification.getQuantityAdded() > 0) {
                    redirectModel.addFlashAttribute(GlobalMessages.CONF_MESSAGES_HOLDER,

                    Collections.singletonList("guidedselling.basket.page.message.added"));

                    return cartModification.getEntry().getBundleNo();
                }
            }
        } catch (final CommerceCartModificationException ex) {
            redirectModel.addFlashAttribute(GlobalMessages.ERROR_MESSAGES_HOLDER,
                    Collections.singletonList("basket.error.occurred"));
            LOG.warn("Couldn't add product of code " + code + " to cart.", ex);
        }

        return -2;
    }

    @RequestMapping(value = "/removeEntry", method = RequestMethod.POST)
    public String updateCartQuantities(@RequestParam("entryNumber") final long entryNumber, final Model model,
            @Valid final UpdateQuantityForm form, @RequestParam("bundleNo") final String bundleNo,
            @RequestParam("componentId") final String componentId, final BindingResult bindingResult,
            final RedirectAttributes redirectModel) throws CMSItemNotFoundException {
        if (bindingResult.hasErrors()) {
            for (final ObjectError error : bindingResult.getAllErrors()) {
                if (error.getCode().equals("typeMismatch")) {
                    GlobalMessages.addErrorMessage(model, "basket.error.quantity.invalid");
                } else {
                    GlobalMessages.addErrorMessage(model, error.getDefaultMessage());
                }
            }
        } else if (cartFacade.getSessionCart().getEntries() != null) {
            try {
                final CartModificationData cartModification = cartFacade.updateCartEntry(entryNumber, form
                        .getQuantity().longValue());
                if (cartModification.getQuantity() == form.getQuantity().longValue()) {
                    // Success

                    if (cartModification.getQuantity() == 0) {
                        // Success in removing entry
                        redirectModel.addFlashAttribute(GlobalMessages.CONF_MESSAGES_HOLDER,
                                Collections.singletonList("guidedselling.basket.page.message.removed"));
                    } else {
                        // Success in update quantity
                        redirectModel.addFlashAttribute(GlobalMessages.CONF_MESSAGES_HOLDER,
                                Collections.singletonList("basket.page.message.update"));
                    }
                } else {
                    // Less than successful

                    if (form.getQuantity().longValue() == 0) {
                        // Failed to remove entry
                        redirectModel.addFlashAttribute(
                                GlobalMessages.ERROR_MESSAGES_HOLDER,
                                Collections.singletonList("basket.information.quantity.reducedNumberOfItemsAdded."
                                        + cartModification.getStatusCode()));
                    } else {
                        // Failed to update quantity
                        redirectModel.addFlashAttribute(
                                GlobalMessages.ERROR_MESSAGES_HOLDER,
                                Collections.singletonList("basket.information.quantity.reducedNumberOfItemsAdded."
                                        + cartModification.getStatusCode()));
                    }
                }

                return REDIRECT_PREFIX + "/bundle/edit-component/" + bundleNo + "/component/" + componentId;
            } catch (final CommerceCartModificationException ex) {
                LOG.warn("Couldn't update product with the entry number: " + entryNumber + ".", ex);
                return REDIRECT_PREFIX + "/bundle/edit-component/" + bundleNo + "/component/" + componentId;
            }
        }

        return REDIRECT_PREFIX + "/cart";
    }
}

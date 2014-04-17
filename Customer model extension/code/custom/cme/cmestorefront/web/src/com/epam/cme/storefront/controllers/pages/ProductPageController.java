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

import de.hybris.platform.category.model.CategoryModel;
import de.hybris.platform.cms2.exceptions.CMSItemNotFoundException;
import de.hybris.platform.cms2.model.pages.AbstractPageModel;
import de.hybris.platform.cms2.servicelayer.services.CMSPageService;
import de.hybris.platform.commercefacades.product.ProductFacade;
import de.hybris.platform.commercefacades.product.ProductOption;
import de.hybris.platform.commercefacades.product.data.BaseOptionData;
import de.hybris.platform.commercefacades.product.data.CategoryData;
import de.hybris.platform.commercefacades.product.data.ImageData;
import de.hybris.platform.commercefacades.product.data.ImageDataType;
import de.hybris.platform.commercefacades.product.data.ProductData;
import de.hybris.platform.commercefacades.product.data.ReviewData;
import de.hybris.platform.commerceservices.category.CommerceCategoryService;
import de.hybris.platform.commerceservices.url.UrlResolver;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.product.ProductService;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.servicelayer.exceptions.UnknownIdentifierException;
import de.hybris.platform.subscriptionservices.model.SubscriptionProductModel;
import com.epam.cme.core.model.DeviceModel;
import com.epam.cme.core.model.ServiceAddOnModel;
import com.epam.cme.core.model.ServicePlanModel;
import com.epam.cme.storefront.breadcrumb.Breadcrumb;
import com.epam.cme.storefront.breadcrumb.impl.ProductBreadcrumbBuilder;
import com.epam.cme.storefront.constants.WebConstants;
import com.epam.cme.storefront.controllers.ControllerConstants;
import com.epam.cme.storefront.controllers.util.GlobalMessages;
import com.epam.cme.storefront.controllers.util.ProductDataHelper;
import com.epam.cme.storefront.forms.ReviewForm;
import com.epam.cme.storefront.util.MetaSanitizerUtil;
import com.epam.cme.storefront.util.XSSFilterUtil;
import com.epam.cme.storefront.variants.VariantSortStrategy;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * Controller for product details page
 */
@Controller
@RequestMapping(value = "/**/p")
public class ProductPageController extends AbstractPageController {
    private static final Logger LOG = Logger.getLogger(ProductPageController.class);

    /**
     * We use this suffix pattern because of an issue with Spring 3.1 where a Uri value is
     * incorrectly extracted if it contains on or more '.' characters. Please see
     * https://jira.springsource.org/browse/SPR-6164 for a discussion on the issue and future
     * resolution.
     */
    private static final String PRODUCT_CODE_PATH_VARIABLE_PATTERN = "/{productCode:.*}";
    private static final String REVIEWS_PATH_VARIABLE_PATTERN = "{numberOfReviews:.*}";

    @Resource(name = "productModelUrlResolver")
    private UrlResolver<ProductModel> productModelUrlResolver;

    @Resource(name = "commerceCategoryService")
    private CommerceCategoryService commerceCategoryService;

    @Resource(name = "productFacade")
    private ProductFacade productFacade;

    @Resource(name = "productService")
    private ProductService productService;

    @Resource(name = "productBreadcrumbBuilder")
    private ProductBreadcrumbBuilder productBreadcrumbBuilder;

    @Resource(name = "categoryConverter")
    private Converter<CategoryModel, CategoryData> categoryConverter;

    @Resource(name = "categoryDataUrlResolver")
    private UrlResolver<CategoryData> categoryDataUrlResolver;

    @Resource(name = "cmsPageService")
    private CMSPageService cmsPageService;

    @Resource(name = "variantSortStrategy")
    private VariantSortStrategy variantSortStrategy;

    @RequestMapping(value = PRODUCT_CODE_PATH_VARIABLE_PATTERN, method = RequestMethod.GET)
    public String productDetail(@PathVariable("productCode") final String productCode, final Model model,
            final HttpServletRequest request, final HttpServletResponse response) throws CMSItemNotFoundException,
            UnsupportedEncodingException {
        final ProductModel productModel = productService.getProductForCode(productCode);
        final String redirection = checkRequestUrl(request, response, productModelUrlResolver.resolve(productModel));
        if (StringUtils.isNotEmpty(redirection)) {
            return redirection;
        }

        updatePageTitle(productModel, model);
        populateProductDetailForDisplay(productModel, model, request);
        model.addAttribute(new ReviewForm());
        final ProductData productData = productFacade.getProductForOptions(productModel,
                Arrays.asList(ProductOption.REVIEW, ProductOption.GALLERY));
        final int totalReviews = productData.getReviews().size();
        model.addAttribute("totalReviews", Integer.valueOf(totalReviews));
        model.addAttribute("showingReviews", Integer.valueOf(productData.getReviews().size()));
        model.addAttribute("pageType", PageType.Product);

        final String metaKeywords = MetaSanitizerUtil.sanitizeKeywords(productModel.getKeywords());
        final String metaDescription = MetaSanitizerUtil.sanitizeDescription(productModel.getDescription());
        setUpMetaData(model, metaKeywords, metaDescription);
        return getViewForPage(model);
    }

    @RequestMapping(value = PRODUCT_CODE_PATH_VARIABLE_PATTERN + "/zoomImages", method = RequestMethod.GET)
    public String showZoomImages(@PathVariable("productCode") final String productCode,
            @RequestParam(value = "galleryPosition", required = false) final String galleryPosition, final Model model) {
        final ProductModel productModel = productService.getProductForCode(productCode);
        final ProductData productData = productFacade.getProductForOptions(productModel,
                Collections.singleton(ProductOption.GALLERY));
        final List<Map<String, ImageData>> images = getGalleryImages(productData);
        model.addAttribute("galleryImages", images);
        model.addAttribute("product", productData);
        if (galleryPosition != null) {
            try {
                model.addAttribute("zoomImageUrl", images.get(Integer.parseInt(galleryPosition)).get("zoom").getUrl());
            } catch (final IndexOutOfBoundsException ioebe) {
                model.addAttribute("zoomImageUrl", "");
            } catch (final NumberFormatException e) {
                model.addAttribute("zoomImageUrl", "");
            }
        }
        return ControllerConstants.Views.Fragments.Product.ZoomImagesPopup;
    }

    @RequestMapping(value = PRODUCT_CODE_PATH_VARIABLE_PATTERN + "/quickView", method = RequestMethod.GET)
    public String showQuickView(@PathVariable("productCode") final String productCode, final Model model,
            final HttpServletRequest request) {
        final ProductModel productModel = productService.getProductForCode(productCode);
        final ProductData productData = productFacade.getProductForOptions(productModel, Arrays.asList(
                ProductOption.BASIC, ProductOption.PRICE, ProductOption.SUMMARY, ProductOption.DESCRIPTION,
                ProductOption.CATEGORIES, ProductOption.PROMOTIONS, ProductOption.STOCK, ProductOption.REVIEW));
        populateProductData(productData, model, request);
        return ControllerConstants.Views.Fragments.Product.QuickViewPopup;
    }

    @RequestMapping(value = PRODUCT_CODE_PATH_VARIABLE_PATTERN + "/review", method = RequestMethod.POST)
    public String postReview(@PathVariable final String productCode, @Valid final ReviewForm form,
            final BindingResult result, final Model model, final HttpServletRequest request,
            final RedirectAttributes redirectAttrs) throws CMSItemNotFoundException {
        final ProductModel productModel = productService.getProductForCode(productCode);

        if (result.hasErrors()) {
            GlobalMessages.addErrorMessage(model, "review.general.error");
            model.addAttribute("showReviewForm", Boolean.TRUE);
            populateProductDetailForDisplay(productModel, model, request);
            storeCmsPageInModel(model, getPageForProduct(productModel));
            return getViewForPage(model);
        }

        final ReviewData review = new ReviewData();
        review.setHeadline(XSSFilterUtil.filter(form.getHeadline()));
        review.setComment(XSSFilterUtil.filter(form.getComment()));
        review.setRating(form.getRating());
        review.setAlias(XSSFilterUtil.filter(form.getAlias()));
        productFacade.postReview(productCode, review);
        redirectAttrs.addFlashAttribute(GlobalMessages.CONF_MESSAGES_HOLDER,
                Collections.singletonList("review.confirmation.thank.you.title"));
        return REDIRECT_PREFIX + productModelUrlResolver.resolve(productModel);
    }

    @RequestMapping(value = PRODUCT_CODE_PATH_VARIABLE_PATTERN + "/reviewhtml/" + REVIEWS_PATH_VARIABLE_PATTERN, method = RequestMethod.GET)
    public String reviewHtml(@PathVariable("productCode") final String productCode,
            @PathVariable("numberOfReviews") final String numberOfReviews, final Model model,
            final HttpServletRequest request) {
        final ProductModel productModel = productService.getProductForCode(productCode);
        final ProductData productData = productFacade.getProductForOptions(productModel, Arrays.asList(
                ProductOption.BASIC, ProductOption.PRICE, ProductOption.SUMMARY, ProductOption.DESCRIPTION,
                ProductOption.GALLERY, ProductOption.CATEGORIES, ProductOption.REVIEW, ProductOption.PROMOTIONS,
                ProductOption.CLASSIFICATION));
        populateProductData(productData, model, request);
        model.addAttribute(new ReviewForm());
        final List reviews = (List) productData.getReviews();

        final int totalReviews = productData.getReviews().size();
        int numReviews = 0;
        if (!"all".equals(numberOfReviews)) {
            numReviews = Integer.parseInt(numberOfReviews);
            if (numReviews > totalReviews) {
                numReviews = totalReviews;
            }
            productData.setReviews(reviews.subList(0, numReviews));
        }

        model.addAttribute("totalReviews", Integer.valueOf(totalReviews));
        model.addAttribute("showingReviews", Integer.valueOf(productData.getReviews().size()));
        model.addAttribute("product", productData);

        return ControllerConstants.Views.Fragments.Product.ReviewsTab;
    }

    protected void updatePageTitle(final ProductModel productModel, final Model model) {
        storeContentPageTitleInModel(model, getPageTitleResolver().resolveProductPageTitle(productModel));
    }

    @ExceptionHandler(UnknownIdentifierException.class)
    public String handleUnknownIdentifierException(final UnknownIdentifierException exception,
            final HttpServletRequest request) {
        request.setAttribute("message", exception.getMessage());
        return FORWARD_PREFIX + "/404";
    }

    protected void populateProductDetailForDisplay(final ProductModel productModel, final Model model,
            final HttpServletRequest request) throws CMSItemNotFoundException {
        final List<ProductOption> options = new ArrayList<ProductOption>();
        options.addAll(Arrays
                .asList(ProductOption.BASIC, ProductOption.PRICE, ProductOption.SUMMARY, ProductOption.DESCRIPTION,
                        ProductOption.GALLERY, ProductOption.CATEGORIES, ProductOption.REVIEW,
                        ProductOption.PROMOTIONS, ProductOption.CLASSIFICATION, ProductOption.VARIANT_FULL,
                        ProductOption.STOCK));

        if (productModel instanceof DeviceModel) {
            options.add(ProductOption.DEVICE_BUNDLE_TABS);
        } else if (productModel instanceof SubscriptionProductModel) {
            if (productModel instanceof ServicePlanModel) {
                options.add(ProductOption.SERVICE_PLAN_BUNDLE_TABS);
            } else if (productModel instanceof ServiceAddOnModel) {
                options.add(ProductOption.SERVICE_ADDON_BUNDLE_TABS);
            }
        }

        final ProductData productData = productFacade.getProductForOptions(productModel, options);

        sortVariantOptionData(productData);
        storeCmsPageInModel(model, getPageForProduct(productModel));
        populateProductData(productData, model, request);

        final List<Breadcrumb> breadcrumbs = productBreadcrumbBuilder.getBreadcrumbs(productModel);

        model.addAttribute(WebConstants.BREADCRUMBS_KEY, breadcrumbs);

        // Note: This is the index of the category above the product's
        // supercategory
        int productSuperSuperCategoryIndex = breadcrumbs.size() - 3;
        final List<CategoryData> superCategories = new ArrayList<CategoryData>();

        if (productSuperSuperCategoryIndex == 0) {
            // The category at index 0 is never displayed as a supercategory; for
            // display purposes, the category at index 1 is the root category
            productSuperSuperCategoryIndex = 1;
        }

        if (productSuperSuperCategoryIndex > 0) {
            // When product has any supercategory
            final Breadcrumb productSuperSuperCategory = breadcrumbs.get(productSuperSuperCategoryIndex);
            final CategoryModel superSuperCategory = commerceCategoryService
                    .getCategoryForCode(productSuperSuperCategory.getCategoryCode());

            for (final CategoryModel superCategory : superSuperCategory.getCategories()) {
                final CategoryData categoryData = new CategoryData();

                categoryData.setName(superCategory.getName());
                categoryData.setUrl(categoryDataUrlResolver.resolve(categoryConverter.convert(superCategory)));

                superCategories.add(categoryData);
            }
        }

        model.addAttribute(WebConstants.SUPERCATEGORIES_KEY, superCategories);
    }

    protected void populateProductData(final ProductData productData, final Model model,
            final HttpServletRequest request) {
        ProductDataHelper.setCurrentProduct(request, productData.getCode());
        model.addAttribute("galleryImages", getGalleryImages(productData));
        model.addAttribute("product", productData);
    }

    protected void sortVariantOptionData(final ProductData productData) {
        if (CollectionUtils.isNotEmpty(productData.getBaseOptions())) {
            for (final BaseOptionData baseOptionData : productData.getBaseOptions()) {
                if (CollectionUtils.isNotEmpty(baseOptionData.getOptions())) {
                    Collections.sort(baseOptionData.getOptions(), variantSortStrategy);
                }
            }
        }

        if (CollectionUtils.isNotEmpty(productData.getVariantOptions())) {
            Collections.sort(productData.getVariantOptions(), variantSortStrategy);
        }
    }

    protected AbstractPageModel getPageForProduct(final ProductModel product) throws CMSItemNotFoundException {
        return cmsPageService.getPageForProduct(product);
    }

    protected List<Map<String, ImageData>> getGalleryImages(final ProductData productData) {
        final List<Map<String, ImageData>> galleryImages = new ArrayList<Map<String, ImageData>>();
        if (CollectionUtils.isNotEmpty(productData.getImages())) {
            final List<ImageData> images = new ArrayList<ImageData>();
            for (final ImageData image : productData.getImages()) {
                if (ImageDataType.GALLERY.equals(image.getImageType())) {
                    images.add(image);
                }
            }
            Collections.sort(images, new Comparator<ImageData>() {
                @Override
                public int compare(final ImageData image1, final ImageData image2) {
                    return image1.getGalleryIndex().compareTo(image2.getGalleryIndex());
                }
            });

            if (CollectionUtils.isNotEmpty(images)) {
                int currentIndex = images.get(0).getGalleryIndex().intValue();
                Map<String, ImageData> formats = new HashMap<String, ImageData>();
                for (final ImageData image : images) {
                    if (currentIndex != image.getGalleryIndex().intValue()) {
                        galleryImages.add(formats);
                        formats = new HashMap<String, ImageData>();
                        currentIndex = image.getGalleryIndex().intValue();
                    }
                    formats.put(image.getFormat(), image);
                }
                if (!formats.isEmpty()) {
                    galleryImages.add(formats);
                }
            }
        }
        return galleryImages;
    }

    @RequestMapping(value = PRODUCT_CODE_PATH_VARIABLE_PATTERN + "/writeReview", method = RequestMethod.GET)
    public String writeReview(@PathVariable final String productCode, final Model model)
            throws CMSItemNotFoundException {
        final ProductModel productModel = productService.getProductForCode(productCode);
        updatePageTitle(productModel, model);

        final ProductData productData = productFacade.getProductForOptions(productModel, Arrays.asList(
                ProductOption.BASIC, ProductOption.PRICE, ProductOption.SUMMARY, ProductOption.DESCRIPTION,
                ProductOption.CATEGORIES, ProductOption.PROMOTIONS, ProductOption.STOCK, ProductOption.REVIEW));
        model.addAttribute("productData", productData);
        final int totalReviews = productData.getReviews().size();
        model.addAttribute("totalReviews", Integer.valueOf(totalReviews));
        model.addAttribute("showingReviews", Integer.valueOf(productData.getReviews().size()));
        model.addAttribute(new ReviewForm());
        storeCmsPageInModel(model, getPageForProduct(productModel));
        return ControllerConstants.Views.Pages.Product.writeReview;
    }

    @RequestMapping(value = PRODUCT_CODE_PATH_VARIABLE_PATTERN + "/writeReview", method = RequestMethod.POST)
    public String writeReview(@PathVariable final String productCode, @Valid final ReviewForm form,
            final BindingResult result, final Model model, final HttpServletRequest request,
            final RedirectAttributes redirectAttrs) throws CMSItemNotFoundException {
        final ProductModel productModel = productService.getProductForCode(productCode);
        final ProductData productData = productFacade.getProductForOptions(productModel, Arrays.asList(
                ProductOption.BASIC, ProductOption.PRICE, ProductOption.SUMMARY, ProductOption.DESCRIPTION,
                ProductOption.CATEGORIES, ProductOption.PROMOTIONS, ProductOption.STOCK, ProductOption.REVIEW));
        model.addAttribute("productData", productData);
        if (result.hasErrors()) {
            GlobalMessages.addErrorMessage(model, "review.general.error");
            populateProductDetailForDisplay(productModel, model, request);
            storeCmsPageInModel(model, getPageForProduct(productModel));
            return ControllerConstants.Views.Pages.Product.writeReview;
        }

        final ReviewData review = new ReviewData();
        review.setHeadline(XSSFilterUtil.filter(form.getHeadline()));
        review.setComment(XSSFilterUtil.filter(form.getComment()));
        review.setRating(form.getRating());
        review.setAlias(XSSFilterUtil.filter(form.getAlias()));
        productFacade.postReview(productCode, review);
        redirectAttrs.addFlashAttribute(GlobalMessages.CONF_MESSAGES_HOLDER,
                Collections.singletonList("review.confirmation.thank.you.title"));
        return REDIRECT_PREFIX + productModelUrlResolver.resolve(productModel);
    }

}

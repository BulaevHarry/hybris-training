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
package com.epam.cme.storefront.tags;

import de.hybris.platform.category.model.CategoryModel;
import de.hybris.platform.cms2.model.contents.components.CMSLinkComponentModel;
import de.hybris.platform.cms2.model.pages.ContentPageModel;
import de.hybris.platform.commercefacades.order.data.CartData;
import de.hybris.platform.commercefacades.order.data.PromotionOrderEntryConsumedData;
import de.hybris.platform.commercefacades.product.data.CategoryData;
import de.hybris.platform.commercefacades.product.data.ImageData;
import de.hybris.platform.commercefacades.product.data.ImageDataType;
import de.hybris.platform.commercefacades.product.data.ProductData;
import de.hybris.platform.commercefacades.product.data.PromotionResultData;
import de.hybris.platform.commercefacades.storelocator.data.PointOfServiceData;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.jalo.extension.ExtensionManager;
import de.hybris.platform.servicelayer.dto.converter.Converter;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Collection;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.springframework.web.servlet.support.RequestContextUtils;

/**
 * JSP EL Functions. This file contains static methods that are used by JSP EL.
 */
public class Functions {
    /**
     * JSP EL Function to get a primary Image for a Product in a specific format
     * 
     * @param product
     *            the product
     * @param format
     *            the desired format
     * @return the image
     */
    public static ImageData getPrimaryImageForProductAndFormat(final ProductData product, final String format) {
        if (product != null && format != null) {
            final Collection<ImageData> images = product.getImages();
            if (images != null && !images.isEmpty()) {
                for (final ImageData image : images) {
                    if (ImageDataType.PRIMARY.equals(image.getImageType()) && format.equals(image.getFormat())) {
                        return image;
                    }
                }
            }
        }
        return null;
    }

    /**
     * JSP EL Function to get an Image for a Store in a specific format
     * 
     * @param store
     *            the store
     * @param format
     *            the desired image format
     * @return the image
     */
    public static ImageData getImageForStoreAndFormat(final PointOfServiceData store, final String format) {
        if (store != null && format != null) {
            final Collection<ImageData> images = store.getStoreImages();
            if (images != null && !images.isEmpty()) {
                for (final ImageData image : images) {
                    if (format.equals(image.getFormat())) {
                        return image;
                    }
                }
            }
        }
        return null;
    }

    /**
     * JSP EL Function to get the URL for a CMSLinkComponent
     * 
     * @param component
     *            The Link Component
     * @param httpRequest
     *            The current request (used to lookup spring beans)
     * @return The URL
     */
    public static String getUrlForCMSLinkComponent(final CMSLinkComponentModel component,
            final HttpServletRequest httpRequest) {
        return getUrlForCMSLinkComponent(component, httpRequest, null, null);
    }

    public static String getUrlForCMSLinkComponent(final CMSLinkComponentModel component,
            final HttpServletRequest httpRequest, final Converter<ProductModel, ProductData> productUrlConverter,
            final Converter<CategoryModel, CategoryData> categoryUrlConverter) {
        // Try to get the URL from the component
        {
            final String url = component.getUrl();
            if (url != null && !url.isEmpty()) {
                return url;
            }
        }

        // Try to get the label for the content page
        {
            final ContentPageModel contentPage = component.getContentPage();
            if (contentPage != null) {
                return contentPage.getLabel();
            }
        }

        // Try to get the category and build a URL to the category
        final CategoryModel category = component.getCategory();
        if (category != null) {
            final Converter<CategoryModel, CategoryData> urlConverter = categoryUrlConverter != null ? categoryUrlConverter
                    : getCategoryUrlConverter(httpRequest);
            return urlConverter.convert(category).getUrl();
        }

        // Try to get the product and build a URL to the product
        final ProductModel product = component.getProduct();
        if (product != null) {
            final Converter<ProductModel, ProductData> urlConverter = productUrlConverter != null ? productUrlConverter
                    : getProductUrlConverter(httpRequest);
            return urlConverter.convert(product).getUrl();
        }
        return null;
    }

    protected static Converter<ProductModel, ProductData> getProductUrlConverter(final HttpServletRequest httpRequest) {
        return getSpringBean(httpRequest, "productUrlConverter", Converter.class);
    }

    protected static Converter<CategoryModel, CategoryData> getCategoryUrlConverter(final HttpServletRequest httpRequest) {
        return getSpringBean(httpRequest, "categoryUrlConverter", Converter.class);
    }

    /**
     * Returns the Spring bean with name <code>beanName</code> and of type <code>beanClass</code>.
     * 
     * @param <T>
     *            type of the bean
     * @param httpRequest
     *            the http request
     * @param beanName
     *            name of the bean
     * @param beanClass
     *            expected type of the bean
     * @return the bean matching the given arguments or <code>null</code> if no bean could be
     *         resolved
     */
    public static <T> T getSpringBean(final HttpServletRequest httpRequest, final String beanName,
            final Class<T> beanClass) {
        return RequestContextUtils.getWebApplicationContext(httpRequest, httpRequest.getSession().getServletContext())
                .getBean(beanName, beanClass);
    }

    /**
     * Test if a cart has an applied promotion for the specified entry number.
     * 
     * @param cart
     *            the cart
     * @param entryNumber
     *            the entry number
     * @return true if there is an applied promotion for the entry number
     */
    public static boolean doesAppliedPromotionExistForOrderEntry(final CartData cart, final int entryNumber) {
        return cart != null && doesPromotionExistForOrderEntry(cart.getAppliedProductPromotions(), entryNumber);
    }

    /**
     * Test if a cart has an potential promotion for the specified entry number.
     * 
     * @param cart
     *            the cart
     * @param entryNumber
     *            the entry number
     * @return true if there is an potential promotion for the entry number
     */
    public static boolean doesPotentialPromotionExistForOrderEntry(final CartData cart, final int entryNumber) {
        return cart != null && doesPromotionExistForOrderEntry(cart.getPotentialProductPromotions(), entryNumber);
    }

    protected static boolean doesPromotionExistForOrderEntry(final List<PromotionResultData> productPromotions,
            final int entryNumber) {
        if (productPromotions != null && !productPromotions.isEmpty()) {
            final Integer entryNumberToFind = Integer.valueOf(entryNumber);

            for (final PromotionResultData productPromotion : productPromotions) {
                if (StringUtils.isNotBlank(productPromotion.getDescription())) {
                    final List<PromotionOrderEntryConsumedData> consumedEntries = productPromotion.getConsumedEntries();
                    if (consumedEntries != null && !consumedEntries.isEmpty()) {
                        for (final PromotionOrderEntryConsumedData consumedEntry : consumedEntries) {
                            if (entryNumberToFind.equals(consumedEntry.getOrderEntryNumber())) {
                                return true;
                            }
                        }
                    }
                }
            }
        }
        return false;
    }

    /**
     * Utility method that encodes given URL
     * 
     * @param url
     * @return encoded URL
     */
    public static String encodeUrl(final String url) {
        try {
            return URLEncoder.encode(url, "UTF-8");
        } catch (final UnsupportedEncodingException e) {
            return url;
        }
    }

    /**
     * Utility method that checks if an extension is installed
     * 
     * @param extensionName
     *            extension to be checked
     * @return <code>true</code>, if extensions is installed, otherwise <code>false</code>
     */
    public static boolean isExtensionInstalled(final String extensionName) {
        return ExtensionManager.getInstance().isExtensionInstalled(extensionName);
    }

}

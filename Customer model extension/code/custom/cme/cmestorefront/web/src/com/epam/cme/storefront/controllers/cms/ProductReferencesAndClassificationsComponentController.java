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
package com.epam.cme.storefront.controllers.cms;

import de.hybris.platform.acceleratorcms.model.components.ProductReferencesComponentModel;
import de.hybris.platform.commercefacades.product.data.ProductData;
import com.epam.cme.storefront.controllers.cms.ProductReferencesComponentController;
import com.epam.cme.storefront.controllers.util.ProductDataHelper;
import com.epam.cme.core.model.ProductReferencesAndClassificationsComponentModel;
import com.epam.cme.facades.product.TelcoProductFacade;
import com.epam.cme.storefront.controllers.TelcoControllerConstants;

import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Component controller that displays controller for the product references and feature compatible
 * products
 */
@Controller("ProductReferencesAndClassificationsComponentController")
@RequestMapping(value = TelcoControllerConstants.Actions.Cms.ProductReferencesAndClassificationsComponent)
public class ProductReferencesAndClassificationsComponentController extends ProductReferencesComponentController {
    @Resource(name = "telcoProductFacade")
    private TelcoProductFacade telcoProductFacade;

    @Override
    protected void fillModel(final HttpServletRequest request, final Model model,
            final ProductReferencesComponentModel component) {
        final ProductReferencesAndClassificationsComponentModel refAndClassificationsComponent = (ProductReferencesAndClassificationsComponentModel) component;

        final List<ProductData> referenceAndClassificationsProducts = telcoProductFacade
                .getProductReferencesAndFeatureCompatibleProductsForCode(ProductDataHelper.getCurrentProduct(request),
                        refAndClassificationsComponent.getProductReferenceTypes(),
                        ProductReferencesComponentController.PRODUCT_OPTIONS,
                        refAndClassificationsComponent.getMaximumNumberProducts(),
                        refAndClassificationsComponent.getClassAttributeAssignment(),
                        refAndClassificationsComponent.getTargetItemType());

        model.addAttribute("title", refAndClassificationsComponent.getTitle());

        model.addAttribute("productAccessories", referenceAndClassificationsProducts);
    }
}

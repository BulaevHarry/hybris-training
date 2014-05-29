package com.epam.cme.services.bundle.impl;

import de.hybris.platform.commerceservices.order.CommerceCartModification;
import de.hybris.platform.commerceservices.order.CommerceCartModificationException;
import de.hybris.platform.configurablebundleservices.bundle.impl.DefaultBundleCommerceCartService;
import de.hybris.platform.configurablebundleservices.model.BundleSelectionCriteriaModel;
import de.hybris.platform.configurablebundleservices.model.BundleTemplateModel;
import de.hybris.platform.configurablebundleservices.model.PickExactlyNBundleSelectionCriteriaModel;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.CartEntryModel;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.servicelayer.util.ServicesUtil;
import de.hybris.platform.subscriptionservices.subscription.impl.DefaultSubscriptionCommerceCartService;

import java.util.Collections;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;

public class DefaultCmeBundleCommerceCartService extends DefaultBundleCommerceCartService {

    private DefaultSubscriptionCommerceCartService defaultSubscriptionCommerceCartService;

    @Override
    public CommerceCartModification updateQuantityForCartEntry(final CartModel masterCartModel, final long entryNumber,
            final long newQuantity) throws CommerceCartModificationException {
        ServicesUtil.validateParameterNotNullStandardMessage("masterCartModel", masterCartModel);

        Logger.getLogger(DefaultCmeBundleCommerceCartService.class).info("qty: " + newQuantity);

        if (!(isMasterCart(masterCartModel))) {
            throw new IllegalArgumentException("Provided cart '" + masterCartModel.getCode() + "' is not a master cart");
        }

        int bundleNo = 0;
        AbstractOrderEntryModel cartEntryModel = getCartEntryToBeUpdated(masterCartModel, entryNumber);
        BundleTemplateModel bundleTemplate = cartEntryModel.getBundleTemplate();

        if (bundleTemplate != null) {
            bundleNo = cartEntryModel.getBundleNo().intValue();

            if (newQuantity < 0L) {
                throw new CommerceCartModificationException("Product '" + cartEntryModel.getProduct().getCode()
                        + "' is part of bundle '" + cartEntryModel.getBundleTemplate().getId()
                        + "' and must have a new quantity >= 0, quantity given: " + newQuantity);
            }

            checkAutoPickRemoval((CartEntryModel) cartEntryModel);
            checkSelectionCriteriaNotUnderThreshold((CartEntryModel) cartEntryModel, newQuantity);
            checkAndRemoveDependentComponents(masterCartModel, bundleNo, bundleTemplate);
            checkIsComponentDependencyMetAfterRemoval(masterCartModel, cartEntryModel.getBundleTemplate(), bundleNo);

            setCartEntriesInSameBundleToNotCalculated(cartEntryModel);
        }

        CommerceCartModification commerceCartModification = defaultSubscriptionCommerceCartService
                .updateQuantityForCartEntry(masterCartModel, entryNumber, newQuantity);

        if (bundleNo != 0) {
            calculateCart(masterCartModel);
        }

        updateLastModifiedEntriesList(masterCartModel, Collections.singletonList(commerceCartModification));

        return commerceCartModification;
    }

    protected void checkSelectionCriteriaNotUnderThreshold(final CartEntryModel cartEntry, final long newQuantity)
            throws CommerceCartModificationException {
        boolean isEntryRemovable = checkIsEntryRemovable(cartEntry);

        if (isEntryRemovable)
            return;
        int minSelections = 0;
        String message = "";
        ProductModel product = cartEntry.getProduct();
        BundleTemplateModel bundleTemplate = cartEntry.getBundleTemplate();
        String templateName = "'"
                + ((bundleTemplate.getName() == null) ? bundleTemplate.getId() : bundleTemplate.getName()) + "'";
        BundleSelectionCriteriaModel selectionCriteria = bundleTemplate.getBundleSelectionCriteria();

        if (selectionCriteria instanceof PickExactlyNBundleSelectionCriteriaModel) {
            minSelections = ((PickExactlyNBundleSelectionCriteriaModel) selectionCriteria).getN().intValue();
            Logger.getLogger(DefaultCmeBundleCommerceCartService.class).info(
                    "instanceof PickExactlyNBundleSelectionCriteriaModel, newQuantity: " + newQuantity
                            + " minSelections: " + minSelections);
            if (newQuantity < minSelections) {
                message = getL10NService().getLocalizedString("bundleservices.validation.productnotremovable",
                        new Object[] { "'" + product.getName() + "'", String.valueOf(minSelections), templateName });
                throw new CommerceCartModificationException(message);                
            } else {
                return;
            }
        } else if (newQuantity == 0) {
            message = getL10NService().getLocalizedString("bundleservices.validation.productnotremovablesimple",
                    new Object[] { "'" + product.getName() + "'", templateName });
            throw new CommerceCartModificationException(message);
        }
    }

    @Required
    public void setDefaultSubscriptionCommerceCartService(
            final DefaultSubscriptionCommerceCartService defaultSubscriptionCommerceCartService) {
        this.defaultSubscriptionCommerceCartService = defaultSubscriptionCommerceCartService;
    }

}

package com.epam.cme.cockpits.widgets.controllers.impl;

import de.hybris.platform.cscockpit.widgets.controllers.impl.DefaultCallContextController;

import com.epam.cme.core.model.BlockableCustomerModel;

public class CmeCallContextController extends DefaultCallContextController {

    public void unblockCustomer() {
        final BlockableCustomerModel model = (BlockableCustomerModel) getCurrentCustomer().getObject();
        model.setBlockedStatus(new Boolean(false));
        model.setAttemptCount(Integer.valueOf(0));
        getModelService().save(model);
    }

    public void blockCustomer() {
        final BlockableCustomerModel model = (BlockableCustomerModel) getCurrentCustomer().getObject();
        model.setBlockedStatus(new Boolean(true));
        getModelService().save(model);
    }

}

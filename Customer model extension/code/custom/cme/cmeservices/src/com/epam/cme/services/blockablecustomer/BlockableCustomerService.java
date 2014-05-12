package com.epam.cme.services.blockablecustomer;

import com.epam.cme.core.model.BlockableCustomerModel;

public interface BlockableCustomerService {

    BlockableCustomerModel getBlockableCustomerByUid(String uid);

}

package com.epam.cme.services.blockablecustomer.dao;

import java.util.List;

import com.epam.cme.core.model.BlockableCustomerModel;

public interface BlockableCustomerDao {

    List<BlockableCustomerModel> findBlockableCustomerByUid(String uid);

}

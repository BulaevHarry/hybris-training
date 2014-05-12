package com.epam.cme.services.blockablecustomer.impl;

import de.hybris.platform.servicelayer.exceptions.AmbiguousIdentifierException;
import de.hybris.platform.servicelayer.exceptions.UnknownIdentifierException;

import java.util.List;

import org.springframework.beans.factory.annotation.Required;
import org.springframework.stereotype.Service;

import com.epam.cme.core.model.BlockableCustomerModel;
import com.epam.cme.services.blockablecustomer.BlockableCustomerService;
import com.epam.cme.services.blockablecustomer.dao.BlockableCustomerDao;

@Service("blockableCustomerService")
public class DefaultBlockableCustomerService implements BlockableCustomerService {

    private BlockableCustomerDao blockableCustomerDao;

    @Override
    public BlockableCustomerModel getBlockableCustomerByUid(final String uid) {
        final List<BlockableCustomerModel> result = blockableCustomerDao.findBlockableCustomerByUid(uid);
        if (result.isEmpty()) {
            throw new UnknownIdentifierException("BlockableCustomer with uid '" + uid + "' not found!");
        } else if (result.size() > 1) {
            throw new AmbiguousIdentifierException("BlockableCustomer with uid '" + uid + "' is not unique, "
                    + result.size() + " BlockableCustomers found!");
        }
        return result.get(0);
    }

    @Required
    public void setBlockableCustomerDao(final BlockableCustomerDao blockableCustomerDao) {
        this.blockableCustomerDao = blockableCustomerDao;
    }

}

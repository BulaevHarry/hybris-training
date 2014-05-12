package com.epam.cme.services.blockablecustomer.dao.impl;

import de.hybris.platform.servicelayer.search.FlexibleSearchQuery;
import de.hybris.platform.servicelayer.search.FlexibleSearchService;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.epam.cme.core.model.BlockableCustomerModel;
import com.epam.cme.core.model.OrganizationModel;
import com.epam.cme.services.blockablecustomer.dao.BlockableCustomerDao;

@Component("blockableCustomerDao")
public class DefaultBlockableCustomerDao implements BlockableCustomerDao {

    @Autowired
    private FlexibleSearchService flexibleSearchService;

    private static final String FIND_BLOCKABLECUSTOMER_BY_UID_QUERY = "SELECT {p:" + OrganizationModel.PK + "} "
            + "FROM {" + BlockableCustomerModel._TYPECODE + " AS p} " + "WHERE " + "{p:" + BlockableCustomerModel.UID
            + "}=?uid ";

    @Override
    public List<BlockableCustomerModel> findBlockableCustomerByUid(final String uid) {
        final FlexibleSearchQuery query = new FlexibleSearchQuery(FIND_BLOCKABLECUSTOMER_BY_UID_QUERY);
        query.addQueryParameter("uid", uid);
        return flexibleSearchService.<BlockableCustomerModel>search(query).getResult();
    }

}

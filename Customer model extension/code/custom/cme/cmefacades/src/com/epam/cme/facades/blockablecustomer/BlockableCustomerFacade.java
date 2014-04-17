package com.epam.cme.facades.blockablecustomer;

import de.hybris.platform.commercefacades.customer.CustomerFacade;
import de.hybris.platform.commerceservices.customer.DuplicateUidException;
import de.hybris.platform.servicelayer.exceptions.UnknownIdentifierException;

import com.epam.cme.facades.user.data.CmeRegisterData;

public interface BlockableCustomerFacade extends CustomerFacade {

    void register(CmeRegisterData cmeRegisterData) throws DuplicateUidException, UnknownIdentifierException,
            IllegalArgumentException;

}

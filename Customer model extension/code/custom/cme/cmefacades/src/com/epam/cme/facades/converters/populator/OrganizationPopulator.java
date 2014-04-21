package com.epam.cme.facades.converters.populator;

import de.hybris.platform.converters.Populator;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;

import com.epam.cme.core.model.OrganizationModel;
import com.epam.cme.facades.organization.data.OrganizationData;

public class OrganizationPopulator implements Populator<OrganizationModel, OrganizationData> {

    @Override
    public void populate(final OrganizationModel source, final OrganizationData target) throws ConversionException {
        target.setCode(source.getId());
        target.setName(source.getName());
        target.setEmail(source.getEmail());
        target.setPhone(source.getPhone());
    }

}

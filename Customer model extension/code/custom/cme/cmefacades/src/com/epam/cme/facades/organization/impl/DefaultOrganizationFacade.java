package com.epam.cme.facades.organization.impl;

import de.hybris.platform.converters.Converters;
import de.hybris.platform.servicelayer.dto.converter.Converter;

import java.util.List;

import org.apache.commons.lang.Validate;
import org.springframework.beans.factory.annotation.Required;

import com.epam.cme.core.model.OrganizationModel;
import com.epam.cme.facades.organization.OrganizationFacade;
import com.epam.cme.facades.organization.data.OrganizationData;
import com.epam.cme.services.organization.OrganizationService;

public class DefaultOrganizationFacade implements OrganizationFacade {

    private OrganizationService organizationService;
    private Converter<OrganizationModel, OrganizationData> organizationConverter;

    @Override
    public List<OrganizationData> getOrganizations() {
        final List<OrganizationModel> models = organizationService.getOrganizations();
        return Converters.convertAll(models, getOrganizationConverter());
    }

    @Override
    public OrganizationData getOrganizationById(final Integer id) {
        OrganizationModel model = null;
        Validate.notNull(id, "Organization with id " + id + "not found");
        model = organizationService.getOrganizationById(id);
        return getOrganizationConverter().convert(model);
    }

    public Converter<OrganizationModel, OrganizationData> getOrganizationConverter() {
        return organizationConverter;
    }

    @Required
    public void setOrganizationConverter(final Converter<OrganizationModel, OrganizationData> organizationConverter) {
        this.organizationConverter = organizationConverter;
    }

    @Required
    public void setOrganizationService(final OrganizationService organizationService) {
        this.organizationService = organizationService;
    }

}

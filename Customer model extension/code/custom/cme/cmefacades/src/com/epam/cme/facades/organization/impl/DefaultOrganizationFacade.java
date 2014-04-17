package com.epam.cme.facades.organization.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Required;

import com.epam.cme.core.model.OrganizationModel;
import com.epam.cme.facades.organization.OrganizationFacade;
import com.epam.cme.facades.organization.data.OrganizationData;
import com.epam.cme.services.organization.OrganizationService;

public class DefaultOrganizationFacade implements OrganizationFacade {

    private OrganizationService organizationService;

    @Override
    public List<OrganizationData> getOrganizations() {
        final List<OrganizationModel> models = organizationService.getOrganizations();
        final List<OrganizationData> datas = new ArrayList<OrganizationData>();
        for (final OrganizationModel om : models) {
            final OrganizationData od = new OrganizationData();
            od.setCode(om.getId());
            od.setName(om.getName());
            od.setPhone(om.getPhone());
            datas.add(od);
        }
        return datas;
    }

    @Override
    public OrganizationData getOrganizationById(final Integer id) {
        OrganizationModel model = null;
        if (id != null) {
            model = organizationService.getOrganizationById(id);
        } else {
            throw new IllegalArgumentException("Organization with id " + id + "not found");
        }
        final OrganizationData data = new OrganizationData();
        data.setCode(model.getId());
        data.setName(model.getName());
        data.setPhone(model.getPhone());
        return data;
    }

    @Required
    public void setOrganizationService(final OrganizationService organizationService) {
        this.organizationService = organizationService;
    }

}

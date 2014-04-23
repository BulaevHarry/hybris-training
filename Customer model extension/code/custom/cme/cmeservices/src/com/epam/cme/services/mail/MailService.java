package com.epam.cme.services.mail;

import com.epam.cme.core.model.OrganizationModel;

public interface MailService {

    void sendCustomersListMail(OrganizationModel organization);

}
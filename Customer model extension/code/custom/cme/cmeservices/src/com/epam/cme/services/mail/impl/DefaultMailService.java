package com.epam.cme.services.mail.impl;

import de.hybris.platform.servicelayer.config.ConfigurationService;

import java.util.Iterator;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.mail.javamail.MimeMessagePreparator;
import org.springframework.stereotype.Service;

import com.epam.cme.core.model.BlockableCustomerModel;
import com.epam.cme.core.model.OrganizationModel;
import com.epam.cme.services.mail.MailService;

@Service("mailService")
public class DefaultMailService implements MailService, InitializingBean {

    private final static Logger LOG = Logger.getLogger(DefaultMailService.class);
    private String fromAddress;
    private String replyToAddress;

    @Autowired
    private JavaMailSender mailSender;
    @Autowired
    ConfigurationService configurationService;

    @Override
    public void afterPropertiesSet() {
        fromAddress = configurationService.getConfiguration().getString("mail.from");
        replyToAddress = configurationService.getConfiguration().getString("mail.replyto");
        if (fromAddress == null || fromAddress.isEmpty() || replyToAddress == null || replyToAddress.isEmpty()) {
            throw new IllegalStateException(
                    "Can not start mail service, please configure properties 'mail.from' and 'mail.replyto'");
        }
    }

    @Override
    public void sendCustomersListMail(final OrganizationModel organization) {
        final MailPreparator preparer = new MailPreparator() {

            @Override
            public void prepare(final MimeMessageHelper message) throws MessagingException {
                message.setSubject("Customers list");
                final StringBuilder sb = new StringBuilder();
                final Iterator<BlockableCustomerModel> it = organization.getCustomers().iterator();
                while (it.hasNext()) {
                    sb.append(it.next().getName());
                    if (it.hasNext()) {
                        sb.append(", ");
                    }
                }
                message.setText(sb.toString());
            }

        };
        send(preparer, organization);
    }

    protected void send(final MailPreparator preparer, final OrganizationModel organization) {
        final MimeMessagePreparator preparator = new MimeMessagePreparator() {

            @Override
            public void prepare(final MimeMessage mimeMessage) throws Exception {
                final MimeMessageHelper message = new MimeMessageHelper(mimeMessage, "UTF-8");
                message.setTo(organization.getEmail());
                message.setFrom(fromAddress);
                message.setReplyTo(replyToAddress);
                preparer.prepare(message);
            }

        };
        try {
            mailSender.send(preparator);
        } catch (final MailException e) {
            LOG.error("Can't send mail to " + organization.getName() + " - " + organization.getEmail(), e);
        }
    }

    protected interface MailPreparator {

        void prepare(MimeMessageHelper message) throws Exception;

    }

}

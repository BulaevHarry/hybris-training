package com.epam.cme.services.mail.impl;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import de.hybris.platform.servicelayer.config.ConfigurationService;

import org.apache.commons.configuration.Configuration;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessagePreparator;

import com.epam.cme.core.model.OrganizationModel;

@RunWith(MockitoJUnitRunner.class)
public class DefaultMailServiceUnitTest {

    private Configuration cfg;

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Mock
    private ConfigurationService configurationService;
    @Mock
    private JavaMailSender mailSender;

    @InjectMocks
    private final DefaultMailService service = new DefaultMailService();

    @Before
    public void init() {
        MockitoAnnotations.initMocks(this);
        cfg = mock(Configuration.class);
        when(configurationService.getConfiguration()).thenReturn(cfg);
    }

    @Test
    public void testAfrerPropertiesSetFromAddressNullIllegalStateException() {
        thrown.expect(IllegalStateException.class);
        when(cfg.getString("mail.from")).thenReturn(null);
        service.afterPropertiesSet();
    }

    @Test
    public void testAfrerPropertiesSetToAddressEmptyIllegalStateException() {
        thrown.expect(IllegalStateException.class);
        when(cfg.getString("mail.replyto")).thenReturn(null);
        service.afterPropertiesSet();
    }

    @Test
    public void testAfrerPropertiesSetFromAddressEmptyIllegalStateException() {
        thrown.expect(IllegalStateException.class);
        when(cfg.getString("mail.from")).thenReturn("");
        service.afterPropertiesSet();
    }

    @Test
    public void testAfrerPropertiesSetToAddressNullIllegalStateException() {
        thrown.expect(IllegalStateException.class);
        when(cfg.getString("mail.replyto")).thenReturn("");
        service.afterPropertiesSet();
    }

    @Test
    public void testSendMailSenderSendMethodInvoked() throws Exception {
        service.sendCustomersListMail(mock(OrganizationModel.class));
        verify(mailSender).send(any(MimeMessagePreparator.class));
    }

}

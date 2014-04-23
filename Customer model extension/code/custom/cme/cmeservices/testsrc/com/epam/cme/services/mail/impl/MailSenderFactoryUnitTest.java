package com.epam.cme.services.mail.impl;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
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
import org.springframework.beans.factory.BeanCreationException;
import org.springframework.mail.javamail.JavaMailSenderImpl;

@RunWith(MockitoJUnitRunner.class)
public class MailSenderFactoryUnitTest {

    private Configuration cfg;
    private static final String PARAM_HOST = "mail.smtp.server";
    private static final String PARAM_PORT = "mail.smtp.port";
    private static final String PARAM_USER = "mail.smtp.user";
    private static final String PARAM_PWD = "mail.smtp.password";
    private static final String PARAM_START_TLS = "mail.smtp.starttls.enable";
    private static final String host = "testserver";
    private static final int port = 1;
    private static final String user = "testusr";
    private static final String pwd = "testpwd";
    private static final String tls = "true";

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Mock
    private ConfigurationService configurationService;

    @InjectMocks
    private final MailSenderFactory factory = new MailSenderFactory();

    @Before
    public void init() {
        MockitoAnnotations.initMocks(this);
        cfg = mock(Configuration.class);
        when(cfg.getString(PARAM_HOST)).thenReturn(host);
        when(cfg.getString(PARAM_PORT)).thenReturn(String.valueOf(port));
        when(cfg.getString(PARAM_USER)).thenReturn(user);
        when(cfg.getString(PARAM_PWD)).thenReturn(pwd);
        when(cfg.getString(PARAM_START_TLS)).thenReturn(tls);
        when(configurationService.getConfiguration()).thenReturn(cfg);
    }

    @Test
    public void testReturnObjectHasCorrectConfigurationValues() {
        factory.afterPropertiesSet();
        final JavaMailSenderImpl jmsi = (JavaMailSenderImpl) factory.getObject();
        assertEquals(host, jmsi.getHost());
        assertEquals(port, jmsi.getPort());
        assertEquals(user, jmsi.getUsername());
        assertEquals(pwd, jmsi.getPassword());
        assertEquals(tls, jmsi.getJavaMailProperties().getProperty(PARAM_START_TLS));
    }

    @Test
    public void testHostNotSpecifiedBeanCreationException() {
        thrown.expect(BeanCreationException.class);
        when(cfg.getString(PARAM_HOST)).thenReturn(null);
        factory.afterPropertiesSet();
    }

    @Test
    public void testPortNotSpecifiedBeanCreationException() {
        thrown.expect(BeanCreationException.class);
        when(cfg.getString(PARAM_PORT)).thenReturn(null);
        factory.afterPropertiesSet();
    }

}

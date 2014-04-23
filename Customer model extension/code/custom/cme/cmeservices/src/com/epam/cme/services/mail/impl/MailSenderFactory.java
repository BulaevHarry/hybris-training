package com.epam.cme.services.mail.impl;

import de.hybris.platform.servicelayer.config.ConfigurationService;

import java.util.Properties;

import org.springframework.beans.factory.BeanCreationException;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.stereotype.Service;

@Service("mailSender")
public class MailSenderFactory implements FactoryBean, InitializingBean {

    private static final String PARAM_HOST = "mail.smtp.server";
    private static final String PARAM_PORT = "mail.smtp.port";
    private static final String PARAM_USER = "mail.smtp.user";
    private static final String PARAM_PWD = "mail.smtp.password";
    private static final String PARAM_START_TLS = "mail.smtp.starttls.enable";

    private JavaMailSender mailSender;

    @Autowired
    ConfigurationService configurationService;

    @Override
    public void afterPropertiesSet() {
        final String host = configurationService.getConfiguration().getString(PARAM_HOST);
        final String user = configurationService.getConfiguration().getString(PARAM_USER);
        final String port = configurationService.getConfiguration().getString(PARAM_PORT);
        final String pwd = configurationService.getConfiguration().getString(PARAM_PWD);
        final String startTls = configurationService.getConfiguration().getString(PARAM_START_TLS);

        if (host == null || host.isEmpty() || port == null || port.isEmpty()) {
            throw new BeanCreationException("Can not start mail sender, please configure properties " + PARAM_HOST
                    + " and " + PARAM_PORT);
        } else {
            final JavaMailSenderImpl sender = new JavaMailSenderImpl();
            sender.setHost(host);
            sender.setPort(Integer.parseInt(port));
            if (user != null) {
                sender.setUsername(user);
            }
            if (pwd != null) {
                sender.setPassword(pwd);
            }
            if (startTls != null && Boolean.parseBoolean(startTls)) {
                final Properties javaMailProperties = new Properties();
                javaMailProperties.setProperty(PARAM_START_TLS, "true");
                sender.setJavaMailProperties(javaMailProperties);
            }
            mailSender = sender;
        }
    }

    @Override
    public Object getObject() {
        return mailSender;
    }

    @Override
    public Class getObjectType() {
        return JavaMailSender.class;
    }

    @Override
    public boolean isSingleton() {
        return true;
    }

}

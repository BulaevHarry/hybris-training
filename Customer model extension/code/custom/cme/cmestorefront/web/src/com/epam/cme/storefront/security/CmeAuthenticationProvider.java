package com.epam.cme.storefront.security;

import de.hybris.platform.servicelayer.exceptions.UnknownIdentifierException;
import de.hybris.platform.spring.security.CoreAuthenticationProvider;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;

import com.epam.cme.core.model.BlockableCustomerModel;
import com.epam.cme.services.blockablecustomer.BlockableCustomerService;

public class CmeAuthenticationProvider extends CoreAuthenticationProvider {

    private AcceleratorAuthenticationProvider acceleratorAuthenticationProvider;
    private BlockableCustomerService blockableCustomerService;
    private static final Logger LOG = Logger.getLogger(CmeAuthenticationProvider.class);

    @Override
    public Authentication authenticate(final Authentication authentication) throws AuthenticationException {
        final String username = (authentication.getPrincipal() == null) ? "NONE_PROVIDED" : authentication.getName();
        final BlockableCustomerModel customerModel;
        try {
            customerModel = blockableCustomerService.getBlockableCustomerByUid(username);
        } catch (final UnknownIdentifierException ue) {
            throw new BadCredentialsException(messages.getMessage("CoreAuthenticationProvider.badCredentials",
                    "Bad credentials"));
        }
        if (customerModel.getBlockedStatus()) {
            throw new LockedException("User blocked");
        } else {
            if (acceleratorAuthenticationProvider.getBruteForceAttackCounter().isAttack(username)) {
                try {
                    customerModel.setBlockedStatus(true);
                    acceleratorAuthenticationProvider.getModelService().save(customerModel);
                } catch (final UnknownIdentifierException e) {
                    LOG.warn("Brute force attack attempt for non existing user name " + username);
                } finally {
                    throw new BadCredentialsException(messages.getMessage("CoreAuthenticationProvider.badCredentials",
                            "Bad credentials"));
                }
            }
        }
        acceleratorAuthenticationProvider.checkUserCart(username);
        return super.authenticate(authentication);
    }

    @Override
    protected void additionalAuthenticationChecks(final UserDetails details,
            final AbstractAuthenticationToken authentication) throws AuthenticationException {
        acceleratorAuthenticationProvider.additionalAuthenticationChecks(details, authentication);
    }

    @Required
    public void setAcceleratorAuthenticationProvider(
            final AcceleratorAuthenticationProvider acceleratorAuthenticationProvider) {
        this.acceleratorAuthenticationProvider = acceleratorAuthenticationProvider;
    }

    @Required
    public void setBlockableCustomerService(final BlockableCustomerService blockableCustomerService) {
        this.blockableCustomerService = blockableCustomerService;
    }

}

/*
 * [y] hybris Platform
 *
 * Copyright (c) 2000-2013 hybris AG
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of hybris
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with hybris.
 * 
 *  
 */
package com.epam.cme.storefront.forms;

import javax.validation.constraints.NotNull;

public class LoginForm {
    @NotNull(message = "{general.required}")
    private String j_username;
    @NotNull(message = "{general.required}")
    private String j_password;

    /**
     * @return the j_username
     */
    public String getJ_username() {
        return j_username;
    }

    /**
     * @param j_username
     *            the j_username to set
     */
    public void setJ_username(final String j_username) {
        this.j_username = j_username;
    }

    /**
     * @return the j_password
     */
    public String getJ_password() {
        return j_password;
    }

    /**
     * @param j_password
     *            the j_password to set
     */
    public void setJ_password(final String j_password) {
        this.j_password = j_password;
    }
}

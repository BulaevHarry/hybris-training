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
import javax.validation.constraints.Size;

import org.hibernate.validator.constraints.NotBlank;

/**
 * Form object for updating profile.
 */
public class UpdateProfileForm {

    private String titleCode;
    private String firstName;
    private String lastName;

    /**
     * @return the titleCode
     */
    @NotNull(message = "{profile.title.invalid}")
    @Size(min = 1, max = 255, message = "{profile.title.invalid}")
    public String getTitleCode() {
        return titleCode;
    }

    /**
     * @param titleCode
     *            the titleCode to set
     */
    public void setTitleCode(final String titleCode) {
        this.titleCode = titleCode;
    }

    /**
     * @return the firstName
     */
    @NotNull(message = "{profile.firstName.invalid}")
    @Size(min = 1, max = 255, message = "{profile.firstName.invalid}")
    @NotBlank(message = "{profile.firstName.invalid}")
    public String getFirstName() {
        return firstName;
    }

    /**
     * @param firstName
     *            the firstName to set
     */
    public void setFirstName(final String firstName) {
        this.firstName = firstName;
    }

    /**
     * @return the lastName
     */
    @NotNull(message = "{profile.lastName.invalid}")
    @Size(min = 1, max = 255, message = "{profile.lastName.invalid}")
    @NotBlank(message = "{profile.lastName.invalid}")
    public String getLastName() {
        return lastName;
    }

    /**
     * @param lastName
     *            the lastName to set
     */
    public void setLastName(final String lastName) {
        this.lastName = lastName;
    }

}

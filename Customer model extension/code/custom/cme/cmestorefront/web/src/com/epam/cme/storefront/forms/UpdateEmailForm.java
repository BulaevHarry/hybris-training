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

import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.NotBlank;


/**
 * Form object for updating email
 */
public class UpdateEmailForm
{

	private String email;
	private String chkEmail;
	private String password;


	/**
	 * @return the password
	 */
	@NotNull(message = "{profile.pwd.invalid}")
	@Size(min = 6, max = 255, message = "{profile.pwd.invalid}")
	@NotBlank(message = "{profile.pwd.invalid}")
	public String getPassword()
	{
		return password;
	}


	/**
	 * @param password
	 *           the password to set
	 */
	public void setPassword(final String password)
	{
		this.password = password;
	}

	/**
	 * @return the email
	 */
	@NotNull(message = "{profile.email.invalid}")
	@Size(min = 1, max = 255, message = "{profile.email.invalid}")
	@Email(message = "{profile.email.invalid}")
	public String getEmail()
	{
		return email;
	}

	/**
	 * @param email
	 *           the email to set
	 */
	public void setEmail(final String email)
	{
		this.email = email;
	}


	/**
	 * @return the chkEmail
	 */
	@NotNull(message = "{profile.checkEmail.invalid}")
	public String getChkEmail()
	{
		return chkEmail;
	}


	/**
	 * @param chkEmail
	 *           the chkEmail to set
	 */
	public void setChkEmail(final String chkEmail)
	{
		this.chkEmail = chkEmail;
	}

}

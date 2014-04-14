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

import com.epam.cme.storefront.forms.validation.EqualAttributes;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.NotBlank;




/**
 * Form object for registration
 */
@EqualAttributes(message = "{validation.checkPwd.equals}", value =
{ "pwd", "checkPwd" })
public class RegisterForm
{

	private String titleCode;
	private String firstName;
	private String lastName;
	private String email;
	private String pwd;
	private String checkPwd;

	/**
	 * @return the titleCode
	 */
	@NotNull(message = "{register.title.invalid}")
	@Size(min = 1, max = 255, message = "{register.title.invalid}")
	public String getTitleCode()
	{
		return titleCode;
	}

	/**
	 * @param titleCode
	 *           the titleCode to set
	 */
	public void setTitleCode(final String titleCode)
	{
		this.titleCode = titleCode;
	}

	/**
	 * @return the firstName
	 */
	@NotBlank(message = "{register.firstName.invalid}")
	@Size(min = 1, max = 255, message = "{register.firstName.invalid}")
	public String getFirstName()
	{
		return firstName;
	}

	/**
	 * @param firstName
	 *           the firstName to set
	 */
	public void setFirstName(final String firstName)
	{
		this.firstName = firstName;
	}

	/**
	 * @return the lastName
	 */
	@NotBlank(message = "{register.lastName.invalid}")
	@Size(min = 1, max = 255, message = "{register.lastName.invalid}")
	public String getLastName()
	{
		return lastName;
	}

	/**
	 * @param lastName
	 *           the lastName to set
	 */
	public void setLastName(final String lastName)
	{
		this.lastName = lastName;
	}

	/**
	 * @return the email
	 */
	@NotBlank(message = "{register.email.invalid}")
	@Size(min = 1, max = 255, message = "{register.email.invalid}")
	@Email(message = "{register.email.invalid}")
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
	 * @return the pwd
	 */
	@NotBlank(message = "{register.pwd.invalid}")
	@Size(min = 6, max = 255, message = "{register.pwd.invalid}")
	public String getPwd()
	{
		return pwd;
	}

	/**
	 * @param pwd
	 *           the pwd to set
	 */
	public void setPwd(final String pwd)
	{
		this.pwd = pwd;
	}

	/**
	 * @return the checkPwd
	 */
	@NotNull(message = "{register.checkPwd.invalid}")
	public String getCheckPwd()
	{
		return checkPwd;
	}

	/**
	 * @param checkPwd
	 *           the checkPwd to set
	 */
	public void setCheckPwd(final String checkPwd)
	{
		this.checkPwd = checkPwd;
	}
}

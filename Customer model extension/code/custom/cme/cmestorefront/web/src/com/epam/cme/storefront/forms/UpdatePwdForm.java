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



/**
 * Form object for updating the password.
 */
@EqualAttributes(message = "{validation.checkPwd.equals}", value =
{ "pwd", "checkPwd" })
public class UpdatePwdForm
{
	private String pwd;
	private String checkPwd;
	private String token;


	/**
	 * @return the pwd
	 */
	@NotNull(message = "{updatePwd.pwd.invalid}")
	@Size(min = 6, max = 255, message = "{updatePwd.pwd.invalid}")
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
	@NotNull(message = "{updatePwd.checkPwd.invalid}")
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

	/**
	 * @return the token
	 */
	public String getToken()
	{
		return token;
	}

	/**
	 * @param token
	 *           the token to set
	 */
	public void setToken(final String token)
	{
		this.token = token;
	}
}

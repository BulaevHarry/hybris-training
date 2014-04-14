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

import javax.validation.constraints.Digits;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;


/**
 * Validation form for Deleting a bundle
 */
public class DeleteBundleForm
{
	@NotNull(message = "{basket.error.bundleno.notNull}")
	@Min(value = 1, message = "{basket.error.bundleno.invalid}")
	@Digits(fraction = 0, integer = 10, message = "{basket.error.bundleno.invalid}")
	private int bundleNo;

	public int getBundleNo()
	{
		return bundleNo;
	}

	public void setBundleNo(final int bundleNo)
	{
		this.bundleNo = bundleNo;
	}

}

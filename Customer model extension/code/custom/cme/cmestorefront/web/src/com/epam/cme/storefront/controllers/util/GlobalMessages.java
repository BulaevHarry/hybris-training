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
package com.epam.cme.storefront.controllers.util;

import org.springframework.ui.Model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Displays "confirmation, information, error" messages
 */
public class GlobalMessages
{
	public static final String CONF_MESSAGES_HOLDER = "accConfMsgs";
	public static final String INFO_MESSAGES_HOLDER = "accInfoMsgs";
	public static final String ERROR_MESSAGES_HOLDER = "accErrorMsgs";

	public static void addConfMessage(final Model model, final String messageKey)
	{
		addMessage(model, CONF_MESSAGES_HOLDER, messageKey);
	}

	public static void addInfoMessage(final Model model, final String messageKey)
	{
		addMessage(model, INFO_MESSAGES_HOLDER, messageKey);
	}

	public static void addErrorMessage(final Model model, final String messageKey)
	{
		addMessage(model, ERROR_MESSAGES_HOLDER, messageKey);
	}

	protected static void addMessage(final Model model, final String messageHolder, final String messageKey)
	{
		if (model.containsAttribute(messageHolder))
		{
			final Map<String, Object> modelMap = model.asMap();
			final List<String> messageKeys = new ArrayList<String>((List<String>) modelMap.get(messageHolder));
			messageKeys.add(messageKey);
			model.addAttribute(messageHolder, messageKeys);
		}
		else
		{
			model.addAttribute(messageHolder, Collections.singletonList(messageKey));
		}
	}
}

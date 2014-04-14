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
package com.epam.cme.storefront.util;

import de.hybris.platform.catalog.model.KeywordModel;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.jsoup.Jsoup;


/**
 * 
 * Utility class for sanitizing up content that will appear in HTML meta tags.
 * 
 */
public class MetaSanitizerUtil
{
	/**
	 * Takes a List of KeywordModels and returns a comma separated list of keywords as String.
	 * 
	 * @param keywords
	 *           List of KeywordModel objects
	 * @return String of comma separated keywords
	 */
	public static String sanitizeKeywords(final List<KeywordModel> keywords)
	{
		if (keywords != null && !keywords.isEmpty())
		{
			// Remove duplicates
			final Set<String> keywordSet = new HashSet<String>(keywords.size());
			for (final KeywordModel kw : keywords)
			{
				keywordSet.add(kw.getKeyword());
			}

			// Format keywords, join with comma
			final StringBuilder sb = new StringBuilder();
			for (final String kw : keywordSet)
			{
				sb.append(kw).append(',');
			}
			if (sb.length() > 0)
			{
				// Remove last comma
				return sb.substring(0, sb.length() - 1);
			}
		}
		return "";
	}

	/**
	 * Takes a string of words, removes duplicates and returns a comma separated list of keywords as a String
	 * 
	 * @param s
	 *           Keywords to be sanitized
	 * @return String of comma separated keywords
	 */
	public static String sanitizeKeywords(final String s)
	{
		final String clean = (StringUtils.isNotEmpty(s) ? Jsoup.parse(s).text() : ""); // Clean html
		final String[] sa = StringUtils.split(clean.replace("\"", "")); // Clean quotes

		// Remove duplicates
		String noDupes = "";
		for (final String aSa : sa)
		{
			if (!noDupes.contains(aSa))
			{
				noDupes += aSa + ",";
			}
		}
		if (!noDupes.isEmpty())
		{
			noDupes = noDupes.substring(0, noDupes.length() - 1);
		}
		return noDupes;
	}

	/**
	 * Removes all HTML tags and double quotes and returns a String
	 * 
	 * @param s
	 *           Description to be sanitized
	 * @return String object
	 */
	public static String sanitizeDescription(final String s)
	{
		if (s != null)
		{
			final String clean = Jsoup.parse(s).text();
			return clean.replace("\"", "");
		}
		else
		{
			return "";
		}
	}
}

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
package com.epam.cme.storefront.security.cookie;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.FieldPosition;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;



/**
 * Stripped out org.apache.tomcat.util.http.ServerCookie for use of #appendCookieValue(StringBuffer, int, String,
 * String, String, String, String, int, boolean, boolean)
 */
public class ServerCookie implements Serializable
{

	// Other fields
	private static final String OLD_COOKIE_PATTERN = "EEE, dd-MMM-yyyy HH:mm:ss z";
	private static final DateFormat OLD_COOKIE_FORMAT;

	private static final String ancientDate;

	static
	{
		OLD_COOKIE_FORMAT = new SimpleDateFormat(OLD_COOKIE_PATTERN, Locale.US);
		OLD_COOKIE_FORMAT.setTimeZone(TimeZone.getTimeZone("GMT"));
		ancientDate = OLD_COOKIE_FORMAT.format(new Date(10000));
	}

	private ServerCookie()
	{

	}


	// -------------------- utils --------------------

	private static final String tspecials = ",; ";
	private static final String tspecials2 = "()<>@,;:\\\"/[]?={} \t";
	private static final String tspecials2NoSlash = "()<>@,;:\\\"[]?={} \t";

	private static boolean isToken(final String value, final String literals)
	{
		final String tspecials = (literals == null ? ServerCookie.tspecials : literals);
		if (value == null)
		{
			return true;
		}
		final int len = value.length();

		for (int i = 0; i < len; i++)
		{
			final char c = value.charAt(i);

			if (tspecials.indexOf(c) != -1)
			{
				return false;
			}
		}
		return true;
	}

	private static boolean containsCTL(final String value, final int version)
	{
		if (value == null)
		{
			return false;
		}
		final int len = value.length();
		for (int i = 0; i < len; i++)
		{
			final char c = value.charAt(i);
			if (c < 0x20 || c >= 0x7f)
			{
				if (c == 0x09)
				{
					continue; //allow horizontal tabs
				}
				return true;
			}
		}
		return false;
	}



	private static boolean isToken2(final String value, final String literals)
	{
		final String tspecials2 = (literals == null ? ServerCookie.tspecials2 : literals);
		if (value == null)
		{
			return true;
		}
		final int len = value.length();

		for (int i = 0; i < len; i++)
		{
			final char c = value.charAt(i);
			if (tspecials2.indexOf(c) != -1)
			{
				return false;
			}
		}
		return true;
	}


	// -------------------- Cookie parsing tools

	// TODO RFC2965 fields also need to be passed
	public static void appendCookieValue(final StringBuffer headerBuf, int version, final String name, final String value,
			final String path, final String domain, final String comment, final int maxAge, final boolean isSecure,
			final boolean isHttpOnly)
	{
		final StringBuffer buf = new StringBuffer(name);
		// Servlet implementation checks name
		buf.append('=');
		// Servlet implementation does not check anything else

		version = maybeQuote2(version, buf, value, true);

		// Add version 1 specific information
		if (version == 1)
		{
			// Version=1 ... required
			buf.append("; Version=1");

			// Comment=comment
			if (comment != null)
			{
				buf.append("; Comment=");
				maybeQuote2(version, buf, comment);
			}
		}

		// Add domain information, if present
		if (domain != null)
		{
			buf.append("; Domain=");
			maybeQuote2(version, buf, domain);
		}

		// Max-Age=secs ... or use old "Expires" format
		// TODO RFC2965 Discard
		if (maxAge >= 0)
		{
			if (version > 0)
			{
				buf.append("; Max-Age=");
				buf.append(maxAge);
			}
			// IE6, IE7 and possibly other browsers don't understand Max-Age.
			// They do understand Expires, even with V1 cookies!
			if (version == 0)
			{
				// Wdy, DD-Mon-YY HH:MM:SS GMT ( Expires Netscape format )
				buf.append("; Expires=");
				// To expire immediately we need to set the time in past
				if (maxAge == 0)
				{
					buf.append(ancientDate);
				}
				else
				{
					OLD_COOKIE_FORMAT.format(new Date(System.currentTimeMillis() + maxAge * 1000L), buf, new FieldPosition(0));
				}
			}
		}

		// Path=path
		if (path != null)
		{
			buf.append("; Path=");
			if (version == 0)
			{
				maybeQuote2(version, buf, path);
			}
			else
			{
				maybeQuote2(version, buf, path, ServerCookie.tspecials2NoSlash, false);
			}
		}

		// Secure
		if (isSecure)
		{
			buf.append("; Secure");
		}

		// HttpOnly
		if (isHttpOnly)
		{
			buf.append("; HttpOnly");
		}
		headerBuf.append(buf);
	}



	private static boolean alreadyQuoted(final String value)
	{
		if (value == null || value.length() == 0)
		{
			return false;
		}
		return (value.charAt(0) == '\"' && value.charAt(value.length() - 1) == '\"');
	}

	/**
	 * Quotes values using rules that vary depending on Cookie version.
	 * 
	 * @param version
	 * @param buf
	 * @param value
	 */
	public static int maybeQuote2(final int version, final StringBuffer buf, final String value)
	{
		return maybeQuote2(version, buf, value, false);
	}

	public static int maybeQuote2(final int version, final StringBuffer buf, final String value, final boolean allowVersionSwitch)
	{
		return maybeQuote2(version, buf, value, null, allowVersionSwitch);
	}

	public static int maybeQuote2(int version, final StringBuffer buf, final String value, final String literals,
			final boolean allowVersionSwitch)
	{
		if (value == null || value.length() == 0)
		{
			buf.append("\"\"");
		}
		else if (containsCTL(value, version))
		{
			throw new IllegalArgumentException("Control character in cookie value, consider BASE64 encoding your value");
		}
		else if (alreadyQuoted(value))
		{
			buf.append('"');
			buf.append(escapeDoubleQuotes(value, 1, value.length() - 1));
			buf.append('"');
		}
		else if (allowVersionSwitch && version == 0 && !isToken2(value, literals))
		{
			buf.append('"');
			buf.append(escapeDoubleQuotes(value, 0, value.length()));
			buf.append('"');
			version = 1;
		}
		else if (version == 0 && !isToken(value, literals))
		{
			buf.append('"');
			buf.append(escapeDoubleQuotes(value, 0, value.length()));
			buf.append('"');
		}
		else if (version == 1 && !isToken2(value, literals))
		{
			buf.append('"');
			buf.append(escapeDoubleQuotes(value, 0, value.length()));
			buf.append('"');
		}
		else
		{
			buf.append(value);
		}
		return version;
	}


	/**
	 * Escapes any double quotes in the given string.
	 * 
	 * @param s
	 *           the input string
	 * @param beginIndex
	 *           start index inclusive
	 * @param endIndex
	 *           exclusive
	 * @return The (possibly) escaped string
	 */
	private static String escapeDoubleQuotes(final String s, final int beginIndex, final int endIndex)
	{

		if (s == null || s.length() == 0 || s.indexOf('"') == -1)
		{
			return s;
		}

		final StringBuffer b = new StringBuffer();
		for (int i = beginIndex; i < endIndex; i++)
		{
			final char c = s.charAt(i);
			if (c == '\\')
			{
				b.append(c);
				//ignore the character after an escape, just append it
				if (++i >= endIndex)
				{
					throw new IllegalArgumentException("Invalid escape character in cookie value.");
				}
				b.append(s.charAt(i));
			}
			else if (c == '"')
			{
				b.append('\\').append('"');
			}
			else
			{
				b.append(c);
			}
		}

		return b.toString();
	}


}

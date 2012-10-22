/*
 * C5Connector.Java - The Java backend for the filemanager of corefive.
 * It provides a simple object for creating an editor instance.
 * Copyright (C) Thilo Schwarz
 * 
 * == BEGIN LICENSE ==
 * 
 * Licensed under the terms of any of the following licenses at your
 * choice:
 * 
 *  - GNU General Public License Version 2 or later (the "GPL")
 *    http://www.gnu.org/licenses/gpl.html
 * 
 *  - GNU Lesser General Public License Version 2.1 or later (the "LGPL")
 *    http://www.gnu.org/licenses/lgpl.html
 * 
 *  - Mozilla Public License Version 1.1 or later (the "MPL")
 *    http://www.mozilla.org/MPL/MPL-1.1.html
 * 
 * == END LICENSE ==
 */
package de.thischwa.c5c.util;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

import de.thischwa.c5c.resource.PropertiesLoader;

/**
 * Some static string helper methods.
 */
public class StringUtils {
	
	/** The empty string {@code ""}. */
	public static final String EMPTY_STRING = "";

	/**
	 * Checks if is null or empty.
	 *
	 * @param strings the strings
	 * @return true, if is null or empty
	 */
	public static final boolean isNullOrEmpty(final String... strings) {
		for (String string : strings) {
			if (!isNullOrEmpty(string))
				return false;
		}
		return true;
	}

	/**
	 * Checks if is null or empty.
	 *
	 * @param str the str
	 * @return true, if is null or empty
	 */
	private static boolean isNullOrEmpty(final String str) {
		return (str == null || str.length() == 0);
	}

	/**
	 * Checks if is null or empty or blank.
	 *
	 * @param str the str
	 * @return true, if is null or empty or blank
	 */
	public static final boolean isNullOrEmptyOrBlank(final String str) {
		if (isNullOrEmpty(str))
			return true;
		for (int i = 0; i < str.length(); i++) {
			if (!Character.isWhitespace(str.charAt(i)))
				return false;
		}
		return true;
	}

	/**
	 * Encode.
	 *
	 * @param str the str
	 * @return the string
	 */
	public static final String encode(final String str) {
		try {
			return URLEncoder.encode(str, PropertiesLoader.getDefaultEncoding());
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * Decode.
	 *
	 * @param str the str
	 * @return the string
	 */
	public static String decode(String str) {
		try {
			return URLDecoder.decode(str, PropertiesLoader.getDefaultEncoding());
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException(e);
		}
	}
	
	/**
	 * Divide and decode query string.
	 *
	 * @param query the query
	 * @return the map
	 */
	public static Map<String, String> divideAndDecodeQueryString(String query) {
		Map<String, String> params = new HashMap<String, String>();
		if(isNullOrEmptyOrBlank(query))
			return params;
		String[] keyValues = query.split("&");
		for (String keyValue : keyValues) {
			String[] kv = keyValue.split("=");
			String key = kv[0];
			String val = (kv.length == 2) ? decode(kv[1]) : "";
			params.put(key, val);
		}
		return params;
	}
}

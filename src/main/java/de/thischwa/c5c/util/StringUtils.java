/*
 * C5Connector.Java - The Java backend for the filemanager of corefive.
 * It's a bridge between the filemanager and a storage backend and 
 * works like a transparent VFS or proxy.
 * Copyright (C) Thilo Schwarz
 * 
 * == BEGIN LICENSE ==
 * 
 * Licensed under the terms of any of the following licenses at your
 * choice:
 * 
 *  - GNU Lesser General Public License Version 3.0 or later (the "LGPL")
 *    http://www.gnu.org/licenses/lgpl-3.0.html
 * 
 *  - Mozilla Public License Version 2.0 or later (the "MPL")
 *    http://www.mozilla.org/MPL/2.0/
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

	public static final boolean isNullOrEmpty(final String... strings) {
		for (String string : strings) {
			if (!_isNullOrEmpty(string))
				return false;
		}
		return true;
	}

	private static boolean _isNullOrEmpty(final String str) {
		return (str == null || str.length() == 0);
	}

	public static final boolean isNullOrEmptyOrBlank(final String str) {
		if (_isNullOrEmpty(str))
			return true;
		for (int i = 0; i < str.length(); i++) {
			if (!Character.isWhitespace(str.charAt(i)))
				return false;
		}
		return true;
	}

	public static final String encode(final String str) {
		try {
			return URLEncoder.encode(str, PropertiesLoader.getDefaultEncoding());
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException(e);
		}
	}

	public static String decode(String str) {
		try {
			return URLDecoder.decode(str, PropertiesLoader.getDefaultEncoding());
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException(e);
		}
	}

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

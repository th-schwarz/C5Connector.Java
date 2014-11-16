/*
 * C5Connector.Java - The Java backend for the filemanager of corefive.
 * It's a bridge between the filemanager and a storage backend and 
 * works like a transparent VFS or proxy.
 * Copyright (C) Thilo Schwarz
 * 
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package codes.thischwa.c5c.util;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.apache.commons.io.FilenameUtils;

import codes.thischwa.c5c.PropertiesLoader;

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
		Map<String, String> params = new HashMap<>();
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
	
	public static String getUniqueName(Set<String> existingNames, String name) {
		if(!existingNames.contains(name))
			return name;
		
		int count = 1;
		String ext = FilenameUtils.getExtension(name);
		String baseName = FilenameUtils.getBaseName(name);
		String tmpName;
		do {
			tmpName = String.format("%s_%d", baseName, count);
			if(!_isNullOrEmpty(ext))
				tmpName = String.format("%s.%s", tmpName, ext);
			count ++;
		} while(existingNames.contains(tmpName));
		return tmpName;
	}
}

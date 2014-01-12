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
package de.thischwa.c5c.util;

import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.junit.Test;

public class StringUtilsTest {

	@Test
	public void testIsNullOrEmpty() {
		String actual = null;
		assertTrue(StringUtils.isNullOrEmpty(actual));

		actual = "";
		assertTrue(StringUtils.isNullOrEmpty(actual));

		actual = " ";
		assertFalse(StringUtils.isNullOrEmpty(actual));

		actual = "abc";
		assertFalse(StringUtils.isNullOrEmpty(actual));
	}

	@Test
	public void testIsNullOrEmptyOrBlank() {
		String actual = null;
		assertTrue(StringUtils.isNullOrEmptyOrBlank(actual));

		actual = "";
		assertTrue(StringUtils.isNullOrEmptyOrBlank(actual));

		actual = " ";
		assertTrue(StringUtils.isNullOrEmptyOrBlank(actual));

		actual = "abc";
		assertFalse(StringUtils.isNullOrEmptyOrBlank(actual));

		actual = "a";
		assertFalse(StringUtils.isNullOrEmptyOrBlank(actual));
	}

	@Test
	public void testMultiNullOrEmpty() {
		String[] actual = { null, null };
		assertTrue(StringUtils.isNullOrEmpty(actual));

		actual = new String[] { null, "" };
		assertTrue(StringUtils.isNullOrEmpty(actual));

		actual = new String[] { null, "a" };
		assertFalse(StringUtils.isNullOrEmpty(actual));
	}

	@Test
	public void testDivideAndDecodeQueryString() {
		String query = null;
		assertEquals(0, StringUtils.divideAndDecodeQueryString(query).size());
		
		query = "path=%2ffolder";
		assertEquals("/folder", StringUtils.divideAndDecodeQueryString(query).get("path"));

		query = "path=%2ffolder&file=img.png";
		assertEquals(2, StringUtils.divideAndDecodeQueryString(query).size());
	}
	
	@Test
	public void testUniqueName() {
		Set<String> existingNames = new HashSet<>(Arrays.asList("name.ext", "name_1.ext", "file", "name_2.ext"));
		
		assertEquals("name_3.ext", StringUtils.getUniqueName(existingNames, "name.ext"));
		assertEquals("file_1", StringUtils.getUniqueName(existingNames, "file"));

		assertEquals("file.ext", StringUtils.getUniqueName(existingNames, "file.ext"));
	}
}

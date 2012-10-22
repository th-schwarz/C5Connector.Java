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

import static org.junit.Assert.*;

import org.junit.Test;

import de.thischwa.c5c.util.StringUtils;

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
}

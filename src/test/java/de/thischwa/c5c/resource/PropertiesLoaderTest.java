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
package de.thischwa.c5c.resource;

import static org.junit.Assert.*;

import java.util.Locale;

import org.junit.Test;

public class PropertiesLoaderTest {

	@Test
	public void testGetDefaultLocale() {
		Locale expected = Locale.ENGLISH;
		assertEquals(expected, PropertiesLoader.getDefaultLocale());
	}

}

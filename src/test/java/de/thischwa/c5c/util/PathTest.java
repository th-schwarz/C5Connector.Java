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

import org.junit.Test;

public class PathTest {

	@Test
	public void testPathString() {
		assertEquals("/folder/sub/", new Path("/folder/sub").toString());
	
		assertEquals("folder/sub/", new Path("folder/sub").toString());
	}

	@Test
	public void testAddFolder() {
		assertEquals("folder/sub/", new Path().addFolder("folder").addFolder("sub").toString());
		assertEquals("/folder/sub/", new Path("/folder/").addFolder("/sub").toString());
		assertEquals("/folder/sub/", new Path("/folder").addFolder("/sub").toString());
	}

}

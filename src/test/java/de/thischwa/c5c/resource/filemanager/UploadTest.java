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
package de.thischwa.c5c.resource.filemanager;

import static org.junit.Assert.*;

import org.junit.Test;

public class UploadTest {

	@Test
	public void testIsFileSizeLimitAuto() {
		Upload u = new Upload();
		u.setFileSizeLimit(20);
		assertFalse(u.isFileSizeLimitAuto());
		assertEquals(20, u.getFileSizeLimit());
		
		u.setFileSizeLimit();
		assertTrue(u.isFileSizeLimitAuto());
		assertEquals(-1, u.getFileSizeLimit());
	}

}

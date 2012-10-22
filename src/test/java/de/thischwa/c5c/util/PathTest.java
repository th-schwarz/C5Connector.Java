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

import de.thischwa.c5c.util.Path;

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
	}

}

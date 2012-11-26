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

public class VirtualFileTest {

	@Test
	public void testFile01() {
		VirtualFile vf = new VirtualFile("/tmp/folder/img.png");
		assertEquals("/tmp/folder/", vf.getFolder());
		assertEquals("img.png", vf.getName());
		assertEquals("png", vf.getExtension());
		assertEquals(VirtualFile.Type.FILE, vf.getType());
	}

	@Test
	public void testFile02() {
		VirtualFile vf = new VirtualFile("/img.png");
		assertEquals("/", vf.getFolder());
		assertEquals("img.png", vf.getName());
		assertEquals("png", vf.getExtension());
		assertEquals(VirtualFile.Type.FILE, vf.getType());
	}

	@Test
	public void testFile03() {
		VirtualFile vf = new VirtualFile("img.png");
		assertEquals("/", vf.getFolder());
		assertEquals("img.png", vf.getName());
		assertEquals("png", vf.getExtension());
		assertEquals(VirtualFile.Type.FILE, vf.getType());
	}

	@Test
	public void testDir01() {
		VirtualFile vf = new VirtualFile("/tmp/folder/");
		assertEquals("/tmp/", vf.getFolder());
		assertEquals("folder", vf.getName());
		assertNull(vf.getExtension());
		assertEquals(VirtualFile.Type.DIRECTORY, vf.getType());
	}
	
	@Test
	public void testDir02() {
		VirtualFile vf = new VirtualFile("/tmp/folder", true);
		assertEquals("/tmp/", vf.getFolder());
		assertEquals("folder", vf.getName());
		assertNull(vf.getExtension());
		assertEquals(VirtualFile.Type.DIRECTORY, vf.getType());
	}
	
	@Test
	public void testDir03() {
		VirtualFile vf = new VirtualFile("/tmp/folder.sub", true);
		assertEquals("/tmp/", vf.getFolder());
		assertEquals("folder.sub", vf.getName());
		assertNull(vf.getExtension());
		assertEquals(VirtualFile.Type.DIRECTORY, vf.getType());
	}
}

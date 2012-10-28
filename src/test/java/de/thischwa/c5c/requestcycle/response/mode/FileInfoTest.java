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
package de.thischwa.c5c.requestcycle.response.mode;

import static org.junit.Assert.*;

import org.junit.Test;

public class FileInfoTest {

	@Test
	public void testToString() {
		FileInfo fileInfo = new FileInfo("/tmp/img01.png", false);
		fileInfo.setFileProperties(100, 200, 30024, null);
		String expected = "{\"Error\":\"\",\"Code\":0,\"Properties\":{\"Date Created\":null,\"Date Modified\":null,\"Height\":100,\"Width\":200,\"Size\":30024},\"Path\":\"/tmp/img01.png\",\"Capabilities\":[],\"Preview\":null,\"Filename\":\"img01.png\",\"File Type\":\"png\"}";
		assertEquals(expected, fileInfo.toString());
	}

}

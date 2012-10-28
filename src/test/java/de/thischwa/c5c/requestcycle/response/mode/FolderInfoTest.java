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

import de.thischwa.c5c.requestcycle.response.AResponse;
import de.thischwa.c5c.requestcycle.response.mode.FileInfo;
import de.thischwa.c5c.requestcycle.response.mode.FolderInfo;

public class FolderInfoTest {
	
	@Test
	public void testToString() {
		AResponse resp = new FolderInfo();
		
		FileInfo fileInfo = new FileInfo("/tmp/img01.png", false);
		fileInfo.setFileProperties(100, 200, 30024, null);
		((FolderInfo)resp).add(fileInfo);
		
		fileInfo = new FileInfo("/tmp/folder", true);
		fileInfo.setFileProperties(200, 300, 40024, null);
		fileInfo.setFolderProperties(null);
		((FolderInfo)resp).add(fileInfo);

		String expected = "{\"/tmp/img01.png\":{\"Filename\":\"img01.png\",\"File Type\":\"png\",\"Path\":\"/tmp/img01.png\",\"Capabilities\":[],\"Preview\":null,\"Properties\":{\"Size\":30024,\"Date Created\":null,\"Date Modified\":null,\"Height\":100,\"Width\":200},\"Error\":\"\",\"Code\":0},\"/tmp/folder/\":{\"Filename\":\"folder\",\"File Type\":\"dir\",\"Path\":\"/tmp/folder/\",\"Capabilities\":[],\"Preview\":null,\"Properties\":{\"Size\":null,\"Date Created\":null,\"Date Modified\":null,\"Height\":null,\"Width\":null},\"Error\":\"\",\"Code\":0}}";
		assertEquals(expected, resp.toString());
	}

}

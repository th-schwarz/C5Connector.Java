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
package de.thischwa.c5c.requestcycle.response.mode;

import static org.junit.Assert.*;

import org.junit.Test;

public class FolderInfoTest {
	
	@Test
	public void testToString() {
		FolderInfo resp = new FolderInfo();
		
		FileInfo fileInfo = new FileInfo("/tmp/", false);
		fileInfo.setFileProperties(new FileInfoProperties("img01.png", 200, 100, 30024, null));
		resp.add(fileInfo);
		
		fileInfo = new FileInfo("/tmp", true);
		fileInfo.setFileProperties(new FileInfoProperties("folder", null));
		resp.add(fileInfo);

		String expected = "{\"/tmp/img01.png\":{\"Error\":\"\",\"Code\":0,\"Properties\":{\"Date Created\":null,\"Date Modified\":null,\"Height\":100,\"Width\":200,\"Size\":30024},\"Path\":\"/tmp/img01.png\",\"Capabilities\":[],\"Preview\":null,\"Filename\":\"img01.png\",\"File Type\":\"png\"},\"/tmp/folder/\":{\"Error\":\"\",\"Code\":0,\"Properties\":{\"Date Created\":null,\"Date Modified\":null,\"Height\":null,\"Width\":null,\"Size\":null},\"Path\":\"/tmp/folder/\",\"Capabilities\":[],\"Preview\":null,\"Filename\":\"folder\",\"File Type\":\"dir\"}}";
		assertEquals(expected, resp.toString());
	}

}

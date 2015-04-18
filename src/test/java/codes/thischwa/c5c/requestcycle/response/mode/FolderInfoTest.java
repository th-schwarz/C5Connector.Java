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
package codes.thischwa.c5c.requestcycle.response.mode;

import static org.junit.Assert.*;

import org.junit.Test;

import codes.thischwa.c5c.requestcycle.response.mode.FileInfo;
import codes.thischwa.c5c.requestcycle.response.mode.FileInfoProperties;
import codes.thischwa.c5c.requestcycle.response.mode.FolderInfo;

public class FolderInfoTest {
	
	@Test
	public void testToString() {
		FolderInfo resp = new FolderInfo();
		
		FileInfo fileInfo = new FileInfo("/tmp/", true, false);
		fileInfo.setFileProperties(new FileInfoProperties("img01.png", false, 200, 100, 30024, null));
		resp.add(fileInfo);
		
		fileInfo = new FileInfo("/tmp", true, false);
		fileInfo.setFileProperties(new FileInfoProperties("folder", false, null));
		resp.add(fileInfo);

		String expected = "{\"\\/tmp\\/img01.png\":{\"Capabilities\":[],\"Code\":0,\"Error\":\"\",\"File Type\":\"png\",\"Filename\":\"img01.png\",\"PathBuilder\":\"\\/tmp\\/img01.png\",\"Preview\":null,\"Properties\":{\"Date Created\":null,\"Date Modified\":null,\"Height\":100,\"Size\":30024,\"Width\":200},\"Protected\":0},\"\\/tmp\\/folder\\/\":{\"Capabilities\":[],\"Code\":0,\"Error\":\"\",\"File Type\":\"dir\",\"Filename\":\"folder\",\"PathBuilder\":\"\\/tmp\\/folder\\/\",\"Preview\":null,\"Properties\":{\"Date Created\":null,\"Date Modified\":null,\"Height\":null,\"Size\":null,\"Width\":null},\"Protected\":0}}";
		assertEquals(expected, resp.toString());
	}

}

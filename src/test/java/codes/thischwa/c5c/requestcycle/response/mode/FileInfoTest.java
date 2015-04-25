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

import java.util.Date;

import org.junit.Test;

import codes.thischwa.c5c.requestcycle.response.mode.FileInfo;
import codes.thischwa.c5c.requestcycle.response.mode.FileInfoProperties;

public class FileInfoTest {

	@Test
	public void testToString() {
		//Sat Apr 25 00:00:00 CEST 2015
		Date date = new Date(1429912800000l);
		FileInfo fileInfo = new FileInfo("/tmp", true, false);
		fileInfo.setFileProperties(new FileInfoProperties("img01.png", false, 200, 100, 30024, date));
		String expected = "{\"Capabilities\":[],\"Code\":0,\"Error\":\"\",\"File Type\":\"png\",\"Filename\":\"img01.png\",\"Path\":\"\\/tmp\\/img01.png\",\"Preview\":null,\"Properties\":{\"Date Created\":null,\"Date Modified\":null,\"Height\":100,\"Size\":30024,\"Width\":200},\"Protected\":0}";
		assertEquals(expected, fileInfo.toString());
	}

}

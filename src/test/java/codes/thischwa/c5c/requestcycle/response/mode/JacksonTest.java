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

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

import codes.thischwa.c5c.requestcycle.response.mode.FileInfo;
import codes.thischwa.c5c.requestcycle.response.mode.FileInfoProperties;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class JacksonTest {

	@Test
	public void testReadValue() throws JsonParseException, JsonMappingException, IOException {
		String str = "{'test name': 'test', 'value': 'val'}".replace("'", "\"");
		Map<String, Object> objs = new HashMap<>();
		ObjectMapper mapper = new ObjectMapper();
		objs = mapper.readValue(str, new TypeReference<Map<String, Object>>() {});
		assertEquals("test", objs.get("test name"));
	}

	@Test
	public void testAnnotation() throws JsonGenerationException, JsonMappingException, IOException {
		FileInfo fileInfo = new FileInfo("/tmp", true, false);
		fileInfo.setError("Test Error", 5);
		fileInfo.setFileProperties(new FileInfoProperties("img.png", false, 200, 100, 30024, null));
		String actual = "{\"Capabilities\":[],\"Code\":5,\"Error\":\"Test Error\",\"File Type\":\"png\",\"Filename\":\"img.png\",\"PathBuilder\":\"\\/tmp\\/img.png\",\"Preview\":null,\"Properties\":{\"Date Created\":null,\"Date Modified\":null,\"Height\":100,\"Size\":30024,\"Width\":200},\"Protected\":0}";
		assertEquals(actual, fileInfo.toString());
	}
}

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

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class JacksonTest {

	@Test
	public void testReadValue() throws JsonParseException, JsonMappingException, IOException {
		//String str = "{ name: 'basics', items : [ 'Bold','Italic','Strike','-','About' ] }".replace("'", "\"");
		String str = "{'test name': 'test', 'value': 'val'}".replace("'", "\"");
		Map<String, Object> objs = new HashMap<>();
		ObjectMapper mapper = new ObjectMapper();
		objs = mapper.readValue(str, new TypeReference<Map<String, Object>>() {});
		assertEquals("test", objs.get("test name"));
	}

	@Test
	public void testAnnotation() throws JsonGenerationException, JsonMappingException, IOException {
		FileInfo fileInfo = new FileInfo("/tmp", false);
		fileInfo.setError("Test Error", 5);
		fileInfo.setFileProperties(new FileInfoProperties("img.png", 200, 100, 30024, null));
		String actual = "{\"Error\":\"Test Error\",\"Code\":5,\"Properties\":{\"Date Created\":null,\"Date Modified\":null,\"Height\":100,\"Width\":200,\"Size\":30024},\"Path\":\"\\/tmp\\/img.png\",\"Capabilities\":[],\"Preview\":null,\"Filename\":\"img.png\",\"File Type\":\"png\"}";
		assertEquals(actual, fileInfo.toString());
	}
}

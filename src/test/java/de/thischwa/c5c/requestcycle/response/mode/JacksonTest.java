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

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;


import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;
import org.junit.Test;

import de.thischwa.c5c.requestcycle.response.mode.FileInfo;

public class JacksonTest {

	@Test
	public void testReadValue() throws JsonParseException, JsonMappingException, IOException {
		//String str = "{ name: 'basics', items : [ 'Bold','Italic','Strike','-','About' ] }".replace("'", "\"");
		String str = "{'test name': 'test', 'value': 'val'}".replace("'", "\"");
		Map<String, Object> objs = new HashMap<String, Object>();
		ObjectMapper mapper = new ObjectMapper();
		objs = mapper.readValue(str, new TypeReference<Map<String, Object>>() {});
		assertEquals("test", objs.get("test name"));
	}

	@Test
	public void testAnnotation() throws JsonGenerationException, JsonMappingException, IOException {
		FileInfo fileInfo = new FileInfo("/tmp/img.png", false);
		fileInfo.setError("Test Error", 5);
		fileInfo.setFileProperties(100, 200, 30024, null);
		String actual = "{\"Filename\":\"img.png\",\"File Type\":\"png\",\"Path\":\"/tmp/img.png\",\"Preview\":null,\"Properties\":{\"Size\":30024,\"Date Created\":null,\"Date Modified\":null,\"Height\":100,\"Width\":200},\"Error\":\"Test Error\",\"Code\":5}";
		assertEquals(actual, fileInfo.toString());
	}
}

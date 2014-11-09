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
package codes.thischwa.c5c.util.jackson;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;

import com.fasterxml.jackson.databind.ObjectMapper;

public class JacksonTest {

	private ObjectMapper mapper = new ObjectMapper(); 
	private Map<String, Object> data = new LinkedHashMap<>();
	private DataHolder dataHolder = new DataHolder();
	
	@Before
	public void init() {
		data.put("name", "john");
		data.put("key", Boolean.TRUE);
		
		dataHolder.setName("john");
		dataHolder.setKey(Boolean.TRUE);
	}
	
	@Test
	public void testMapBool() throws Exception {
		String dataStr = mapper.writeValueAsString(data);
		assertEquals("{\"name\":\"john\",\"key\":true}", dataStr);
	}

	@Test
	public void testMapStr() throws Exception {
		data.put("key", "value");
		String dataStr = mapper.writeValueAsString(data);
		assertEquals("{\"name\":\"john\",\"key\":\"value\"}", dataStr);
	}
	
	@Test
	public void testObjBool() throws Exception {
		String dataStr = mapper.writeValueAsString(dataHolder);
		assertEquals("{\"name\":\"john\",\"key\":true}", dataStr);
	}

	@Test
	public void testReadObjBool() throws Exception {
		DataHolder dh = mapper.readValue("{\"name\":\"john\",\"key\":true}", DataHolder.class);
		assertEquals("john", dh.getName());
		assertTrue(dh.getKey() instanceof Boolean);
		assertEquals(Boolean.TRUE, dh.getKey());
	}
	
	@Test
	public void testReadObjStr() throws Exception {
		DataHolder dh = mapper.readValue("{\"name\":\"john\",\"key\":\"value\"}", DataHolder.class);
		assertEquals("john", dh.getName());
		assertTrue(dh.getKey() instanceof String);
		assertEquals("value", dh.getKey());
	}

	@Test
	public void testObjStr() throws Exception {
		dataHolder.setKey("value");
		String dataStr = mapper.writeValueAsString(dataHolder);
		assertEquals("{\"name\":\"john\",\"key\":\"value\"}", dataStr);
	}
	
	@Test
	public void testReadList() throws Exception {
		@SuppressWarnings("unchecked")
		List<String> list = mapper.readValue("[\"a\",\"b\"]", ArrayList.class);
		assertTrue(list.get(0).equals("a"));
		assertTrue(list.get(1).equals("b"));
	}
	
	@Test
	public void testWriteList() throws Exception {
		List<String> list = new ArrayList<>();
		list.add("a");
		list.add("b");
		String dataStr = mapper.writeValueAsString(list);
		assertEquals("[\"a\",\"b\"]", dataStr);
	}
}

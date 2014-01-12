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
package de.thischwa.c5c;

import static org.junit.Assert.*;

import java.io.File;

import org.eclipse.jetty.testing.HttpTester;
import org.eclipse.jetty.testing.ServletTester;
import org.junit.Before;
import org.junit.Test;

public class RootGetRequestsTest extends GenericRequestTest {

	@Before
	public void setUp() throws Exception {
		File resourceBaseDir = new File("src/test/resources/requesttest");
		tester = new ServletTester();
		tester.setContextPath("/");
		tester.setResourceBase(resourceBaseDir.getAbsolutePath());
		tester.addServlet(ConnectorServlet.class, "/filemanager/connectors/java/*");
		tester.start();
	}

	@Test
	public void testGetFolder() throws Exception {
		HttpTester request = HttpTestFactory.buildInitialRequest();
		request.setURI("/filemanager/connectors/java/filemanager.java?path=%2Ffilemanager%2Fuserfiles%2F&mode=getfolder&showThumbs=true&time=241");
		String requestStr = request.generate();
		
		String responseStr = tester.getResponses(requestStr);
		HttpTester response = new HttpTester();
		response.parse(responseStr);
		
		assertEquals(200, response.getStatus());
		String actual = cleanResponse(response.getContent());
		String epected = cleanResponse("{\"/filemanager/userfiles/folder/\":{\"Error\":\"\",\"Code\":0,\"Properties\":{\"Date Created\":null,\"Height\":null,\"Width\":null,\"Size\":null},\"Path\":\"/filemanager/userfiles/folder/\",\"Capabilities\":[\"select\",\"delete\",\"rename\",\"download\"],\"Preview\":\"/filemanager/images/fileicons/_Open.png\",\"Filename\":\"folder\",\"File Type\":\"dir\"},\"/filemanager/userfiles/pic01.png\":{\"Error\":\"\",\"Code\":0,\"Properties\":{\"Date Created\":null,\"Height\":70,\"Width\":110,\"Size\":2250},\"Path\":\"/filemanager/userfiles/pic01.png\",\"Capabilities\":[\"select\",\"delete\",\"rename\",\"download\"],\"Preview\":\"/filemanager/images/fileicons/png.png\",\"Filename\":\"pic01.png\",\"File Type\":\"png\"}}");
		assertEquals(epected, actual);
	}

	@Test
	public void testGetInfo() throws Exception {
		HttpTester request = HttpTestFactory.buildInitialRequest(); 
		request.setURI("/filemanager/connectors/java/filemanager.java?mode=getinfo&path=%2Ffilemanager%2Fuserfiles%2Fpic01.png&time=244");
		String requestStr = request.generate();
		
		String responseStr = tester.getResponses(requestStr);
		HttpTester response = new HttpTester();
		response.parse(responseStr);
		
		assertEquals(200, response.getStatus());
		String actual = cleanResponse(response.getContent());
		String expected = cleanResponse("{\"Error\":\"\",\"Code\":0,\"Properties\":{\"Date Created\":null,\"Date Modified\":\"11\\/14\\/13\",\"Height\":70,\"Width\":110,\"Size\":2250},\"Path\":\"\\/filemanager\\/userfiles\\/pic01.png\",\"Capabilities\":[\"select\",\"delete\",\"rename\",\"download\"],\"Preview\":\"\\/filemanager\\/images\\/fileicons\\/png.png\",\"Filename\":\"pic01.png\",\"File Type\":\"png\"}");
		assertEquals(expected, actual);
	}
	
}

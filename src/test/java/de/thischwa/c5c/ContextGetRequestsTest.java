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
package de.thischwa.c5c;

import static org.junit.Assert.*;

import org.eclipse.jetty.testing.HttpTester;
import org.eclipse.jetty.testing.ServletTester;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class ContextGetRequestsTest {
	
	private ServletTester tester;

	@Before
	public void setUp() throws Exception {
		tester = new ServletTester();
		tester.setContextPath("/context");
		tester.setResourceBase("src/test/resources/requesttest");
		tester.addServlet(ConnectorServlet.class, "/filemanager/connectors/java/*");
		tester.start();
	}

	@After
	public void tearDown() throws Exception {
		tester.stop();
	}

	@Test
	public void testGetFolder() throws Exception {
		HttpTester request = HttpTestFactory.buildInitialRequest();
		request.setURI("/context/filemanager/connectors/java/filemanager.java?path=%2Fuserfiles%2F&mode=getfolder&showThumbs=true&time=258");
		String requestStr = request.generate();
		
		String responseStr = tester.getResponses(requestStr);
		HttpTester response = new HttpTester();
		response.parse(responseStr);
		
		assertEquals(200, response.getStatus());
		assertEquals("{\"/userfiles/folder/\":{\"Filename\":\"folder\",\"File Type\":\"dir\",\"Path\":\"/userfiles/folder/\",\"Preview\":\"/filemanager/images/fileicons/_Open.png\",\"Properties\":{\"Size\":null,\"Height\":null,\"Date Created\":null,\"Date Modified\":\"9/26/12\",\"Width\":null},\"Capabilities\":[\"select\",\"delete\",\"rename\",\"download\"],\"Code\":0,\"Error\":\"\"},\"/userfiles/pic01.png\":{\"Filename\":\"pic01.png\",\"File Type\":\"png\",\"Path\":\"/userfiles/pic01.png\",\"Preview\":\"/filemanager/images/fileicons/png.png\",\"Properties\":{\"Size\":2250,\"Height\":110,\"Date Created\":null,\"Date Modified\":\"7/16/12\",\"Width\":70},\"Capabilities\":[\"select\",\"delete\",\"rename\",\"download\"],\"Code\":0,\"Error\":\"\"}}", response.getContent());
	}

	@Test
	public void testGetInfo() throws Exception {
		HttpTester request = HttpTestFactory.buildInitialRequest(); 
		request.setURI("/context/filemanager/connectors/java/filemanager.java?mode=getinfo&path=%2Fuserfiles%2Fpic01.png&time=244");
		String requestStr = request.generate();
		
		String responseStr = tester.getResponses(requestStr);
		HttpTester response = new HttpTester();
		response.parse(responseStr);
		
		assertEquals(200, response.getStatus());
		assertEquals("{\"Filename\":\"pic01.png\",\"File Type\":\"png\",\"Path\":\"/userfiles/pic01.png\",\"Preview\":\"/filemanager/images/fileicons/png.png\",\"Properties\":{\"Size\":2250,\"Height\":110,\"Date Created\":null,\"Date Modified\":\"7/16/12\",\"Width\":70},\"Capabilities\":[\"select\",\"delete\",\"rename\",\"download\"],\"Code\":0,\"Error\":\"\"}", response.getContent());
	}
}

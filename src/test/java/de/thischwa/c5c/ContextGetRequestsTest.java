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

import org.eclipse.jetty.http.HttpStatus;
import org.eclipse.jetty.testing.HttpTester;
import org.eclipse.jetty.testing.ServletTester;
import org.junit.Before;
import org.junit.Test;

public class ContextGetRequestsTest extends GenericRequestTest {

	@Before
	public void setUp() throws Exception {
		tester = new ServletTester();
		tester.setContextPath("/context");
		tester.setResourceBase("src/test/resources/requesttest");
		tester.addServlet(ConnectorServlet.class, "/filemanager/connectors/java/*");
		tester.start();
	}

	@Test
	public void testGetFolder() throws Exception {
		HttpTester request = HttpTestFactory.buildInitialRequest();
		request.setURI("/context/filemanager/connectors/java/filemanager.java?path=%2Fuserfiles%2F&mode=getfolder&showThumbs=true&time=258");
		String requestStr = request.generate();
		
		String responseStr = tester.getResponses(requestStr);
		HttpTester response = new HttpTester();
		response.parse(responseStr);
		
		assertEquals(HttpStatus.OK_200, response.getStatus());
		String actual = cleanResponse(response.getContent());
		String expected = cleanResponse("{\"/userfiles/folder/\":{\"Error\":\"\",\"Code\":0,\"Properties\":{\"Date Created\":null,\"Date Modified\":\"9/26/12\",\"Height\":null,\"Width\":null,\"Size\":null},\"Path\":\"/userfiles/folder/\",\"Capabilities\":[\"select\",\"delete\",\"rename\",\"download\"],\"Preview\":\"/context/filemanager/images/fileicons/_Open.png\",\"Filename\":\"folder\",\"File Type\":\"dir\"},\"/userfiles/pic01.png\":{\"Error\":\"\",\"Code\":0,\"Properties\":{\"Date Created\":null,\"Date Modified\":\"7/16/12\",\"Height\":70,\"Width\":110,\"Size\":2250},\"Path\":\"/userfiles/pic01.png\",\"Capabilities\":[\"select\",\"delete\",\"rename\",\"download\"],\"Preview\":\"/context/filemanager/images/fileicons/png.png\",\"Filename\":\"pic01.png\",\"File Type\":\"png\"}}");
		assertEquals(expected, actual);
	}

	@Test
	public void testGetInfo() throws Exception {
		HttpTester request = HttpTestFactory.buildInitialRequest(); 
		request.setURI("/context/filemanager/connectors/java/filemanager.java?mode=getinfo&path=%2Fuserfiles%2Fpic01.png&time=244");
		String requestStr = request.generate();
		
		String responseStr = tester.getResponses(requestStr);
		HttpTester response = new HttpTester();
		response.parse(responseStr);
		
		assertEquals(HttpStatus.OK_200, response.getStatus());
		String actual = cleanResponse(response.getContent());
		String expected = cleanResponse("{\"Error\":\"\",\"Code\":0,\"Properties\":{\"Date Created\":null,\"Date Modified\":\"7/16/12\",\"Height\":70,\"Width\":110,\"Size\":2250},\"Path\":\"/userfiles/pic01.png\",\"Capabilities\":[\"select\",\"delete\",\"rename\",\"download\"],\"Preview\":\"/context/filemanager/images/fileicons/png.png\",\"Filename\":\"pic01.png\",\"File Type\":\"png\"}");
		assertEquals(expected, actual);
	}
}

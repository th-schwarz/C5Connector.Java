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
package codes.thischwa.c5c;

import java.io.File;

import javax.servlet.ServletContext;

import org.eclipse.jetty.testing.HttpTester;
import org.eclipse.jetty.testing.ServletTester;
import org.junit.After;
import org.junit.Before;

public abstract class GenericRequestTest {

	protected ServletTester servletTester = null;

	@Before
	public void setUp() throws Exception {
		servletTester = new ServletTester();
		servletTester.setResourceBase("src/test/resources");
		servletTester.getContext().getServer().getConnectors()[0].setMaxIdleTime(1000*60*10);
		servletTester.getContext().setAttribute(ServletContext.TEMPDIR, new File(System.getProperty("java.io.tmpdir")));
		servletTester.addServlet(ConnectorServlet.class, "/filemanager/connectors/java/*");
		initTester();
		servletTester.start();
	}
	
	protected abstract void initTester();
	
	@After
	public void tearDown() throws Exception {
		servletTester.stop();
		servletTester = null;
	}

	protected String cleanResponse(String response) {
		String actual = response.replaceAll("\\\"Date Modified\\\":\\\"\\d*\\\\/\\d*\\\\/\\d*\\\",", "");
		actual = actual.replaceAll("&t=\\d*", "");
		return actual;
	}

	protected HttpTester buildInitialRequest() {
		HttpTester tester = new HttpTester(PropertiesLoader.getDefaultEncoding());
		tester.setMethod("GET");
	    tester.setVersion("HTTP/1.0");
	    tester.setHeader("Host","localhost");
	    return tester;
	}
}

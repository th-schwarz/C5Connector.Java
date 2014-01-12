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

import org.eclipse.jetty.testing.HttpTester;

import de.thischwa.c5c.resource.PropertiesLoader;

/**
 * A factory for creating HttpTest objects.
 */
public class HttpTestFactory {

	/**
	 * Builds the initial request.
	 *
	 * @return the http tester
	 */
	static HttpTester buildInitialRequest() {
		HttpTester request = new HttpTester(PropertiesLoader.getDefaultEncoding());
		request.setMethod("GET");
        request.setVersion("HTTP/1.0");
        request.setHeader("Host","localhost");
        return request;
	}
}

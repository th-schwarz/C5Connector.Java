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

import org.eclipse.jetty.testing.ServletTester;
import org.junit.After;

public class GenericRequestTest {

	protected ServletTester tester;

	@After
	public void tearDown() throws Exception {
		tester.stop();
	}

	String cleanResponse(String response) {
		String actual = response.replaceAll("\\\"Date Modified\\\":\\\"\\d*/\\d*/\\d*\\\",", "");
		return actual;
	}
}

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

import de.thischwa.c5c.requestcycle.response.GenericResponse;


/**
 * Abstract class for each dispatcher (put and get).
 */
abstract class GenericDispatcher {
	protected Connector connector;

	GenericDispatcher(Connector connector) {
		this.connector = connector;
	}
	
	abstract GenericResponse doRequest();
	
	protected String buildBackendPath(String urlPath) {
		return UserObjectProxy.getBackendPath(urlPath);
	}
}

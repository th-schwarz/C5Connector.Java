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
package codes.thischwa.c5c.requestcycle;

import javax.servlet.ServletContext;

import codes.thischwa.c5c.Connector;


/**
 * Interface for mapping the path from the url-request to the backend-path. <br/>
 * <br/>
 * <b>Hint:</b> You are free to implement this interface they way you need it. The return value can be global (regardless of a request) or
 * on a per-request basis.
 */
public interface BackendPathBuilder {

	/**
	 * Returns the absolute bankend-path of the requested url-path. The provided implementation of {@link Connector} will use
	 * this value to resolve the server-side location of resources. <br/>
	 * <br/>
	 * <b>Hint:</b> E.g. {@link Context} and/or {@link ServletContext} can be used to to implement a filesystem storage for each user.
	 * 
	 * @param urlPath
	 * @param context
	 * @param servletContext
	 * @return the constructed server-side path
	 */
	public String getBackendPath(String urlPath, Context context, ServletContext servletContext);

}

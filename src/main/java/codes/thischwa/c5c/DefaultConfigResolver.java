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

import javax.servlet.ServletContext;

import codes.thischwa.c5c.filemanager.FilemanagerConfig;

/**
 * An interface to resolve the default configuration of the filemanager.
 */
public interface DefaultConfigResolver {

	/**
	 * Initialization of the context.
	 * 
	 * @param servletContext
	 */
	public void initContext(ServletContext servletContext);

	/**
	 * Fetches the default configuration of the filemanager.
	 * 
	 * @return the default configuration
	 * 
	 * @throws RuntimeException
	 *             must be thrown if the configuration couldn't be read
	 */
	public FilemanagerConfig read() throws RuntimeException;
}

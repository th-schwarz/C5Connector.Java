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
import javax.servlet.http.HttpServletRequest;

import codes.thischwa.c5c.Connector;
import codes.thischwa.c5c.filemanager.FilemanagerConfig;

/**
 * An interface to specify the configuration of the filemanager.<br/>
 * The implemented object has to fill the {@link FilemanagerConfig}. This configuration object is 
 * used to dispatch the configuration settings to the implementation of the {@link Connector}.<br/>
 * <br/>
 * <b>Hint:</b> You are free to implement this interface the way you need it, 
 * in other words your return values can be global, regardless of the request, or on a per-request basis. 
 * For example, it would be possible to build a separate configuration for each user. But the 'icons'-section
 * should *not* be changed!
 * 
 * @see FilemanagerConfig
 */
public interface FilemanagerConfigBuilder {

	/** Base filename of the config file of the filemanager. */
	public static final String BASE_FILE_NAME = "filemanager.config.js";
	
	/**
	 * Builds the configuration of the filemanager. 
	 * 
	 * @param req the {@link HttpServletRequest} of the current request
	 * @param servletContext
	 * @return a filled {@link FilemanagerConfig} object
	 */
	public FilemanagerConfig getConfig(HttpServletRequest req, ServletContext servletContext);
}

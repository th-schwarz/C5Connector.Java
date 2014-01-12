/*
 * C5Connector.Java - The Java backend for the filemanager of corefive.
 * It's a bridge between the filemanager and a storage backend and 
 * works like a transparent VFS or proxy.
 * Copyright (C) Thilo Schwarz
 * 
 * == BEGIN LICENSE ==
 * 
 * Licensed under the terms of any of the following licenses at your
 * choice:
 * 
 *  - GNU Lesser General Public License Version 3.0 or later (the "LGPL")
 *    http://www.gnu.org/licenses/lgpl-3.0.html
 * 
 *  - Mozilla Public License Version 2.0 or later (the "MPL")
 *    http://www.mozilla.org/MPL/2.0/
 * 
 * == END LICENSE ==
 */
package de.thischwa.c5c.requestcycle;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;

import org.eclipse.jetty.server.Connector;

import de.thischwa.c5c.resource.filemanager.FilemanagerConfig;

/**
 * An interface to specify the configuration of the filemanager.<br/>
 * The implemented object has to fill the {@link FilemanagerConfig}. This configuration object is 
 * used by the implementation of the {@link Connector}.<br/>
 * <br/>
 * <b>Hint:</b> You are free to implement this interface the way you need it, 
 * in other words your return values can be global, regardless of the request, or on a per-request basis. 
 * For example, it would be possible to build a separate configuration for each user. But the 'icons'-section
 * should *not* be chanded!
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

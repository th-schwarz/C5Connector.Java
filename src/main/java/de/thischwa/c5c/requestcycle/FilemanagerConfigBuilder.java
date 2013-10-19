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

import de.thischwa.c5c.resource.filemanager.FilemanagerConfig;

/**
 * An Interface to determine the configuration of the filemanager.<br/>
 * 
 * @see FilemanagerConfig
 */
public interface FilemanagerConfigBuilder {

	public static final String BASE_FILE_NAME = "filemanager.config.js";
	
	public FilemanagerConfig getConfig(HttpServletRequest req, ServletContext servletContext);
}

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
package de.thischwa.c5c.requestcycle;

import javax.servlet.ServletContext;

import de.thischwa.c5c.util.VirtualFile;
import de.thischwa.c5c.util.VirtualFile.Type;

/**
 * An interface to determine the path to the icons which are displayed in the filemanager for directories and different files. <br/>
 * <i>Hint:</i> It determines the 'preview'-property of the JSON-response.
 */
public interface IconResolver {

	/** Response key to signal a directory */
	public static final String key_directory = Type.directory.toString().toLowerCase();

	/** Response key to signal an unknown file. */
	public static final String key_unknown = "unknown";

	public void setServletContext(ServletContext servletContext);

	/**
	 * Obtains the url-path of the requested file.
	 * 
	 * @param vf
	 *            A {@link VirtualFile} that represents the requested file.
	 * @return the url-path of the requested file
	 */
	public String getIconPath(VirtualFile vf);
}

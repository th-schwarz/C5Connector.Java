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

import de.thischwa.c5c.util.VirtualFile.Type;

/**
 * An interface to determine the path to the icons which are displayed in the filemanager for directories and different files. <br/>
 * <i>Hint:</i> It determines the 'preview'-property of the JSON-response.
 */
public interface IconResolver {

	/** GenericResponse key to signal a directory */
	public static final String key_directory = Type.directory.toString().toLowerCase();

	/** GenericResponse key to signal an unknown file. */
	public static final String key_default = "unknown";

	/**
	 * Initialization.
	 * 
	 * @param servletContext
	 * @param iconPath
	 *            the full url-path to the icons, e.g. '/filemanager/images/fileicons/
	 * @param defaultIcon
	 *            filename of the default icon
	 * @param directoryIcon
	 *            filename of the icons for directories
	 */
	public void init(ServletContext servletContext, String iconPath, String defaultIcon, String directoryIcon);

	public String getIconPath(String extension);

	public String getIconPathForDirectory();
}

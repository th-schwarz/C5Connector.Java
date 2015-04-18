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

import codes.thischwa.c5c.util.VirtualFile.Type;

/**
 * An interface to determine the path to the icons which are displayed in the filemanager for directories and different files. <br/>
 * <i>Hint:</i> It determines the 'preview'-property of the JSON-response.
 */
public interface IconResolver {

	/** GenericResponse key to signal a directory */
	public static final String key_directory = Type.directory.toString().toLowerCase();
	public static final String key_directory_lock = key_directory + "_lock"; 

	/** GenericResponse key to signal an unknown file. */
	public static final String key_default = "unknown";
	public static final String key_default_lock = "unknown_lock";

	/**
	 * Initialization of the context.
	 * 
	 * @param servletContext
	 */
	public void initContext(ServletContext servletContext);

	/**
	 * Initialization of the request.
	 * 
	 * @param defaultIcon
	 *            filename of the default icon
	 * @param directoryIcon
	 *            filename of the icons for directories
	 * @return an {@link IconRequestResolver} initialized for the current request
	 */
	public IconRequestResolver initRequest(String iconPath, String defaultIcon, String directoryIcon);
}

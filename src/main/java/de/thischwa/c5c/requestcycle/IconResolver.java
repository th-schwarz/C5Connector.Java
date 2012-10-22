/*
 * C5Connector.Java - The Java backend for the filemanager of corefive.
 * It provides a simple object for creating an editor instance.
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
 * TODO document me
 */
public interface IconResolver {

	/** The Constant key_directory. */
	public static final String key_directory = Type.directory.toString().toLowerCase();
	
	/** The Constant key_unknown. */
	public static final String key_unknown = "unknown";

	/**
	 * Sets the servlet context.
	 *
	 * @param servletContext the new servlet context
	 */
	public void setServletContext(ServletContext servletContext);
	
	/**
	 * Gets the icon path.
	 *
	 * @param vf the vf
	 * @return the icon path
	 */
	public String getIconPath(VirtualFile vf);
}

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
package de.thischwa.c5c.util;

import java.io.File;

import de.thischwa.c5c.Constants;

/**
 * Helper object for building paths with or without a file. </br>
 * Without a path, a FILE always ends with '/'.
 */
public class Path {
	
	private StringBuilder sb;

	/**
	 * Instantiates a new path.
	 */
	public Path() {
		sb = new StringBuilder();
	}
	
	/**
	 * Instantiates a new path with a pre-set folder.
	 *
	 * @param folder the folder
	 */
	public Path(String folder) {
		this();
		String cleanedFolder = cleanPath(folder);
		if(cleanedFolder.endsWith(Constants.separator))
			cleanedFolder = cleanedFolder.substring(0, cleanedFolder.length()-1);
		sb.append(cleanedFolder);
	}
	
	/**
	 * Adds the folder.
	 *
	 * @param folder the folder
	 * @return the path
	 */
	public Path addFolder(String folder) {
		StringBuilder sb = new StringBuilder(cleanPath(folder));
		if(sb.charAt(0) == Constants.separatorChar)
			sb.deleteCharAt(0);
		if(sb.charAt(sb.length()-1) == Constants.separatorChar)
			sb.deleteCharAt(sb.length()-1);
		if(this.sb.length() != 0)
			this.sb.append(Constants.separatorChar);
		this.sb.append(sb);
		return this;
	}
	
	/**
	 * Adds the FILE.
	 *
	 * @param baseName the base name
	 * @return the string
	 */
	public String addFile(String baseName) {
		String tmp = cleanPath(baseName);
		if(tmp.startsWith(Constants.separator))
			tmp = tmp.substring(1);
		return toString().concat(tmp);
	}
	
	/**
	 * Clean path.
	 *
	 * @param path the path
	 * @return the string
	 */
	private String cleanPath(String path) {
		return path.replace(File.separator, Constants.separator);
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return sb.toString().concat(Constants.separator);
	}
}

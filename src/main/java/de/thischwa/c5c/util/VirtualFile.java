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
package de.thischwa.c5c.util;

import de.thischwa.c5c.Constants;

/**
 * A virtual file object which represents a file from the filemanager. 
 * It provides methods to get basic information like name, extension and so on.  
 */
public class VirtualFile {

	/** Type of the virtual file object. */
	public enum Type { DIRECTORY, FILE }

	private Type type;

	private String folder;

	private String fullPath;

	private String extension;

	private String name;

	public VirtualFile(String fullPath, boolean isDir) {
		this.fullPath = fullPath;
		if(!fullPath.startsWith(Constants.separator))
			fullPath = Constants.separator.concat(fullPath);
		if (isDir && !fullPath.endsWith(Constants.separator)) // simple normalization
			fullPath += Constants.separator;
		
		type = Type.FILE;
		String cleanPath = fullPath;
		if(cleanPath.endsWith(Constants.separator))
			cleanPath = cleanPath.substring(0, cleanPath.length()-1);
		if (fullPath.endsWith(Constants.separator)) {
			folder = fullPath;
			type = Type.DIRECTORY;
		} else {
			int extPos = fullPath.lastIndexOf(".");
			if (extPos == -1)
				extension = null;
			else
				extension = fullPath.substring(extPos + 1);
		}
		
		int pathPos = cleanPath.lastIndexOf(Constants.separator);
		if (pathPos != -1) {
			folder = cleanPath.substring(0, pathPos + 1);
			name = cleanPath.substring(pathPos + 1, cleanPath.length());
		}
	}

	public VirtualFile(String fullPath) {
		this(fullPath, false);
	}

	public String getFullPath() {
		return fullPath;
	}

	public String getFolder() {
		return folder;
	}

	public String getName() {
		return name;
	}

	public String getExtension() {
		return extension;
	}

	public Type getType() {
		return type;
	}
}

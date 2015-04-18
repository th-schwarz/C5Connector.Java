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
package codes.thischwa.c5c.util;

import codes.thischwa.c5c.Constants;
import codes.thischwa.c5c.GenericConnector;
import codes.thischwa.c5c.requestcycle.response.mode.FileInfoProperties;

/**
 * A virtual file object which represents a file from the filemanager. 
 * It provides methods to get basic information like name, extension and so on.  
 */
public class VirtualFile {

	/** Type of the virtual file object. */
	public enum Type { 
		/** Indicates a directory. */
		directory, 

		/** Indicates a file. */
		file }

	private Type type;

	private String folder;

	private String fullPath;

	private String extension;

	private String name;
	
	private boolean protect; 
	
	public VirtualFile(GenericConnector.FileProperties fp) {
		this(fp.getName(), fp.isDir(), fp.isProtected());
	}
	
	public VirtualFile(FileInfoProperties fp, boolean isDir) {
		this(fp.getName(), isDir, fp.isProtected());
	}

	VirtualFile(String fullPath, boolean isDir, boolean protect) {
		this.protect = protect;
		this.fullPath = fullPath;
		if(!fullPath.startsWith(Constants.defaultSeparator))
			fullPath = Constants.defaultSeparator.concat(fullPath);
		if (isDir && !fullPath.endsWith(Constants.defaultSeparator)) // simple normalization
			fullPath += Constants.defaultSeparator;
		
		type = Type.file;
		String cleanPath = fullPath;
		if(cleanPath.endsWith(Constants.defaultSeparator))
			cleanPath = cleanPath.substring(0, cleanPath.length()-1);
		if (fullPath.endsWith(Constants.defaultSeparator)) {
			folder = fullPath;
			type = Type.directory;
		} else {
			int extPos = fullPath.lastIndexOf(".");
			if (extPos == -1)
				extension = null;
			else
				extension = fullPath.substring(extPos + 1).toLowerCase();
		}
		
		int pathPos = cleanPath.lastIndexOf(Constants.defaultSeparator);
		if (pathPos != -1) {
			folder = cleanPath.substring(0, pathPos + 1);
			name = cleanPath.substring(pathPos + 1, cleanPath.length());
		}
	}

	// for files
	public VirtualFile(String fullPath, boolean protect) {
		this(fullPath, false, protect);
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
	
	public boolean isProtect() {
		return protect;
	}
}

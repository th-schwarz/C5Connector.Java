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

import java.io.File;

import codes.thischwa.c5c.Constants;

/**
 * Helper object for building paths with or without a file. </br>
 * Without a path, a file always ends with '/'.
 */
public class PathBuilder {
	
	private StringBuilder sb;

	/**
	 * Instantiates a new path.
	 */
	public PathBuilder() {
		sb = new StringBuilder();
	}
	
	/**
	 * Instantiates a new path with a pre-set folder.
	 *
	 * @param folder the folder
	 */
	public PathBuilder(String folder) {
		this();
		String cleanedFolder = cleanPath(folder);
		if(cleanedFolder.endsWith(Constants.defaultSeparator))
			cleanedFolder = cleanedFolder.substring(0, cleanedFolder.length()-1);
		sb.append(cleanedFolder);
	}
	
	/**
	 * Adds the folder.
	 *
	 * @param folder the folder
	 * @return the path
	 */
	public PathBuilder addFolder(String folder) {
		StringBuilder sb = new StringBuilder(cleanPath(folder));
		if(sb.charAt(0) == Constants.defaultSeparatorChar)
			sb.deleteCharAt(0);
		if(sb.charAt(sb.length()-1) == Constants.defaultSeparatorChar)
			sb.deleteCharAt(sb.length()-1);
		if(this.sb.length() != 0)
			this.sb.append(Constants.defaultSeparatorChar);
		this.sb.append(sb);
		return this;
	}
	
	/**
	 * Adds the file.
	 *
	 * @param baseName the base name
	 * @return the string
	 */
	public String addFile(String baseName) {
		String tmp = cleanPath(baseName);
		if(tmp.startsWith(Constants.defaultSeparator))
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
		return path.replace(File.separator, Constants.defaultSeparator);
	}
	
	/**
	 * Builds the path.
	 * 
	 * @return the path builded with the defaultSeparator {@link Constants#defaultSeparator}
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return sb.toString().concat(Constants.defaultSeparator);
	}
}

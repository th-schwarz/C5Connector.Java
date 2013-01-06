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
package de.thischwa.c5c;

import java.io.InputStream;
import java.util.Date;

import de.thischwa.c5c.requestcycle.response.FileProperties;
import de.thischwa.c5c.requestcycle.response.mode.DownloadInfo;
import de.thischwa.c5c.requestcycle.response.mode.FileInfoProperties;
import de.thischwa.c5c.requestcycle.response.mode.UploadFile;

/**
 * Factory to simplify the building of objects which are used as response objects of some methods of 
 * the implementation of the {@link Connector} interface.
 */
public class ResponseFactory {

	/**
	 * Builds the {@link FileInfoProperties} which holds the basic properties of a representation
	 * of a file for the filemanager.
	 *  
	 * @param name - the name of the file
	 * @param size - the absolute size of the file
	 * @param modified - the date the file was last modified
	 * @return The initialized {@link FileInfoProperties}. 
	 */
	public static FileProperties buildFileProperties(String name, long size, Date modified) {
		return new FileProperties(name, size, modified);
	}

	/**
	 * Builds the {@link DownloadInfo}.
	 * 
	 * @param in - inputstream of the file
	 * @param fileSize - the absolute size of the file
	 * @return The initialized {@link DownloadInfo}.
	 */
	public static DownloadInfo buildDownloadInfo(final InputStream in, long fileSize) {
		return new DownloadInfo(in, fileSize);
	}

	/**
	 * Builds the {@link UploadFile}.
	 * 
	 * @param urlPath - the original 'path' parameter
	 * @param sanitizedName - the sanitized name of the file
	 * @param fileSize - the absolute size of the file
	 * @return The initialized {@link UploadFile}.
	 */
	public static UploadFile buildUploadFile(String urlPath, String sanitizedName, Long fileSize) {
		// TODO check a max-upload-size (to be set in the properties)
		return new UploadFile(urlPath, sanitizedName);
	}
}

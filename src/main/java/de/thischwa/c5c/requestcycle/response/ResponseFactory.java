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
package de.thischwa.c5c.requestcycle.response;

import java.io.InputStream;
import java.util.Date;

import de.thischwa.c5c.Connector;
import de.thischwa.c5c.Constants;
import de.thischwa.c5c.requestcycle.response.mode.Delete;
import de.thischwa.c5c.requestcycle.response.mode.DownloadInfo;
import de.thischwa.c5c.requestcycle.response.mode.FileInfoProperties;
import de.thischwa.c5c.requestcycle.response.mode.Rename;
import de.thischwa.c5c.requestcycle.response.mode.UploadFile;

/**
 * Factory to simplify the building of objects which are used as response objects of some methods of the implementation of the
 * {@link Connector} interface.
 */
public class ResponseFactory {

	/**
	 * Builds the {@link FileInfoProperties} which holds the basic properties of a representation of a file for the filemanager.
	 * 
	 * @param name
	 *            the name of the file
	 * @param isDir TODO
	 * @param size
	 *            the absolute size of the file
	 * @param modified
	 *            the date the file was last modified
	 * @return The initialized {@link FileInfoProperties}.
	 */
	public static FileProperties buildFileProperties(String name, boolean isDir, long size, Date modified) {
		return (isDir) ? new FileProperties(name, modified) : new FileProperties(name, size, modified);
	}

	/**
	 * Builds the {@link Delete}-response.
	 * 
	 * @param path
	 *            full path of the file or directory to delete
	 * @param isDirectory
	 *            <code>true</code> if a directory was deleted, otherwise <code>false</code>
	 * @return The initialized {@link Delete}.
	 */
	public static Delete buildDelete(String path, boolean isDirectory) {
		String delPath = path;
		if (isDirectory && !delPath.endsWith(Constants.defaultSeparator))
			delPath += Constants.defaultSeparator;
		return new Delete(delPath);
	}

	/**
	 * Builds the {@link Rename}.
	 * 
	 * @param urlPath the 'original' path parameter
	 * @param newSanitizedName the sanitized new name
	 * @param isDirectory
	 *            <code>true</code> if a directory was deleted, otherwise <code>false</code>
	 * @return The initialized {@link Rename}.
	 */
	public static Rename buildRename(String urlPath, String newSanitizedName, boolean isDirectory) {
		return new Rename(urlPath, newSanitizedName, isDirectory);
	}

	/**
	 * Builds the {@link DownloadInfo}.
	 * 
	 * @param in
	 *            {@link InputStream} in which the file data has to put
	 * @param fileSize
	 *            the absolute size of the file
	 * @return The initialized {@link DownloadInfo}.
	 */
	public static DownloadInfo buildDownloadInfo(final InputStream in, long fileSize) {
		return new DownloadInfo(in, fileSize);
	}

	/**
	 * Builds the {@link UploadFile}.
	 * 
	 * @param urlPath
	 *            the original 'path' parameter
	 * @param sanitizedName
	 *            the sanitized name of the file
	 * @param fileSize
	 *            the absolute size of the file
	 * @return The initialized {@link UploadFile}.
	 */
	public static UploadFile buildUploadFile(String urlPath, String sanitizedName, Long fileSize) {
		// TODO check a max-upload-size (to be set in the properties)
		return new UploadFile(urlPath, sanitizedName);
	}
}

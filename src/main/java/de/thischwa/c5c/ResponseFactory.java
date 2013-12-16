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
package de.thischwa.c5c;

import java.io.InputStream;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonIgnore;

import de.thischwa.c5c.requestcycle.response.mode.FileInfoProperties;

/**
 * Factory class for building response objects. 
 */
public final class ResponseFactory {

	/**
	 * Builds the {@link DownloadInfo} which holds the data for the response of the download.
	 * 
	 * @param in {@link InputStream} of the file to download
	 * @param fileSize size of the file to download
	 * @return The initialized {@link DownloadInfo}.
	 */
	public static DownloadInfo buildDownloadInfo(InputStream in, long fileSize) {
		return new DownloadInfo(in, fileSize);
	}

	/**
	 * Builds the {@link FileInfoProperties} which holds the basic properties of a representation of a directory of the filemanager.
	 * 
	 * @param name
	 *            the name of the file
	 * @param modified
	 *            the date the file was last modified
	 * @return The initialized {@link FileInfoProperties}.
	 */
	public static FileProperties buildForDirectory(String name, Date modified) {
		return new FileProperties(name, modified);
	}

	/**
	 * Builds the {@link FileInfoProperties} which holds the basic properties of a representation of a file of the filemanager.
	 * 
	 * @param name
	 *            the name of the file
	 * @param size
	 *            the absolute size of the file
	 * @param modified
	 *            the date the file was last modified
	 * @return The initialized {@link FileInfoProperties}.
	 */
	public static FileProperties buildForFile(String name, long size, Date modified) {
		return new FileProperties(name, size, modified);
	}

	/**
	 * Builds the {@link FileInfoProperties} which holds the basic properties of a representation of a image of the filemanager.
	 * 
	 * @param name
	 *            the name of the file
	 * @param width
	 *            the width of the image
	 * @param height
	 *            the height of the image
	 * @param size
	 *            the absolute size of the file
	 * @param modified
	 *            the date the file was last modified
	 * @return The initialized {@link FileInfoProperties}.
	 */
	public static FileProperties buildForImage(String name, int width, int height, long size, Date modified) {
		return new FileProperties(name, width, height, size, modified);
	}

	/**
	 * Simple container object to hold data which is needed for the download action.
	 */
	public static class DownloadInfo {

		private InputStream in;
		private long fileSize;

		DownloadInfo(InputStream in, long fileSize) {
			this.in = in;
			this.fileSize = fileSize;
		}

		public InputStream getInputStream() {
			return in;
		}

		public long getFileSize() {
			return fileSize;
		}
	}
	
	/**
	 * Object to hold the properties of a file or folder. It is used for the response of a get-request of an implementation of the
	 * {@link Connector}.
	 */
	public static class FileProperties extends FileInfoProperties {

		private boolean isDir;

		FileProperties(String name, Date modified) {
			super(name, modified);
			isDir = true;
		}

		FileProperties(String name, long size, Date modified) {
			super(name, size, modified);
			isDir = false;
		}

		FileProperties(String name, int width, int height, long size, Date modified) {
			super(name, width, height, size, modified);
			isDir = false;
		}

		@JsonIgnore
		public boolean isDir() {
			return isDir;
		}
	}
}

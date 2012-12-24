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
import de.thischwa.c5c.requestcycle.response.mode.UploadFile;

/**
 * ResponseFactory.java - TODO DOCUMENTME!
 */
public class ResponseFactory {

	public static FileProperties buildFileProperties(String name, long size, Date modified) {
		return new FileProperties(name, size, modified);
	}

	public static UploadFile buildUploadFile(String path, String sanitizedName, Long fileSize) {
		// TODO check a max-upload-size (to be set in the properties)
		return new UploadFile(path, sanitizedName);
	}

	public static DownloadInfo buildDownloadInfo(InputStream in, long fileSize) {
		return new DownloadInfo(in, fileSize);
	}
}

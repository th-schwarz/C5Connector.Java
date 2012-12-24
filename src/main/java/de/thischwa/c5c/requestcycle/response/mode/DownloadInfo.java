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
package de.thischwa.c5c.requestcycle.response.mode;

import java.io.InputStream;

/**
 * Simple container object to hold data which is relevant for download. 
 */
public final class DownloadInfo {
	
	private InputStream in;
	private long fileSize;
	
	public DownloadInfo(InputStream in, long fileSize) {
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

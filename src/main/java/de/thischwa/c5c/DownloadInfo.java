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

/**
 * Simple container object to hold data which is needed for the download action. 
 */
public final class DownloadInfo {
	
	private InputStream in;
	private long fileSize;
	
	DownloadInfo() {
	}
	
	public void init(InputStream in, long fileSize) {
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
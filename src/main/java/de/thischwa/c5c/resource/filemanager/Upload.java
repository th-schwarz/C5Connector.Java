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
package de.thischwa.c5c.resource.filemanager;


/**
 * Represents the type <code>upload</code> of the JSON configuration of the filemanager. 
 */
public class Upload {

	private boolean overwrite;
	private boolean imagesOnly;
	private Object fileSizeLimit;

	Upload() {
	}

	public boolean isOverwrite() {
		return overwrite;
	}

	public void setOverwrite(boolean overwrite) {
		this.overwrite = overwrite;
	}

	public boolean isImagesOnly() {
		return imagesOnly;
	}

	public void setImagesOnly(boolean imagesOnly) {
		this.imagesOnly = imagesOnly;
	}

	public int getFileSizeLimit() {
		try {
			return ((Integer)fileSizeLimit).intValue();
		} catch (Exception e) {
			return -1;
		}
	}

	public void setFileSizeLimit(int fileSizeLimit) {
		this.fileSizeLimit = fileSizeLimit;
	}
	
	public void setFileSizeLimit() {
		this.fileSizeLimit = "auto";
	}
	
	public boolean isFileSizeLimitAuto() {
		return (fileSizeLimit != null && fileSizeLimit.equals("auto"));
	}
}

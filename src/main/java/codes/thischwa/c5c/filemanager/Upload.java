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
package codes.thischwa.c5c.filemanager;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;


/**
 * Represents the type <code>upload</code> of the JSON configuration of the filemanager. 
 */
@JsonIgnoreProperties(ignoreUnknown = true)
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
	
	@JsonIgnore
	public boolean isFileSizeLimitAuto() {
		return (fileSizeLimit != null && fileSizeLimit.equals("auto"));
	}
}

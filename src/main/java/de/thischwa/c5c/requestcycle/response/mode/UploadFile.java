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
package de.thischwa.c5c.requestcycle.response.mode;

import com.fasterxml.jackson.annotation.JsonProperty;

import de.thischwa.c5c.Constants;
import de.thischwa.c5c.requestcycle.response.GenericResponse;
import de.thischwa.c5c.util.StringUtils;

/**
 * Holds the data of an upload GenericResponse.
 */
public final class UploadFile extends GenericResponse {

	private String path;
	
	private String name;
	
	public UploadFile(String path, String name) {
		this.path = path;
		this.name = name;
		if(!StringUtils.isNullOrEmpty(this.path) && !this.path.endsWith(Constants.defaultSeparator))
			this.path += Constants.defaultSeparator;
	}
	
	public UploadFile(String errorMessage, int errorCode) {
		setError(errorMessage, errorCode);
	}

	@JsonProperty("Path")
	public String getPath() {
		return path;
	}
	
	public void setPath(String path) {
		this.path = path;
	}

	@JsonProperty("Name")
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	@Override
	public String toString() {
		String jsonStr = super.toString();
		return String.format("<textarea>%s</textarea>", jsonStr);
	}
}

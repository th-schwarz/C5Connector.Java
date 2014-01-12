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
package de.thischwa.c5c.requestcycle.response.mode;

import com.fasterxml.jackson.annotation.JsonProperty;

import de.thischwa.c5c.Constants;
import de.thischwa.c5c.FilemanagerAction;
import de.thischwa.c5c.requestcycle.response.GenericResponse;
import de.thischwa.c5c.util.StringUtils;

/**
 * Holds the data of an upload GenericResponse.
 */
public final class UploadFile extends GenericResponse {

	private String path;
	
	private String name;
	
	public UploadFile(String path, String name) {
		super(FilemanagerAction.UPLOAD);
		this.path = path;
		this.name = name;
		if(!StringUtils.isNullOrEmpty(this.path) && !this.path.endsWith(Constants.defaultSeparator))
			this.path += Constants.defaultSeparator;
	}
	
	public UploadFile(String errorMessage, int errorCode) {
		super(FilemanagerAction.UPLOAD);
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

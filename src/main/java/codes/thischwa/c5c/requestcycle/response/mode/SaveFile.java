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
package codes.thischwa.c5c.requestcycle.response.mode;

import codes.thischwa.c5c.FilemanagerAction;
import codes.thischwa.c5c.requestcycle.response.GenericResponse;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Holds the data for a Savefile response.
 */
public final class SaveFile extends GenericResponse {

	private String fullPath;

	public SaveFile(String fullPath) {
		super(FilemanagerAction.SAVEFILE);
		this.fullPath = fullPath;
	}
	
	@JsonProperty("Builder")
	public String getFullPath() {
		return fullPath;
	}
}

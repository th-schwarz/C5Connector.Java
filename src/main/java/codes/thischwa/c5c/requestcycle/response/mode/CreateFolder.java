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
 * Holds the data for a CreateFolder response.
 */
public final class CreateFolder extends GenericResponse {

	private String parentUrlPath;
	
	private String folderName;
	
	public CreateFolder(String parentUrlPath, String folderName) {
		super(FilemanagerAction.CREATEFOLDER);
		this.parentUrlPath = parentUrlPath;
		this.folderName = folderName;
	}

	@JsonProperty("Parent")
	public String getParentUrlPath() {
		return parentUrlPath;
	}
	
	@JsonProperty("Name")
	public String getFolderName() {
		return folderName;
	}
}

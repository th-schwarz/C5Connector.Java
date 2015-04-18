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

import codes.thischwa.c5c.Constants;
import codes.thischwa.c5c.FilemanagerAction;
import codes.thischwa.c5c.requestcycle.response.GenericResponse;
import codes.thischwa.c5c.util.PathBuilder;
import codes.thischwa.c5c.util.VirtualFile;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Holds the data of a Rename response.
 */
public final class Rename extends GenericResponse {
	
	private String oldFullPath;
	
	private String oldName;
	
	private String newFullPath;
	
	private String newName;
	
	public Rename(String oldFullPath, String newName, boolean isDirectory) {
		super(FilemanagerAction.RENAME);
		this.oldFullPath = oldFullPath;
		this.newName = newName;
		VirtualFile oldVF = new VirtualFile(oldFullPath, false);
		oldName = oldVF.getName();
		newFullPath = new PathBuilder(oldVF.getFolder()).addFile(newName).toString();
		if(isDirectory && !newFullPath.endsWith(Constants.defaultSeparator))
			this.newFullPath += Constants.defaultSeparator;
		if(isDirectory && !this.oldFullPath.endsWith(Constants.defaultSeparator))
			this.oldFullPath += Constants.defaultSeparator;
	}
	
	@JsonProperty("Old Path")
	public String getOldFullPath() {
		return oldFullPath;
	}
	
	@JsonProperty("Old Name")
	public String getOldName() {
		return oldName;
	}

	@JsonProperty("New Path")
	public String getNewFullPath() {
		return newFullPath;
	}
	
	@JsonProperty("New Name")
	public String getNewName() {
		return newName;
	}
}

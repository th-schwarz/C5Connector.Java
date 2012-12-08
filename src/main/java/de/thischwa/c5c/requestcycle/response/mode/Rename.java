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

import com.fasterxml.jackson.annotation.JsonProperty;

import de.thischwa.c5c.requestcycle.response.Response;
import de.thischwa.c5c.util.Path;
import de.thischwa.c5c.util.VirtualFile;

/**
 * TODO document me
 */
public class Rename extends Response {
	
	private String oldFullPath;
	
	private String oldName;
	
	private String newFullPath;
	
	private String newName;
	
	Rename(String oldFullPath, String newName) {
		this.oldFullPath = oldFullPath;
		this.newName = newName;
		VirtualFile oldVF = new VirtualFile(oldFullPath);
		oldName = oldVF.getName();
		newFullPath = new Path(oldVF.getFolder()).addFile(newName).toString();
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

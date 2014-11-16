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

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Represents the type <code>icons</code> of the JSON configuration of the filemanager. 
 */
public class Icons {

	private String path;
	private String directory;
	
	@JsonProperty("default")
	private String defaultIcon;

	Icons() {
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public String getDirectory() {
		return directory;
	}

	public void setDirectory(String directory) {
		this.directory = directory;
	}

	public String getDefaultIcon() {
		return defaultIcon;
	}

	public void setDefaultIcon(String defaultIcon) {
		this.defaultIcon = defaultIcon;
	}
}

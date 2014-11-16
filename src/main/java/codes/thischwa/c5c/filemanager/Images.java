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

import java.util.Set;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Represents the type <code>images</code> of the JSON configuration of the filemanager. 
 */
@JsonIgnoreProperties(ignoreUnknown=true)
public class Images {

	@JsonProperty("imagesExt")
	private Set<String> extensions;
	
	private Resize resize = new Resize();

	Images() {
	}

	public Set<String> getExtensions() {
		return extensions;
	}

	public void setExtensions(Set<String> extensions) {
		this.extensions = extensions;
	}

	public Resize getResize() {
		return resize;
	}
	
	public void setResize(Resize resize) {
		this.resize = resize;
	}
}

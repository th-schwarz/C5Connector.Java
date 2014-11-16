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

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Represents the type <code>edit</code> of the JSON configuration of the filemanager.
 */
public class Edit {
	private boolean enabled;
	private boolean lineNumbers;
	private boolean lineWrapping;
	private boolean codeHighlight;
	private String theme;

	@JsonProperty("editExt")
	private Set<String> extensions;
	
	Edit() {
	}

	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	public boolean isLineNumbers() {
		return lineNumbers;
	}

	public void setLineNumbers(boolean lineNumbers) {
		this.lineNumbers = lineNumbers;
	}
	
	public boolean isLineWrapping() {
		return lineWrapping;
	}
	
	public void setLineWrapping(boolean lineWrapping) {
		this.lineWrapping = lineWrapping;
	}

	public boolean isCodeHighlight() {
		return codeHighlight;
	}

	public void setCodeHighlight(boolean codeHighlight) {
		this.codeHighlight = codeHighlight;
	}

	public Set<String> getExtensions() {
		return extensions;
	}

	public void setExtensions(Set<String> extensions) {
		this.extensions = extensions;
	}
	
	public String getTheme() {
		return theme;
	}
	
	public void setTheme(String theme) {
		this.theme = theme;
	}
}

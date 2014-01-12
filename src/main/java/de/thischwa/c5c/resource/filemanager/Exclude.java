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
package de.thischwa.c5c.resource.filemanager;

import java.util.Set;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Represents the type <code>exclude</code> of the JSON configuration of the filemanager. 
 */
public class Exclude {

	@JsonProperty("unallowed_files")
	private Set<String> disallowedFiles;
	
	@JsonProperty("unallowed_dirs")
	private Set<String> disallowedDirs;
	
	@JsonProperty("unallowed_files_REGEXP")
	private String disallowedFilesRegex;

	@JsonProperty("unallowed_dirs_REGEXP")
	private String disallowedDirsRegex;
	
	Exclude() {
	}

	public Set<String> getDisallowedFiles() {
		return disallowedFiles;
	}

	public void setDisallowedFiles(Set<String> disallowedFiles) {
		this.disallowedFiles = disallowedFiles;
	}

	public Set<String> getDisallowedDirs() {
		return disallowedDirs;
	}

	public void setDisallowedDirs(Set<String> disallowedDirs) {
		this.disallowedDirs = disallowedDirs;
	}

	public String getDisallowedFilesRegex() {
		return disallowedFilesRegex;
	}

	public void setDisallowedFilesRegex(String disallowedFilesRegex) {
		this.disallowedFilesRegex = disallowedFilesRegex;
	}

	public String getDisallowedDirsRegex() {
		return disallowedDirsRegex;
	}

	public void setDisallowedDirsRegex(String disallowedDirsRegex) {
		this.disallowedDirsRegex = disallowedDirsRegex;
	}
	
}

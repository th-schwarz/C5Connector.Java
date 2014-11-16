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
 * Represents the type <code>security</code> of the JSON configuration of the filemanager. 
 */
@JsonIgnoreProperties(ignoreUnknown=true)
public class Security {

	public enum UPLOAD_POLICY {
		ALLOW_ALL, DISALLOW_ALL;
	}

	private UPLOAD_POLICY uploadPolicy;

	@JsonProperty("uploadRestrictions")
	private Set<String> allowedExtensions;

	Security() {
	}

	public UPLOAD_POLICY getUploadPolicy() {
		return uploadPolicy;
	}

	public void setUploadPolicy(UPLOAD_POLICY uploadPolicy) {
		this.uploadPolicy = uploadPolicy;
	}

	public Set<String> getAllowedExtensions() {
		return allowedExtensions;
	}

	public void setAllowedExtensions(Set<String> allowedExtensions) {
		this.allowedExtensions = allowedExtensions;
	}
}

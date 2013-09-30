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
 *  - GNU Lesser General Public License Version 3.0 or later (the "LGPL")
 *    http://www.gnu.org/licenses/lgpl-3.0.html
 * 
 *  - Mozilla Public License Version 2.0 or later (the "MPL")
 *    http://www.mozilla.org/MPL/2.0/
 * 
 * == END LICENSE ==
 */
package de.thischwa.c5c.resource.filemanager;

import java.util.Set;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Represents the type <code>security</code> of the JSON configuration of the filemanager. 
 */
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

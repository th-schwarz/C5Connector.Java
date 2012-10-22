/*
 * C5Connector.Java - The Java backend for the filemanager of corefive.
 * It provides a simple object for creating an editor instance.
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

import org.codehaus.jackson.annotate.JsonProperty;

import de.thischwa.c5c.Constants;
import de.thischwa.c5c.requestcycle.response.AResponse;
import de.thischwa.c5c.util.StringUtils;

/**
 * TODO document me
 */
public class UploadFile extends AResponse {

	/** The path. */
	private String path;
	
	/** The name. */
	private String name;
	
	UploadFile(String path, String name) {
		this.path = path;
		this.name = name;
		if(!StringUtils.isNullOrEmpty(this.path) && !this.path.endsWith(Constants.separator))
			this.path += Constants.separator;
	}

	@JsonProperty("Path")
	public String getPath() {
		return path;
	}

	@JsonProperty("Name")
	public String getName() {
		return name;
	}

	@Override
	public String toString() {
		String jsonStr = super.toString();
		return String.format("<textarea>%s</textarea>", jsonStr);
	}
}

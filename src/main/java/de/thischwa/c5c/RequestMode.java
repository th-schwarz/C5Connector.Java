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
package de.thischwa.c5c;

import java.util.Arrays;

/**
 * TODO document me
 */
public enum RequestMode {
	
	INFO(RequestMode.CONTENTTYPE_JSON, "getinfo"),
	
	FOLDER(RequestMode.CONTENTTYPE_JSON, "getfolder"),
	
	RENAME(RequestMode.CONTENTTYPE_JSON, "rename"),
	
	DELETE(RequestMode.CONTENTTYPE_JSON, "delete"),
	
	CREATEFOLDER(RequestMode.CONTENTTYPE_JSON, "addfolder"),
	
	UPLOAD(RequestMode.CONTENTTYPE_HTML, "add"),

	DOWNLOAD(RequestMode.CONTENTTYPE_DOWNLOAD, "download");
	
	/** The allowed get request modes. */
	private static RequestMode[] allowedGetRequestModes = new RequestMode[] {INFO,FOLDER,RENAME,DELETE,CREATEFOLDER}; 
	
	private String contentType;
	
	private String parameterName;

	private static final String CONTENTTYPE_DOWNLOAD = "application/x-download";

	private static final String CONTENTTYPE_HTML = "text/html";

	private static final String CONTENTTYPE_JSON = "application/json";
	
	private RequestMode(String contentType, String parameterName) {
		this.contentType = contentType;
		this.parameterName = parameterName;
	}
	
	public String getContentType() {
		return contentType;
	}
	
	public String getParameterName() {
		return parameterName;
	}
	
	public static RequestMode valueOfIgnoreCase(String mode) throws IllegalArgumentException {
		for(RequestMode rm : RequestMode.values()) {
			if(rm.getParameterName().equalsIgnoreCase(mode))
				return rm;
		}
		throw new IllegalArgumentException(String.format("No mode found for %s", mode));
	}
	
	/**
	 * Checks if the desired mode is one for a get request.
	 *
	 * @param mode the mode
	 * @return true, if it is a get request
	 */
	public static boolean isGetRequest(RequestMode mode) {
		return Arrays.asList(allowedGetRequestModes).contains(mode);
	}
}

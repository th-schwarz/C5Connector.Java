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
 * Handles the content-type and parameter names for each action of the filemanager.
 */
public enum FilemanagerAction {
	
	INFO(FilemanagerAction.CONTENTTYPE_JSON, "getinfo"),
	
	FOLDER(FilemanagerAction.CONTENTTYPE_JSON, "getfolder"),
	
	RENAME(FilemanagerAction.CONTENTTYPE_JSON, "rename"),
	
	DELETE(FilemanagerAction.CONTENTTYPE_JSON, "delete"),
	
	CREATEFOLDER(FilemanagerAction.CONTENTTYPE_JSON, "addfolder"),
	
	UPLOAD(FilemanagerAction.CONTENTTYPE_HTML, "add"),

	DOWNLOAD(FilemanagerAction.CONTENTTYPE_DOWNLOAD, "download");
	
	/** The allowed get request modes. */
	private static FilemanagerAction[] allowedGetRequestModes = new FilemanagerAction[] {INFO,FOLDER,RENAME,DELETE,CREATEFOLDER}; 
	
	private String contentType;
	
	private String parameterName;

	private static final String CONTENTTYPE_DOWNLOAD = "application/x-download";

	private static final String CONTENTTYPE_HTML = "text/html";

	private static final String CONTENTTYPE_JSON = "application/json";
	
	private FilemanagerAction(String contentType, String parameterName) {
		this.contentType = contentType;
		this.parameterName = parameterName;
	}
	
	public String getContentType() {
		return contentType;
	}
	
	public String getParameterName() {
		return parameterName;
	}
	
	public static FilemanagerAction valueOfIgnoreCase(String mode) throws IllegalArgumentException {
		for(FilemanagerAction rm : FilemanagerAction.values()) {
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
	public static boolean isGetRequest(FilemanagerAction mode) {
		return Arrays.asList(allowedGetRequestModes).contains(mode);
	}
}

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

	DOWNLOAD(null, "download"),
	
	THUMBNAIL(null, "thumbnail");
	
	/** The allowed get request modes. */
	private static FilemanagerAction[] allowedGetRequestModes = new FilemanagerAction[] {INFO,FOLDER,RENAME,DELETE,CREATEFOLDER}; 
	
	private String contentType;
	
	private String parameterName;

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

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
package de.thischwa.c5c.exception;

import de.thischwa.c5c.FilemanagerAction;
import de.thischwa.c5c.UserObjectProxy;
import de.thischwa.c5c.util.StringUtils;

/**
 * Thrown to indicate exceptions of the filemanager. The messages based on the localized known messages provided by
 * {@link UserObjectProxy#getFilemanagerErrorMessage(String)}.
 */
public class FilemanagerException extends ConnectorException {
	
	private static final long serialVersionUID = 1L;
	
	public final static String KEY_AUTHORIZATION_REQUIRED = "AUTHORIZATION_REQUIRED";
	
	public final static String KEY_INVALID_ACTION = "INVALID_ACTION";
	
	public final static String KEY_MODE_ERROR = "MODE_ERROR";
	
	public final static String KEY_DIRECTORY_ALREADY_EXISTS = "DIRECTORY_ALREADY_EXISTS";
	
	public final static String KEY_FILE_ALREADY_EXISTS = "FILE_ALREADY_EXISTS";
	
	public final static String KEY_UNABLE_TO_CREATE_DIRECTORY = "UNABLE_TO_CREATE_DIRECTORY";
	
	public final static String KEY_INVALID_VAR = "INVALID_VAR";
	
	public final static String KEY_DIRECTORY_NOT_EXIST = "DIRECTORY_NOT_EXIST";
	
	public final static String KEY_UNABLE_TO_OPEN_DIRECTORY = "UNABLE_TO_OPEN_DIRECTORY";
	
	public final static String KEY_ERROR_RENAMING_DIRECTORY = "ERROR_RENAMING_DIRECTORY";
	
	public final static String KEY_ERROR_RENAMING_FILE = "ERROR_RENAMING_FILE";
	
	public final static String KEY_INVALID_DIRECTORY_OR_FILE = "INVALID_DIRECTORY_OR_FILE";
	
	public final static String KEY_INVALID_FILE_UPLOAD = "INVALID_FILE_UPLOAD";
	
	public final static String KEY_UPLOAD_FILES_SMALLER_THAN = "UPLOAD_FILES_SMALLER_THAN";
	
	public final static String KEY_UPLOAD_IMAGES_ONLY = "UPLOAD_IMAGES_ONLY";
	
	public final static String KEY_UPLOAD_IMAGES_TYPE_JPEG_GIF_PNG = "UPLOAD_IMAGES_TYPE_JPEG_GIF_PNG";
	
	public final static String KEY_FILE_NOT_EXIST = "FILE_DOES_NOT_EXIST";
	
	public final static String KEY_LANGUAGE_FILE_NOT_FOUND = "LANGUAGE_FILE_NOT_FOUND";

	public FilemanagerException(FilemanagerAction mode, String key, String... params) {
		super(mode, buildMessage(key, params));
	}

	public FilemanagerException(String key, String... params) {
		super(buildMessage(key, params));
	}

	private static String buildMessage(String key, String... params) {
		String rawMsg = UserObjectProxy.getFilemanagerErrorMessage(key);
		if(StringUtils.isNullOrEmptyOrBlank(key))
			rawMsg = UserObjectProxy.getFilemanagerErrorMessage(KEY_INVALID_VAR);
		String msg = (params == null || params.length == 0) ? rawMsg : String.format(rawMsg, ((Object[]) params));
		return msg;
	}
}

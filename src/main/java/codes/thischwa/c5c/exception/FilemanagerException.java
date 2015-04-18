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
package codes.thischwa.c5c.exception;

import codes.thischwa.c5c.FilemanagerAction;
import codes.thischwa.c5c.UserObjectProxy;

/**
 * Thrown to indicate known exceptions of the filemanager. The messages based on the localized known messages provided by
 * {@link UserObjectProxy#getFilemanagerErrorMessage(FilemanagerException.Key)}.
 */
public class FilemanagerException extends C5CException {
	
	private static final long serialVersionUID = 1L;
	
	/** The keys of the localized messages of the filemanager. */
	public enum Key {
		AllowedFileType("ALLOWED_FILE_TYPE"),
		AuthorizationRequired("AUTHORIZATION_REQUIRED"),
		InvalidAction("INVALID_ACTION"),
		ModeError("MODE_ERROR"),
		DirectoryAlreadyExists("DIRECTORY_ALREADY_EXISTS"),
		FileAlreadyExists("FILE_ALREADY_EXISTS"),
		UnableToCreateDirectory("UNABLE_TO_CREATE_DIRECTORY"),
		InvalidVar("INVALID_VAR"),
		DirectoryNotExist("DIRECTORY_NOT_EXIST"),
		UnableToOpenDirectory("UNABLE_TO_OPEN_DIRECTORY"),
		ErrorRenamingDirectory("ERROR_RENAMING_DIRECTORY"),
		ErrorRenamingFile("ERROR_RENAMING_FILE"),
		ErrorOpeningFile("ERROR_OPENING_FILE"),
		ErrorSavingFile("ERROR_SAVING_FILE"),
		ErrorReplacingFile("ERROR_REPLACING_FILE"),
		InvalidDirectoryOrFile("INVALID_DIRECTORY_OR_FILE"),
		InvalidFileUpload("INVALID_FILE_UPLOAD"),
		NotAllowed("NOT_ALLOWED"),
		NotAllowedSystem("NOT_ALLOWED_SYSTEM"),
		UploadFilesSmallerThan("UPLOAD_FILES_SMALLER_THAN"),
		UploadImagesOnly("UPLOAD_IMAGES_ONLY"),
		UploadImagesTypeJpegGifPng("UPLOAD_IMAGES_TYPE_JPEG_GIF_PNG"),
		FileNotExists("FILE_DOES_NOT_EXIST"),
		LanguageFileNotFound("LANGUAGE_FILE_NOT_FOUND");
		
		private String propertyName;
		
		private Key(String propertyName) {
			this.propertyName = propertyName;
		}
		
		public String getPropertyName() {
			return propertyName;
		}
	}

	public FilemanagerException(FilemanagerAction mode, Key key, String... params) {
		super(mode, buildMessage(key, params));
	}

	public FilemanagerException(Key key, String... params) {
		super(buildMessage(key, params));
	}

	private static String buildMessage(Key key, String... params) {
		String rawMsg = UserObjectProxy.getFilemanagerErrorMessage(key);
		if(rawMsg == null)
			return String.format("No message found for key: %s", key);
		String msg = (params == null || params.length == 0) ? rawMsg : String.format(rawMsg, ((Object[]) params));
		return msg;
	}
}

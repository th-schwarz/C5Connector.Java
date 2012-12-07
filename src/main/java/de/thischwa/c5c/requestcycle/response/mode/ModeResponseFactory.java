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

import java.io.InputStream;

import de.thischwa.c5c.RequestMode;
import de.thischwa.c5c.exception.UserActionException;
import de.thischwa.c5c.requestcycle.RequestData;
import de.thischwa.c5c.resource.C5UserActionMessageHolder;

public class ModeResponseFactory {

	public static FileInfo buildFileInfo(String path, boolean isDir) {
		return new FileInfo(path, isDir);
	}
	
	public static UploadFile buildUploadFile(String path, String sanitizedName, Long size) {
		// TODO check a max-upload-size (to be set in the properties)
		return new UploadFile(path, sanitizedName);
	}

	public static UploadFile buildUploadFileForError(String path, String sanitizedName) {
		UploadFile uploadFile = new UploadFile(path, sanitizedName);
		uploadFile.setError(C5UserActionMessageHolder.get(RequestData.getLocale(), UserActionException.KEY_UPLOAD_NOT_ALLOWED), 200);
		uploadFile.setMode(RequestMode.UPLOAD);
		return uploadFile;
	}
	
	public static Delete buildDelete(String fullPath) {
		return new Delete(fullPath);
	}
	
	public static Download buildDownload(String fullPath, long contentLength, InputStream in) {
		return new Download(fullPath, contentLength, in);
	}
	
	public static CreateFolder buildCreateFolder(String parentUrlPath, String folderName) {
		return new CreateFolder(parentUrlPath, folderName);
	}
	
	public static Rename buildRenameFile(String oldFullPath, String newName) { 
		return new Rename(oldFullPath, newName);
	}
}

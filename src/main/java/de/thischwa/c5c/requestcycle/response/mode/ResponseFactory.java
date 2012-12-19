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
package de.thischwa.c5c.requestcycle.response.mode;

import java.io.InputStream;

import de.thischwa.c5c.FilemanagerAction;
import de.thischwa.c5c.UserObjectProxy;
import de.thischwa.c5c.exception.UserActionException;
import de.thischwa.c5c.requestcycle.RequestData;
import de.thischwa.c5c.requestcycle.response.FileProperties;
import de.thischwa.c5c.resource.UserActionMessageHolder;

/**
 * ResponseFactory.java - TODO DOCUMENTME!
 */
public class ResponseFactory {

	public static FileProperties buildFileProperties(String name, long size, String modified) {
		return new FileProperties(name, size, modified);
	}

	public static FileInfo buildFileInfo(String urlPath, FileProperties fp) {
		FileInfo fi = new FileInfo(urlPath, fp.isDir());
		fi.setFileProperties(fp);
		return fi;
	}
	
	public static void setPreviewPath(FileInfo fi, String previewPath) {
		fi.setPreviewPath(previewPath);
	}

	public static void setCapabilities(FileInfo fi, String urlPath) {
		fi.setCapabilities(UserObjectProxy.getC5FileCapabilities(urlPath));
	}

	public static UploadFile buildUploadFile(String path, String sanitizedName, Long size) {
		// TODO check a max-upload-size (to be set in the properties)
		return new UploadFile(path, sanitizedName);
	}

	public static UploadFile buildUploadFileForError(String path, String sanitizedName) {
		UploadFile uploadFile = new UploadFile(path, sanitizedName);
		uploadFile.setError(UserActionMessageHolder.get(RequestData.getLocale(), UserActionException.Key.UploadNotAllowed.getPropertyName()), 200);
		uploadFile.setMode(FilemanagerAction.UPLOAD);
		return uploadFile;
	}

	public static Delete buildDelete(String fullPath) {
		return new Delete(fullPath);
	}

	public static Download buildDownload(String fullPath, long contentLength, InputStream in) {
		return new Download(fullPath, contentLength, in);
	}
	
	public static DownloadInfo buildDownloadInfo(InputStream in, long fileSize) {
		return new DownloadInfo(in, fileSize);
	}

	public static CreateFolder buildCreateFolder(String parentUrlPath, String folderName) {
		return new CreateFolder(parentUrlPath, folderName);
	}

	public static Rename buildRenameFile(String oldFullPath, String newName) {
		return new Rename(oldFullPath, newName);
	}

	public static FolderInfo buildFolderInfo() {
		return new FolderInfo();
	}

	public static void add(FolderInfo folderInfo, FileInfo fileInfo) {
		folderInfo.add(fileInfo);
	}
}

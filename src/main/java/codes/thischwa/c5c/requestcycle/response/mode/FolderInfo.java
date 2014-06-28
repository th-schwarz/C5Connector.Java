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
package codes.thischwa.c5c.requestcycle.response.mode;

import java.util.LinkedHashMap;
import java.util.Map;

import codes.thischwa.c5c.Constants;
import codes.thischwa.c5c.FilemanagerAction;
import codes.thischwa.c5c.requestcycle.response.GenericResponse;

/**
 * Holds the data of a FolderInfo response.
 */
public final class FolderInfo extends GenericResponse {

	private Map<String, FileInfo> folderItems;

	public FolderInfo() {
		super(FilemanagerAction.FOLDER);
		folderItems = new LinkedHashMap<>();
	}

	public void add(final FileInfo fileInfo) {
		String path = fileInfo.getPath();
		if(fileInfo.isDir() && !path.endsWith(Constants.defaultSeparator))
			path += Constants.defaultSeparator;
		folderItems.put(path, fileInfo);
	}

	@Override
	public String toString() {
		return serialize(folderItems);
	}
}

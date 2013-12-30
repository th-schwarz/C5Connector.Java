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
package de.thischwa.c5c.requestcycle.response.mode;

import java.util.LinkedHashMap;
import java.util.Map;

import de.thischwa.c5c.Constants;
import de.thischwa.c5c.FilemanagerAction;
import de.thischwa.c5c.requestcycle.response.GenericResponse;

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

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
package codes.thischwa.c5c;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import codes.thischwa.c5c.GenericConnector.FileProperties;
import codes.thischwa.c5c.filemanager.Options;
import codes.thischwa.c5c.filemanager.Options.FILE_SORTING;
import codes.thischwa.c5c.requestcycle.response.GenericResponse;


/**
 * Abstract class for each dispatcher (put and get).
 */
abstract class GenericDispatcher {
	protected Connector connector;

	GenericDispatcher(Connector connector) {
		this.connector = connector;
	}
	
	abstract GenericResponse doRequest();
	
	protected String buildBackendPath(String urlPath) {
		return UserObjectProxy.getBackendPath(urlPath);
	}
	

	/**
	 * Sorting files and folder defined in: https://github.com/simogeo/Filemanager/wiki/How-to-change-files-and-folders-order-in-list%3F
	 *
	 * @param fps
	 * @param sorting
	 */
	protected void sortFileProperties(List<FileProperties> fps, Options.FILE_SORTING sorting) {
		if(fps == null || fps.isEmpty())
			return;
		
		if(sorting == FILE_SORTING.DEFAULT || sorting == FILE_SORTING.TYPE_ASC || sorting == FILE_SORTING.TYPE_DESC) {
			List<FileProperties> dirs = new ArrayList<>();
			List<FileProperties> files = new ArrayList<>();
			for(FileProperties fp : fps) {
				if(fp.isDir())
					dirs.add(fp);
				else 
					files.add(fp);
			}
			// sorting files and dirs descending
			Collections.sort(dirs, new FilePropertiesComparator(FILE_SORTING.NAME_DESC));
			Collections.sort(files, new FilePropertiesComparator(FILE_SORTING.NAME_DESC));
			fps.clear();
			
			if(sorting == FILE_SORTING.DEFAULT || sorting == FILE_SORTING.TYPE_DESC) {
				fps.addAll(files);
				fps.addAll(dirs);
			} else {
				fps.addAll(dirs);
				fps.addAll(files);
			}			
		} else {
			Collections.sort(fps, new FilePropertiesComparator(sorting));
		}
	}
	
	/**
	 * {@link Comparator} for files and folders.
	 */
	private static class FilePropertiesComparator implements Comparator<FileProperties> {
		private Options.FILE_SORTING fileSorting;
		
		FilePropertiesComparator(FILE_SORTING fileSorting) {
			this.fileSorting = fileSorting;
		}

		@Override
		public int compare(FileProperties fp1, FileProperties fp2) {
			switch(fileSorting) {
			case MODIFIED_ASC:
				return fp2.getRawModified().compareTo(fp1.getRawModified());
			case MODIFIED_DESC:
				return fp1.getRawModified().compareTo(fp2.getRawModified());
			case NAME_ASC:
				return fp2.getName().compareToIgnoreCase(fp1.getName());
			case NAME_DESC:
			case DEFAULT:
			default:
				return fp1.getName().compareToIgnoreCase(fp2.getName());
			}
		}
	}
}

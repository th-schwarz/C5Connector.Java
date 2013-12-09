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
package de.thischwa.c5c.requestcycle.impl;

import java.io.File;
import java.io.FilenameFilter;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletContext;

import org.apache.commons.io.FilenameUtils;

import de.thischwa.c5c.requestcycle.IconResolver;
import de.thischwa.c5c.resource.PropertiesLoader;
import de.thischwa.c5c.util.Path;
import de.thischwa.c5c.util.StringUtils;

/**
 * The default implementation of {@link IconResolver} which resolves the icons from the c5 filemanager. 
 * It respects the file manager path settings from the properties.
 */
public class FilemanagerIconResolver implements IconResolver {
	
	private Map<String, String> iconsPerType = new HashMap<>();
	
	public void init(ServletContext servletContext, String iconPath, String defaultIcon, String directoryIcon) throws RuntimeException {
		Path fileSystemPath = new Path(iconPath);

		File iconFolder = new File(servletContext.getRealPath(fileSystemPath.toString()));
		if(!iconFolder.exists())
			throw new RuntimeException(String.format("C5 file icons folder couldn't be found! (%s / %s)", PropertiesLoader.getFilemanagerPath(), iconFolder.getAbsolutePath()));
		
		Path urlPath;
		if(!StringUtils.isNullOrEmpty(servletContext.getContextPath())) {
			urlPath = new Path(servletContext.getContextPath());
			urlPath.addFolder(fileSystemPath.toString());
		}
		else {
			urlPath = new Path(fileSystemPath.toString());
		}

		for(File icon : iconFolder.listFiles(new IconNameFilter())) {
			String knownExtension = FilenameUtils.getBaseName(icon.getName());
			iconsPerType.put(knownExtension, urlPath.addFile(icon.getName()));
		}
		
		iconsPerType.put(IconResolver.key_directory, urlPath.addFile(directoryIcon));
		iconsPerType.put(IconResolver.key_default, urlPath.addFile(defaultIcon));
	}
	
	@Override
	public String getIconPath(final String extension) {
		if(extension == null || !iconsPerType.containsKey(extension.toLowerCase()))
			return iconsPerType.get(IconResolver.key_default);
		return iconsPerType.get(extension.toLowerCase());
	}
	
	@Override
	public String getIconPathForDirectory() {
		return iconsPerType.get(IconResolver.key_directory);
	}
	
	private class IconNameFilter implements FilenameFilter {
		
		@Override
		public boolean accept(File dir, String name) {
			return !name.contains("_") && name.endsWith("png");
		}
	}
}

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
package de.thischwa.c5c.requestcycle.impl;

import java.io.File;
import java.io.FilenameFilter;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletContext;

import org.apache.commons.io.FilenameUtils;

import de.thischwa.c5c.requestcycle.IconRequestResolver;
import de.thischwa.c5c.requestcycle.IconResolver;
import de.thischwa.c5c.resource.PropertiesLoader;
import de.thischwa.c5c.util.Path;
import de.thischwa.c5c.util.StringUtils;

/**
 * The default implementation of {@link IconResolver} which resolves the icons from the c5 filemanager. 
 * It respects the file manager path settings from the properties.
 */
public class FilemanagerIconResolver implements IconResolver {
	
	private ServletContext servletContext;
	
	// cache of icons <path, <ext, icon>>
	private Map<String, IconRequestResolver> iconCache = new HashMap<>();
	
	@Override
	public void initContext(ServletContext servletContext) throws RuntimeException {
		this.servletContext = servletContext;
	}
	
	@Override
	public IconRequestResolver initRequest(String iconPath, String defaultIcon, String directoryIcon) {
		if(!iconCache.containsKey(iconPath))
			buildCache(iconPath, defaultIcon, directoryIcon);
		return iconCache.get(iconPath);
	}
	
	private void buildCache(String iconPath, String defaultIcon, String directoryIcon) {		
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

		Map<String, String> iconsPerType = new HashMap<>();
		for(File icon : iconFolder.listFiles(new IconNameFilter())) {
			String knownExtension = FilenameUtils.getBaseName(icon.getName());
			iconsPerType.put(knownExtension, urlPath.addFile(icon.getName()));
		}
		
		iconsPerType.put(IconResolver.key_directory, urlPath.addFile(directoryIcon));
		iconsPerType.put(IconResolver.key_default, urlPath.addFile(defaultIcon));
		
		iconCache.put(iconPath, new IconRequestResolver(iconsPerType));
	}
	
	private class IconNameFilter implements FilenameFilter {
		
		@Override
		public boolean accept(File dir, String name) {
			return !name.contains("_") && name.endsWith("png");
		}
	}
}

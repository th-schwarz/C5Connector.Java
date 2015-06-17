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
package codes.thischwa.c5c.requestcycle.impl;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletContext;

import org.apache.commons.io.FilenameUtils;

import codes.thischwa.c5c.PropertiesLoader;
import codes.thischwa.c5c.requestcycle.IconRequestResolver;
import codes.thischwa.c5c.requestcycle.IconResolver;
import codes.thischwa.c5c.util.PathBuilder;
import codes.thischwa.c5c.util.StringUtils;

/**
 * The default implementation of {@link IconResolver} which resolves the icons from the c5 filemanager. 
 * It respects the file manager path settings from the properties.
 */
public class FilemanagerIconResolver implements IconResolver {
	
	private ServletContext servletContext;
	protected String defaultIcon;
	protected String directoryIcon;
	
	// cache of icons <path, <ext, icon>>
	protected Map<String, IconRequestResolver> iconCache = new HashMap<>();
	
	@Override
	public void initContext(ServletContext servletContext) {
		this.servletContext = servletContext;
	}
	
	@Override
	public IconRequestResolver initRequest(String iconPath, String defaultIcon, String directoryIcon) {
		if(!iconCache.containsKey(iconPath))
			buildCache(iconPath, defaultIcon, directoryIcon);
		return iconCache.get(iconPath);
	}
	
	private void buildCache(String iconPath, String defaultIcon, String directoryIcon) {		
		this.defaultIcon = defaultIcon;
		this.directoryIcon = directoryIcon;
		PathBuilder fileSystemIconPath = new PathBuilder(iconPath);

		java.nio.file.Path iconFolder = Paths.get(servletContext.getRealPath(fileSystemIconPath.toString()));
		if(!Files.exists(iconFolder))
			throw new RuntimeException(String.format("C5 file icons folder couldn't be found! (%s / %s)", PropertiesLoader.getFilemanagerPath(), iconFolder.toAbsolutePath().toString()));
		
		PathBuilder urlPath;
		if(!StringUtils.isNullOrEmpty(servletContext.getContextPath())) {
			urlPath = new PathBuilder(servletContext.getContextPath());
			urlPath.addFolder(fileSystemIconPath.toString());
		}
		else {
			urlPath = new PathBuilder(fileSystemIconPath.toString());
		}

		collectIcons(iconPath, iconFolder, urlPath);
	}
	
	protected void collectIcons(String iconPath, java.nio.file.Path iconFolder, PathBuilder urlPath) {
		Map<String, String> iconsPerType = new HashMap<>();
		try {
			for(java.nio.file.Path icon : Files.newDirectoryStream(iconFolder, new DirectoryStream.Filter<java.nio.file.Path>() {
				@Override
				public boolean accept(java.nio.file.Path entry) throws IOException {
					if(Files.isDirectory(entry))  
						return false;
					String name = entry.getFileName().toString();
					return !name.contains("_") && name.endsWith("png");
				}})) {
				String name = icon.getFileName().toString();
				String knownExtension = FilenameUtils.getBaseName(name);
				iconsPerType.put(knownExtension, urlPath.addFile(name));
			}
		} catch (IOException e) {
			throw new RuntimeException("Couldn't read the icon files!", e);
		}
		
		iconsPerType.put(IconResolver.key_directory, urlPath.addFile(directoryIcon));
		iconsPerType.put(IconResolver.key_default, urlPath.addFile(defaultIcon));
		iconsPerType.put(IconResolver.key_directory_lock, urlPath.addFile("locked_" + directoryIcon));
		iconsPerType.put(IconResolver.key_default_lock, urlPath.addFile("locked_" + defaultIcon));
		
		iconCache.put(iconPath, new IconRequestResolver(iconsPerType));
	}
}

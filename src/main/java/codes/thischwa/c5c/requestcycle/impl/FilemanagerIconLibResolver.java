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
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletContext;

import org.apache.commons.io.Charsets;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;

import codes.thischwa.c5c.PropertiesLoader;
import codes.thischwa.c5c.requestcycle.IconRequestResolver;
import codes.thischwa.c5c.requestcycle.IconResolver;
import codes.thischwa.c5c.util.PathBuilder;

/**
 * The default implementation of {@link IconResolver} which resolves the icons from the filemanager
 * inside the resource folder.
 */
public class FilemanagerIconLibResolver implements IconResolver {
	
	protected String defaultIcon;
	protected String directoryIcon;
	
	// cache of icons <path, <ext, icon>>
	protected Map<String, IconRequestResolver> iconCache = new HashMap<>();
	
	@Override
	public void initContext(ServletContext servletContext) {
	}
	
	@Override
	public IconRequestResolver initRequest(String iconPath, String defaultIcon, String directoryIcon) {
		if(iconCache.isEmpty())
			buildCache(iconPath, defaultIcon, directoryIcon);
		return iconCache.get(iconPath);
	}
	
	private void buildCache(String iconPath, String defaultIcon, String directoryIcon) {		
		this.defaultIcon = defaultIcon;
		this.directoryIcon = directoryIcon;

		collectIcons(iconPath);
	}
	
	protected void collectIcons(String iconPath) {
		Map<String, String> iconsPerType = new HashMap<>();
		InputStream inIcons = null;
		try {
			inIcons = FilemanagerIconLibResolver.class.getResourceAsStream(iconPath);
			List<String> files = IOUtils.readLines(inIcons,
					Charsets.toCharset(PropertiesLoader.getDefaultEncoding()));
			for(String f : files) {
				if(!(f.contains("_") && f.endsWith("png")))
					continue;
				String icon = new PathBuilder(iconPath).addFile(f);
				String knownExtension = FilenameUtils.getBaseName(f);
				iconsPerType.put(knownExtension, icon);
			}
		} catch (IOException e) {
			throw new RuntimeException("Couldn't read the icon files!", e);
		} finally {
			IOUtils.closeQuietly(inIcons);
		}
		
		iconsPerType.put(IconResolver.key_directory, new PathBuilder(iconPath).addFile(directoryIcon));
		iconsPerType.put(IconResolver.key_default, new PathBuilder(iconPath).addFile(defaultIcon));
		iconsPerType.put(IconResolver.key_directory_lock, new PathBuilder(iconPath).addFile("locked_" + directoryIcon));
		iconsPerType.put(IconResolver.key_default_lock, new PathBuilder(iconPath).addFile("locked_" + defaultIcon));
		
		iconCache.put(iconPath, new IconRequestResolver(iconsPerType));
	}
}

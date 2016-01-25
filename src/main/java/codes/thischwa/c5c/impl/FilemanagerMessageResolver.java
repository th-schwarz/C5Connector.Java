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
package codes.thischwa.c5c.impl;

import java.io.File;
import java.io.FilenameFilter;
import java.util.Map;

import javax.servlet.ServletContext;

import org.apache.commons.io.FilenameUtils;

import com.fasterxml.jackson.databind.ObjectMapper;

import codes.thischwa.c5c.MessageResolver;
import codes.thischwa.c5c.PropertiesLoader;
import codes.thischwa.c5c.util.PathBuilder;

/**
 * This implementation of the {@link MessageResolver} works like the default implementation {@link FilemanagerMessageLibResolver},
 * but it uses the filemanager files from the local file system. 
 * The base-folder of the filemanager is defined by the property 'connector.filemanagerPath'.
 */
public class FilemanagerMessageResolver extends AbstractFilemanagerMessageResolver {

	protected static String langPath = "scripts/languages";
	
	protected FilenameFilter jsFilter = new FilenameFilter() {
		@Override
		public boolean accept(File dir, String name) {
			return name.endsWith(".js");
		}
	};

	@Override
	public void setServletContext(ServletContext servletContext) throws RuntimeException {
		super.setServletContext(servletContext);
		PathBuilder path = new PathBuilder(PropertiesLoader.getFilemanagerPath()).addFolder(langPath);
		File msgFolder = new File(servletContext.getRealPath(path.toString()));
		if(!msgFolder.exists())
			throw new RuntimeException("C5 scripts folder couldn't be found!");

		ObjectMapper mapper = new ObjectMapper();
		try {
			for(File file: msgFolder.listFiles(jsFilter)) {
				String lang = FilenameUtils.getBaseName(file.getName());
		        @SuppressWarnings("unchecked")
				Map<String, String> langData = mapper.readValue(file, Map.class);
				collectLangData(lang, langData);
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
}

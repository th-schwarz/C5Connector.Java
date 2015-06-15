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

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

import javax.servlet.ServletContext;

import org.apache.commons.io.IOUtils;

import codes.thischwa.c5c.DefaultConfigResolver;
import codes.thischwa.c5c.PropertiesLoader;
import codes.thischwa.c5c.filemanager.FilemanagerConfig;
import codes.thischwa.c5c.requestcycle.FilemanagerConfigBuilder;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * The default implementation of {@link DefaultConfigResolver}.
 */
public class FilemanagerDefaultConfigResolver implements DefaultConfigResolver {
	private ServletContext servletContext;

	@Override
	public void initContext(ServletContext servletContext) {
		this.servletContext = servletContext;
	}
	
	@Override
	public FilemanagerConfig read() throws RuntimeException {
		File fmScriptDir = new File(servletContext.getRealPath(PropertiesLoader.getFilemanagerPath()), "scripts");
		String fmDefaultConfig = FilemanagerConfigBuilder.BASE_FILE_NAME.concat(".default");
		File defaultConfig = new File(fmScriptDir, fmDefaultConfig);
		if(!defaultConfig.exists())
			throw new RuntimeException(String.format("Default config file doesn't exists: %s", defaultConfig.getAbsolutePath()));
		InputStream configIn = null;
		 
		try {
			// load the object
			configIn = new BufferedInputStream(new FileInputStream(defaultConfig));
			ObjectMapper mapper = new ObjectMapper();
			FilemanagerConfig filemanagerDefaultConfig = mapper.readValue(configIn, FilemanagerConfig.class);
			return filemanagerDefaultConfig;
		} catch (Exception e) {
			throw new RuntimeException(e);
		} finally {
			IOUtils.closeQuietly(configIn);
		}
	}

}

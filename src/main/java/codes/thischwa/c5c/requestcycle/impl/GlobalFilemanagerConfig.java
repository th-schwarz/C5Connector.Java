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

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import codes.thischwa.c5c.PropertiesLoader;
import codes.thischwa.c5c.filemanager.FilemanagerConfig;
import codes.thischwa.c5c.filemanager.Options;
import codes.thischwa.c5c.requestcycle.FilemanagerConfigBuilder;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Default implementation of {@link FilemanagerConfigBuilder}. It loads the 
 * common config file [filemanager-dir]/scripts/filemanager.config.js. 
 * If it isn't exists the default one ([filemanager-dir]/scripts/filemanager.config.js.default)
 * will be loaded.
 * The configuration is globally available.<br/>
 * <br/>
 * The following defaults are set:
 * <ul>
 * <li><i>_comment</i>: Built by the C5Connector.Java</li>
 * <li><i>options</i>#lang: java</li>
 * </ul>
 */
public class GlobalFilemanagerConfig implements FilemanagerConfigBuilder {

	private static Logger logger = LoggerFactory.getLogger(GlobalFilemanagerConfig.class);
	
	protected FilemanagerConfig userConfig = null;

	@Override
	public FilemanagerConfig getConfig(HttpServletRequest req, ServletContext servletContext) {
		if(userConfig == null) {
			loadConfigFile(servletContext);
			
			// set some defaults
			userConfig.setComment("Built by the C5Connector.Java");
			Options options = userConfig.getOptions();
			options.setLang("java");
			postLoadConfigFileHook();
		}
		return userConfig;
	}
	
	/**
	 * This method hook will be called after the successful loading of a userConfig file. 
	 * That's an easy point for inherited objects to change properties globally.
	 */
	protected void postLoadConfigFileHook() {
	}

	private void loadConfigFile(ServletContext context) throws RuntimeException {
		InputStream in = null;
		
		try {
			File fmScriptDir = new File(context.getRealPath(PropertiesLoader.getFilemanagerPath()), "scripts");
			
			// defined: filemanager/scripts/filemanager.config.js
			File configFile = new File(fmScriptDir, BASE_FILE_NAME);
			if(configFile.exists()) {
				logger.info("Defined userConfig file found.");
				in = new BufferedInputStream(new FileInputStream(configFile));
			}

			// defined: filemanager/scripts/filemanager.config.js.default
			configFile = new File(fmScriptDir, BASE_FILE_NAME+".default");
			if(configFile.exists()) {
				logger.info("Defined filemanagers default config file found.");
				in = new BufferedInputStream(new FileInputStream(configFile));
			}
			
			// load the object
			ObjectMapper mapper = new ObjectMapper();
			userConfig = mapper.readValue(in, FilemanagerConfig.class);
		} catch (Exception e) {
			logger.error("Error while loading the config file!", e);
			throw new RuntimeException(e);
		} finally {
			IOUtils.closeQuietly(in);
		}
	}
}

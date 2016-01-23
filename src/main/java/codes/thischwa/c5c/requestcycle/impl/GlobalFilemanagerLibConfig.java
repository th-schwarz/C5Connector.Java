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

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import codes.thischwa.c5c.filemanager.FilemanagerConfig;
import codes.thischwa.c5c.filemanager.Options;
import codes.thischwa.c5c.impl.FilemanagerDefaultConfigLibResolver;
import codes.thischwa.c5c.requestcycle.FilemanagerConfigBuilder;

/**
 * Default implementation of {@link FilemanagerConfigBuilder}. It loads the 
 * the default configuration file ([filemanager-dir]/scripts/filemanager.config.js.default)
 * from inside the resource folder.
 * The configuration is globally available.<br/>
 * <br/>
 * The following defaults are set:
 * <ul>
 * <li><i>_comment</i>: Built by the C5Connector.Java</li>
 * <li><i>options</i>#lang: java</li>
 * </ul>
 */
public class GlobalFilemanagerLibConfig implements FilemanagerConfigBuilder {

	private static Logger logger = LoggerFactory.getLogger(GlobalFilemanagerLibConfig.class);
	
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
		FilemanagerDefaultConfigLibResolver configResolver = new FilemanagerDefaultConfigLibResolver();
		configResolver.initContext(context);
		userConfig = configResolver.read();
		logger.debug("Default configuration of the filemanager successful loaded.");
	}
}

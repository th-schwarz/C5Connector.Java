package de.thischwa.c5c.requestcycle.impl;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;

import de.thischwa.c5c.requestcycle.FilemanagerConfigBuilder;
import de.thischwa.c5c.resource.PropertiesLoader;
import de.thischwa.c5c.resource.filemanager.FilemanagerConfig;
import de.thischwa.c5c.util.Path;

/**
 * Default implementation of {@link FilemanagerConfigBuilder}.
 * 
 * The order of searching files to load the defaults is the following:
 * <ol>
 * <li>[filemanger-dir]/scripts/filemanager.config.js</li>
 * <li>[filemanger-dir]/scripts/filemanager.config.js.default</li>
 * <li>internal config file</li>
 * </ol>
 * The first file which is found, will be loaded.
 */
public class DefaultFilemanagerConfig implements FilemanagerConfigBuilder {

	private static Logger logger = LoggerFactory.getLogger(DefaultFilemanagerConfig.class);
	
	protected FilemanagerConfig config = null;

	@Override
	public FilemanagerConfig getConfig(HttpServletRequest req, ServletContext servletContext) {
		if(config == null)
			loadConfigFile(servletContext);
		return config;
	}

	private void loadConfigFile(ServletContext context) throws RuntimeException {
		InputStream in = null;
		
		try {
			Path path = new Path(PropertiesLoader.getFilemangerPath()).addFolder("scripts");
			File fmScriptDir = new File(context.getRealPath(path.toString()));
			
			// 1. defined: filemanager/scripts/filemanager.js
			File configFile = new File(fmScriptDir, BASE_FILE_NAME);
			if(configFile.exists()) {
				logger.info("Defined config file found.");
				in = new BufferedInputStream(new FileInputStream(configFile));
			}
			
			// 2. default: filemanager/scripts/filemanager.js.default
			configFile = new File(fmScriptDir, BASE_FILE_NAME+".default");
			if(configFile.exists()){
				logger.info("Default config file found.");
				in = new BufferedInputStream(new FileInputStream(configFile));
			}
			
			// 3. lib-default
			logger.info("Lib-default config file found.");
			in = FilemanagerConfig.class.getResourceAsStream(BASE_FILE_NAME+".default");
			
			// load the object
			ObjectMapper mapper = new ObjectMapper();
			config = mapper.readValue(in, FilemanagerConfig.class);
		} catch (Exception e) {
			logger.error("Error while loading the config file!", e);
			throw new RuntimeException(e);
		} finally {
			IOUtils.closeQuietly(in);
		}
	}
}

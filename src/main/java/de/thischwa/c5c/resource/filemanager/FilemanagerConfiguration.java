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
package de.thischwa.c5c.resource.filemanager;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import javax.servlet.ServletContext;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;

import de.thischwa.c5c.resource.PropertiesLoader;
import de.thischwa.c5c.util.Path;

/**
 * Static object for managing the configuration of the filemanager (filemanager.config.js).<br/>
 * The order of searching files to load the defaults is the following:
 * <ol>
 * <li>[filemanger-dir]/scripts/filemanager.config.js</li>
 * <li>[filemanger-dir]/scripts/filemanager.config.js.default</li>
 * <li>internal config file</li>
 * </ol>
 * The first file which is found, will be loaded.<br/>
 * <br/>
 * The properties can be set programmatically to. But here is the same as for the {@link PropertiesLoader}: 
 * Make sure to override the properties before the first call of any property.
 */
public class FilemanagerConfiguration {
	public static final Logger logger = LoggerFactory.getLogger(FilemanagerConfiguration.class);
	private static FilemanagerConfiguration config;
	
	private static final String config_base_file_name = "filemanager.config.js";

	@JsonProperty("_comment")
	private String comment;

	private Options options = new Options();
	private Security security = new Security();
	private Upload upload = new Upload(); 
	private Exclude exclude = new Exclude();
	private Images images = new Images();
	private Videos videos = new Videos();
	private Audios audios = new Audios();
	private Extras extras = new Extras();
	private Icons icons = new Icons();

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public Options getOptions() {
		return options;
	}

	public Security getSecurity() {
		return security;
	}

	public Upload getUpload() {
		return upload;
	}

	public Exclude getExclude() {
		return exclude;
	}

	public Images getImages() {
		return images;
	}

	public Videos getVideos() {
		return videos;
	}

	public Audios getAudios() {
		return audios;
	}

	public Extras getExtras() {
		return extras;
	}

	public Icons getIcons() {
		return icons;
	}

	public static FilemanagerConfiguration getConfiguration() {
		if(config == null)
			throw new IllegalArgumentException("Configuration isn't loaded!");
		return config;
	}

	public static void init(final InputStream in) {
		ObjectMapper mapper = new ObjectMapper();
		try {
			config = mapper.readValue(in, FilemanagerConfiguration.class);
			logger.info("Configuration of the filemanager successful loaded.");
		} catch (IOException e) {
			throw new RuntimeException(e);
		} finally {
			IOUtils.closeQuietly(in);
		}
	}
	
	public static void loadDefault(ServletContext context) {
		try {
			init(searchConfigFile(context));
		} catch (FileNotFoundException e) {
			throw new RuntimeException(e);
		}
	}
	
	private static InputStream searchConfigFile(ServletContext context) throws FileNotFoundException {
		Path path = new Path(PropertiesLoader.getFilemangerPath()).addFolder("scripts");
		File fmScriptDir = new File(context.getRealPath(path.toString()));
		
		// 1. defined: filemanager/scripts/filemanager.js
		File configFile = new File(fmScriptDir, config_base_file_name);
		if(configFile.exists()) {
			logger.info("Defined config file found.");
			return new FileInputStream(configFile);
		}
		
		// 2. default: filemanager/scripts/filemanager.js.default
		configFile = new File(fmScriptDir, config_base_file_name+".default");
		if(configFile.exists()){
			logger.info("Default config file found.");
			return new FileInputStream(configFile);
		}
		
		// 3. lib-default
		logger.info("Lib-default config file found.");
		return FilemanagerConfiguration.class.getResourceAsStream(config_base_file_name+".default");
	}
}

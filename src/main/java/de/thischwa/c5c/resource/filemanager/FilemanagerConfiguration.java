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

import java.io.IOException;
import java.io.InputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Main object to load and hold the JSON-configuration of the filemanager (filemanager.config.js).
 */
public class FilemanagerConfiguration {
	public static final Logger logger = LoggerFactory.getLogger(FilemanagerConfiguration.class);
	private static FilemanagerConfiguration config;

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
		}
	}
}

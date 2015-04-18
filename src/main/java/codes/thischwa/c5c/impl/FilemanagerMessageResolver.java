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
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import javax.servlet.ServletContext;

import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import codes.thischwa.c5c.MessageResolver;
import codes.thischwa.c5c.PropertiesLoader;
import codes.thischwa.c5c.exception.FilemanagerException;
import codes.thischwa.c5c.util.PathBuilder;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * The default implementation of the {@link MessageResolver} interface. It holds the messages 
 * provided by several javascript files located in the path 'scripts/languages' inside
 * the folder of filemanager.
 */
public class FilemanagerMessageResolver implements MessageResolver {
	private static Logger logger = LoggerFactory.getLogger(FilemanagerMessageResolver.class);

	protected static String langPath = "scripts/languages";
	
	protected FilenameFilter jsFilter = new FilenameFilter() {
		@Override
		public boolean accept(File dir, String name) {
			return name.endsWith(".js");
		}
	};

	private Map<String, Map<String, String>> messageStore = new HashMap<>();

	@Override
	public void setServletContext(ServletContext servletContext) throws RuntimeException {
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
	
	protected void collectLangData(final String lang, final Map<String, String> data) {
		messageStore.put(lang, data);
	}
	
	@Override
	public String getMessage(Locale locale, FilemanagerException.Key key) throws IllegalArgumentException {
		String lang = locale.getLanguage().toLowerCase();
		if(!messageStore.containsKey(lang)) {
			logger.warn("Language [{}] not supported, take the default.", lang);
			lang = PropertiesLoader.getDefaultLocale().getLanguage().toLowerCase();
		}
		Map<String, String> messages = messageStore.get(lang);
		if(!messages.containsKey(key.getPropertyName()))
			throw new IllegalArgumentException("Message key not found: " + key.getPropertyName());
		return messages.get(key.getPropertyName());
	}
}

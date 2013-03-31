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
 *  - GNU General Public License Version 2 or later (the "GPL")
 *    http://www.gnu.org/licenses/gpl.html
 * 
 *  - GNU Lesser General Public License Version 2.1 or later (the "LGPL")
 *    http://www.gnu.org/licenses/lgpl.html
 * 
 *  - Mozilla Public License Version 1.1 or later (the "MPL")
 *    http://www.mozilla.org/MPL/MPL-1.1.html
 * 
 * == END LICENSE ==
 */
package de.thischwa.c5c;

import java.io.File;
import java.io.FilenameFilter;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import javax.servlet.ServletContext;

import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;

import de.thischwa.c5c.exception.FilemanagerException;
import de.thischwa.c5c.resource.PropertiesLoader;
import de.thischwa.c5c.util.Path;

/**
 * Holds the messages provided by several javascript files located in the path 'scripts/languages' inside
 * the folder of filemanager.
 */
class FilemanagerMessageHolder {
	private static Logger logger = LoggerFactory.getLogger(FilemanagerMessageHolder.class);

	private static String scriptPath = "scripts/languages";
	
	private FilenameFilter jsFilter = new FilenameFilter() {
		@Override
		public boolean accept(File dir, String name) {
			return name.endsWith(".js");
		}
	};

	private Map<String, Map<String, String>> messageStore;

	FilemanagerMessageHolder(ServletContext servletContext) throws RuntimeException {
		Path path = new Path(PropertiesLoader.getFilemangerPath()).addFolder(scriptPath);
		File msgFolder = new File(servletContext.getRealPath(path.toString()));
		if(!msgFolder.exists())
			throw new RuntimeException("C5 scripts folder couldn't be found!");

		messageStore = new HashMap<String, Map<String,String>>();		
		ObjectMapper mapper = new ObjectMapper();
		try {
			for(File file: msgFolder.listFiles(jsFilter)) {
				String lang = FilenameUtils.getBaseName(file.getName());
		        @SuppressWarnings("unchecked")
				Map<String, String> langData = mapper.readValue(file, Map.class);
				messageStore.put(lang, langData);
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		logger.info("*** MessageHolder successful initialized.");
	}
	
	/**
	 * Gets the message for the desired 'locale' and 'key'. 
	 * 
	 * @param locale The {@link Locale} of desired message.
	 * @param key The key for the message.
	 * 
	 * @return The message for the desired 'locale' and 'key'. If the locale is unknown the default locale is taken.
	 * @throws IllegalArgumentException If the 'key is unknown.
	 */
	String getMessage(Locale locale, FilemanagerException.Key key) throws IllegalArgumentException {
		String lang = locale.getLanguage().toLowerCase();
		if(!messageStore.containsKey(lang)) {
			logger.warn("Language [{}] not supported, take the default.", lang);
			lang = PropertiesLoader.getDefaultLocale().getLanguage().toLowerCase();
		}
		Map<String, String> messages = messageStore.get(lang);
		if(!messages.containsKey(key.getPropertyName()))
			throw new IllegalArgumentException("Message key not found: " + key.getPropertyName());
		return messages.get(key);
	}
}

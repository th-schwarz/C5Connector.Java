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

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import javax.servlet.ServletContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import codes.thischwa.c5c.MessageResolver;
import codes.thischwa.c5c.PropertiesLoader;
import codes.thischwa.c5c.exception.FilemanagerException;

/**
 * Abstract helper class for all implementations of the {@link MessageResolver}.
 */
abstract class AbstractFilemanagerMessageResolver implements MessageResolver {
	private static Logger logger = LoggerFactory.getLogger(AbstractFilemanagerMessageResolver.class);

	protected ServletContext servletContext;
	private Map<String, Map<String, String>> messageStore = new HashMap<>();
	
	@Override
	public void setServletContext(ServletContext servletContext) {
		this.servletContext = servletContext;
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

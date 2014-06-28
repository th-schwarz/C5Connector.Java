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
package codes.thischwa.c5c;

import java.util.Locale;

import javax.servlet.ServletContext;

import codes.thischwa.c5c.exception.FilemanagerException;

/**
 * An interface to resolve the messages for the filemanager,
 */
public interface MessageResolver {

	public void setServletContext(ServletContext servletContext);
	
	/**
	 * Gets the message for the desired 'locale' and 'key'. 
	 * 
	 * @param locale The {@link Locale} of desired message.
	 * @param key The key for the message.
	 * 
	 * @return The message for the desired 'locale' and 'key'. If the locale is unknown the default locale is taken.
	 * @throws IllegalArgumentException If the 'key is unknown.
	 */
	public String getMessage(Locale locale, FilemanagerException.Key key) throws IllegalArgumentException; 

}
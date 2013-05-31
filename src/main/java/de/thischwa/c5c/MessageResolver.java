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
package de.thischwa.c5c;

import java.util.Locale;

import javax.servlet.ServletContext;

import de.thischwa.c5c.exception.FilemanagerException;

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
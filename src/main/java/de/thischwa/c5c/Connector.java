/*
 * C5Connector.Java - The Java backend for the filemanager of corefive.
 * It provides a simple object for creating an editor instance.
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

import java.io.InputStream;

import javax.servlet.ServletContext;

import de.thischwa.c5c.exception.ConnectorException;
import de.thischwa.c5c.requestcycle.response.AResponse;

/**
 * The backend interface for the connector servlet of the filemanager of corefive. <br/>
 * In general a connector serves and manages files and folders accessed through the filemanager on 
 * an arbitrary backend system. The connector will retrieve a valid request. 'Valid' means in termes of
 * correct and reasonable parameters.
 */
public interface Connector {

	/**
	 * Initializes the connector.
	 *
	 * @param servletContext the servlet context
	 * @throws Exception if the initialization fails.
	 */
	public void init(final ServletContext servletContext) throws RuntimeException;

	public AResponse getFolder(String urlPath, boolean needSize, boolean showThumbnailsInGrid) throws ConnectorException;

	public AResponse getInfo(String urlPath, boolean needSize, boolean showThumbnailsInGrid) throws ConnectorException;
	
	public AResponse rename(String oldPath, String newName) throws ConnectorException;
	
	public AResponse createFolder(String urlPath, String sanitizedName) throws ConnectorException;
	
	public AResponse delete(String urlPath) throws ConnectorException;

	public AResponse upload(String urlPath, String sanitizedName, InputStream fileIn) throws ConnectorException;

	public AResponse downlad(String urlPath) throws ConnectorException;
}

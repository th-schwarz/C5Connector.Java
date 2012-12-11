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

import java.io.InputStream;
import java.util.List;

import javax.servlet.ServletContext;

import de.thischwa.c5c.exception.C5CException;
import de.thischwa.c5c.requestcycle.response.FileProperties;
import de.thischwa.c5c.requestcycle.response.Response;

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

	public List<FileProperties> getFolder(String urlPath, boolean needSize, boolean showThumbnailsInGrid) throws C5CException;

	public FileProperties getInfo(String urlPath, boolean needSize, boolean showThumbnailsInGrid) throws C5CException;
	
	public void rename(String oldPath, String santizedNewName) throws C5CException;
	
	public Response createFolder(String urlPath, String sanitizedName) throws C5CException;
	
	public void delete(String urlPath) throws C5CException;

	public Response upload(String urlPath, String sanitizedName, InputStream fileIn) throws C5CException;

	public Response download(String urlPath) throws C5CException;
}

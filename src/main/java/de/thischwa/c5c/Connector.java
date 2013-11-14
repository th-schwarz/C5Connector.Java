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

import java.io.InputStream;
import java.util.List;

import de.thischwa.c5c.exception.C5CException;
import de.thischwa.c5c.exception.FilemanagerException;
import de.thischwa.c5c.requestcycle.response.FileProperties;
import de.thischwa.c5c.requestcycle.response.mode.DownloadInfo;
import de.thischwa.c5c.requestcycle.response.mode.UploadFile;

/**
 * The backend interface for the connector servlet of the filemanager of corefive. <br/>
 * In general a connector serves and manages files and folders accessed through the filemanager on an arbitrary backend system. The
 * connector will retrieve a valid request. 'Valid' means in terms of correct and reasonable parameters.<br/>
 * <br/>
 * <b>Hint for implementations:</b> There are a factory object, which helps to build the required response objects: {@link ResponseFactory}.
 * Another important object is the {@link UserObjectProxy}. It provides helpful wrapper methods to the configurable user-objects.
 * For throwing known exceptions of the filemanager, the {@link FilemanagerException} must be used! Helpful constructors are provided.
 */
public interface Connector {

	/**
	 * Initializes the connector.
	 * 
	 * @throws Exception
	 *             if the initialization fails.
	 */
	public void init() throws RuntimeException;

	/**
	 * Executes the 'getfolder'-method of the filemanager.
	 * 
	 * @param urlPath
	 *            the requested folder, e.g. <code>/UserFiles/Image/</code>
	 * @param needSize
	 *            Mainly for image files. <code>true</code> indicates that the dimension of the image should be set in the
	 *            {@link FileProperties}.
	 * @param showThumbnailsInGrid
	 *            indicates if a 'real' preview image is needed
	 * @return a list of {@link FileProperties} objects prefilled with data of the files inside the requested folder
	 * @throws C5CException
	 */
	public List<FileProperties> getFolder(String urlPath, boolean needSize, boolean showThumbnailsInGrid) throws C5CException;

	/**
	 * Executes the 'getinfo'-method of the filemanager.
	 * 
	 * @param urlPath
	 *            the requested file, e.g. <code>/UserFiles/Image/logo.png</code>
	 * @param needSize
	 *            Mainly for image files. <code>true</code> indicates that the dimension of the image should be set in the
	 *            {@link FileProperties}.
	 * @param showThumbnailsInGrid
	 *            indicates if a 'real' preview image is needed
	 * @return a {@link FileProperties} object prefilled with data of the requested file
	 * @throws C5CException
	 */
	public FileProperties getInfo(String urlPath, boolean needSize, boolean showThumbnailsInGrid) throws C5CException;

	/**
	 * Executes the 'rename'-method of the filemanager.
	 * 
	 * @param oldPath
	 *            the requested file to rename, e.g. <code>/UserFiles/Image/logo.png</code>
	 * @param sanitizedNewName
	 *            the new (sanitized) name of the file, e.g. <code>img.png</code>
	 * @throws C5CException
	 */
	public void rename(String oldPath, String sanitizedNewName) throws C5CException;

	/**
	 * Executes the 'addfolder'-method of the filemanger.
	 * 
	 * @param urlDirectory
	 *            the path to the directory in which the new folder has to be created, e.g. <code>/UserFiles/</code>
	 * @param sanitizedName
	 *            the (sanitized) name of the folder hat should be created, e.g. <code>new_folder</code>
	 * @throws C5CException
	 */
	public void createFolder(String urlDirectory, String sanitizedName) throws C5CException;

	/**
	 * Executes the 'delete'-method of the filemanager.
	 * 
	 * @param urlPath
	 *            the requested file to delete, e.g. <code>/UserFiles/Image/logo.png</code>
	 * @throws C5CException
	 */
	public void delete(String urlPath) throws C5CException;

	/**
	 * Executes the 'add'-method of the filemanager.
	 * 
	 * @param urlDirectory
	 *            the path to the directory in which the new folder has to be created, e.g. <code>/UserFiles/</code>
	 * @param sanitizedName
	 *            the (sanitized) name of the folder hat should be created, e.g. <code>logo.png</code>
	 * @param in
	 *            {@link InputStream} in which the file data has to put in
	 * @return a {@link UploadFile} object prefilled with data of the requested file
	 * @throws C5CException
	 */
	public UploadFile upload(String urlDirectory, String sanitizedName, InputStream in) throws C5CException;

	/**
	 * Executes the 'download'-method of the filemanager.
	 * 
	 * @param urlPath
	 *            the requested file to download, e.g. <code>/UserFiles/folder/text.pdf</code>
	 * @return a {@link DownloadInfo} object prefilled with data of the requested file
	 * @throws C5CException
	 */
	public DownloadInfo download(String urlPath) throws C5CException;
}

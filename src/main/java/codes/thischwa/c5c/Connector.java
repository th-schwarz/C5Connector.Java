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
package codes.thischwa.c5c;

import java.awt.Dimension;
import java.io.IOException;
import java.io.InputStream;
import java.util.Set;

import codes.thischwa.c5c.GenericConnector.FileProperties;
import codes.thischwa.c5c.GenericConnector.StreamContent;
import codes.thischwa.c5c.exception.C5CException;
import codes.thischwa.c5c.exception.FilemanagerException;
import codes.thischwa.c5c.requestcycle.BackendPathBuilder;

/**
 * The backend interface for the connector servlet of the filemanager of corefive. <br/>
 * In general a connector serves and manages files and folders accessed through the filemanager on an arbitrary backend system. The
 * connector will retrieve a valid request. 'Valid' means in terms of correct and reasonable parameters.<br/>
 * Most of the methods get a parameter named 'backendPath' or similar. This is the requested url-path mapped to a path of the backend by the
 * {@link BackendPathBuilder}.<br/>
 * <br/>
 * <b>Hint for implementations:</b> For throwing known exceptions of the filemanager, the {@link FilemanagerException} must be used! Helpful
 * constructors are provided.<br/>
 * The implementation don't need to worry about restriction of single files or directories. This is done by the caller!
 */
public interface Connector {

	/**
	 * Initializes the connector.
	 * 
	 * @throws RuntimeException
	 *             if the initialization fails.
	 */
	public void init() throws RuntimeException;

	/**
	 * Setter for the file extensions for images. It will be used for additional checks and the handling of file properties.
	 * 
	 * @param imageExtensions
	 */
	public void setImageExtensions(Set<String> imageExtensions);

	/**
	 * Executes the 'getfolder'-method of the filemanager. <br/>
	 * The implementation shouldn't take care of the sorting of files and folders. This is done by the caller! 
	 * 
	 * @param backendPath
	 *            the requested backend folder, e.g. <code>/UserFiles/Image/</code>
	 * @param needSize
	 *            Mainly for image files. <code>true</code> indicates that the dimension of the image should be set in the
	 *            {@link GenericConnector.FileProperties}.
	 * @return a list of {@link GenericConnector.FileProperties} objects prefilled with data of the files inside the requested folder. To
	 *         initialize this object use {@link GenericConnector#buildForFile(String, boolean, long, java.util.Date)},
	 *         {@link GenericConnector#buildForDirectory(String, boolean, java.util.Date)} or
	 *         {@link GenericConnector#buildForImage(String, boolean, int, int, long, java.util.Date)}
	 * @throws C5CException
	 */
	public Set<FileProperties> getFolder(String backendPath, boolean needSize) throws C5CException;

	/**
	 * Executes the 'getinfo'-method of the filemanager.
	 * 
	 * @param backendPath
	 *            the requested backend file, e.g. <code>/UserFiles/Image/logo.png</code>
	 * @param needSize
	 *            Mainly for image files. <code>true</code> indicates that the dimension of the image should be set in the
	 *            {@link GenericConnector.FileProperties}.
	 * @return a {@link GenericConnector.FileProperties} object prefilled with data of the requested file
	 * @throws C5CException
	 */
	public GenericConnector.FileProperties getInfo(String backendPath, boolean needSize) throws C5CException;

	/**
	 * Executes the 'rename'-method of the filemanager.
	 * 
	 * @param oldBackendPath
	 *            the requested backend file to rename, e.g. <code>/UserFiles/Image/logo.png</code>
	 * @param sanitizedNewName
	 *            the new (sanitized) name of the file, e.g. <code>img.png</code>
	 * @return <code>true</code> if the renamed file is a directory, otherwise <code>false</code>
	 * @throws C5CException
	 */
	public boolean rename(String oldBackendPath, String sanitizedNewName) throws C5CException;

	/**
	 * Executes the 'addfolder'-method of the filemanger.
	 * 
	 * @param backendDirectory
	 *            the path to the directory, in which the new folder has to be created, e.g. <code>/UserFiles/</code>
	 * @param sanitizedName
	 *            the (sanitized) name of the folder hat should be created, e.g. <code>new_folder</code>
	 * @throws C5CException
	 */
	public void createFolder(String backendDirectory, String sanitizedName) throws C5CException;

	/**
	 * Executes the 'delete'-method of the filemanager.
	 * 
	 * @param backendPath
	 *            the requested file to delete, e.g. <code>/UserFiles/Image/logo.png</code>
	 * @return <code>true</code> if the file or directory was deleted successful, otherwise <code>false</code>
	 * @throws C5CException
	 */
	public boolean delete(String backendPath) throws C5CException;

	/**
	 * Executes the 'add'-method of the filemanager. The implementation has to overwrite the file, if there exists one with the same name.
	 * 
	 * @param backendDirectory
	 *            the path to the directory, in which the new folder has to be created, e.g. <code>/UserFiles/</code>
	 * @param sanitizedName
	 *            the (sanitized) name of the file that should be created, e.g. <code>logo.png</code>
	 * @param in
	 *            {@link InputStream} that contains the file data, it will be closed by the caller
	 * @throws C5CException
	 */
	public void upload(String backendDirectory, String sanitizedName, InputStream in) throws C5CException;

	/**
	 * Executes the 'download'-method of the filemanager.
	 * 
	 * @param backendPath
	 *            the requested file to download, e.g. <code>/UserFiles/folder/text.pdf</code>
	 * @return {@link GenericConnector.StreamContent} which holds the required data for the download action. Use
	 *         {@link GenericConnector#buildStreamContent(InputStream, long)} to build it.
	 * @throws C5CException
	 */
	public GenericConnector.StreamContent download(String backendPath) throws C5CException;

	/**
	 * Generates a thumbnail of the requested image ('backendPath') and writes it to the returned {@link StreamContent}. The caller has to
	 * ensure that 'backendPath' is an image. <br/>
	 * Hint: The implementation should use {@link #resize(InputStream, String, Dimension)} internally.
	 * 
	 * @param backendPath
	 *            the requested file to build an {@link InputStream} for the preview, e.g. <code>/UserFiles/Image/logo.png</code>
	 * @param dim
	 *            the dimension of the thumbnail, shouldn't be null
	 * @return {@link GenericConnector.StreamContent} which holds the required data of the thumbnail. Use
	 *         {@link GenericConnector#buildStreamContent(InputStream, long)} to build it.
	 * @throws C5CException
	 */
	public StreamContent buildThumbnail(String backendPath, Dimension dim) throws C5CException;

	/**
	 * Resizes an image and writes it to the returned {@link StreamContent}.
	 * 
	 * @param imageIn
	 *            {@link InputStream} of the image
	 * @param imageExt
	 *            file extension of the image
	 * @param dim
	 *            the dimension of an image
	 * @return {@link GenericConnector.StreamContent} which holds the required data of the image. Use
	 *         {@link GenericConnector#buildStreamContent(InputStream, long)} to build it.
	 * @throws IOException
	 */
	public StreamContent resize(InputStream imageIn, String imageExt, Dimension dim) throws IOException;

	/**
	 * Generates a preview of the requested image ('backendPath') and writes it to the returned {@link StreamContent}.
	 * 
	 * @param backendPath
	 *            the requested file to build an {@link InputStream} for the preview, e.g. <code>/UserFiles/Image/logo.png</code>
	 * @param maxDim
	 *            the max. dimension of an image, if it is <code>null</code> or greater than the original size it should be shown in its original
	 *            size
	 * @return {@link GenericConnector.StreamContent} which holds the required data of the image. Use
	 *         {@link GenericConnector#buildStreamContent(InputStream, long)} to build it.
	 * @throws C5CException
	 */
	public StreamContent preview(String backendPath, Dimension maxDim) throws C5CException;

	/**
	 * Executes the 'editfile'-method of the filemanager.
	 * 
	 * @param backendPath
	 *            the requested file to get the content from, e.g. <code>/UserFiles/sub/text.txt</code>
	 * @return the content of the requested file
	 * @throws C5CException
	 */
	public String editFile(String backendPath) throws C5CException;

	/**
	 * Executes the 'savefile'-method of the filemanager.
	 * 
	 * @param backendPath
	 *            the requested file to get the content from, e.g. <code>/UserFiles/sub/text.txt</code>
	 * @param content
	 *            content of the file to save
	 * @throws C5CException
	 */
	public void saveFile(String backendPath, String content) throws C5CException;

	/**
	 * Executes the 'savefile'-method of the filemanager.
	 * 
	 * @param backendPath
	 *            the requested file to get the content from, e.g. <code>/UserFiles/sub/text.txt</code>
	 * @param in
	 *            {@link InputStream} that contains the file data, it will be closed by the caller
	 * @throws C5CException
	 */
	public void replace(String backendPath, InputStream in) throws C5CException;
	
	
	public boolean isProtected(String backendPath);
}

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
package de.thischwa.c5c.impl;

import java.awt.Dimension;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.DateFormat;
import java.util.Locale;

import javax.servlet.ServletContext;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.io.filefilter.DirectoryFileFilter;
import org.apache.commons.io.filefilter.FileFileFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.thischwa.c5c.Connector;
import de.thischwa.c5c.Constants;
import de.thischwa.c5c.FilemanagerAction;
import de.thischwa.c5c.UserObjectProxy;
import de.thischwa.c5c.exception.C5CException;
import de.thischwa.c5c.exception.FilemanagerException;
import de.thischwa.c5c.requestcycle.RequestData;
import de.thischwa.c5c.requestcycle.response.Response;
import de.thischwa.c5c.requestcycle.response.mode.FileInfo;
import de.thischwa.c5c.requestcycle.response.mode.FolderInfo;
import de.thischwa.c5c.requestcycle.response.mode.ResponseFactory;
import de.thischwa.c5c.requestcycle.response.mode.UploadFile;
import de.thischwa.c5c.resource.Extension;
import de.thischwa.c5c.util.Path;
import de.thischwa.jii.IDimensionProvider;
import de.thischwa.jii.core.SimpleImageInfoWrapper;
import de.thischwa.jii.exception.ReadException;

/**
 * Real local filesystem backend connector. The file access is translated as-is
 * to the local filesystem. A servlet context is respected, if it exists.
 */
public class LocalConnector implements Connector {
	
	private static Logger logger = LoggerFactory.getLogger(LocalConnector.class);
	
	private ServletContext servletContext;
	
	public void init(ServletContext servletContext) {
		logger.info("*** {} sucessful initialized.", this.getClass().getName());
		this.servletContext = servletContext;
	}
	
	@Override
	public Response getFolder(String urlPath, boolean needSize, boolean showThumbnailsInGrid) throws C5CException {
		File folder = buildAndCheckFolder(urlPath);
		return constructFromDirRequest(urlPath, folder, needSize, showThumbnailsInGrid);
	}
	
	@Override
	public Response getInfo(String urlPath, boolean needSize, boolean showThumbnailsInGrid) throws C5CException {
		File file = buildRealFile(urlPath);
		if(!file.exists()) {
			logger.error("Requested file not exits: {}", file.getAbsolutePath());
			throw new FilemanagerException(FilemanagerAction.INFO, FilemanagerException.KEY_FILE_NOT_EXIST, urlPath);
		}
		return constructFileInfo(false, urlPath, file, needSize, showThumbnailsInGrid);
	}
	
	@Override
	public Response rename(String oldPath, String sanitizedName) throws C5CException {
		File src = buildRealFile(oldPath);
		if(!src.exists()) {
			logger.error("Source file not found: {}", src.getAbsolutePath());
			String key = (src.isDirectory()) ? FilemanagerException.KEY_DIRECTORY_NOT_EXIST : FilemanagerException.KEY_FILE_NOT_EXIST;
			throw new FilemanagerException(FilemanagerAction.RENAME, key, oldPath);
		}
	
		File dest = new File(src.getParentFile(), sanitizedName);
		if(dest.exists()) {
			logger.warn("Destination file already exists: {}", dest.getAbsolutePath());
			String key = (dest.isDirectory()) ? FilemanagerException.KEY_DIRECTORY_ALREADY_EXISTS : FilemanagerException.KEY_FILE_ALREADY_EXISTS;
			throw new FilemanagerException(FilemanagerAction.RENAME, key, sanitizedName);
		}
		
		boolean success = false;
		try {
			success = src.renameTo(dest);
		} catch (SecurityException e) {
			logger.warn(String.format("Error while renaming [%s] to [%s]", src.getAbsolutePath(), dest.getAbsolutePath()), e);
		}
		if(!success) {
			String key = (src.isDirectory()) ? FilemanagerException.KEY_ERROR_RENAMING_DIRECTORY : FilemanagerException.KEY_ERROR_RENAMING_FILE;
			throw new FilemanagerException(FilemanagerAction.RENAME, key, oldPath, sanitizedName);
		}
		return ResponseFactory.buildRenameFile(oldPath, sanitizedName);
	}
	
	@Override
	public Response createFolder(String urlPath, String sanitizedFolderName) throws C5CException {
		File parentFolder = buildAndCheckFolder(urlPath);
		File newFolder = new File(parentFolder, sanitizedFolderName);
		if(newFolder.exists()) {
			logger.warn("Destination file already exists: {}", newFolder.getAbsolutePath());
			String key = (newFolder.isDirectory()) ? FilemanagerException.KEY_DIRECTORY_ALREADY_EXISTS : FilemanagerException.KEY_FILE_ALREADY_EXISTS;
			throw new FilemanagerException(FilemanagerAction.CREATEFOLDER, key, sanitizedFolderName);
		}

		boolean success = false;
		try {
			success = newFolder.mkdirs();
		} catch (SecurityException e) {
			logger.warn(String.format("Error while creating folder [%s]", newFolder.getAbsolutePath()), e);
		}
		if(!success) {
			throw new FilemanagerException(FilemanagerAction.RENAME, FilemanagerException.KEY_UNABLE_TO_CREATE_DIRECTORY, sanitizedFolderName);
		}
		return ResponseFactory.buildCreateFolder(urlPath, sanitizedFolderName);
	}
	
	/**
	 * Builds the and check folder.
	 *
	 * @param urlPath the url path
	 * @return the file
	 * @throws FilemanagerException the known exception
	 */
	private File buildAndCheckFolder(String urlPath) throws FilemanagerException {
		File parentFolder = buildRealFile(urlPath);
		if(!parentFolder.exists()) {
			logger.error("Source file nont found: {}", parentFolder.getAbsolutePath());
			String key = (parentFolder.isDirectory()) ? FilemanagerException.KEY_DIRECTORY_NOT_EXIST : FilemanagerException.KEY_FILE_NOT_EXIST;
			throw new FilemanagerException(FilemanagerAction.CREATEFOLDER, key, urlPath);
		}		
		return parentFolder;
	}
	
	@Override
	public Response delete(String urlPath) throws C5CException {
		File file = buildRealFile(urlPath);
		if(!file.exists()) {
			logger.error("Requested file not exits: {}", file.getAbsolutePath());
			String key = (file.isDirectory()) ? FilemanagerException.KEY_DIRECTORY_NOT_EXIST : FilemanagerException.KEY_FILE_NOT_EXIST;
			throw new FilemanagerException(FilemanagerAction.DELETE, key, urlPath);
		}
		boolean success = org.apache.commons.io.FileUtils.deleteQuietly(file);
		if(!success) {
			throw new FilemanagerException(FilemanagerAction.DELETE, FilemanagerException.KEY_INVALID_DIRECTORY_OR_FILE, urlPath);
		}
		return ResponseFactory.buildDelete(urlPath);
	}
	
	/**
	 * Builds the real file.
	 *
	 * @param urlPath the url path
	 * @return the file
	 */
	private File buildRealFile(String urlPath) {
		String path = servletContext.getRealPath(urlPath);
		return new File(path);
	}
	
	/**
	 * Construct file info.
	 *
	 * @param isDirRequest the is dir request
	 * @param urlPath the url path
	 * @param file the file
	 * @param needSize the need size
	 * @param showThumbnailsInGrid the show thumbnails in grid
	 * @return the file info
	 * @throws C5CException the connector exception
	 */
	private FileInfo constructFileInfo(boolean isDirRequest, String urlPath, File file, boolean needSize, boolean showThumbnailsInGrid) throws C5CException {
		FileInfo fi; 
		String fixedUrlPath;
		if(isDirRequest) {
			fixedUrlPath = urlPath;
			Path p = new Path(fixedUrlPath);
			if(!file.isDirectory())
				throw new IllegalArgumentException(String.format("It's a folder request, but requested folder isn't a directory: %s", file.getName()));
			fi = ResponseFactory.buildFileInfo(p.addFile(file.getName()), true);
		} else {
			fixedUrlPath = (urlPath.contains(Constants.separator)) ? urlPath.substring(0, urlPath.lastIndexOf(Constants.separatorChar)) : urlPath;
			Path p = new Path(fixedUrlPath);
			if(file.isDirectory())
				throw new IllegalArgumentException(String.format("It's a file request, but requested file is a directory: %s", file.getName()));
			fi = ResponseFactory.buildFileInfo(p.addFile(file.getName()), false);
		}
		ResponseFactory.setPreviewPath(fi, UserObjectProxy.getIconPath(fi.getVirtualFile()));
		try {
			Locale locale = RequestData.getLocale();
			DateFormat df = DateFormat.getDateInstance(DateFormat.SHORT, locale);
			String dateStr = df.format(file.lastModified());
			ResponseFactory.setCapabilities(fi, UserObjectProxy.getC5FileCapabilities(fixedUrlPath));
			if(fi.isDir()) 
				ResponseFactory.setFolderProperties(fi, dateStr);
			else {
				// 'needsize' isn't implemented in the filemanager yet, so the dimension is set if we have an image.
				if(Extension.IMAGE.isAllowedExtension(FilenameUtils.getExtension(file.getPath()))) {
					IDimensionProvider dp = new SimpleImageInfoWrapper();
					dp.set(file);
					Dimension dim = dp.getDimension();
					ResponseFactory.setFileProperties(fi, dim.width, dim.height, file.length(), dateStr);
				} else
					ResponseFactory.setFileProperties(fi, file.length(), dateStr);
			}
		} catch (SecurityException e) {
			throw new C5CException(String.format("Error while analysing %s: %s", file.getPath(), e.getMessage()));
		} catch (ReadException e) {
			throw new C5CException(String.format("Error while getting the dimension of the image %s: %s", file.getPath(), e.getMessage()));			
		} catch (FileNotFoundException e) {
			throw new C5CException(String.format("File not found: %s", file.getPath()));
		} 
		return fi;
	}
	
	/**
	 * Construct from dir request.
	 *
	 * @param urlPath the url path
	 * @param dir the dir
	 * @param needSize the need size
	 * @param showThumbnailsInGrid the show thumbnails in grid
	 * @return the folder info
	 * @throws C5CException the connector exception
	 */
	private FolderInfo constructFromDirRequest(String urlPath, File dir, boolean needSize, boolean showThumbnailsInGrid) throws C5CException {
		FolderInfo folderInfo = ResponseFactory.buildFolderInfo();
		// add dirs
		File[] fileList = dir.listFiles((FileFilter) DirectoryFileFilter.DIRECTORY);
		for (File file : fileList) {
			FileInfo fileInfo = constructFileInfo(true, urlPath, file, needSize, showThumbnailsInGrid);
			ResponseFactory.add(folderInfo, fileInfo);
		}

		// add files
		fileList = dir.listFiles((FileFilter) FileFileFilter.FILE);
		for (File file : fileList) {
			FileInfo fileInfo = constructFileInfo(false, urlPath, file, needSize, showThumbnailsInGrid);
			ResponseFactory.add(folderInfo, fileInfo);
		}
		return folderInfo;
	}
	
	@Override
	public Response upload(String urlPath, String sanitizedName, InputStream fileIn) throws C5CException {
		File parentFolder = buildAndCheckFolder(urlPath);
		File fileToSave = new File(parentFolder, sanitizedName);
		if(fileToSave.exists())
			throw new FilemanagerException(FilemanagerException.KEY_FILE_ALREADY_EXISTS, urlPath);
		try {
			Long size = IOUtils.copyLarge(fileIn, new FileOutputStream(fileToSave));
			UploadFile uf = ResponseFactory.buildUploadFile(urlPath, sanitizedName, size);
			return uf;
		} catch (IOException e) {
			throw new FilemanagerException(FilemanagerAction.UPLOAD, FilemanagerException.KEY_INVALID_FILE_UPLOAD, urlPath);
		}
	}
	
	@Override
	public Response downlad(String urlPath) throws C5CException {
		File file = buildRealFile(urlPath);
		try {
			InputStream in = new BufferedInputStream(new FileInputStream(file));
			return ResponseFactory.buildDownload(urlPath, file.length(), in);
		} catch (FileNotFoundException e) {
			logger.error("Requested file not exits: {}", file.getAbsolutePath());
			throw new FilemanagerException(FilemanagerAction.DOWNLOAD, FilemanagerException.KEY_FILE_NOT_EXIST, urlPath);
		}
	}
}

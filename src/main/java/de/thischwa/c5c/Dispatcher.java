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

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileItemFactory;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.thischwa.c5c.exception.ConnectorException;
import de.thischwa.c5c.exception.UserActionException;
import de.thischwa.c5c.requestcycle.RequestData;
import de.thischwa.c5c.requestcycle.response.Response;
import de.thischwa.c5c.requestcycle.response.ErrorResponseFactory;
import de.thischwa.c5c.requestcycle.response.mode.ModeResponseFactory;
import de.thischwa.c5c.resource.PropertiesLoader;
import de.thischwa.c5c.util.FileUtils;
import de.thischwa.c5c.util.StringUtils;

/**
 * Dispatch the request from the 'main' servlet {@link ConnectorServlet} to the implementation of the 
 * {@link Connector} interface.
 */
class Dispatcher {
	private static Logger logger = LoggerFactory.getLogger(Dispatcher.class);
	
	private Connector connector;
	
	/**
	 * Instantiates and initializes the implementation of the {@link Connector}.
	 * 
	 * @param servletContext
	 * @param connectorClassName 
	 * @throws RuntimeException
	 */
	Dispatcher(final ServletContext servletContext, String connectorClassName) throws RuntimeException {
		if (StringUtils.isNullOrEmpty(connectorClassName))
			throw new RuntimeException("Empty Connector implementation class name not allowed.");
		else {
			try {
				Class<?> clazz = Class.forName(connectorClassName);
				connector = (Connector) clazz.newInstance();
				logger.info("Connector instantiated to {}", connectorClassName);
			} catch (Throwable e) {
				logger.error("Connector implementation {} could not be instantiated", connectorClassName);
				throw new RuntimeException("Connector implementation " + connectorClassName + " could not be instantiated", e);
			}
		}
		connector.init(servletContext);
		logger.info("Dispatcher successful initialized.");
	}

	private RequestMode resolveMode(String mode) throws ConnectorException { 
		if(mode == null)
			throw new IllegalArgumentException("Missing 'mode' parameter.");
		try {
			return RequestMode.valueOfIgnoreCase(mode);
		} catch (IllegalArgumentException e) {
			logger.error("'mode' not found: {}", mode);
			throw new ConnectorException(UserObjectProxy.getFilemanagerErrorMessage("MODE_ERROR"));
		}
	}

	/**
	 * getinfo, getfolder, rename, delete, download.
	 *
	 * @return the response 
	 */
	Response doGet() {
		logger.debug("Entering Dispatcher#doGet");
		HttpServletRequest req = RequestData.getRequest();
		try {
			Response resp = null;
			RequestMode mode = resolveMode(req.getParameter("mode"));
			switch (mode) {
			case FOLDER: {
				String urlPath = req.getParameter("path");
				boolean needSize = Boolean.parseBoolean(req.getParameter("getsize"));
				boolean showThumbnailsInGrid = Boolean.parseBoolean(req.getParameter("showThumbs")); 
				logger.debug("* getFolder -> urlPath: {}, needSize: {}, showThumbnails: {}", new Object[] { urlPath, needSize, showThumbnailsInGrid });
				resp = connector.getFolder(urlPath, needSize, showThumbnailsInGrid);
				break;}
			case INFO: {
				String urlPath = req.getParameter("path");
				boolean needSize = Boolean.parseBoolean(req.getParameter("getsize"));
				boolean showThumbnailsInGrid = Boolean.parseBoolean(req.getParameter("showThumbs")); 
				logger.debug("* getInfo -> urlPath: {}, needSize: {}, showThumbnails: {}", new Object[] { urlPath, needSize, showThumbnailsInGrid });
				resp = connector.getInfo(urlPath, needSize, showThumbnailsInGrid);
				break;}
			case RENAME: {
				String oldUrlPath = req.getParameter("old");
				String newName = req.getParameter("new");
				String sanitizedName = FileUtils.sanitizeName(newName);
				logger.debug("* rename -> oldUrlPath: {}, new name: {}, santized new name: {}", new Object[] { oldUrlPath, newName, sanitizedName });
				resp = connector.rename(oldUrlPath, sanitizedName);
				break;}
			case CREATEFOLDER: {
				if(!UserObjectProxy.isCreateFolderEnabled())
					throw new UserActionException(UserActionException.KEY_CREATEFOLDER_NOT_ALLOWED);
				String urlPath = req.getParameter("path");
				String folderName = req.getParameter("name");
				String santizedFolderName = FileUtils.sanitizeName(folderName);
				logger.debug("* createFolder -> urlPath: {}, name: {}, santized name: {}", new Object[] { urlPath, folderName, santizedFolderName });
				resp = connector.createFolder(urlPath, santizedFolderName);
				break;}
			case DELETE: {
				String urlPath = req.getParameter("path");
				logger.debug("* delete -> urlPath: {}", urlPath);
				resp = connector.delete(urlPath);
				break;}
			case DOWNLOAD: {
				String urlPath = req.getParameter("path");
				logger.debug("* download -> urlPath: {}", urlPath);
				resp = connector.downlad(urlPath);
				break;}
			default: {
				logger.error("'mode' not found: {}", req.getParameter("mode"));
				throw new ConnectorException(UserObjectProxy.getFilemanagerErrorMessage("MODE_ERROR"));}
			}
			resp.setMode(mode);
			return resp;
		} catch (ConnectorException e) {
			return ErrorResponseFactory.buildException(e);
		}
		
	}

	/**
	 * Do post, just for FILE upload.
	 *
	 * @return the response
	 */
	Response doPost() {
		logger.debug("Entering Dispatcher#doPost");
		HttpServletRequest req = RequestData.getRequest();

		try {
			Response resp = null;
			Map<String, String> params = new HashMap<String, String>();
			FileItemFactory factory = new DiskFileItemFactory();
			ServletFileUpload upload = new ServletFileUpload(factory);
			@SuppressWarnings("unchecked")
			List<FileItem> items = upload.parseRequest(req);
			FileItem uplFile = null;
			
			// analyze the multi part upload
			for(FileItem item : items) {
				if(item.isFormField()) {
					params.put(item.getFieldName(), item.getString(PropertiesLoader.getDefaultEncoding()));
				} else if(uplFile == null) // We upload just one FILE at the same time.
					uplFile = item;
			}
			
			RequestMode mode = resolveMode(params.get("mode"));
			switch (mode) {
			case UPLOAD: {
				String urlPath = params.get("currentpath");
				// Some browsers transfer the entire source path not just the filename
				String fileName = FilenameUtils.getName(uplFile.getName()); // TODO check the extensions with {@link Exception}.
				String sanitizedName = FileUtils.sanitizeName(fileName);
				logger.debug("* upload -> currentpath: {}, filename: {}, sanitized filename: {}", urlPath, fileName, sanitizedName);
				if(!UserObjectProxy.isFileUploadEnabled()) {
					// we have to use explicit the UploadFile object here because of the textarea stuff
					resp = ModeResponseFactory.buildUploadFileForError(urlPath, sanitizedName);
				} else {
					resp = connector.upload(urlPath, sanitizedName, uplFile.getInputStream());
				}
				resp.setMode(RequestMode.UPLOAD);
				return resp;
				}
			default: {
				logger.error("'mode' not found: {}", req.getParameter("mode"));
				throw new ConnectorException(UserObjectProxy.getFilemanagerErrorMessage("MODE_ERROR"));
				}
			}
		} catch (ConnectorException e) {
			logger.error("A ConnectionException was thrown while uploading: " + e.getMessage(), e);
			return ErrorResponseFactory.buildException(e);
		} catch (FileUploadException e) {
			logger.error("A FileUploadException was thrown while uploading: " + e.getMessage(), e);
			return ErrorResponseFactory.buildErrorResponse(e.getMessage(), 200);
		} catch (IOException e) {
			logger.error("A IOException was thrown while uploading: " + e.getMessage(), e);
			return ErrorResponseFactory.buildErrorResponse(e.getMessage(), 200);
		}
	}
}

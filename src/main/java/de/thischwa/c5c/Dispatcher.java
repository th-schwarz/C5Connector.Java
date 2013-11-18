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

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.Part;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.thischwa.c5c.exception.C5CException;
import de.thischwa.c5c.exception.FilemanagerException.Key;
import de.thischwa.c5c.requestcycle.Context;
import de.thischwa.c5c.requestcycle.RequestData;
import de.thischwa.c5c.requestcycle.response.FileProperties;
import de.thischwa.c5c.requestcycle.response.Response;
import de.thischwa.c5c.requestcycle.response.mode.CreateFolder;
import de.thischwa.c5c.requestcycle.response.mode.Download;
import de.thischwa.c5c.requestcycle.response.mode.DownloadInfo;
import de.thischwa.c5c.requestcycle.response.mode.FileInfo;
import de.thischwa.c5c.requestcycle.response.mode.FolderInfo;
import de.thischwa.c5c.requestcycle.response.mode.Rename;
import de.thischwa.c5c.resource.PropertiesLoader;
import de.thischwa.c5c.util.FileUtils;
import de.thischwa.c5c.util.StringUtils;
import de.thischwa.c5c.util.VirtualFile;

/**
 * Dispatches the request from the 'main' servlet {@link ConnectorServlet} to the implementation of the 
 * {@link Connector} interface. The parameters of the request will be prepared before dispatching them to connector. 
 */
final class Dispatcher {
	private static Logger logger = LoggerFactory.getLogger(Dispatcher.class);
	
	private Connector connector;
	
	/**
	 * Instantiates and initializes the implementation of the {@link Connector}.
	 * 
	 * @param servletContext the servlet context
	 * @param connectorClassName FQN of the implementation of the connector 
	 * @throws RuntimeException if the connector couldn't be instantiated
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
				String msg = String.format("Connector implementation [%s] couldn't be instatiated.", connectorClassName);
				logger.error(msg);
				throw new RuntimeException(msg, e);
			}
		}
		connector.init();
		logger.info("Dispatcher successful initialized.");
	}

	/**
	 * Processes the get-request. Known modes are: getinfo, getfolder, rename, delete, download.
	 *
	 * @return the response 
	 */
	Response doGet() {
		logger.debug("Entering Dispatcher#doGet");
		Context ctx = RequestData.getContext();
		FilemanagerAction mode = ctx.getMode();
		HttpServletRequest req = RequestData.getContext().getServletRequest();
		try {
			Response resp = null;
			switch (mode) {
			case FOLDER: {
				String urlPath = req.getParameter("path");
				boolean needSize = Boolean.parseBoolean(req.getParameter("getsize"));
				boolean showThumbnailsInGrid = Boolean.parseBoolean(req.getParameter("showThumbs")); 
				logger.debug("* getFolder -> urlPath: {}, needSize: {}, showThumbnails: {}", urlPath, needSize, showThumbnailsInGrid);
				List<FileProperties> props = connector.getFolder(urlPath, needSize, showThumbnailsInGrid);
				resp = buildFolder(urlPath, props);
				break;}
			case INFO: {
				String urlPath = req.getParameter("path");
				boolean needSize = Boolean.parseBoolean(req.getParameter("getsize"));
				boolean showThumbnailsInGrid = Boolean.parseBoolean(req.getParameter("showThumbs")); 
				logger.debug("* getInfo -> urlPath: {}, needSize: {}, showThumbnails: {}", urlPath, needSize, showThumbnailsInGrid);
				FileProperties fp = connector.getInfo(urlPath, needSize, showThumbnailsInGrid);
				resp = buildInfo(urlPath, fp);
				break;}
			case RENAME: {
				String oldUrlPath = req.getParameter("old");
				String newName = req.getParameter("new");
				String sanitizedName = FileUtils.sanitizeName(newName);
				logger.debug("* rename -> oldUrlPath: {}, new name: {}, santized new name: {}", oldUrlPath, newName, sanitizedName);
				connector.rename(oldUrlPath, sanitizedName);
				resp = Dispatcher.buildRenameFile(oldUrlPath, sanitizedName);
				break;}
			case CREATEFOLDER: {
				String urlPath = req.getParameter("path");
				String folderName = req.getParameter("name");
				String sanitizedFolderName = FileUtils.sanitizeName(folderName);
				logger.debug("* createFolder -> urlPath: {}, name: {}, sanitized name: {}", urlPath, folderName, sanitizedFolderName);
				connector.createFolder(urlPath, sanitizedFolderName);
				resp = Dispatcher.buildCreateFolder(urlPath, sanitizedFolderName);
				break;}
			case DELETE: {
				String urlPath = req.getParameter("path");
				logger.debug("* delete -> urlPath: {}", urlPath);
				resp = connector.delete(urlPath);
				break;}
			case DOWNLOAD: {
				String urlPath = req.getParameter("path");
				logger.debug("* download -> urlPath: {}", urlPath);
				DownloadInfo di = connector.download(urlPath);
				resp = Dispatcher.buildDownload(urlPath, di.getFileSize(), di.getInputStream());
				break;}
			default: {
				logger.error("'mode' not found: {}", req.getParameter("mode"));
				throw new C5CException(UserObjectProxy.getFilemanagerErrorMessage(Key.ModeError));}
			}
			resp.setMode(mode);
			return resp;
		} catch (C5CException e) {
			return ErrorResponseFactory.buildException(e);
		}		
	}
	
	private FolderInfo buildFolder(String urlPath, List<FileProperties> props) {
		FolderInfo folderInfo = Dispatcher.buildFolderInfo();
		if(props == null)
			return folderInfo;
		List<FileInfo> infos = new ArrayList<>(props.size());
		for (FileProperties fileProperties : props) {
			FileInfo fileInfo = Dispatcher.buildFileInfo(urlPath, fileProperties);
			Dispatcher.setCapabilities(fileInfo, urlPath);
			VirtualFile vf = new VirtualFile(fileInfo.getPath(), fileInfo.isDir());
			Dispatcher.setPreviewPath(fileInfo, UserObjectProxy.getIconPath(vf));
			infos.add(fileInfo);
			Dispatcher.add(folderInfo, fileInfo);
		}
		return folderInfo;
	}
	
	private FileInfo buildInfo(String urlPath, FileProperties props) {
		FileInfo fileInfo = Dispatcher.buildFileInfo(urlPath, props);
		Dispatcher.setCapabilities(fileInfo, urlPath);
		Dispatcher.setPreviewPath(fileInfo, UserObjectProxy.getIconPath(fileInfo.getVirtualFile()));
		return fileInfo;
	}

	/**
	 * Processes the post-request. It's just for file upload.
	 *
	 * @return the response
	 */
	Response doPost() {
		logger.debug("Entering Dispatcher#doPost");
		Context ctx = RequestData.getContext();
		FilemanagerAction mode = ctx.getMode();
		HttpServletRequest req = RequestData.getContext().getServletRequest();

		Integer maxFileSize = (UserObjectProxy.getFilemanagerConfig(req).getUpload().isFileSizeLimitAuto())
				? PropertiesLoader.getMaxUploadSize()
				: UserObjectProxy.getFilemanagerConfig(req).getUpload().getFileSizeLimit();
		try {
			Response resp = null;
			
			switch (mode) {
			case UPLOAD: {
				String urlPath = IOUtils.toString(req.getPart("currentpath").getInputStream());
				Part uploadPart = req.getPart("newfile");
				String newName = getFileName(uploadPart);
				// Some browsers transfer the entire source path not just the filename
				String fileName = FilenameUtils.getName(newName); // TODO check forceSingleExtension
				String sanitizedName = FileUtils.sanitizeName(fileName);
				logger.debug("* upload -> currentpath: {}, filename: {}, sanitized filename: {}", urlPath, fileName, sanitizedName);
				resp = connector.upload(urlPath, sanitizedName, uploadPart.getInputStream(), maxFileSize);
				// TODO add file size constraint
				logger.debug("successful uploaded {} bytes", uploadPart.getSize());
				resp.setMode(FilemanagerAction.UPLOAD);
				return resp;
				}
			default: {
				logger.error("'mode' not found: {}", req.getParameter("mode"));
				throw new C5CException(UserObjectProxy.getFilemanagerErrorMessage(Key.ModeError));
				}
			}
		} catch (C5CException e) {
			logger.error("A ConnectionException was thrown while uploading: " + e.getMessage(), e);
			return ErrorResponseFactory.buildException(e);
		} catch (ServletException e) {
			logger.error("A ServletException was thrown while uploading: " + e.getMessage(), e);
			return ErrorResponseFactory.buildErrorResponse(e.getMessage(), 200);
		} catch (IOException e) {
			logger.error("A IOException was thrown while uploading: " + e.getMessage(), e);
			return ErrorResponseFactory.buildErrorResponse(e.getMessage(), 200);
		} 
	}

	private String getFileName(final Part part) {
	    final String partHeader = part.getHeader("content-disposition");
	    for (String content : partHeader.split(";")) {
	        if (content.trim().startsWith("filename")) {
	            return content.substring(
	                    content.indexOf('=') + 1).trim().replace("\"", "");
	        }
	    }
	    return null;
	}

	private static void add(FolderInfo folderInfo, FileInfo fileInfo) {
		folderInfo.add(fileInfo);
	}

	private static FolderInfo buildFolderInfo() {
		return new FolderInfo();
	}

	private static Rename buildRenameFile(String oldFullPath, String newName) {
		return new Rename(oldFullPath, newName);
	}

	private static CreateFolder buildCreateFolder(String parentUrlPath, String folderName) {
		return new CreateFolder(parentUrlPath, folderName);
	}

	private static Download buildDownload(String fullPath, long contentLength, InputStream in) {
		return new Download(fullPath, contentLength, in);
	}

	private static void setPreviewPath(FileInfo fi, String previewPath) {
		fi.setPreviewPath(previewPath);
	}

	private static void setCapabilities(FileInfo fi, String urlPath) {
		fi.setCapabilities(UserObjectProxy.getC5FileCapabilities(urlPath));
	}

	private static FileInfo buildFileInfo(String urlPath, FileProperties fp) {
		FileInfo fi = new FileInfo(urlPath, fp.isDir());
		fi.setFileProperties(fp);
		return fi;
	}
}

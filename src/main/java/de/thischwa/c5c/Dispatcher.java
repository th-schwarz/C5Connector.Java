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
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.Part;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.thischwa.c5c.ResponseFactory.DownloadInfo;
import de.thischwa.c5c.ResponseFactory.FileProperties;
import de.thischwa.c5c.exception.C5CException;
import de.thischwa.c5c.exception.FilemanagerException;
import de.thischwa.c5c.exception.FilemanagerException.Key;
import de.thischwa.c5c.requestcycle.Context;
import de.thischwa.c5c.requestcycle.RequestData;
import de.thischwa.c5c.requestcycle.response.GenericResponse;
import de.thischwa.c5c.requestcycle.response.mode.CreateFolder;
import de.thischwa.c5c.requestcycle.response.mode.Delete;
import de.thischwa.c5c.requestcycle.response.mode.Download;
import de.thischwa.c5c.requestcycle.response.mode.FileInfo;
import de.thischwa.c5c.requestcycle.response.mode.FolderInfo;
import de.thischwa.c5c.requestcycle.response.mode.Rename;
import de.thischwa.c5c.requestcycle.response.mode.UploadFile;
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
	GenericResponse doGet() {
		logger.debug("Entering Dispatcher#doGet");
		Context ctx = RequestData.getContext();
		FilemanagerAction mode = ctx.getMode();
		HttpServletRequest req = RequestData.getContext().getServletRequest();
		Set<String> allowedImageExtensions = UserObjectProxy.getFilemanagerConfig().getImages().getExtensions();
		try {
			GenericResponse resp = null;
			switch (mode) {
			case FOLDER: {
				String urlPath = req.getParameter("path");
				String backendPath = buildBackendPath(urlPath);
				boolean needSize = Boolean.parseBoolean(req.getParameter("getsize"));
				boolean showThumbnailsInGrid = Boolean.parseBoolean(req.getParameter("showThumbs")); 
				logger.debug("* getFolder -> urlPath: {}, backendPath: {}, needSize: {}, showThumbnails: {}", urlPath, backendPath, needSize, showThumbnailsInGrid);
				List<FileProperties> props = connector.getFolder(backendPath, needSize, showThumbnailsInGrid, allowedImageExtensions);
				resp = buildFolder(urlPath, props);
				break;}
			case INFO: {
				String urlPath = req.getParameter("path");
				String backendPath = buildBackendPath(urlPath);
				boolean needSize = Boolean.parseBoolean(req.getParameter("getsize"));
				boolean showThumbnailsInGrid = Boolean.parseBoolean(req.getParameter("showThumbs")); 
				logger.debug("* getInfo -> urlPath: {}, backendPath {}, needSize: {}, showThumbnails: {}", urlPath, backendPath, needSize, showThumbnailsInGrid);
				FileProperties fp = connector.getInfo(backendPath, needSize, showThumbnailsInGrid, allowedImageExtensions);
				resp = buildInfo(urlPath, fp);
				break;}
			case RENAME: {
				String oldUrlPath = req.getParameter("old");
				String oldBackendPath = buildBackendPath(oldUrlPath);
				String newName = req.getParameter("new");
				String sanitizedName = FileUtils.sanitizeName(newName);
				logger.debug("* rename -> oldUrlPath: {}, backendPath: {}, new name: {}, santized new name: {}", oldUrlPath, oldBackendPath, newName, sanitizedName);
				boolean isDirectory = connector.rename(oldBackendPath, sanitizedName);
				resp = buildRename(oldUrlPath, sanitizedName, isDirectory);
				break;}
			case CREATEFOLDER: {
				String urlPath = req.getParameter("path");
				String backendPath = buildBackendPath(urlPath);
				String folderName = req.getParameter("name");
				String sanitizedFolderName = FileUtils.sanitizeName(folderName);
				logger.debug("* createFolder -> urlPath: {}, backendPath: {}, name: {}, sanitized name: {}", urlPath, backendPath, folderName, sanitizedFolderName);
				connector.createFolder(backendPath, sanitizedFolderName);
				resp = buildCreateFolder(urlPath, sanitizedFolderName);
				break;}
			case DELETE: {
				String urlPath = req.getParameter("path");
				String backendPath = buildBackendPath(urlPath);
				logger.debug("* delete -> urlPath: {}, backendPath: {}", urlPath, backendPath);
				boolean isDirectory = connector.delete(backendPath);
				resp = buildDelete(urlPath, isDirectory);
				break;}
			case DOWNLOAD: {
				String urlPath = req.getParameter("path");
				String backendPath = buildBackendPath(urlPath);
				logger.debug("* download -> urlPath: {}, backendPath", urlPath, backendPath);
				DownloadInfo di = connector.download(backendPath);
				resp = buildDownload(backendPath, di);
				break;}
			default: {
				logger.error("Unknown 'mode' for GET: {}", req.getParameter("mode"));
				throw new C5CException(UserObjectProxy.getFilemanagerErrorMessage(Key.ModeError));}
			}
			resp.setMode(mode);
			return resp;
		} catch (C5CException e) {
			return ErrorResponseFactory.buildException(e);
		}		
	}
	
	private String buildBackendPath(String urlPath) {
		return UserObjectProxy.getBackendPath(urlPath);
	}
	
	private FolderInfo buildFolder(String urlPath, List<FileProperties> props) {
		FolderInfo folderInfo = buildFolderInfo();
		if(props == null)
			return folderInfo;
		List<FileInfo> infos = new ArrayList<>(props.size());
		for (FileProperties fileProperties : props) {
			FileInfo fileInfo = buildFileInfo(urlPath, fileProperties);
			setCapabilities(fileInfo, urlPath);
			VirtualFile vf = new VirtualFile(fileInfo.getPath(), fileInfo.isDir());
			setPreviewPath(fileInfo, UserObjectProxy.getIconPath(vf));
			infos.add(fileInfo);
			add(folderInfo, fileInfo);
		}
		return folderInfo;
	}
	
	private FileInfo buildInfo(String urlPath, FileProperties props) {
		FileInfo fileInfo = buildFileInfo(urlPath, props);
		setCapabilities(fileInfo, urlPath);
		setPreviewPath(fileInfo, UserObjectProxy.getIconPath(fileInfo.getVirtualFile()));
		return fileInfo;
	}

	/**
	 * Processes the post-request. It's just for file upload.
	 *
	 * @return the response
	 */
	GenericResponse doPost() {
		logger.debug("Entering Dispatcher#doPost");
		Context ctx = RequestData.getContext();
		FilemanagerAction mode = ctx.getMode();
		HttpServletRequest req = RequestData.getContext().getServletRequest();

		Integer maxFileSize = (UserObjectProxy.getFilemanagerConfig(req).getUpload().isFileSizeLimitAuto())
				? PropertiesLoader.getMaxUploadSize()
				: UserObjectProxy.getFilemanagerConfig(req).getUpload().getFileSizeLimit();
		boolean overwrite = UserObjectProxy.getFilemanagerConfig(req).getUpload().isOverwrite();
				
		GenericResponse resp = null;
		InputStream in = null;
		try {
			switch (mode) {
			case UPLOAD: {
				String currentPath = IOUtils.toString(req.getPart("currentpath").getInputStream());
				String backendPath = buildBackendPath(currentPath);
				Part uploadPart = req.getPart("newfile");
				String newName = getFileName(uploadPart);
				
				// Some browsers transfer the entire source path not just the filename
				String fileName = FilenameUtils.getName(newName); // TODO check forceSingleExtension
				String sanitizedName = FileUtils.sanitizeName(fileName);
				logger.debug("* upload -> currentpath: {}, filename: {}, sanitized filename: {}", currentPath, fileName, sanitizedName);
				
				// check 'overwrite' and unambiguity
				String uniqueName = getUniqueName(sanitizedName);
				if(!overwrite && !uniqueName.equals(sanitizedName)) {
					throw new FilemanagerException(FilemanagerAction.UPLOAD, FilemanagerException.Key.FileAlreadyExists, sanitizedName);
				}
				sanitizedName = uniqueName;
				
				// check the max. upload size
				if(uploadPart.getSize() > maxFileSize.longValue() * 1024 * 1024)
					throw new FilemanagerException(FilemanagerAction.UPLOAD, FilemanagerException.Key.UploadFilesSmallerThan, maxFileSize.toString());
				in = uploadPart.getInputStream();
				connector.upload(backendPath, sanitizedName, in);
				
				logger.debug("successful uploaded {} bytes", uploadPart.getSize());
				resp = new UploadFile(currentPath, sanitizedName);
				resp.setMode(FilemanagerAction.UPLOAD);
				return resp;
				}
			default: {
				logger.error("Unknown 'mode' for POST: {}", req.getParameter("mode"));
				throw new C5CException(UserObjectProxy.getFilemanagerErrorMessage(Key.ModeError));
				}
			}
		} catch (C5CException e) {
			GenericResponse genericResp = ErrorResponseFactory.buildException(e);
			// the exception must be wrap into an UploadFile because of the special response handling of upload
			genericResp.setMode(FilemanagerAction.UPLOAD);
			return genericResp;
		} catch (ServletException e) {
			logger.error("A ServletException was thrown while uploading: " + e.getMessage(), e);
			return ErrorResponseFactory.buildErrorResponse(e.getMessage(), 200);
		} catch (IOException e) {
			logger.error("A IOException was thrown while uploading: " + e.getMessage(), e);
			return ErrorResponseFactory.buildErrorResponse(e.getMessage(), 200);
		} finally {
			IOUtils.closeQuietly(in);
		}
	}
	
	private String getUniqueName(String name) throws C5CException {
		List<FileProperties> props = connector.getFolder(name, false, false, null);
		Set<String> existingNames = new HashSet<>();
		for(FileProperties fp : props) {
			existingNames.add(fp.getName());
		}
		return StringUtils.getUniqueName(existingNames, name);
	}

	private String getFileName(final Part part) {
	    final String partHeader = part.getHeader("content-disposition");
	    for (String content : partHeader.split(";")) {
	        if (content.trim().startsWith("filename")) {
	            return content.substring(content.indexOf('=') + 1).trim().replace("\"", "");
	        }
	    }
	    return null;
	}

	private  void add(FolderInfo folderInfo, FileInfo fileInfo) {
		folderInfo.add(fileInfo);
	}
	
	private Rename buildRename(String urlPath, String newSanitizedName, boolean isDirectory) {
		return new Rename(urlPath, newSanitizedName, isDirectory);
	}
	
	private Delete buildDelete(String path, boolean isDirectory) {
		String delPath = path;
		if (isDirectory && !delPath.endsWith(Constants.defaultSeparator))
			delPath += Constants.defaultSeparator;
		return new Delete(delPath);
	}

	private FolderInfo buildFolderInfo() {
		return new FolderInfo();
	}

	private CreateFolder buildCreateFolder(String parentUrlPath, String folderName) {
		return new CreateFolder(parentUrlPath, folderName);
	}

	private Download buildDownload(String fullPath, DownloadInfo di) {
		return new Download(fullPath, di.getFileSize(), di.getInputStream());
	}

	private void setPreviewPath(FileInfo fi, String previewPath) {
		fi.setPreviewPath(previewPath);
	}

	private void setCapabilities(FileInfo fi, String urlPath) {
		fi.setCapabilities(UserObjectProxy.getC5FileCapabilities(urlPath));
	}

	private FileInfo buildFileInfo(String urlPath, FileProperties fp) {
		FileInfo fi = new FileInfo(urlPath, fp.isDir());
		fi.setFileProperties(fp);
		return fi;
	}
}

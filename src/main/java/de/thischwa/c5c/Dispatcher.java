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
package de.thischwa.c5c;

import java.awt.Dimension;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
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

import de.thischwa.c5c.exception.C5CException;
import de.thischwa.c5c.exception.FilemanagerException;
import de.thischwa.c5c.exception.FilemanagerException.Key;
import de.thischwa.c5c.requestcycle.Context;
import de.thischwa.c5c.requestcycle.RequestData;
import de.thischwa.c5c.requestcycle.response.GenericResponse;
import de.thischwa.c5c.requestcycle.response.mode.CreateFolder;
import de.thischwa.c5c.requestcycle.response.mode.Delete;
import de.thischwa.c5c.requestcycle.response.mode.Download;
import de.thischwa.c5c.requestcycle.response.mode.EditFile;
import de.thischwa.c5c.requestcycle.response.mode.FileInfo;
import de.thischwa.c5c.requestcycle.response.mode.FolderInfo;
import de.thischwa.c5c.requestcycle.response.mode.Rename;
import de.thischwa.c5c.requestcycle.response.mode.SaveFile;
import de.thischwa.c5c.requestcycle.response.mode.ShowThumbnail;
import de.thischwa.c5c.requestcycle.response.mode.UploadFile;
import de.thischwa.c5c.resource.PropertiesLoader;
import de.thischwa.c5c.resource.filemanager.FilemanagerConfig;
import de.thischwa.c5c.util.FileUtils;
import de.thischwa.c5c.util.StringUtils;
import de.thischwa.c5c.util.VirtualFile;

/**
 * Dispatches the request from the 'main' servlet {@link ConnectorServlet} to the implementation of the object which extends the
 * {@link Connector}. The parameters of the request will be prepared before dispatching them to connector.
 */
final class Dispatcher {
	private static Logger logger = LoggerFactory.getLogger(Dispatcher.class);

	private Connector connector;

	/**
	 * Instantiates and initializes the connector (object which extends the {@link GenericConnector});
	 * 
	 * @param servletContext
	 *            the servlet context
	 * @param connectorClassName
	 *            FQN of the implementation of the connector
	 * @throws RuntimeException
	 *             if the connector couldn't be instantiated
	 */
	Dispatcher(final ServletContext servletContext, String connectorClassName) throws RuntimeException {
		if(StringUtils.isNullOrEmpty(connectorClassName))
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
		Set<String> imageExtensions = UserObjectProxy.getFilemanagerConfig().getImages().getExtensions();
		connector.setImageExtensions(imageExtensions);
		try {
			Context ctx = RequestData.getContext();
			FilemanagerAction mode = ctx.getMode();
			HttpServletRequest req = RequestData.getContext().getServletRequest();
			GenericResponse resp = null;
			switch(mode) {
			case FOLDER: {
				String urlPath = req.getParameter("path");
				String backendPath = buildBackendPath(urlPath);
				boolean needSize = Boolean.parseBoolean(req.getParameter("getsize"));
				logger.debug("* getFolder -> urlPath: {}, backendPath: {}, needSize: {}", urlPath, backendPath, needSize);
				List<GenericConnector.FileProperties> props = connector.getFolder(backendPath, needSize);
				resp = buildFolder(urlPath, props);
				break;
			}
			case INFO: {
				String urlPath = req.getParameter("path");
				String backendPath = buildBackendPath(urlPath);
				boolean needSize = Boolean.parseBoolean(req.getParameter("getsize"));
				logger.debug("* getInfo -> urlPath: {}, backendPath {}, needSize: {}", urlPath, backendPath, needSize);
				GenericConnector.FileProperties fp = connector.getInfo(backendPath, needSize);
				resp = buildFileInfo(urlPath, fp);
				break;
			}
			case RENAME: {
				String oldUrlPath = req.getParameter("old");
				String oldBackendPath = buildBackendPath(oldUrlPath);
				String newName = req.getParameter("new");
				String sanitizedName = FileUtils.sanitizeName(newName);
				logger.debug("* rename -> oldUrlPath: {}, backendPath: {}, new name: {}, santized new name: {}", oldUrlPath,
						oldBackendPath, newName, sanitizedName);
				boolean isDirectory = connector.rename(oldBackendPath, sanitizedName);
				resp = buildRename(oldUrlPath, sanitizedName, isDirectory);
				break;
			}
			case CREATEFOLDER: {
				String urlPath = req.getParameter("path");
				String backendPath = buildBackendPath(urlPath);
				String folderName = req.getParameter("name");
				String sanitizedFolderName = FileUtils.sanitizeName(folderName);
				logger.debug("* createFolder -> urlPath: {}, backendPath: {}, name: {}, sanitized name: {}", urlPath, backendPath,
						folderName, sanitizedFolderName);
				connector.createFolder(backendPath, sanitizedFolderName);
				resp = buildCreateFolder(urlPath, sanitizedFolderName);
				break;
			}
			case DELETE: {
				String urlPath = req.getParameter("path");
				String backendPath = buildBackendPath(urlPath);
				logger.debug("* delete -> urlPath: {}, backendPath: {}", urlPath, backendPath);
				boolean isDirectory = connector.delete(backendPath);
				resp = buildDelete(urlPath, isDirectory);
				break;
			}
			case DOWNLOAD: {
				String urlPath = req.getParameter("path");
				String backendPath = buildBackendPath(urlPath);
				logger.debug("* download -> urlPath: {}, backendPath: {}", urlPath, backendPath);
				GenericConnector.StreamContent sc = connector.download(backendPath);
				resp = buildDownload(backendPath, sc);
				break;
			}
			case THUMBNAIL: {
				String urlPath = req.getParameter("path");
				String backendPath = buildBackendPath(urlPath);
				logger.debug("* thumbnail -> urlPath: {}, backendPath: {}", urlPath, backendPath);
				Dimension dim = UserObjectProxy.getThumbnailDimension();
				GenericConnector.StreamContent sc = connector.buildThumbnail(backendPath, dim.width, dim.height);
				resp = buildThumbnailView(backendPath, sc);
				break;
			}
			case EDITFILE: {
				String urlPath = req.getParameter("path");
				String backendPath = buildBackendPath(urlPath);
				logger.debug("* editfile -> urlPath: {}, backendPath: {}", urlPath, backendPath);
				resp = new EditFile(backendPath, connector.editFile(backendPath));
				break;
			}
			default: {
				logger.error("Unknown 'mode' for GET: {}", req.getParameter("mode"));
				throw new C5CException(UserObjectProxy.getFilemanagerErrorMessage(Key.ModeError));
			}
			}
			return resp;
		} catch (C5CException e) {
			return ErrorResponseFactory.buildException(e);
		}
	}

	private String buildBackendPath(String urlPath) {
		return UserObjectProxy.getBackendPath(urlPath);
	}

	private FolderInfo buildFolder(String urlPath, List<GenericConnector.FileProperties> props) {
		FolderInfo folderInfo = buildFolderInfo();
		if(props == null)
			return folderInfo;
		List<FileInfo> infos = new ArrayList<>(props.size());
		for(GenericConnector.FileProperties fileProperties : props) {
			FileInfo fileInfo = buildFileInfo(urlPath, fileProperties);
			infos.add(fileInfo);
			add(folderInfo, fileInfo);
		}
		return folderInfo;
	}

	private FileInfo buildFileInfo(String urlPath, GenericConnector.FileProperties fp) {
		FilemanagerConfig fConfig = UserObjectProxy.getFilemanagerConfig();
		FileInfo fi = new FileInfo(urlPath, fp.isDir());
		fi.setFileProperties(fp);
		setCapabilities(fi, urlPath);
		VirtualFile vf = new VirtualFile(fp.getName(), fp.isDir());
		if(fConfig.getOptions().isShowThumbs() && vf.getType()==VirtualFile.Type.file && fConfig.getImages().getExtensions().contains(vf.getExtension())) {
			// attention: urlPath can be with or without a file name!
			HttpServletRequest req = RequestData.getContext().getServletRequest();
			String previewUrlPath = (urlPath.endsWith(vf.getName())) ? urlPath : urlPath.concat(fp.getName());
			String query =  String.format("?mode=%s&path=%s", FilemanagerAction.THUMBNAIL.getParameterName(), encode(previewUrlPath));
			String preview = String.format("%s%s%s",req.getContextPath(), req.getServletPath(), query); 
			fi.setPreviewPath(preview);
		} else {
			fi.setPreviewPath(UserObjectProxy.getDefaultIconPath(vf));
		}
		return fi;
	}

	/**
	 * Processes the post-request. It's just for file upload.
	 * 
	 * @return the response
	 */
	GenericResponse doPost() {
		logger.debug("Entering Dispatcher#doPost");

		UploadFile resp = null;
		InputStream in = null;
		String currentPath = null;
		String newName = null;
		try {
			Context ctx = RequestData.getContext();
			FilemanagerAction mode = ctx.getMode();
			HttpServletRequest req = ctx.getServletRequest();
			FilemanagerConfig conf = UserObjectProxy.getFilemanagerConfig(req);
			switch(mode) {
			case UPLOAD: {
				Integer maxFileSize = (conf.getUpload().isFileSizeLimitAuto()) ? PropertiesLoader.getMaxUploadSize() : conf.getUpload()
						.getFileSizeLimit();
				boolean overwrite = conf.getUpload().isOverwrite();
				currentPath = IOUtils.toString(req.getPart("currentpath").getInputStream());
				String backendPath = buildBackendPath(currentPath);
				Part uploadPart = req.getPart("newfile");
				newName = getFileName(uploadPart);

				// check image only
				String ext = FilenameUtils.getExtension(newName);
				boolean isImageExt = conf.getImages().getExtensions().contains(ext);
				if(conf.getUpload().isImagesOnly() && !isImageExt)
					throw new FilemanagerException(FilemanagerAction.UPLOAD, FilemanagerException.Key.UploadImagesOnly);

				// Some browsers transfer the entire source path not just the filename
				String fileName = FilenameUtils.getName(newName); // TODO check forceSingleExtension
				String sanitizedName = FileUtils.sanitizeName(fileName);
				if(!overwrite)
					sanitizedName = getUniqueName(backendPath, sanitizedName);
				logger.debug("* upload -> currentpath: {}, filename: {}, sanitized filename: {}", currentPath, fileName, sanitizedName);

				// check 'overwrite' and unambiguity
				String uniqueName = getUniqueName(backendPath, sanitizedName);
				if(!overwrite && !uniqueName.equals(sanitizedName)) {
					throw new FilemanagerException(FilemanagerAction.UPLOAD, FilemanagerException.Key.FileAlreadyExists, sanitizedName);
				}
				sanitizedName = uniqueName;

				// check the max. upload size
				if(uploadPart.getSize() > maxFileSize.longValue() * 1024 * 1024)
					throw new FilemanagerException(FilemanagerAction.UPLOAD, FilemanagerException.Key.UploadFilesSmallerThan,
							maxFileSize.toString());
				in = uploadPart.getInputStream();

				// check if the file is really an image
				if(isImageExt && !UserObjectProxy.isImage(in))
					throw new FilemanagerException(FilemanagerAction.UPLOAD, FilemanagerException.Key.UploadImagesOnly);

				connector.upload(backendPath, sanitizedName, in);

				logger.debug("successful uploaded {} bytes", uploadPart.getSize());
				resp = new UploadFile(currentPath, sanitizedName);
				return resp;
			} case SAVEFILE: {
				String urlPath = req.getParameter("path");
				String backendPath = buildBackendPath(urlPath);
				logger.debug("* savefile -> urlPath: {}, backendPath: {}", urlPath, backendPath);
				String content = req.getParameter("content");
				connector.saveFile(backendPath, content);
				return new SaveFile(urlPath);
			}
			default: {
				logger.error("Unknown 'mode' for POST: {}", req.getParameter("mode"));
				throw new C5CException(UserObjectProxy.getFilemanagerErrorMessage(Key.ModeError));
			}
			}
		} catch (C5CException e) {
			resp = ErrorResponseFactory.buildErrorResponseForUpload(e.getMessage());
		} catch (ServletException e) {
			logger.error("A ServletException was thrown while uploading: " + e.getMessage(), e);
			resp = ErrorResponseFactory.buildErrorResponseForUpload(e.getMessage(), 200);
		} catch (IOException e) {
			logger.error("A IOException was thrown while uploading: " + e.getMessage(), e);
			resp = ErrorResponseFactory.buildErrorResponseForUpload(e.getMessage(), 200);
		} finally {
			IOUtils.closeQuietly(in);
		}

		if(currentPath != null)
			resp.setPath(currentPath);
		if(newName != null)
			resp.setName(newName);
		return resp;
	}

	private String getUniqueName(String backendPath, String name) throws C5CException {
		List<GenericConnector.FileProperties> props = connector.getFolder(backendPath, false);
		Set<String> existingNames = new HashSet<>();
		for(GenericConnector.FileProperties fp : props) {
			existingNames.add(fp.getName());
		}
		return StringUtils.getUniqueName(existingNames, name);
	}

	private String getFileName(final Part part) {
		final String partHeader = part.getHeader("content-disposition");
		for(String content : partHeader.split(";")) {
			if(content.trim().startsWith("filename")) {
				return content.substring(content.indexOf('=') + 1).trim().replace("\"", "");
			}
		}
		return null;
	}

	private void add(FolderInfo folderInfo, FileInfo fileInfo) {
		folderInfo.add(fileInfo);
	}

	private Rename buildRename(String urlPath, String newSanitizedName, boolean isDirectory) {
		return new Rename(urlPath, newSanitizedName, isDirectory);
	}

	private Delete buildDelete(String path, boolean isDirectory) {
		String delPath = path;
		if(isDirectory && !delPath.endsWith(Constants.defaultSeparator))
			delPath += Constants.defaultSeparator;
		return new Delete(delPath);
	}

	private FolderInfo buildFolderInfo() {
		return new FolderInfo();
	}

	private CreateFolder buildCreateFolder(String parentUrlPath, String folderName) {
		return new CreateFolder(parentUrlPath, folderName);
	}

	private Download buildDownload(String fullPath, GenericConnector.StreamContent di) {
		return new Download(fullPath, di.getSize(), di.getInputStream());
	}

	private ShowThumbnail buildThumbnailView(String fullPath, GenericConnector.StreamContent di) {
		return new ShowThumbnail(fullPath, di.getSize(), di.getInputStream());
	}
	
	private void setCapabilities(FileInfo fi, String urlPath) {
		fi.setCapabilities(UserObjectProxy.getC5FileCapabilities(urlPath));
	}
	
	private String encode(String str) {
		try {
			return URLEncoder.encode(str, PropertiesLoader.getConnectorDefaultEncoding());
		} catch (UnsupportedEncodingException e) {
			return "--unsupportedencoding--";
		}
	}
}

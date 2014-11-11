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
package codes.thischwa.c5c;

import java.awt.Dimension;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import codes.thischwa.c5c.GenericConnector.StreamContent;
import codes.thischwa.c5c.exception.C5CException;
import codes.thischwa.c5c.exception.FilemanagerException.Key;
import codes.thischwa.c5c.requestcycle.Context;
import codes.thischwa.c5c.requestcycle.RequestData;
import codes.thischwa.c5c.requestcycle.response.GenericResponse;
import codes.thischwa.c5c.requestcycle.response.mode.CreateFolder;
import codes.thischwa.c5c.requestcycle.response.mode.Delete;
import codes.thischwa.c5c.requestcycle.response.mode.Download;
import codes.thischwa.c5c.requestcycle.response.mode.EditFile;
import codes.thischwa.c5c.requestcycle.response.mode.FileInfo;
import codes.thischwa.c5c.requestcycle.response.mode.FolderInfo;
import codes.thischwa.c5c.requestcycle.response.mode.Prieview;
import codes.thischwa.c5c.requestcycle.response.mode.Rename;
import codes.thischwa.c5c.requestcycle.response.mode.ShowThumbnail;
import codes.thischwa.c5c.resource.PropertiesLoader;
import codes.thischwa.c5c.resource.filemanager.FilemanagerConfig;
import codes.thischwa.c5c.util.FileUtils;
import codes.thischwa.c5c.util.VirtualFile;

/**
 * Dispatches the GET-request from the 'main' servlet {@link ConnectorServlet} to the implementation of the object which extends the
 * {@link Connector}. The parameters of the request will be prepared before dispatching them to connector.
 */
final class DispatcherGET extends GenericDispatcher {
	private static Logger logger = LoggerFactory.getLogger(DispatcherGET.class);

	/**
	 * Instantiates and initializes the connector (object which extends the {@link GenericConnector});
	 * 
	 * @param connector
	 *            the implementation of the {@link Connector} interface
	 */
	DispatcherGET(Connector connector) {
		super(connector);
	}

	/**
	 * Processes the get-request. Known modes are: getinfo, getfolder, rename, delete, download.
	 * 
	 * @return the response
	 */
	@Override
	GenericResponse doRequest() {
		logger.debug("Entering DispatcherGET#doRequest");
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
				StreamContent sc = connector.download(backendPath);
				resp = buildDownload(backendPath, sc);
				break;
			}
			case THUMBNAIL: {
				String urlPath = req.getParameter("path");
				String backendPath = buildBackendPath(urlPath);
				logger.debug("* thumbnail -> urlPath: {}, backendPath: {}", urlPath, backendPath);
				Dimension dim = UserObjectProxy.getThumbnailDimension();
				StreamContent sc = connector.buildThumbnail(backendPath, dim.width, dim.height);
				resp = buildThumbnailView(backendPath, sc);
				break;
			}
			case PREVIEW: {
				String urlPath = req.getParameter("path");
				String backendPath = buildBackendPath(urlPath);
				boolean thumbnail = Boolean.valueOf(req.getParameter("thumbnail"));
				logger.debug("* thumbnail -> urlPath: {}, backendPath: {}, thumbnail: {}", urlPath, backendPath, thumbnail);
				if(thumbnail) {
					Dimension dim = UserObjectProxy.getThumbnailDimension();
					StreamContent sc = connector.buildThumbnail(backendPath, dim.width, dim.height);
					resp = buildThumbnailView(backendPath, sc);					
				} else {
					StreamContent sc = connector.preview(backendPath);
					resp = buildPrieview(backendPath, sc);
				}
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
			String query =  String.format("?mode=%s&path=%s&t=%s", FilemanagerAction.PREVIEW.getParameterName(), encode(previewUrlPath), Calendar.getInstance().getTimeInMillis());
			String preview = String.format("%s%s%s",req.getContextPath(), req.getServletPath(), query); 
			fi.setPreviewPath(preview);
		} else {
			fi.setPreviewPath(UserObjectProxy.getDefaultIconPath(vf));
		}
		return fi;
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

	private Download buildDownload(String fullPath, StreamContent sc) {
		return new Download(fullPath, sc.getSize(), sc.getInputStream());
	}

	private ShowThumbnail buildThumbnailView(String fullPath, StreamContent sc) {
		return new ShowThumbnail(fullPath, sc.getSize(), sc.getInputStream());
	}
	
	private Prieview buildPrieview(String fullPath, StreamContent sc) {
		return new Prieview(fullPath, sc.getSize(), sc.getInputStream());
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

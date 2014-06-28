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

import java.io.IOException;
import java.io.InputStream;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.Part;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import codes.thischwa.c5c.exception.C5CException;
import codes.thischwa.c5c.exception.FilemanagerException;
import codes.thischwa.c5c.exception.FilemanagerException.Key;
import codes.thischwa.c5c.requestcycle.Context;
import codes.thischwa.c5c.requestcycle.RequestData;
import codes.thischwa.c5c.requestcycle.response.GenericResponse;
import codes.thischwa.c5c.requestcycle.response.mode.Replace;
import codes.thischwa.c5c.requestcycle.response.mode.SaveFile;
import codes.thischwa.c5c.requestcycle.response.mode.UploadFile;
import codes.thischwa.c5c.resource.PropertiesLoader;
import codes.thischwa.c5c.resource.filemanager.FilemanagerConfig;
import codes.thischwa.c5c.util.FileUtils;
import codes.thischwa.c5c.util.StringUtils;
import codes.thischwa.c5c.util.VirtualFile;

/**
 * Dispatches the PUT-request from the 'main' servlet {@link ConnectorServlet} to the implementation of the object which extends the
 * {@link Connector}. The parameters of the request will be prepared before dispatching them to connector.
 */
final class DispatcherPUT extends GenericDispatcher {
	private static Logger logger = LoggerFactory.getLogger(DispatcherPUT.class);
	
	/**
	 * Instantiates and initializes the connector (object which extends the {@link GenericConnector});
	 * 
	 * @param connector
	 *            the implementation of the {@link Connector} interface
	 */
	DispatcherPUT(Connector connector ) {
		super(connector);
	}

	@Override
	GenericResponse doRequest() {
		logger.debug("Entering DispatcherPUT#doRequest");

		InputStream in = null;
		try {
			Context ctx = RequestData.getContext();
			FilemanagerAction mode = ctx.getMode();
			HttpServletRequest req = ctx.getServletRequest();
			FilemanagerConfig conf = UserObjectProxy.getFilemanagerConfig(req);
			Integer maxFileSize = (conf.getUpload().isFileSizeLimitAuto()) ? PropertiesLoader.getMaxUploadSize() : conf.getUpload()
					.getFileSizeLimit();
			switch(mode) {
			case UPLOAD: {
				boolean overwrite = conf.getUpload().isOverwrite();
				String currentPath = IOUtils.toString(req.getPart("currentpath").getInputStream());
				String backendPath = buildBackendPath(currentPath);
				Part uploadPart = req.getPart("newfile");
				String newName = getFileName(uploadPart);

				// check image only
				boolean isImageExt = checkImageExtension(backendPath, conf.getUpload().isImagesOnly(), conf.getImages().getExtensions());
				
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
				checkUploadSize(maxFileSize.longValue() * 1024 * 1024, uploadPart.getSize());
				
				in = uploadPart.getInputStream();

				// check if the file is really an image
				if(isImageExt && !UserObjectProxy.isImage(in))
	 				throw new FilemanagerException(FilemanagerAction.UPLOAD, FilemanagerException.Key.UploadImagesOnly);

				connector.upload(backendPath, sanitizedName, in);

				logger.debug("successful uploaded {} bytes", uploadPart.getSize());
				UploadFile ufResp = new UploadFile(currentPath, sanitizedName);
				ufResp.setName(newName);
				ufResp.setPath(currentPath);
				return ufResp;
			} case SAVEFILE: {
				String urlPath = req.getParameter("path");
				String backendPath = buildBackendPath(urlPath);
				logger.debug("* savefile -> urlPath: {}, backendPath: {}", urlPath, backendPath);
				String content = req.getParameter("content");
				connector.saveFile(backendPath, content);
				return new SaveFile(urlPath);
			} case REPLACE: {
				String newFilePath = IOUtils.toString(req.getPart("newfilepath").getInputStream());
				String backendPath = buildBackendPath(newFilePath);
				logger.debug("* replacefile -> urlPath: {}, backendPath: {}", newFilePath, backendPath);

				// check if file already exits
				VirtualFile vf = new VirtualFile(backendPath);
				String fileName = vf.getName();
				String uniqueName = getUniqueName(vf.getFolder(), fileName);
				if(uniqueName.equals(fileName)) {
					throw new FilemanagerException(FilemanagerAction.UPLOAD, FilemanagerException.Key.FileNotExists, backendPath);
				}
				
				// check image only
				boolean isImageExt = checkImageExtension(backendPath, conf.getUpload().isImagesOnly(), conf.getImages().getExtensions());
				
				Part uploadPart = req.getPart("fileR");
				
				// check the max. upload size
				checkUploadSize(maxFileSize.longValue() * 1024 * 1024, uploadPart.getSize());
				
				in = uploadPart.getInputStream();
				
				// check if the file is really an image
				if(isImageExt && !UserObjectProxy.isImage(in))
	 				throw new FilemanagerException(FilemanagerAction.UPLOAD, FilemanagerException.Key.UploadImagesOnly);
				
				connector.replace(backendPath, in);
				logger.debug("successful replaced {} bytes", uploadPart.getSize());
				VirtualFile vfUrlPath = new VirtualFile(newFilePath);
 				return new Replace(vfUrlPath.getFolder(), vfUrlPath.getName());
			}
			default: {
				logger.error("Unknown 'mode' for POST: {}", req.getParameter("mode"));
				throw new C5CException(UserObjectProxy.getFilemanagerErrorMessage(Key.ModeError));
			}
			}
		} catch (C5CException e) {
			return ErrorResponseFactory.buildErrorResponseForUpload(e.getMessage());
		} catch (ServletException e) {
			logger.error("A ServletException was thrown while uploading: " + e.getMessage(), e);
			return ErrorResponseFactory.buildErrorResponseForUpload(e.getMessage(), 200);
		} catch (IOException e) {
			logger.error("A IOException was thrown while uploading: " + e.getMessage(), e);
			return ErrorResponseFactory.buildErrorResponseForUpload(e.getMessage(), 200);
		} finally {
			IOUtils.closeQuietly(in);
		}
	};
	
	private boolean checkImageExtension(String path, boolean imageOnly, Set<String> imageExtensions) throws FilemanagerException {
		String imgExt = FilenameUtils.getExtension(path);
		boolean isImgExt = imageExtensions.contains(imgExt);
		if(imageOnly && !isImgExt)
			throw new FilemanagerException(FilemanagerAction.UPLOAD, FilemanagerException.Key.UploadImagesOnly);
		return isImgExt;
	}
	
	private void checkUploadSize(long maxSize, long fileSize) throws FilemanagerException {
		if(fileSize > maxSize)
			throw new FilemanagerException(FilemanagerAction.UPLOAD, FilemanagerException.Key.UploadFilesSmallerThan,
					String.valueOf(maxSize));
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
}

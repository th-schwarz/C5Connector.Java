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
import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.nio.file.StandardOpenOption;
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

import codes.thischwa.c5c.GenericConnector.StreamContent;
import codes.thischwa.c5c.exception.C5CException;
import codes.thischwa.c5c.exception.FilemanagerException;
import codes.thischwa.c5c.exception.FilemanagerException.Key;
import codes.thischwa.c5c.requestcycle.Context;
import codes.thischwa.c5c.requestcycle.RequestData;
import codes.thischwa.c5c.requestcycle.response.GenericResponse;
import codes.thischwa.c5c.requestcycle.response.mode.SaveFile;
import codes.thischwa.c5c.requestcycle.response.mode.UploadFile;
import codes.thischwa.c5c.resource.PropertiesLoader;
import codes.thischwa.c5c.resource.filemanager.FilemanagerConfig;
import codes.thischwa.c5c.resource.filemanager.Resize;
import codes.thischwa.c5c.util.FileUtils;
import codes.thischwa.c5c.util.StringUtils;

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
			switch(mode) {
			case UPLOAD: {
				boolean overwrite = conf.getUpload().isOverwrite();
				String currentPath = IOUtils.toString(req.getPart("currentpath").getInputStream());
				String backendPath = buildBackendPath(currentPath);
				Part uploadPart = req.getPart("newfile");
				String newName = getFileName(uploadPart);

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
				
				in = uploadPart.getInputStream();
				
				// save the file temporary
				Path tempPath = saveTemp(in, sanitizedName);
				logger.debug(tempPath.toAbsolutePath().toString());
				
				// pre-process the upload
				imageProcessingAndSizeCheck(tempPath, sanitizedName, uploadPart.getSize(), conf);
				
				connector.upload(backendPath, sanitizedName, new BufferedInputStream(Files.newInputStream(tempPath)));

				logger.debug("successful uploaded {} bytes", uploadPart.getSize());
				Files.delete(tempPath);
				UploadFile ufResp = new UploadFile(currentPath, sanitizedName);
				ufResp.setName(newName);
				ufResp.setPath(currentPath);
				return ufResp;
			} case REPLACE: {
//				String newFilePath = IOUtils.toString(req.getPart("newfilepath").getInputStream());
//				String backendPath = buildBackendPath(newFilePath);
//				Part uploadPart = req.getPart("fileR");
//				logger.debug("* replacefile -> urlPath: {}, backendPath: {}", newFilePath, backendPath);
//
//				// check if file already exits
//				VirtualFile vf = new VirtualFile(backendPath);
//				String fileName = vf.getName();
//				String uniqueName = getUniqueName(vf.getFolder(), fileName);
//				if(uniqueName.equals(fileName)) {
//					throw new FilemanagerException(FilemanagerAction.UPLOAD, FilemanagerException.Key.FileNotExists, backendPath);
//				}
//				
//				in = uploadPart.getInputStream();
//
//				// pre-process the upload
//				Path tempPath = preProcessUpload(backendPath, in, fileName, uploadPart.getSize(), conf);
//				
//				connector.replace(backendPath, new BufferedInputStream(Files.newInputStream(tempPath)));
//				logger.debug("successful replaced {} bytes", uploadPart.getSize());
//				VirtualFile vfUrlPath = new VirtualFile(newFilePath);
// 				return new Replace(vfUrlPath.getFolder(), vfUrlPath.getName());
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
	
	private void imageProcessingAndSizeCheck(Path tempPath, String sanitizedName, long fileSize, FilemanagerConfig conf) throws C5CException, IOException {
		Integer maxSize = (conf.getUpload().isFileSizeLimitAuto()) ? PropertiesLoader.getMaxUploadSize() : conf.getUpload().getFileSizeLimit();
		if(fileSize > maxSize.longValue() * 1024 * 1024)
			throw new FilemanagerException(FilemanagerAction.UPLOAD, FilemanagerException.Key.UploadFilesSmallerThan,
					String.valueOf(maxSize));
		String extension = FilenameUtils.getExtension(sanitizedName);

		// check image only
		boolean isImageExt = checkImageExtension(sanitizedName, conf.getUpload().isImagesOnly(), conf.getImages().getExtensions());
		if(!isImageExt)
			return;
		
		// remove exif data
		Path woExifPath = UserObjectProxy.removeExif(tempPath);
		if(!tempPath.equals(woExifPath)) {
			Files.move(woExifPath, tempPath, StandardCopyOption.REPLACE_EXISTING);
		}
		
		// check if the file is really an image
		InputStream in = new BufferedInputStream(Files.newInputStream(tempPath, StandardOpenOption.READ));
		Dimension dim = getDimension(in);
		if(isImageExt && dim == null)
				throw new FilemanagerException(FilemanagerAction.UPLOAD, FilemanagerException.Key.UploadImagesOnly);
		IOUtils.closeQuietly(in);
		
		// check if resize is enabled and fix it, if necessary 
		Resize resize = conf.getImages().getResize();
		if(resize.isEnabled() && (dim.getHeight() > resize.getMaxHeight() || dim.getWidth() > resize.getMaxWidth())) {
			logger.debug("process resize");
			StreamContent sc = connector.resize(new BufferedInputStream(Files.newInputStream(tempPath)), extension, new Dimension(resize.getMaxWidth(), resize.getMaxHeight()));
			Files.copy(sc.getInputStream(), tempPath, StandardCopyOption.REPLACE_EXISTING);
			IOUtils.closeQuietly(sc.getInputStream());
		}
	}
	
	private Path saveTemp(InputStream in, String name) throws IOException {
		String baseName = FilenameUtils.getBaseName(name);
		String ext = FilenameUtils.getExtension(name);
		try {
			Path tempPath = Files.createTempFile(UserObjectProxy.getTempDirectory(), baseName, "."+ext);
			Files.copy(in, tempPath, StandardCopyOption.REPLACE_EXISTING);
			return tempPath;
		} catch (IOException e) {
			throw e;
		} finally {
			IOUtils.closeQuietly(in);
		}
		
	}
	
	private Dimension getDimension(InputStream imageIn) {
		try {
			return UserObjectProxy.getDimension(imageIn);
		} catch (IOException e) {
			return null;
		}
	}
	
	private boolean checkImageExtension(String path, boolean imageOnly, Set<String> imageExtensions) throws FilemanagerException {
		String imgExt = FilenameUtils.getExtension(path);
		boolean isImgExt = imageExtensions.contains(imgExt);
		if(imageOnly && !isImgExt)
			throw new FilemanagerException(FilemanagerAction.UPLOAD, FilemanagerException.Key.UploadImagesOnly);
		return isImgExt;
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

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
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import codes.thischwa.c5c.exception.FilemanagerException;
import codes.thischwa.c5c.filemanager.FilemanagerConfig;
import codes.thischwa.c5c.filemanager.Icons;
import codes.thischwa.c5c.impl.FilemanagerMessageResolver;
import codes.thischwa.c5c.requestcycle.BackendPathBuilder;
import codes.thischwa.c5c.requestcycle.Context;
import codes.thischwa.c5c.requestcycle.FilemanagerCapability;
import codes.thischwa.c5c.requestcycle.FilemanagerConfigBuilder;
import codes.thischwa.c5c.requestcycle.IconRequestResolver;
import codes.thischwa.c5c.requestcycle.IconResolver;
import codes.thischwa.c5c.requestcycle.RequestData;
import codes.thischwa.c5c.util.PathBuilder;
import codes.thischwa.c5c.util.StringUtils;
import codes.thischwa.c5c.util.VirtualFile;
import codes.thischwa.c5c.util.VirtualFile.Type;
import codes.thischwa.jii.IDimensionProvider;
import codes.thischwa.jii.exception.ReadException;

/**
 * This object serves as proxy for configurable implementations of the following interfaces (user-objects):
 * <ul>
 * <li>{@link IconResolver}</li>
 * <li>{@link MessageResolver}</li>
 * <li>{@link FilemanagerCapability}</li>
 * <li>{@link BackendPathBuilder}</li>
 * <li>{@link FilemanagerConfigBuilder}</li>
 * <li>{@link IDimensionProvider}</li>
 * <li>{@link ExifRemover}</li>
 * <li>{@link DefaultConfigResolver}</li>
 * </ul>
 * To simplify the usage of these objects just wrapper methods to these user-objects are provided and not the user-objects itself. <br/>
 * A {@link RuntimeException} will be thrown if one of these implementation couldn't be instantiated.
 */
public class UserObjectProxy {
	private static final Logger logger = LoggerFactory.getLogger(UserObjectProxy.class);

	private static Pattern dimensionPattern = Pattern.compile("(\\d+)x(\\d+)");

	private static ServletContext servletContext;

	private static java.nio.file.Path tempDirectory;

	private static IconResolver iconResolver;

	private static MessageResolver messageHolder;

	private static FilemanagerCapability fileCapability;

	private static BackendPathBuilder userPathBuilder;

	private static FilemanagerConfigBuilder configBuilder;

	private static IDimensionProvider imageDimensionProvider;

	private static Dimension thumbnailDimension;

	private static Dimension previewDimension;

	private static Pattern excludeFoldersPattern;

	private static Pattern excludeFilesPattern;

	private static ExifRemover exifRemover;
	
	private static FilemanagerConfig filemanagerDefaultConfig;

	/**
	 * Instantiates all user-objects.
	 *
	 * @param servletContext
	 *            the servlet context
	 * @throws RuntimeException
	 *             is thrown, if one of the required user-objects couldn't be instantiated
	 */
	static void init(ServletContext servletContext) throws RuntimeException {
		UserObjectProxy.servletContext = servletContext;

		// try to instantiate to FileCapacity
		String className = PropertiesLoader.getFileCapabilityImpl();
		if(StringUtils.isNullOrEmpty(className))
			throw new RuntimeException("Empty FilemanagerCapability implementation class name! Depending property must be set!");
		try {
			Class<?> clazz = Class.forName(className);
			fileCapability = (FilemanagerCapability) clazz.newInstance();
			logger.info("FilemanagerCapability initialized to {}", className);
		} catch (Throwable e) {
			String msg = String.format("FilemanagerCapability implementation [%s] couldn't be instantiated.", className);
			logger.error(msg);
			throw new RuntimeException(msg, e);
		}

		// try to initialize the MessageResolver
		className = PropertiesLoader.getMessageResolverImpl();
		if(StringUtils.isNullOrEmpty(className))
			throw new RuntimeException("Empty MessageResolver implementation class name! Depending property must be set!");
		try {
			Class<?> clazz = Class.forName(className);
			messageHolder = (MessageResolver) clazz.newInstance();
			messageHolder.setServletContext(servletContext);
			logger.info("MessageResolver initialized to {}", className);
		} catch (Throwable e) {
			String msg = String.format("MessageResolver implementation [%s] couldn't be instantiated.", className);
			logger.error(msg);
			throw new RuntimeException(msg, e);
		}

		// try to initialize the BackendPathBuilder
		className = PropertiesLoader.getUserPathBuilderImpl();
		if(StringUtils.isNullOrEmpty(className))
			throw new RuntimeException("Empty BackendPathBuilder implementation class name! Depending property must be set!");
		try {
			Class<?> clazz = Class.forName(className);
			userPathBuilder = (BackendPathBuilder) clazz.newInstance();
			logger.info("BackendPathBuilder initialized to {}", className);
		} catch (Throwable e) {
			String msg = "BackendPathBuilder couldn't be initialized.";
			logger.error(msg);
			throw new RuntimeException(msg, e);
		}

		// try to initialize the FilemanagerConfigBuilder
		className = PropertiesLoader.getFilemanagerConfigImpl();
		if(StringUtils.isNullOrEmpty(className))
			throw new RuntimeException("Empty FilemanagerConfigBuilder implementation class name! Depending property must be set!");
		try {
			Class<?> clazz = Class.forName(className);
			configBuilder = (FilemanagerConfigBuilder) clazz.newInstance();
			logger.info("FilemanagerConfigBuilder initialized to {}", className);
		} catch (Throwable e) {
			String msg = "FilemanagerConfigBuilder couldn't be initialized.";
			logger.error(msg);
			throw new RuntimeException(msg, e);
		}

		// try to instantiate the IconResolver object
		className = PropertiesLoader.getIconResolverImpl();
		if(StringUtils.isNullOrEmptyOrBlank(className))
			throw new RuntimeException("Empty IconResolver implementation class name! Depending property must be set!");
		try {
			Class<?> clazz = Class.forName(className);
			iconResolver = (IconResolver) clazz.newInstance();
			iconResolver.initContext(servletContext);
			logger.info("IconResolver initialized to {}", className);
		} catch (Throwable e) {
			String msg = String.format("IconResolver implementation [%s] couldn't be instantiated.", className);
			logger.error(msg);
			throw new RuntimeException(msg, e);
		}

		// try to instantiate the DimensionProvider object
		className = PropertiesLoader.getDimensionProviderImpl();
		if(StringUtils.isNullOrEmptyOrBlank(className))
			throw new RuntimeException("Empty DimensionProvider implementation class name! Depending property must be set!");
		try {
			Class<?> clazz = Class.forName(className);
			imageDimensionProvider = (IDimensionProvider) clazz.newInstance();
			logger.info("DimensionProvider initialized to {}", className);
		} catch (Throwable e) {
			String msg = String.format("DimensionProvider implementation [%s] couldn't be instantiated.", className);
			logger.error(msg);
			throw new RuntimeException(msg, e);
		}

		// try to instantiate the ExifRemover object
		className = PropertiesLoader.getExifRemoverImpl();
		if(StringUtils.isNullOrEmptyOrBlank(className)) {
			logger.warn("Empty ExifRemover implementation class name! EXIF data won't be removed.");
			exifRemover = null;
		} else {
			try {
				Class<?> clazz = Class.forName(className);
				exifRemover = (ExifRemover) clazz.newInstance();
				logger.info("ExifRemover initialized to {}", className);
			} catch (Throwable e) {
				String msg = String.format("ExifRemover implementation [%s] couldn't be instantiated.", className);
				logger.error(msg);
				throw new RuntimeException(msg, e);
			}
		}
		
		// try to read the dimension for thumbnails
		Matcher dimMatcher = dimensionPattern.matcher(PropertiesLoader.getThumbnailDimension());
		if(dimMatcher.matches()) {
			thumbnailDimension = new Dimension(Integer.valueOf(dimMatcher.group(1)), Integer.valueOf(dimMatcher.group(2)));
		}

		// try to read the dimension for preview
		dimMatcher = dimensionPattern.matcher(PropertiesLoader.getPreviewDimension());
		if(dimMatcher.matches()) {
			previewDimension = new Dimension(Integer.valueOf(dimMatcher.group(1)), Integer.valueOf(dimMatcher.group(2)));
		}

		// fetch the temporary directory
		File tempDir = (File) UserObjectProxy.servletContext.getAttribute(ServletContext.TEMPDIR);
		if(tempDir == null) {
			String msg = "No temporary directory according to the Servlet spec SRV.3.7.1 found!";
			logger.error(msg);
			throw new RuntimeException(msg);
		}
		tempDirectory = tempDir.toPath();
		
		// try to instantiate the DefaultConfigResolver object and fetches the default configuration
		className = PropertiesLoader.getDefaultConfigResolverImpl();
		if(StringUtils.isNullOrEmptyOrBlank(className))
			throw new RuntimeException("Empty DefaultConfigResolver implementation class name! Depending property must be set!");
		try {
			Class<?> clazz = Class.forName(className);
			DefaultConfigResolver configResolver = (DefaultConfigResolver) clazz.newInstance();
			configResolver.initContext(servletContext);
			filemanagerDefaultConfig = configResolver.read();
			logger.info("Default configuration of the filemanager successful fetched from {}", className);
		} catch (Throwable e) {
			String msg = String.format("DefaultConfigResolver implementation [%s] couldn't be instantiated.", className);
			logger.error(msg);
			if(e instanceof RuntimeException)
				throw (RuntimeException) e;
			throw new RuntimeException(msg, e);
		}
		
		// build regex pattern
		String folderExcludePatternStr = PropertiesLoader.getRegexToExcludeFolders();
		if(StringUtils.isNullOrEmptyOrBlank(folderExcludePatternStr)) {
			logger.warn("Property 'connector.regex.exclude.folders' isn't set.");
			excludeFoldersPattern = null;
		} else {
			try {
				excludeFoldersPattern = Pattern.compile(folderExcludePatternStr);
			} catch (PatternSyntaxException e) {
				throw new RuntimeException("Exclude pattern for folders couldn't be compiled!");
			}
		}

		String fileExcludePatternStr = PropertiesLoader.getRegexToExcludeFiles();
		if(StringUtils.isNullOrEmptyOrBlank(fileExcludePatternStr)) {
			logger.warn("Property 'connector.regex.exclude.files' isn't set.");
			excludeFilesPattern = null;
		} else {
			try {
				excludeFilesPattern = Pattern.compile(fileExcludePatternStr);
			} catch (PatternSyntaxException e) {
				throw new RuntimeException("Exclude pattern for files couldn't be compiled!");
			}
		}
	}

	/**
	 * Retrieves the url-path of the default-icon for the desired {@link VirtualFile}.
	 * 
	 * @param vf
	 *            the {@link VirtualFile} for which to retrieve the url-path of the icon
	 * 
	 * @return the url-path of the desired {@link VirtualFile}
	 * @see IconResolver
	 */
	static String getDefaultIconPath(final VirtualFile vf) {
		Icons icons = getFilemanagerConfig().getIcons();
		PathBuilder fullIconPath = new PathBuilder(PropertiesLoader.getFilemanagerPath());
		String iconPath = fullIconPath.addFolder(icons.getPath()).toString();
		IconRequestResolver iconRequestResolver = iconResolver.initRequest(iconPath, icons.getDefaultIcon(), icons.getDirectory());
		String defaultIconPath = (vf.getType() == Type.directory) 
				? iconRequestResolver.getIconPathForDirectory(vf.isProtect()) 
						: iconRequestResolver.getIconPath(vf.getExtension(), vf.isProtect());
		return defaultIconPath;
	}

	/**
	 * Retrieves the localized and known message provided by the filemanager.
	 * 
	 * @param key
	 *            the key of the desired message
	 * 
	 * @return the localized and known error message of the filemanager
	 * @see FilemanagerMessageResolver#getMessage(java.util.Locale, codes.thischwa.c5c.exception.FilemanagerException.Key)
	 */
	public static String getFilemanagerErrorMessage(FilemanagerException.Key key) {
		return messageHolder.getMessage(RequestData.getLocale(), key);
	}

	/**
	 * Retrieves the file capabilities for the desired file.
	 * 
	 * @param filePath
	 *            the path of the file for which the capabilities have to retrieve
	 * 
	 * @return the capabilities for the desired file
	 * @see FilemanagerCapability#getCapabilities(Context)
	 */
	static FilemanagerCapability.Capability[] getC5FileCapabilities(String filePath) {
		return fileCapability.getCapabilities(RequestData.getContext());
	}

	/**
	 * Retrieves the server-side path to the desired url-path.
	 * 
	 * @param urlPath
	 *            the url-path for which to retrieve the server-side path
	 * 
	 * @return the server-side path to the desired url-path
	 * @see BackendPathBuilder#getBackendPath(String, Context, ServletContext)
	 */
	static String getBackendPath(final String urlPath) {
		return userPathBuilder.getBackendPath(urlPath, RequestData.getContext(), servletContext);
	}

	/**
	 * Retrieves the (user) {@link FilemanagerConfig}.
	 * 
	 * @param req
	 *            the {@link HttpServletRequest}
	 * 
	 * @return the {@link FilemanagerConfig} for the current request
	 * @see FilemanagerConfigBuilder#getConfig(HttpServletRequest, ServletContext)
	 */
	static FilemanagerConfig getFilemanagerUserConfig(HttpServletRequest req) {
		// we need the HttpServletRequest here because this breaks the request-cycle, see ConnctorServlet#doGet
		return configBuilder.getConfig(req, servletContext);
	}

	/**
	 * Retrieves the {@link FilemanagerConfig} based on the current {@link HttpServletRequest}. It's just a wrapper method to
	 * {@link #getFilemanagerUserConfig(HttpServletRequest)}.
	 * 
	 * @return the {@link FilemanagerConfig} for the current request
	 * @see FilemanagerConfigBuilder#getConfig(HttpServletRequest, ServletContext)
	 */
	public static FilemanagerConfig getFilemanagerConfig() {
		return getFilemanagerUserConfig(RequestData.getContext().getServletRequest());
	}

	/**
	 * Getter for the default configuration of the filemanager.
	 * 
	 * @return the default configuration of the filemanager
	 */
	public static FilemanagerConfig getFilemanagerDefaultConfig() {
		return filemanagerDefaultConfig;
	}

	/**
	 * Retrieves the {@link Dimension} of the image based on the committed 'imageIn'.
	 *
	 * @param imageIn
	 *            the {@link InputStream} of an image
	 * @return the {@link Dimension} of an image
	 * @throws IOException
	 *             if the image data couldn't be analyzed
	 */
	public static synchronized Dimension getDimension(final InputStream imageIn) throws IOException {
		InputStream tmpImageIn = null;
		try {
			// we have to use a copy of the inputstream, because same dimensionProviders uses #mark
			tmpImageIn = new BufferedInputStream(imageIn);
			imageDimensionProvider.set(tmpImageIn);
			Dimension dim = imageDimensionProvider.getDimension();
			return dim;
		} catch (UnsupportedOperationException | ReadException e) {
			throw new IOException(e);
		}
	}

	/**
	 * Getter for the adjusted thumbnail dimension.
	 * 
	 * @return the thumbnail dimension
	 */
	public static Dimension getThumbnailDimension() {
		return thumbnailDimension;
	}

	/**
	 * Getter for the adjusted preview dimension.
	 * 
	 * @return the preview dimension
	 */
	public static Dimension getPreviewDimension() {
		return previewDimension;
	}

	/**
	 * Checks if a folder is allowed to display.
	 * 
	 * @param name
	 *            the name of the folder
	 * @return <code>false</code> if no pattern was found or 'name' matches the pattern.
	 */
	public static boolean isFolderNameAllowed(final String name) {
		if(excludeFoldersPattern == null)
			return true;

		Matcher matcher = excludeFoldersPattern.matcher(name);
		return !matcher.matches();
	}

	/**
	 * Checks if a file is allowed to display.
	 * 
	 * @param name
	 *            the name of the folder
	 * @return <code>false</code> if no pattern was found or 'name' matches the pattern.
	 */
	public static boolean isFileNameAllowed(final String name) {
		if(excludeFilesPattern == null)
			return true;

		Matcher matcher = excludeFilesPattern.matcher(name);
		return !matcher.matches();
	}

	/**
	 * Getter for the temporary directory of the servlet context.
	 * 
	 * @return the temporary directory
	 */
	public static java.nio.file.Path getTempDirectory() {
		return tempDirectory;
	}

	public static java.nio.file.Path removeExif(java.nio.file.Path tempPath) {
		if(exifRemover == null)
			return tempPath;
		try {
			String fileName = tempPath.toString();
			String ext = FilenameUtils.getExtension(fileName);

			java.nio.file.Path woExifPath = Paths.get(tempPath.toString()+"_woExif");
			boolean removed = exifRemover.removeExif(Files.newInputStream(tempPath), Files.newOutputStream(woExifPath, StandardOpenOption.CREATE_NEW), ext);
			logger.debug("potential exif data removed: {}", removed);
			return (removed) ? woExifPath : tempPath;
		} catch (IOException e) {
			logger.warn("Error while removing EXIF data.", e);
			return tempPath;
		}
	}
}

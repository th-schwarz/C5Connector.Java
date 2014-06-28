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
import java.io.IOException;
import java.io.InputStream;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import codes.thischwa.c5c.exception.FilemanagerException;
import codes.thischwa.c5c.requestcycle.BackendPathBuilder;
import codes.thischwa.c5c.requestcycle.Context;
import codes.thischwa.c5c.requestcycle.FilemanagerCapability;
import codes.thischwa.c5c.requestcycle.FilemanagerConfigBuilder;
import codes.thischwa.c5c.requestcycle.IconRequestResolver;
import codes.thischwa.c5c.requestcycle.IconResolver;
import codes.thischwa.c5c.requestcycle.RequestData;
import codes.thischwa.c5c.resource.PropertiesLoader;
import codes.thischwa.c5c.resource.filemanager.FilemanagerConfig;
import codes.thischwa.c5c.resource.filemanager.Icons;
import codes.thischwa.c5c.util.Path;
import codes.thischwa.c5c.util.StringUtils;
import codes.thischwa.c5c.util.VirtualFile;
import codes.thischwa.c5c.util.VirtualFile.Type;
import de.thischwa.jii.IDimensionProvider;
import de.thischwa.jii.exception.ReadException;

/**
 * This class serves as proxy for configurable implementations of the following interfaces (user-objects):
 * <ul>
 * <li>{@link IconResolver}</li>
 * <li>{@link MessageResolver}</li>
 * <li>{@link FilemanagerCapability}</li>
 * <li>{@link BackendPathBuilder}</li> 
 * <li>{@link FilemanagerConfigBuilder}</li> 
 * </ul>
 * To simplify the usage of these objects just wrapper methods to these user-objects are provided and not the
 * user-objects itself.
 * <br/>
 * A {@link RuntimeException} will be thrown if one of these implementation couldn't be instantiated. 
 */
public class UserObjectProxy {
	private static final Logger logger = LoggerFactory.getLogger(UserObjectProxy.class);
	
	private static Pattern dimensionPattern = Pattern.compile("(\\d+)x(\\d+)");
	
	private static ServletContext servletContext;
	
	private static IconResolver iconResolver;
	
	private static MessageResolver messageHolder;
	
	private static FilemanagerCapability fileCapability;
	
	private static BackendPathBuilder userPathBuilder;
	
	private static FilemanagerConfigBuilder configBuilder;
	
	private static IDimensionProvider imageDimensionProvider;
	
	private static Dimension thumbnailDimension;

	private static Pattern excludeFoldersPattern;

	private static Pattern excludeFilesPattern;

	/**
	 * Instantiates all user-objects.
	 *
	 * @param servletContext the servlet context
	 * @throws RuntimeException is thrown, if one of the required user-objects couldn't be instantiated
	 */
	static void init(ServletContext servletContext) throws RuntimeException {
		UserObjectProxy.servletContext = servletContext;

		// 1. try to instantiate to FileCapacity
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
		
		// 2. try to initialize the MessageResolver
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
		
		// 3. try to initialize the BackendPathBuilder
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

		// 4. try to initialize the FilemanagerConfigBuilder
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

		// 5. try to instantiate the IconResolver object
		className = PropertiesLoader.getIconResolverImpl();
		if (StringUtils.isNullOrEmptyOrBlank(className))
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
		
		// 6. try to instantiate the DimensionProvider object 
		className = PropertiesLoader.getDimensionProviderImpl();
		if (StringUtils.isNullOrEmptyOrBlank(className))
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
		
		// 7. try to read the dimension for thumbnails
		Matcher dimMatcher = dimensionPattern.matcher(PropertiesLoader.getThumbnailDimension());
		if(dimMatcher.matches()) {
			thumbnailDimension = new Dimension(Integer.valueOf(dimMatcher.group(1)), Integer.valueOf(dimMatcher.group(2)));
		}

		
		// 8. build regex pattern
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
	 * @param vf the {@link VirtualFile} for which to retrieve the url-path of the icon
	 * 
	 * @return the url-path of the desired {@link VirtualFile}
	 * @see IconResolver
	 */
	static String getDefaultIconPath(final VirtualFile vf) {
		Icons icons = getFilemanagerConfig().getIcons();
		Path fullIconPath = new Path(PropertiesLoader.getFilemanagerPath());
		String iconPath = fullIconPath.addFolder(icons.getPath()).toString();
		IconRequestResolver iconRequestResolver = iconResolver.initRequest(iconPath, icons.getDefaultIcon(), icons.getDirectory());
		String defaultIconPath = (vf.getType() == Type.directory) ? iconRequestResolver.getIconPathForDirectory() : iconRequestResolver.getIconPath(vf.getExtension());
		return defaultIconPath;
	}
	
	/**
	 * Retrieves the localized and known message provided by the filemanager. 
	 * @param key the key of the desired message
	 * 
	 * @return the localized and known error message of the filemanager
	 * @see FilemanagerMessageResolver#getMessage(java.util.Locale, codes.thischwa.c5c.exception.FilemanagerException.Key)
	 */
	public static String getFilemanagerErrorMessage(FilemanagerException.Key key) {
		return messageHolder.getMessage(RequestData.getLocale(), key);
	}

	/**
	 * Retrieves the file capabilities for the desired file.
	 * @param filePath the path of the file for which the capabilities have to retrieve
	 * 
	 * @return the capabilities for the desired file
	 * @see FilemanagerCapability#getCapabilities(Context)
	 */
	static FilemanagerCapability.Capability[] getC5FileCapabilities(String filePath) {
		return fileCapability.getCapabilities(RequestData.getContext());
	}

	/**
	 * Retrieves the server-side path to the desired url-path.
	 * @param urlPath the url-path for which to retrieve the server-side path
	 * 
	 * @return the server-side path to the desired url-path
	 * @see BackendPathBuilder#getBackendPath(String, Context, ServletContext)
	 */
	static String getBackendPath(final String urlPath) {
		return userPathBuilder.getBackendPath(urlPath, RequestData.getContext(), servletContext);
	}
	
	/**
	 * Retrieves the {@link FilemanagerConfig}.
	 * @param req the {@link HttpServletRequest}
	 * 
	 * @return the {@link FilemanagerConfig} for the current request
	 * @see FilemanagerConfigBuilder#getConfig(HttpServletRequest, ServletContext)
	 */
	static FilemanagerConfig getFilemanagerConfig(HttpServletRequest req) {
		// we need the HttpServletRequest here because this breaks the request-cycle, see ConnctorServlet#doGet
		return configBuilder.getConfig(req, servletContext);
	}
	
	/**
	 * Retrieves the {@link FilemanagerConfig} based on the current {@link HttpServletRequest}.
	 * It's just a wrapper method to {@link #getFilemanagerConfig(HttpServletRequest)}.
	 *  
	 * @return the {@link FilemanagerConfig} for the current request
	 * @see FilemanagerConfigBuilder#getConfig(HttpServletRequest, ServletContext)
	 */
	public static FilemanagerConfig getFilemanagerConfig() {
		return getFilemanagerConfig(RequestData.getContext().getServletRequest());
	}
	
	/**
	 * Retrieves the {@link Dimension} of the image based on the committed 'imageIn'.
	 *
	 * @param imageIn the {@link InputStream} of an image
	 * @return the {@link Dimension} of an image
	 * @throws IOException if the image data couldn't be analyzed
	 */
	public static synchronized Dimension getDimension(final InputStream imageIn) throws IOException {
		try {
			imageIn.mark(0);
			imageDimensionProvider.set(imageIn);
			Dimension dim = imageDimensionProvider.getDimension();
			imageIn.reset();
			return dim;
		} catch (UnsupportedOperationException | ReadException e) {
			throw new IOException(e);
		}
	}
	
	/**
	 * Checks if a file is an image.
	 * 
	 * @param imageIn
	 *            {@link InputStream} of the underlying file, it will be reseted!
	 * @return <code>true</code> if the file is really an image, otherwise <code>false</code>
	 */
	public static boolean isImage(final InputStream imageIn) {
		try {
			getDimension(imageIn);
			return true;
		} catch (IOException e) {
			return false;
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
	 * Checks if a folder is allowed to display.
	 * 
	 * @param name the name of the folder
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
	 * @param name the name of the folder
	 * @return <code>false</code> if no pattern was found or 'name' matches the pattern.
	 */
	public static boolean isFileNameAllowed(final String name) {
		if(excludeFilesPattern == null)
			return true;
		
		Matcher matcher = excludeFilesPattern.matcher(name);
		return !matcher.matches();
	}
}

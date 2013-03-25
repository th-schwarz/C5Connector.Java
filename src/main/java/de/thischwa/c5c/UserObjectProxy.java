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
package de.thischwa.c5c;

import javax.servlet.ServletContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.thischwa.c5c.exception.FilemanagerException;
import de.thischwa.c5c.requestcycle.Context;
import de.thischwa.c5c.requestcycle.FilemanagerCapability;
import de.thischwa.c5c.requestcycle.IconResolver;
import de.thischwa.c5c.requestcycle.RequestData;
import de.thischwa.c5c.requestcycle.UserAction;
import de.thischwa.c5c.requestcycle.UserPathBuilder;
import de.thischwa.c5c.resource.PropertiesLoader;
import de.thischwa.c5c.util.StringUtils;
import de.thischwa.c5c.util.VirtualFile;

/**
 * This class serves as proxy for configurable implementations of the following interfaces (user-objects):
 * <ul>
 * <li>{@link UserAction}</li>
 * <li>{@link IconResolver}</li>
 * <li>{@link FilemanagerMessageHolder}</li>
 * <li>{@link FilemanagerCapability}</li>
 * <li>{@link UserPathBuilder}</li> 
 * </ul>
 * To simplify the usage of these objects just wrapper methods to these user-objects are provided and not the
 * user-objects itself.
 * <br/>
 * A {@link RuntimeException} will be thrown if one of these implementation couldn't be instantiated. 
 */
public class UserObjectProxy {
	private static final Logger logger = LoggerFactory.getLogger(UserObjectProxy.class);
	
	private static ServletContext servletContext;
	
	private static UserAction userAction;
	
	private static IconResolver iconResolver;
	
	private static FilemanagerMessageHolder c5messageHolder;
	
	private static FilemanagerCapability fileCapability;
	
	private static UserPathBuilder userPathBuilder;

	/**
	 * Instantiates all user-objects.
	 *
	 * @param servletContext the servlet context
	 * @throws RuntimeException is thrown, if one of the required user-objects couldn't be instantiated
	 */
	static void init(ServletContext servletContext) throws RuntimeException {
		UserObjectProxy.servletContext = servletContext;
		
		// 1. try to instantiate the UserAction object
		String className = PropertiesLoader.getUserActionImpl();
		if (StringUtils.isNullOrEmptyOrBlank(className))
			throw new RuntimeException("Empty UserAction implementation class name. Depending property must be set!");
		else {
			try {
				Class<?> clazz = Class.forName(className);
				userAction = (UserAction) clazz.newInstance();
				logger.info("UserAction initialized to {}", className);
			} catch (Throwable e) {
				String msg = String.format("UserAction implementation [%s] couldn't be instantiated.", className);
				logger.error(msg);
				throw new RuntimeException(msg, e);
			}
		}
		
		// 2. try to instantiate the IconResolver object
		className = PropertiesLoader.getIconResolverImpl();
		if (StringUtils.isNullOrEmptyOrBlank(className))
			throw new RuntimeException("Empty IconResolver implementation class name! Depending property must be set!");
		try {
			Class<?> clazz = Class.forName(className);
			iconResolver = (IconResolver) clazz.newInstance();
			iconResolver.setServletContext(servletContext);
			logger.info("IconResolver initialized to {}", className);
		} catch (Throwable e) {
			String msg = String.format("IconResolver implementation [%s] couldn't be instantiated.", className);
			logger.error(msg);
			throw new RuntimeException(msg, e);
		}

		// 3. try to instantiate to FileCapacity
		className = PropertiesLoader.getFileCapabilityImpl();
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
		
		// 4. try to initialize the MessagesHolder
		try {
			c5messageHolder = new FilemanagerMessageHolder(servletContext);
		} catch (Throwable e) {
			String msg = "MessagesHolder couldn't be initialized.";
			logger.error(msg);
			throw new RuntimeException(msg, e);
		}
		
		// 5. try to initialize the UserPathBuilder
		className = PropertiesLoader.getUserPathBuilderImpl();
		if(StringUtils.isNullOrEmpty(className))
			throw new RuntimeException("Empty UserPathBuilder implementation class name! Depending property must be set!");
		try {
			Class<?> clazz = Class.forName(className);
			userPathBuilder = (UserPathBuilder) clazz.newInstance();
			logger.info("UserPathBuilder initialized to {}", className);
		} catch (Throwable e) {
			String msg = "UserPathBuilder couldn't be initialized.";
			logger.error(msg);
			throw new RuntimeException(msg, e);
		}
	}

	/**
	 * Retrieves whether the current user is allowed to upload files. 
	 * 
	 * @return true if user is allowed to upload files, false otherwise
	 * @see UserAction#isFileUploadEnabled(Context)
	 */
	public static boolean isFileUploadEnabled() {
		return userAction.isFileUploadEnabled(RequestData.getContext());
	}

	/**
	 * Retrieves whether the current user is allowed to create folders. 
	 * 
	 * @return true if user is allowed to create folders, otherwise false
	 * @see UserAction#isCreateFolderEnabled(Context)
	 */
	public static boolean isCreateFolderEnabled() {
		return userAction.isCreateFolderEnabled(RequestData.getContext());
	}
	
	/**
	 * Retrieves the url-path of the icon for the desired {@link VirtualFile}.
	 * @param vf the {@link VirtualFile} for which to retrieve the url-path of the icon
	 * 
	 * @return the url-path of the desired {@link VirtualFile}
	 * @see IconResolver#getIconPath(VirtualFile)
	 */
	static String getIconPath(final VirtualFile vf) {
		return iconResolver.getIconPath(vf);
	}
	
	/**
	 * Retrieves the localized and known message provided by the filemanager. 
	 * @param key the key of the desired message
	 * 
	 * @return the localized and known error message of the filemanager
	 * @see FilemanagerMessageHolder#getMessage(java.util.Locale, de.thischwa.c5c.exception.FilemanagerException.Key)
	 */
	public static String getFilemanagerErrorMessage(FilemanagerException.Key key) {
		return c5messageHolder.getMessage(RequestData.getLocale(), key);
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
	 * @see UserPathBuilder#getServerPath(String, Context, ServletContext)
	 */
	public static String getUserPath(final String urlPath) {
		return userPathBuilder.getServerPath(urlPath, RequestData.getContext(), servletContext);
	}
}

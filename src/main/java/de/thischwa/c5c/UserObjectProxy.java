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

import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.thischwa.c5c.exception.FilemanagerException;
import de.thischwa.c5c.requestcycle.FilemanagerCapability;
import de.thischwa.c5c.requestcycle.UserAction;
import de.thischwa.c5c.requestcycle.IconResolver;
import de.thischwa.c5c.requestcycle.RequestData;
import de.thischwa.c5c.resource.PropertiesLoader;
import de.thischwa.c5c.util.StringUtils;
import de.thischwa.c5c.util.VirtualFile;

/**
 * A proxy for the implementations of {@link UserAction}, {@link IconResolver}, {@link FilemanagerMessageHolder} 
 * and {@link FilemanagerCapability} interfaces. Wrapper methods for these objects are provided.
 * <br/>
 * A {@link RuntimeException} is thrown if one of these implementation couldn't be instantiated. 
 */
public class UserObjectProxy {
	private static final Logger logger = LoggerFactory.getLogger(UserObjectProxy.class);
	
	private static UserAction userAction;
	
	private static IconResolver iconResolver;
	
	private static FilemanagerMessageHolder c5messageHolder;
	
	private static FilemanagerCapability fileCapability;
	
	private static FilemanagerCapability.Capability[] defaultC5FileCapability;

	/**
	 * Initialization.
	 *
	 * @param servletContext the servlet context
	 * @throws RuntimeException Is thrown, if one required objects couldn't be initialized.
	 */
	static void init(ServletContext servletContext) throws RuntimeException {
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
		
		// 5. build the default c5FileCapabilties if exists
		String capabilitiesStr = PropertiesLoader.getDefaultCapacity();
		defaultC5FileCapability = buildDefaultCapabilities(capabilitiesStr);
	}

	/**
	 * Returns <code>true</code> if user is allowed to upload files. The behavior is specified by the current UserAction instance.
	 * 
	 * @return true if user is allowed to upload files, false otherwise
	 * @see UserAction#isFileUploadEnabled(HttpServletRequest)
	 */
	public static boolean isFileUploadEnabled() {
		return userAction.isFileUploadEnabled(RequestData.getContext().getServletRequest());
	}

	/**
	 * Returns <code>true</code> if user is allowed to create folders. The behavior is specified by the current UserAction instance.
	 * 
	 * @return true if user is allowed to create folders, false otherwise
	 * @see UserAction#isCreateFolderEnabled(HttpServletRequest)
	 */
	public static boolean isCreateFolderEnabled() {
		return userAction.isCreateFolderEnabled(RequestData.getContext().getServletRequest());
	}
	
	public static String getIconPath(final VirtualFile vf) {
		return iconResolver.getIconPath(vf);
	}
	
	public static String getFilemanagerErrorMessage(FilemanagerException.Key key) {
		return c5messageHolder.getMessage(RequestData.getLocale(), key);
	}

	public static FilemanagerCapability.Capability[] getC5FileCapabilities(String filePath) {
		return fileCapability.getCapabilities(RequestData.getContext().getServletRequest(), filePath);
	}

	public static FilemanagerCapability.Capability[] getDefaultC5FileCapabilities() {
		return defaultC5FileCapability;
	}
	
	/**
	 * Builds the default capabilities.
	 */
	static FilemanagerCapability.Capability[] buildDefaultCapabilities(String capabilitiesStr) {
		if(StringUtils.isNullOrEmptyOrBlank(capabilitiesStr)) 
			return null;
		
		String[] caps = capabilitiesStr.split(",");
		List<FilemanagerCapability.Capability> capList = new ArrayList<FilemanagerCapability.Capability>(caps.length);
		for (String cap : caps) {
			FilemanagerCapability.Capability capability = FilemanagerCapability.Capability.valueOf(cap.trim().toLowerCase());
			if(capability == null) {
				logger.warn("Couldn't interprete [{}] as FilemanagerCapability!", cap);
			} else {
				capList.add(capability);
			}
		}
		return (capList.isEmpty()) ? null : capList.toArray(new FilemanagerCapability.Capability[capList.size()]);
	}
}

/*
 * C5Connector.Java - The Java backend for the filemanager of corefive.
 * It provides a simple object for creating an editor instance.
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

import de.thischwa.c5c.requestcycle.C5FileCapability;
import de.thischwa.c5c.requestcycle.C5MessageHolder;
import de.thischwa.c5c.requestcycle.C5UserAction;
import de.thischwa.c5c.requestcycle.IconResolver;
import de.thischwa.c5c.requestcycle.RequestData;
import de.thischwa.c5c.resource.PropertiesLoader;
import de.thischwa.c5c.util.StringUtils;
import de.thischwa.c5c.util.VirtualFile;

/**
 * A proxy for the implementations of {@link C5UserAction}, {@link IconResolver}, {@link C5MessageHolder} 
 * and {@link C5FileCapability} interfaces.
 * A {@link RuntimeException} is thrown if one of these implementation couldn't be instantiated. 
 */
public class UserObjectProxy {
	private static final Logger logger = LoggerFactory.getLogger(UserObjectProxy.class);
	
	private static C5UserAction userAction;
	
	private static IconResolver iconResolver;
	
	private static C5MessageHolder c5messageHolder;
	
	private static C5FileCapability fileCapability;
	
	private static C5FileCapability.CAPABILITY[] defaultC5FileCapability;

	/**
	 * Initialization of the {@link UserObjectProxy}.
	 *
	 * @param servletContext the servlet context
	 */
	static void init(ServletContext servletContext) {
		// 1. try to instantiate the C5UserAction object
		String className = PropertiesLoader.getUserActionImpl();
		if (StringUtils.isNullOrEmptyOrBlank(className))
			throw new RuntimeException("Empty C5UserAction implementation class name. Depending property must be set!");
		else {
			try {
				Class<?> clazz = Class.forName(className);
				userAction = (C5UserAction) clazz.newInstance();
				logger.info("C5UserAction initialized to {}", className);
			} catch (Throwable e) {
				String msg = String.format("C5UserAction implementation [%s] couldn't be instantiated.", className);
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
			throw new RuntimeException("Empty C5FileCapability implementation class name! Depending property must be set!");
		try {
			Class<?> clazz = Class.forName(className);
			fileCapability = (C5FileCapability) clazz.newInstance();
		} catch (Throwable e) {
			String msg = String.format("C5FileCapability implementation [%s] couldn't be instantiated.", className);
			logger.error(msg);
			throw new RuntimeException(msg, e);
		}
		
		// 4. try to initialize the MessagesHolder
		try {
			c5messageHolder = new C5MessageHolder(servletContext);
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
	 * Returns <code>true</code> if user is allowed to upload files. The behavior is specified by the current C5UserAction instance.
	 * 
	 * @return true if user is allowed to upload files, false otherwise
	 * @see C5UserAction#isFileUploadEnabled(HttpServletRequest)
	 */
	public static boolean isFileUploadEnabled() {
		return userAction.isFileUploadEnabled(RequestData.getRequest());
	}

	/**
	 * Returns <code>true</code> if user is allowed to create folders. The behavior is specified by the current C5UserAction instance.
	 * 
	 * @return true if user is allowed to create folders, false otherwise
	 * @see C5UserAction#isCreateFolderEnabled(HttpServletRequest)
	 */
	public static boolean isCreateFolderEnabled() {
		return userAction.isCreateFolderEnabled(RequestData.getRequest());
	}
	
	public static String getIconPath(final VirtualFile vf) {
		return iconResolver.getIconPath(vf);
	}
	
	public static String getFilemanagerErrorMessage(String key) {
		return c5messageHolder.getMessage(RequestData.getLocale(), key);
	}

	public static C5FileCapability.CAPABILITY[] getC5FileCapabilities(String filePath) {
		return fileCapability.getCapabilities(RequestData.getRequest(), filePath);
	}

	public static C5FileCapability.CAPABILITY[] getDefaultC5FileCapabilities() {
		return defaultC5FileCapability;
	}
	
	/**
	 * Builds the default capabilities.
	 */
	static C5FileCapability.CAPABILITY[] buildDefaultCapabilities(String capabilitiesStr) {
		if(StringUtils.isNullOrEmptyOrBlank(capabilitiesStr)) 
			return null;
		
		String[] caps = capabilitiesStr.split(",");
		List<C5FileCapability.CAPABILITY> capList = new ArrayList<C5FileCapability.CAPABILITY>(caps.length);
		for (String cap : caps) {
			C5FileCapability.CAPABILITY capability = C5FileCapability.CAPABILITY.valueOf(cap.trim().toLowerCase());
			if(capability == null) {
				logger.warn("Couldn't interprete [{}] as C5FileCapability!", cap);
			} else {
				capList.add(capability);
			}
		}
		return (capList.isEmpty()) ? null : capList.toArray(new C5FileCapability.CAPABILITY[capList.size()]);
	}
}

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
package codes.thischwa.c5c.requestcycle;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import codes.thischwa.c5c.FilemanagerAction;
import codes.thischwa.c5c.UserObjectProxy;
import codes.thischwa.c5c.exception.C5CException;
import codes.thischwa.c5c.exception.FilemanagerException;

/**
 * Maintains the base request parameter.
 */
public class Context {
	private static Logger logger = LoggerFactory.getLogger(Context.class);

	/** action of the filemanager */
	private FilemanagerAction mode;
	
	/** the given path from url pram */
	private String urlPath;

	private HttpServletRequest servletRequest;

	/**
	 * Initializes the base parameters.
	 * 
	 * @param servletRequest
	 * 
	 * @throws C5CException thrown if the parameter 'mode' couldn't be resolved
	 */
	Context(HttpServletRequest servletRequest) throws C5CException {
		this.servletRequest = servletRequest;
		urlPath = servletRequest.getParameter("path");
		String paramMode = servletRequest.getParameter("mode");
		if(paramMode == null && servletRequest.getMethod().equals("POST")) {
			try {
				paramMode = IOUtils.toString(servletRequest.getPart("mode").getInputStream());
			} catch (Exception e) {
				logger.error("Couldn't retrieve the 'mode' parameter from multipart.");
				throw new C5CException(UserObjectProxy.getFilemanagerErrorMessage(FilemanagerException.Key.ModeError));
			}
		} else {
			paramMode = servletRequest.getParameter("mode");
		}
		try {
			mode = FilemanagerAction.valueOfIgnoreCase(paramMode);
		} catch (IllegalArgumentException e) {
			logger.error("Unknown 'mode': {}", paramMode);
			throw new C5CException(UserObjectProxy.getFilemanagerErrorMessage(FilemanagerException.Key.ModeError));
		}
	}
	
	/**
	 * Gets the mode (the action of the filemanager).
	 * 
	 * @return the mode
	 */
	public FilemanagerAction getMode() {
		return mode;
	}
	
	/**
	 * Gets the 'path' parameter from the url.
	 * 
	 * @return the parameter 'path' from the url
	 */
	public String getUrlPath() {
		return urlPath;
	}
	
	/**
	 * Gets the {@link HttpServletRequest}.
	 * 
	 * @return the servlet request
	 */
	public HttpServletRequest getServletRequest() {
		return servletRequest;
	}
}

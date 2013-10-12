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
 *  - GNU Lesser General Public License Version 3.0 or later (the "LGPL")
 *    http://www.gnu.org/licenses/lgpl-3.0.html
 * 
 *  - Mozilla Public License Version 2.0 or later (the "MPL")
 *    http://www.mozilla.org/MPL/2.0/
 * 
 * == END LICENSE ==
 */
package de.thischwa.c5c;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;

import de.thischwa.c5c.resource.PropertiesLoader;
import de.thischwa.c5c.resource.filemanager.FilemanagerConfiguration;

/**
 * FilemanagerConfigurationServlet.java - TODO DOCUMENTME!
 */
public class FilemanagerConfigurationServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;

	private static Logger logger = LoggerFactory.getLogger(FilemanagerConfigurationServlet.class);

	@Override
	public void init(ServletConfig config) throws ServletException {
		String configPath = config.getInitParameter("path");
		if(configPath == null) 
			throw new IllegalArgumentException("Init-param 'path' isn't set!");
		InputStream confIn = null;
		File confFile = new File(config.getServletContext().getRealPath(configPath));
		try {
			confIn = new BufferedInputStream(new FileInputStream(confFile));
			FilemanagerConfiguration.init(confIn);
		} catch (Exception e) {
			
		} finally {
			IOUtils.closeQuietly(confIn);
		}
		logger.info("*** {} sucessful initialized with path: {}", this.getClass().getName(), configPath);
		
	}
	
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		resp.setCharacterEncoding(PropertiesLoader.getDefaultEncoding());
		resp.setContentType("application/json");
		
		logger.debug("**** DOGET");
		if(!req.getServletPath().contains("filemanager.config.js"))
			logger.error("FATAL config-error");

		ObjectMapper mapper = new ObjectMapper();
		try {
			mapper.writeValue(resp.getOutputStream(), FilemanagerConfiguration.getConfiguration());
		} catch (Exception e) {
			throw new RuntimeException(e);
		} finally {
			IOUtils.closeQuietly(resp.getOutputStream());
		}
	}
}

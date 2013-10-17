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
 * Servlet to serve the content of the {@link FilemanagerConfiguration}. So it's possible to change the
 * configuration of the filemanager without changing any javascript-config-file.<br/>
 * To register it in the web.xml the following entries should be used:
 * <pre> {@code
 * <servlet>
 *    <servlet-name>FilemanagerConfigurationServlet</servlet-name>
 *    <servlet-class>de.thischwa.c5c.FilemanagerConfigurationServlet</servlet-class>
 *    <load-on-startup>0</load-on-startup>
 *    <init-param>
 *       <param-name>default</param-name>
 *       <param-value>/path/fm.config.js</param-value>
 *    </init-param>
 *    <servlet-mapping>
 *       <servlet-name>FilemanagerConfigurationServlet</servlet-name>
 *       <url-pattern>/filemanager/scripts/filemanager.config.js</url-pattern>
 *    </servlet-mapping>
 * </servlet>
 * }</pre>
 * The init-param <i>default</i> is the JSON-config-file to load the default settings. But it isn't necessary. 
 * If it is set, the defaults of the {@link FilemanagerConfiguration} will be overridden.
 */
public class FilemanagerConfigurationServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;

	private static Logger logger = LoggerFactory.getLogger(FilemanagerConfigurationServlet.class);

	@Override
	public void init(ServletConfig config) throws ServletException {
		String defaultPath = config.getInitParameter("default");
		if (defaultPath != null) {
			InputStream confIn = null;
			File confFile = new File(config.getServletContext().getRealPath(defaultPath));
			try {
				confIn = new BufferedInputStream(new FileInputStream(confFile));
				FilemanagerConfiguration.init(confIn);
			} catch (Exception e) {
				throw new ServletException(e);
			}
			logger.info("*** {} sucessful initialized with the config-file: {}", this.getClass().getName(), defaultPath);
		} else
			logger.info("*** {} sucessful initialized", this.getClass().getName());
	}

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		if (!req.getServletPath().contains("filemanager.config.js")) {
			logger.error("FATAL config-request-error");
			return;
		}
		resp.setCharacterEncoding(PropertiesLoader.getDefaultEncoding());
		resp.setContentType("application/json");

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

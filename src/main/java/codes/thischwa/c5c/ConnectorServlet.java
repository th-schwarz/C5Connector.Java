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

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import codes.thischwa.c5c.requestcycle.RequestData;
import codes.thischwa.c5c.requestcycle.response.GenericResponse;
import codes.thischwa.c5c.util.StringUtils;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * <b>The</b> connector servlet of the filemanager. <br/>
 * To register it in the web.xml the following entries should be used:
 * 
 * <pre>
 * {@code
 * <servlet>
 * 	<servlet-name>ConnectorServlet</servlet-name>
 * 	<servlet-class>codes.thischwa.c5c.ConnectorServlet</servlet-class>
 * 	<load-on-startup>1</load-on-startup>
 * </servlet>
 * 
 * <servlet-mapping>
 * 	<servlet-name>ConnectorServlet</servlet-name>
 * 	<url-pattern>/filemanager/connectors/java/*</url-pattern> 	
 * </servlet-mapping>
 * 
 * <servlet-mapping>
 * 	<servlet-name>ConnectorServlet</servlet-name>
 * 	<url-pattern>/filemanager/scripts/filemanager.config.js</url-pattern> 	
 * </servlet-mapping>
 * <servlet-mapping>
 * 	<servlet-name>ConnectorServlet</servlet-name>
 * 	<url-pattern>/filemanager/scripts/filemanager.config.js.default</url-pattern> 	
 * </servlet-mapping>
 * }
 * </pre>
 * 
 * Assuming the filemanager is installed in the <code>/filemanager</code> folder in your webapp.<br/>
 * IMPORTANT: The 2nd and 3rd servlet-mapping is necessary only if this servlet should 'serve' the dynamic 
 * configuration of the filemanager.
 */
@MultipartConfig
public class ConnectorServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;
	
	private static final String propertyKey_connectorImpl = "connector.impl";

	private static Logger logger = LoggerFactory.getLogger(ConnectorServlet.class);

	private GenericDispatcher dispatcherGET;
	private GenericDispatcher dispatcherPUT;

	/**
	 * Initializes this servlet. It initializes the {@link DispatcherGET} and {@link UserObjectProxy}.
	 */
	@Override
	public void init() throws ServletException {
		String connectorClassName = PropertiesLoader.getProperty(propertyKey_connectorImpl);
		if(StringUtils.isNullOrEmpty(connectorClassName))
			throw new RuntimeException("Empty Connector implementation class name not allowed.");
		Connector connector;
		try {
			Class<?> clazz = Class.forName(connectorClassName);
			connector = (Connector) clazz.newInstance();
			logger.info("Connector instantiated to {}", connectorClassName);
		} catch (Throwable e) {
			String msg = String.format("Connector implementation [%s] couldn't be instatiated.", connectorClassName);
			logger.error(msg);
			throw new RuntimeException(msg, e);
		}
		
		try {
			UserObjectProxy.init(getServletContext());
		} catch (Exception e) {
			logger.error("UserObjectProxy could not be initialized.", e);
			throw new ServletException(e);
		}

		connector.init();
		
		dispatcherGET = new DispatcherGET(connector);
		dispatcherPUT = new DispatcherPUT(connector);
		
		logger.info(String.format("*** %s sucessful initialized.", this.getClass().getName()));
	}

	private void initResponseHeader(HttpServletResponse resp) {
		// set some default headers 
		resp.setHeader("Cache-Control", "no-cache");
		resp.setContentType("application/json");
		if(!StringUtils.isNullOrEmpty(PropertiesLoader.getConnectorDefaultEncoding()))
			resp.setCharacterEncoding(PropertiesLoader.getConnectorDefaultEncoding());
	}
	
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		initResponseHeader(resp);
		
		if(req.getServletPath().contains("filemanager.config.js")) {
			// this breaks the request-cycle of this library
			// but otherwise an extra servlet is needed to serve the config of the filemanager 
			logger.debug("Filemanager config request.");
			
			ObjectMapper mapper = new ObjectMapper();
			try {
				mapper.writeValue(resp.getOutputStream(), UserObjectProxy.getFilemanagerConfig(req)); 
			} catch (Exception e) {
				logger.error("Handling of 'filemanager.config.js' failed.", e);
				throw new RuntimeException(e);
			} finally {
				IOUtils.closeQuietly(resp.getOutputStream());
			}
			return;
		}
		
		doRequest(req, resp, dispatcherGET);
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		initResponseHeader(resp);
		doRequest(req, resp, dispatcherPUT);
	}
	
	private void doRequest(HttpServletRequest req, HttpServletResponse resp, GenericDispatcher dispatcher) throws ServletException {
		try {
			RequestData.beginRequest(req);
			GenericResponse response = dispatcher.doRequest();
			response.write(resp);
		} catch (Exception e) {
			throw new ServletException(e);
		} finally {
			/*
			 * call this method to prevent detached requests or else the request will probably never be garbage collected and will fill your
			 * memory
			 */
			RequestData.endRequest();
		}
	}
}

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

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;

import de.thischwa.c5c.requestcycle.RequestData;
import de.thischwa.c5c.requestcycle.response.GenericResponse;
import de.thischwa.c5c.resource.PropertiesLoader;
import de.thischwa.c5c.util.StringUtils;

/**
 * <b>The</b> connector servlet of the filemanager. <br/>
 * To register it in the web.xml the following entries should be used:
 * 
 * <pre>
 * {@code
 * <servlet>
 * 	<servlet-name>ConnectorServlet</servlet-name>
 * 	<servlet-class>de.thischwa.c5c.ConnectorServlet</servlet-class>
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
 * }
 * </pre>
 * 
 * Assuming the filemanager is installed in the <code>/filemanager</code> folder in your webapp.<br/>
 * IMPORTANT: The 2nd servlet-mapping will be necessary only if this servlet should 'serve' the dynamic 
 * configuration of the filemanager.
 */
@MultipartConfig
public class ConnectorServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;

	private static Logger logger = LoggerFactory.getLogger(ConnectorServlet.class);

	private Dispatcher dispatcher;

	/**
	 * Initializes this servlet. It initializes the {@link Dispatcher} and {@link UserObjectProxy}.
	 */
	@Override
	public void init() throws ServletException {
		try {
			dispatcher = new Dispatcher(getServletContext(), PropertiesLoader.getConnectorImpl());
		} catch (Exception e) {
			logger.error("Dispatcher could not be instantiated.", e);
			throw new ServletException(e);
		}

		try {
			UserObjectProxy.init(getServletContext());
		} catch (Exception e) {
			logger.error("UserObjectProxy could not be initialized.", e);
			throw new ServletException(e);
		}

		logger.info(String.format("*** %s sucessful initialized.", this.getClass().getName()));
	}

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		doRequest(req, resp, true);
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		doRequest(req, resp, false);
	}

	/**
	 * Processes the request and dispatch it to the {@link Dispatcher}. It initializes the {@link RequestData} too, which maintenances
	 * request based objects.
	 * 
	 * @param req
	 * @param resp
	 * @param isGetRequest
	 * @throws ServletException
	 * @throws IOException
	 */
	private void doRequest(HttpServletRequest req, HttpServletResponse resp, boolean isGetRequest) throws ServletException, IOException {
		// set some default headers 
		resp.setHeader("Cache-Control", "no-cache");
		resp.setContentType("application/json");
		if(!StringUtils.isNullOrEmpty(PropertiesLoader.getConnectorDefaultEncoding()))
			resp.setCharacterEncoding(PropertiesLoader.getConnectorDefaultEncoding());

		if(isGetRequest && req.getServletPath().contains("filemanager.config.js")) {
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
		
		GenericResponse response;
		try {
			RequestData.beginRequest(req);
			if (isGetRequest) {
				response = dispatcher.doGet();
			} else {
				response = dispatcher.doPost();
			}
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

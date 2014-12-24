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

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import codes.thischwa.c5c.filemanager.FilemanagerConfig;
import codes.thischwa.c5c.requestcycle.FilemanagerConfigBuilder;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Filter for the configuration files of the filemanager.<br/>
 * This enables the request-based (user-based) configuration. The implementation of
 * {@link FilemanagerConfigBuilder} will be used to build the configuration.<br/>
 * <br/>
 * <strong>Important:</strong> It must be ensured, that this filter will be initialized
 * *after* the {@link ConnectorServlet} (see the 'load-on-startup' tag). <br/>
 * <br/>
 * To register it in the web.xml the following entries should be used:
 * 
 * <pre>
 * {@code
 * <filter>
 * 	<filter-name>ConfigFilter</filter-name>
 * 	<filter-class>codes.thischwa.c5c.FilemanagerConfigFilter</filter-class>
 * 	<load-on-startup>2</load-on-startup>
 * </filter>
 * <filter-mapping>
 * 	<filter-name>ConfigFilter</filter-name>
 * 	<url-pattern>/filemanager/scripts/*</url-pattern>
 * </filter-mapping>
 * }
 * </pre>
 */
public class FilemanagerConfigFilter implements Filter {
	private static Logger logger = LoggerFactory.getLogger(FilemanagerConfigFilter.class);

	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
		logger.info(String.format("*** %s sucessful initialized.", this.getClass().getName()));
	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
		HttpServletRequest req = (HttpServletRequest)request;
		HttpServletResponse resp = (HttpServletResponse)response;
		String path = req.getServletPath();
		if(path.contains("filemanager.config.js")) {
			// set some default headers 
			ConnectorServlet.initResponseHeader(resp);
			logger.debug("Filemanager config request: {}", path);
			FilemanagerConfig config = (path.endsWith(".default")) ? UserObjectProxy.getFilemanagerDefaultConfig()
					: UserObjectProxy.getFilemanagerUserConfig(req);

			ObjectMapper mapper = new ObjectMapper();
			try {
				mapper.writeValue(resp.getOutputStream(), config);
			} catch (Exception e) {
				logger.error(String.format("Handling of '%s' failed.", path), e);
				throw new RuntimeException(e);
			} finally {
				IOUtils.closeQuietly(resp.getOutputStream());
			}
		} else {
			chain.doFilter(req, resp);
		}
	}

	@Override
	public void destroy() {
	}

}

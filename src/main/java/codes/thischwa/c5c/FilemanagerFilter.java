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
import java.io.InputStream;
import java.io.OutputStream;

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

/**
 * Filter for serving all filemanager files except the configuration files. <br/>
 * To register it in the web.xml the following entries should be used: 
 * <pre>
 * {@code
 * <filter>
 *	 <filter-name>FmFilter</filter-name>
 *	 <filter-class>codes.thischwa.c5c.FilemanagerFilter</filter-class>
 * </filter>
 * <filter-mapping>
 *	 <filter-name>FmFilter</filter-name>
 *	 <url-pattern>/filemanager/*</url-pattern>
 * </filter-mapping>
 * }
 * </pre>
 */
public class FilemanagerFilter implements Filter {
	private static Logger logger = LoggerFactory.getLogger(FilemanagerFilter.class);
	
	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
		logger.info(String.format("*** %s sucessful initialized.", this.getClass().getName()));
	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
		HttpServletRequest req = (HttpServletRequest)request;
		HttpServletResponse resp = (HttpServletResponse)response;
		String path = req.getServletPath();
		if(!path.contains("filemanager.config.") && !path.startsWith(Constants.REQUEST_PATH_TOIGNORE)) {
			InputStream in = FilemanagerFilter.class.getResourceAsStream(path);
			OutputStream out = null;
			try {
				if(in == null) {
					resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
					logger.warn("Requested path not found: {}", path);
				} else {
					out = resp.getOutputStream(); // shouldn't be flushed, because of the filter-chain
					IOUtils.copy(in,out);
				}
			} catch (IOException e) {
				logger.warn("Error while reading requested resource: ", path, e);
			} finally {
				IOUtils.closeQuietly(out);
			}
		} else  {
			chain.doFilter(req, resp);
		}
	}
	
	@Override
	public void destroy() {		
	}
}

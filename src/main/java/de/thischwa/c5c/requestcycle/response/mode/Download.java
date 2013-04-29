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
package de.thischwa.c5c.requestcycle.response.mode;

import java.io.IOException;
import java.io.InputStream;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import de.thischwa.c5c.requestcycle.response.Response;

/**
 * Holds the data for a Download Response.
 */
public final class Download extends Response {

	private String fullPath;
	
	private long contentLength;
	
	private InputStream in;

	public Download(String fullPath, long contentLength, InputStream in) {
		this.fullPath = fullPath;
		this.contentLength = contentLength;
		this.in = in;
	}
	
	@JsonProperty("Path")
	public String getFullPath() {
		return fullPath;
	}
	
	@Override
	@JsonIgnore
	public void write(HttpServletResponse resp) throws IOException {
		resp.setHeader("Content-Type", "application/x-download");
		resp.setHeader("Content-Transfer-Encoding", "Binary");
		resp.setHeader("Content-Length", "" + contentLength);
		resp.setHeader("Content-Disposition", 
				String.format("attachment; filename=\"%s\"", FilenameUtils.getName(fullPath)));
		IOUtils.copy(in, resp.getOutputStream());
	}
}

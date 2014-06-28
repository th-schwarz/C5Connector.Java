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
package codes.thischwa.c5c.requestcycle.response.mode;

import java.io.IOException;
import java.io.InputStream;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;

import codes.thischwa.c5c.FilemanagerAction;
import codes.thischwa.c5c.requestcycle.response.GenericResponse;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Holds the data for a Download GenericResponse.
 */
public final class Download extends GenericResponse {

	private String fullPath;
	
	private long contentLength;
	
	private InputStream in;

	public Download(String fullPath, long contentLength, InputStream in) {
		super(FilemanagerAction.DOWNLOAD);
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
		resp.setHeader("Content-Length", String.valueOf(contentLength));
		resp.setHeader("Content-Disposition", 
				String.format("attachment; filename=\"%s\"", FilenameUtils.getName(fullPath)));
		IOUtils.copy(in, resp.getOutputStream());
	}
}

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
import java.net.FileNameMap;
import java.net.URLConnection;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;

import codes.thischwa.c5c.FilemanagerAction;
import codes.thischwa.c5c.requestcycle.response.GenericResponse;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * Holds the data of a Preview response.
 */
public final class Prieview extends GenericResponse {

	private String fullPath;
	
	private long contentLength;
	
	private InputStream in;

	private static FileNameMap contentTypes = URLConnection.getFileNameMap();

	public Prieview(String fullPath, long contentLength, InputStream in) {
		super(FilemanagerAction.PREVIEW);
		this.fullPath = fullPath;
		this.contentLength = contentLength;
		this.in = in;
	}
	
	@Override
	@JsonIgnore
	public void write(HttpServletResponse resp) throws IOException {
		String contentType = contentTypes.getContentTypeFor(fullPath);
		resp.setHeader("Content-Type", contentType);
		resp.setHeader("Content-Length", String.valueOf(contentLength));
		IOUtils.copy(in, resp.getOutputStream());
	}
}

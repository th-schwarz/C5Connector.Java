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
package de.thischwa.c5c.requestcycle.response;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;

import de.thischwa.c5c.Constants;
import de.thischwa.c5c.FilemanagerAction;

/**
 * Base class for each response objects.
 */
public class GenericResponse {

	/** Default code for no errors. */
	public final static int DEFAULT_NO_ERROR_CODE = 0;

	/** Default error code. */
	public final static int DEFAULT_ERROR_CODE = -1;

	/** The error message. */
	private String error = "";

	/** The error code. */
	private int errorCode = DEFAULT_NO_ERROR_CODE;

	private FilemanagerAction mode = null;

	public void setError(String error, int errorCode) {
		this.error = error;
		this.errorCode = errorCode;
	}

	@JsonProperty("Error")
	public String getError() {
		return error;
	}

	@JsonProperty("Code")
	public int getErrorCode() {
		return errorCode;
	}

	@JsonIgnore
	public boolean hasError() {
		return (errorCode != DEFAULT_NO_ERROR_CODE);
	}

	@JsonIgnore
	public String getErrorMessage() {
		return String.format("Error#%d: %s", errorCode, error);
	}

	@JsonIgnore
	public FilemanagerAction getMode() {
		return mode;
	}

	@JsonIgnore
	public void setMode(FilemanagerAction mode) {
		this.mode = mode;
	}

	/**
	 * Write the response to the {@link HttpServletResponse} (The character encoding of the {@link HttpServletResponse} will
	 * be used. Inherited object could overwrite this to write 
	 * special content or headers.
	 * 
	 * @param resp
	 *            the resp
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	@JsonIgnore
	public void write(HttpServletResponse resp) throws IOException {
		if (mode != null && mode.getContentType() != null)
			resp.setContentType(mode.getContentType());
		OutputStream out = resp.getOutputStream();
		String json = toString();
		IOUtils.write(json, out, resp.getCharacterEncoding());
		IOUtils.closeQuietly(out);
	}
	
	protected String serialize(Object obj) {
		ObjectMapper mapper = new ObjectMapper();
		try {
			String jsonStr = mapper.writeValueAsString(obj);
			// escaping the slashes to use JSON in textareas
			jsonStr = jsonStr.replace(Constants.defaultSeparator, "\\".concat(Constants.defaultSeparator));
			return jsonStr;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public String toString() {
		return serialize(this);
	}

	/**
	 * Builds the {@link DownloadInfo}.
	 * 
	 * @param in
	 *            {@link InputStream} in which the file data has to put
	 * @param fileSize
	 *            the absolute size of the file
	 * @return The initialized {@link DownloadInfo}.
	 */
	public static DownloadInfo buildDownloadInfo(final InputStream in, long fileSize) {
		return new DownloadInfo(in, fileSize);
	}
	
	/**
	 * Simple container object to hold data which is relevant for download. 
	 */
	public static class DownloadInfo {
		
		private InputStream in;
		private long fileSize;
		
		private DownloadInfo(InputStream in, long fileSize) {
			this.in = in;
			this.fileSize = fileSize;
		}

		public InputStream getInputStream() {
			return in;
		}
		
		public long getFileSize() {
			return fileSize;
		}
	}
}

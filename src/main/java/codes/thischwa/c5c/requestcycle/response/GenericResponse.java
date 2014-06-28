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
package codes.thischwa.c5c.requestcycle.response;

import java.io.IOException;
import java.io.OutputStream;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;

import codes.thischwa.c5c.Constants;
import codes.thischwa.c5c.FilemanagerAction;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;

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

	private FilemanagerAction mode;
	
	protected GenericResponse(FilemanagerAction mode) {
		this.mode = mode;
	}

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
}

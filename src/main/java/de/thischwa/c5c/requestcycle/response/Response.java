/*
 * C5Connector.Java - The Java backend for the filemanager of corefive.
 * It provides a simple object for creating an editor instance.
 * Copyright (C) Thilo Schwarz
 * 
 * == BEGIN LICENSE ==
 * 
 * Licensed under the terms of any of the following licenses at your
 * choice:
 * 
 *  - GNU General Public License Version 2 or later (the "GPL")
 *    http://www.gnu.org/licenses/gpl.html
 * 
 *  - GNU Lesser General Public License Version 2.1 or later (the "LGPL")
 *    http://www.gnu.org/licenses/lgpl.html
 * 
 *  - Mozilla Public License Version 1.1 or later (the "MPL")
 *    http://www.mozilla.org/MPL/MPL-1.1.html
 * 
 * == END LICENSE ==
 */
package de.thischwa.c5c.requestcycle.response;

import java.io.IOException;
import java.io.OutputStream;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;

import de.thischwa.c5c.RequestMode;

/**
 * Base class for each response objects.
 */
public abstract class Response {
	
	/** Default code for no errors. */
	public final static int DEFAULT_NO_ERROR_CODE = 0;
	
	/** Default error code. */
	public final static int DEFAULT_ERROR_CODE = -1;

	/** The error message. */
	private String error = "";
	
	private int errorCode = DEFAULT_NO_ERROR_CODE;
	
	private RequestMode mode = null;
	
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
	public RequestMode getMode() {
		return mode;
	}
	
	@JsonIgnore
	public void setMode(RequestMode mode) {
		this.mode = mode;
	}

	/**
	 * Write the response to the {@link HttpServletResponse}. Inherited object could overwrite this to
	 * write special content like files.
	 *
	 * @param resp the resp
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	@JsonIgnore
	public void write(HttpServletResponse resp) throws IOException {
		if(mode != null)
			resp.setContentType(mode.getContentType());
		OutputStream out = resp.getOutputStream();
		IOUtils.write(toString(), out);
		IOUtils.closeQuietly(out);
	}
	
	@Override
	public String toString() {
		ObjectMapper mapper = new ObjectMapper();
	
		try {
			String jsonStr = mapper.writeValueAsString(this);
			return jsonStr;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
}

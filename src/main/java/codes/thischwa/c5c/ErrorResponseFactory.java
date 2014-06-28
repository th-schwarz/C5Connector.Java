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

import codes.thischwa.c5c.exception.C5CException;
import codes.thischwa.c5c.exception.FilemanagerException;
import codes.thischwa.c5c.requestcycle.response.GenericResponse;
import codes.thischwa.c5c.requestcycle.response.mode.UploadFile;

/**
 * Factory for building error responses, - for internal use only.
 */
class ErrorResponseFactory {
	
	static GenericResponse buildErrorResponse(String error, int errorCode) {
		GenericResponse r = new ErrorResponse();
		r.setError(error, errorCode);
		return r;
	}
	
	static GenericResponse buildException(C5CException e) {
		if(e instanceof FilemanagerException) {
			return buildErrorResponse(e.getMessage(), GenericResponse.DEFAULT_ERROR_CODE);
		}
		String msg = (e.getMode() == null) ? e.getMessage()
				: String.format("While executing [{}]: {}", e.getMode().toString(), e.getMessage());
		return buildErrorResponse(msg, 1000);
	}
	
	static UploadFile buildErrorResponseForUpload(String message) {
		return buildErrorResponseForUpload(message, GenericResponse.DEFAULT_ERROR_CODE);
	}

	static UploadFile buildErrorResponseForUpload(String message, int code) {
		UploadFile uf = new UploadFile(message, code);
		return uf;
	}

	private static class ErrorResponse extends GenericResponse {
		ErrorResponse() {
			super(null);
		}
	}
}

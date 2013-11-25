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

import de.thischwa.c5c.exception.C5CException;
import de.thischwa.c5c.exception.FilemanagerException;
import de.thischwa.c5c.requestcycle.response.GenericResponse;

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
		String msg = (e.getMode() == null) ? e.getMessage() : String.format("While executing [{}]: {}", e.getMode().toString(), e.getMessage());
		return buildErrorResponse(msg, 1000);
	}

	private static class ErrorResponse extends GenericResponse {
	}
}

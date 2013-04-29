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

import de.thischwa.c5c.exception.C5CException;
import de.thischwa.c5c.exception.FilemanagerException;
import de.thischwa.c5c.exception.UserActionException;


public class ErrorResponseFactory {

	/** The bad request. */
	public static Response BAD_REQUEST = ErrorResponseFactory.buildErrorResponse("Missing or wrong request parameters.", 10);

	public static Response buildErrorResponse(String error, int errorCode) {
		Response r = new ErrorResponse();
		r.setError(error, errorCode);
		return r;
	}

	public static Response buildExceptionFileNotExists(String filePath) {
		return buildErrorResponse(String.format("File or directory [%s] doesn't exists.", filePath), 100);
	}

	public static Response buildExceptionFileAccessDenied(String filePath) {
		return buildErrorResponse(String.format("Access denied for file [%s].", filePath), 101);
	}

	public static Response buildException(C5CException e) {
		if(e instanceof FilemanagerException || e instanceof UserActionException) {
			return buildErrorResponse(e.getMessage(), Response.DEFAULT_ERROR_CODE);
		}
		String msg = (e.getMode() == null) ? e.getMessage() : String.format("While executing [{}]: {}", e.getMode().toString(), e.getMessage());
		return buildErrorResponse(msg, 1000);
	}

	private static class ErrorResponse extends Response {
	}
}

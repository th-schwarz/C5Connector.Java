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

import de.thischwa.c5c.exception.ConnectorException;
import de.thischwa.c5c.exception.FilemanagerException;
import de.thischwa.c5c.exception.UserActionException;


public class ErrorResponseFactory {

	/** The bad request. */
	public static AResponse BAD_REQUEST = ErrorResponseFactory.buildErrorResponse("Missing or wrong request parameters.", 10);

	public static AResponse buildErrorResponse(String error, int errorCode) {
		AResponse r = new ErrorResponse();
		r.setError(error, errorCode);
		return r;
	}

	public static AResponse buildExceptionFileNotExists(String filePath) {
		return buildErrorResponse(String.format("File or DIRECTORY [%s] doesn't exists.", filePath), 100);
	}

	public static AResponse buildExceptionFileAccessDenied(String filePath) {
		return buildErrorResponse(String.format("Access denied for FILE [%s].", filePath), 101);
	}

	public static AResponse buildException(ConnectorException e) {
		if(e instanceof FilemanagerException || e instanceof UserActionException) {
			return buildErrorResponse(e.getMessage(), AResponse.DEFAULT_ERROR_CODE);
		}
		String msg = (e.getMode() == null) ? e.getMessage() : String.format("While executing [{}]: {}", e.getMode().toString(), e.getMessage());
		return buildErrorResponse(msg, 1000);
	}

	private static class ErrorResponse extends AResponse {
	}
}

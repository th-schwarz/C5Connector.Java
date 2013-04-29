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
package de.thischwa.c5c.exception;

import de.thischwa.c5c.requestcycle.RequestData;
import de.thischwa.c5c.resource.UserActionMessageHolder;


/**
 * Thrown to indicate a user action, which isn't allowed.
 */
public class UserActionException extends C5CException {
	private static final long serialVersionUID = 1L;

	public UserActionException(UserActionMessageHolder.Key key) {
		super(UserActionMessageHolder.get(RequestData.getLocale(), key));
	}
}

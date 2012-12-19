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
package de.thischwa.c5c.exception;

import de.thischwa.c5c.requestcycle.RequestData;
import de.thischwa.c5c.resource.UserActionMessageHolder;


/**
 * Thrown to indicate a user action, which isn't allowed.
 */
public class UserActionException extends C5CException {
	private static final long serialVersionUID = 1L;

	public UserActionException(Key key) {
		super(UserActionMessageHolder.get(RequestData.getLocale(), key.propertyName));
	}

	
	public enum Key {
		UploadNotAllowed("upload.notallowed"),
		CreateFolderNotAllowed("createfolder.notallowed");
		
		private String propertyName;
		
		private Key(String propertyName) {
			this.propertyName = propertyName;
		}
		
		public String getPropertyName() {
			return propertyName;
		}
	}
}

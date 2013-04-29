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

import de.thischwa.c5c.FilemanagerAction;

/**
 * Base exception of this library.
 */
public class C5CException extends Exception {

	private static final long serialVersionUID = 1L;
	
	private FilemanagerAction mode = null;
	
	public C5CException(FilemanagerAction mode, String msg) {
		this(msg);
		this.mode = mode;
	}

	public C5CException(String msg) {
		super(msg);
	}
	
	public FilemanagerAction getMode() {
		return mode;
	}
}

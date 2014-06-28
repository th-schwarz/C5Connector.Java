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
package codes.thischwa.c5c.exception;

import codes.thischwa.c5c.FilemanagerAction;

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

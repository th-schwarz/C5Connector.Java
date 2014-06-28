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


/**
 * Global constants.
 */
public interface Constants {

	/** The default separator char. */
	public final char defaultSeparatorChar = '/';
	
	/** The default separator string. */
	public final String defaultSeparator = String.valueOf(defaultSeparatorChar);
	
	public final String INDICATOR_PREVIEW = "preview";
}

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
package de.thischwa.c5c.requestcycle;


/**
 * An interface to control the capabilities used in a response of an info-request of the filemanager.
 */
public interface FilemanagerCapability {

	/** Available capabilities. */
	public enum Capability {
		/** The selection of the file is allowed. */
		select,

		/** The deletion of the file is allowed. */
		delete,

		/** The renaming of the file is allowed. */
		rename,

		/** The download of the file is allowed. */
		download
	};

	/**
	 * Gets the capabilities for a desired context.
	 * 
	 * @param ctx
	 *            the {@link Context} of the request
	 * @return the capabilities for the desired context.
	 */
	public Capability[] getCapabilities(final Context ctx);
}

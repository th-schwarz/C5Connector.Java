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
package codes.thischwa.c5c.requestcycle;


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
		download,

		/** The replace of the file is allowed. */
		replace
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

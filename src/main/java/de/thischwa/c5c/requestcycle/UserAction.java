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
package de.thischwa.c5c.requestcycle;

import javax.servlet.http.HttpServletRequest;

import de.thischwa.c5c.resource.UserActionMessageHolder;

/**
 * An interface for user action control which aren't cover by the capabilities if 
 * the filemanager. <br/>
 * The equivalent error messages are managed by the {@link UserActionMessageHolder}.<br/>
 * <p>
 * <strong>Note:</strong> You are free to implement this interface the way you
 * need it, in other words your return values can be global, regardless of the
 * request, or on a per-request basis. The {@link HttpServletRequest} will be 
 * passed through each method. This is useful e.g. for integrating an user-based 
 * right-system.
 * </p>
 */
public interface UserAction {

	/**
	 * Checks if the upload of a file is enabled/allowed. 
	 * @param context the {@link Context}
	 * 
	 * @return {@code true} if file upload is enabled/allowed, else {@code
	 *         false}
	 */
	public boolean isFileUploadEnabled(Context context);
	
	/**
	 * Checks if the creation of a folder is enabled/allowed. 
	 * @param context the {@link Context}
	 * 
	 * @return {@code true} if folder creation is enabled/allowed, else {@code
	 *         false}
	 */
	public boolean isCreateFolderEnabled(Context context);
}
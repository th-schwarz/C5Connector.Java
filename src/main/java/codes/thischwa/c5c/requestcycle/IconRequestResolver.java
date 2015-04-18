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

import java.util.Map;

/**
 * Holds the url-paths of the icons of the filemanager. It's used by the implementation of the {@link IconResolver} to hold the icon data
 * for each different url-path.
 */
public final class IconRequestResolver {

	// <extension, icon>
	private Map<String, String> iconsPerExt;

	public IconRequestResolver(Map<String, String> iconsPerExt) {
		this.iconsPerExt = iconsPerExt;
	}

	/**
	 * Obtains the full url-path of the icon for the desired extension.
	 * 
	 * @param extension
	 *            the desired extension
	 * @param isProtected
	 *            signals that the underlying file is protected
	 * 
	 * @return the full url-path of the icon for the desired extension
	 */
	public String getIconPath(String extension, boolean isProtected) {
		if(isProtected)
			return iconsPerExt.get(IconResolver.key_default_lock);
		if(extension == null || !iconsPerExt.containsKey(extension.toLowerCase()))
			return iconsPerExt.get(IconResolver.key_default);
		return iconsPerExt.get(extension.toLowerCase());
	}

	/**
	 * Obtains the full url-path of the icon for a directory.
	 * 
	 * @param isProtected
	 *            signals that the underlying file is protected
	 * 
	 * @return the full url-path of the icon for a directory
	 */
	public String getIconPathForDirectory(boolean isProtected) {
		return (isProtected) ? iconsPerExt.get(IconResolver.key_directory_lock) : iconsPerExt.get(IconResolver.key_directory);
	}
}

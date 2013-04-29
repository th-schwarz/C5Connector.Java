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
package de.thischwa.c5c.resource;

import java.util.HashSet;
import java.util.Set;

import de.thischwa.c5c.util.StringUtils;


/**
 * Handles the extensions of different file types. <br/>
 * The file types are:
 * <ul>
 * <li>Archive</li>
 * <li>Doc</li>
 * <li>Image</li>
 * <li>Other</li>
 * </ul>
 * <b>Important:</b> For all types a allowed or a denied list can be defined. 
 * If an allowed list is pre-defined overwrite it with an empty list and define a denied list as required.
 */
public enum Extension {
	
	/** Handles the extensions for archives. */
	ARCHIVE(PropertiesLoader.getArchiveResourceTypeAllowedExtensions(), PropertiesLoader.getArchiveResourceTypeDeniedExtensions()),

	/** Handles the extensions for documents. */
	DOC(PropertiesLoader.getDocResourceTypeAllowedExtensions(), PropertiesLoader.getDocResourceTypeDeniedExtensions()),

	/** Handles the extensions for images. */
	IMAGE(PropertiesLoader.getImageResourceTypeAllowedExtensions(), PropertiesLoader.getImageResourceTypeDeniedExtensions()),
	
	/** Handles the extensions for all file types other than archive, documents and images. */
	OTHER(PropertiesLoader.getOtherResourceTypeAllowedExtensions(), PropertiesLoader.getOherResourceTypeDeniedExtensions());
	
	private static final String string_list_delimter = "\\|";
	
	private Set<String> allowedExtensions;
	
	private Set<String> deniedExtensions;
	
	private Extension(final String allowedExtensionStringList, final String deniedExtensionsStringList) {
		allowedExtensions = buildSet(allowedExtensionStringList);
		deniedExtensions = buildSet(deniedExtensionsStringList);
	}

	private static Set<String> buildSet(final String stringList) {
		if (StringUtils.isNullOrEmptyOrBlank(stringList))
			return new HashSet<String>();

		Set<String> set = new HashSet<String>();
		String[] items = stringList.split(string_list_delimter);
		for (String item : items) {
			// add item if not empty
			if(!StringUtils.isNullOrEmpty(item))
				set.add(item.toLowerCase());
		}
		return set;
	}

	/**
	 * Returns <code>true</code> if extension is allowed. If denied extensions are set,  
	 * it takes precedence over allowed extensions set, in other words a negative
	 * check is made against denied set and if this fails, allowed set is
	 * checked.
	 * 
	 * @param extension
	 *            the extension to check, empty will fail
	 * @return <code>true</code> if extension is allowed, else
	 *         <code>false</code>
	 */
	public final boolean isAllowedExtension(final String extension) {
		if (StringUtils.isNullOrEmptyOrBlank(extension))
			return false;
		String ext = extension.toLowerCase();
		if (allowedExtensions.isEmpty())
			return !deniedExtensions.contains(ext);
		if (deniedExtensions.isEmpty())
			return allowedExtensions.contains(ext);
		return false;
	}

	/**
	 * Returns <code>true</code> if extension is denied. This method simply
	 * negates {@link #isAllowedExtension(String)}.
	 * 
	 * @param extension
	 *            the extension to check, empty will fail
	 * @return <code>true</code> if extension is denied, else <code>false</code>
	 */
	public final boolean isDeniedExtension(final String extension) {
		return !isAllowedExtension(extension);
	}
}

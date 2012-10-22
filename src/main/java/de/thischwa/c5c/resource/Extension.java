/*
 * C5Connector.Java - The Java backend for the filemanager of corefive.
 * It provides a simple object for creating an editor instance.
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
package de.thischwa.c5c.resource;

import java.util.HashSet;
import java.util.Set;

import de.thischwa.c5c.util.StringUtils;


/**
 * Extension.java - TODO DOCUMENTME!
 */
public enum Extension {
	
	ARCHIVE(PropertiesLoader.getArchiveResourceTypeAllowedExtensions(), PropertiesLoader.getArchiveResourceTypeDeniedExtensions()),
	
	DOC(PropertiesLoader.getDocResourceTypeAllowedExtensions(), PropertiesLoader.getDocResourceTypeDeniedExtensions()),
	
	FILE(PropertiesLoader.getFileResourceTypeAllowedExtensions(), PropertiesLoader.getFileResourceTypeDeniedExtensions()),
	
	IMAGE(PropertiesLoader.getImageResourceTypeAllowedExtensions(), PropertiesLoader.getImageResourceTypeDeniedExtensions()),
	
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

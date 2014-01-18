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
package de.thischwa.c5c.util;

import java.io.IOException;
import java.io.InputStream;

import de.thischwa.jii.IDimensionProvider;
import de.thischwa.jii.exception.ReadException;

/**
 * General file-based utilities.
 */
public class FileUtils {

	/**
	 * Sanitizes a filename from certain chars.<br />
	 * 
	 * This method enforces the <code>forceSingleExtension</code> property and then replaces all occurrences of \, /, |, :, ?, *, &quot;,
	 * &lt;, &gt;, control chars by _ (underscore).
	 * 
	 * @param name
	 *            a potentially 'malicious' filename
	 * @return sanitized filename
	 */
	public static String sanitizeName(final String name) {
		if(StringUtils.isNullOrEmpty(name))
			return name;

		// Remove \ / | : ? * " < > 'Control Chars' with _
		return name.replaceAll("\\\\|/|\\||:|\\?|\\*|\"|<|>|\\p{Cntrl}", "_");
	}

	/**
	 * Replaces all dots in a filename with underscores except the last one.
	 * 
	 * @param filename
	 *            filename to sanitize
	 * @return string with a single dot only
	 */
	public static String forceSingleExtension(final String filename) {
		return filename.replaceAll("\\.(?![^.]+$)", "_");
	}

	/**
	 * Checks if a filename contains more than one dot.
	 * 
	 * @param filename
	 *            filename to check
	 * @return <code>true</code> if filename contains severals dots, else <code>false</code>
	 */
	public static boolean isSingleExtension(final String filename) {
		return filename.matches("[^\\.]+\\.[^\\.]+");
	}

	/**
	 * Checks if a file is an image.
	 * 
	 * @param dimensionProvider
	 *            implementation of the {@link IDimensionProvider} which is used to check the image
	 * @param in
	 *            {@link InputStream} of the underlying file, it will be reseted!
	 * @return <code>true</code> if the file is really an image, otherwise <code>false</code>
	 */
	public static boolean isImage(final IDimensionProvider dimensionProvider, final InputStream in) {
		try {
			in.mark(0);
			dimensionProvider.set(in);
			dimensionProvider.getDimension();
			in.reset();
			return true;
		} catch (UnsupportedOperationException | ReadException | IOException e) {
			return false;
		}
	}
}

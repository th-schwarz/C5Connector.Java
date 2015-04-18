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

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Interface to remove EXIF data from an image.
 */
public interface ExifRemover {

	/**
	 * Removes the EXIF data from an image. <br/>
	 * Based on the 'extension' the implementation has to decide if the image type support EXIF data or not. If not, the implementation
	 * should do nothing! Otherwise a new image file must be created.
	 * 
	 * @param in
	 *            {@link InputStream} of the image
	 * @param out
	 *            {@link OutputStream} of the image without the EXIF data
	 * @param extension
	 *            the extension of the image file
	 * @return <code>true</code> if a new file without EXIF data was created, otherwise <code>false</code>. A file shouldn't be created, if
	 *         the file type doesn't support EXIF data!
	 * @throws IOException
	 */
	public boolean removeExif(InputStream in, OutputStream out, String extension) throws IOException;

}

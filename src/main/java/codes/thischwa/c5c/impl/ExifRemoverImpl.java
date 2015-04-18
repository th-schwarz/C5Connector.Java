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
package codes.thischwa.c5c.impl;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.List;

import org.apache.sanselan.ImageReadException;
import org.apache.sanselan.ImageWriteException;
import org.apache.sanselan.formats.jpeg.exifRewrite.ExifRewriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import codes.thischwa.c5c.ExifRemover;

/**
 * Implementation of the {@link ExifRemover}. It uses the {@link ExifRewriter} of apache-sanselan and should be 
 * replaced by common-imaging, if the project has been released the 1st time.
 */
public class ExifRemoverImpl implements ExifRemover {
	private static final Logger logger = LoggerFactory.getLogger(ExifRemoverImpl.class);
	
	private final List<String> allowed = Arrays.asList("jfif", "jpg", "jpeg");

	@Override
	public boolean removeExif(InputStream in, OutputStream out, String extension) throws IOException {
		if(!allowed.contains(extension)) {
			logger.debug("Removing EXIF data for {} isn't necessary.", extension);
			return false;
		}
		
		try {
			new ExifRewriter().removeExifMetadata(in, out);
			return true;
		} catch (ImageReadException | ImageWriteException e) {
			throw new IOException(e);
		}
	}

}

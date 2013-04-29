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
package de.thischwa.c5c.requestcycle.response;

import java.awt.Dimension;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonIgnore;

import de.thischwa.c5c.Connector;
import de.thischwa.c5c.requestcycle.response.mode.FileInfoProperties;

/**
 * Object to hold the properties of a file or folder. It is used for the response of a get-request of 
 * an implementation of the {@link Connector}.
 */
public final class FileProperties extends FileInfoProperties {

	private boolean isDir = false;
	
	public FileProperties(String name, Date modified) {
		super(name, modified);
	}
	
	public FileProperties(String name, long size, Date modified) {
		super(name, size, modified);
	}
	
	public void setSize(int width, int height) {
		super.setSize(width, height);
	}
	
	public void setSize(Dimension dim) {
		super.setSize(dim.width, dim.height);
	}
	
	@JsonIgnore
	public boolean isDir() {
		return isDir;
	}
	
	public void setDir(boolean isDir) {
		this.isDir = isDir;
	}
}

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
package de.thischwa.c5c.requestcycle.response.mode;

import java.text.DateFormat;
import java.util.Date;
import java.util.Locale;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import de.thischwa.c5c.requestcycle.RequestData;
import de.thischwa.c5c.util.VirtualFile;
import de.thischwa.c5c.util.VirtualFile.Type;

/**
 * Holds the properties of a file, which are relevant for building the response for the filemanager.
 * Just for internal use.
 */
public class FileInfoProperties {
	/** The base name WITHOUT the path, - just for internal use. */
	private String name;
	
	private VirtualFile.Type type = Type.file;
	private Date created = null;
	private Date modified;
	private Integer height = null;
	private Integer width = null;
	private Long size = null;

	public FileInfoProperties(String name, int width, int height, long size, Date modified) {
		this.name = name;
		this.width = width;
		this.height = height;
		this.size = size;
		this.modified = modified;
	}

	public FileInfoProperties(String name, long size, Date modified) {
		this.name = name;
		this.size = size;
		this.modified = modified;
	}

	/**
	 * Just for directories 
	 * 
	 * @param name
	 * @param modified
	 */
	public FileInfoProperties(String name, Date modified) {
		type = Type.directory;
		this.name = name;
		this.modified = modified;
	}

	@JsonIgnore
	public VirtualFile.Type getType() {
		return type;
	}
	
	@JsonIgnore
	public String getName() {
		return name;
	}
	
	@JsonProperty("Date Created")
	String getCreated() {
		return getDate(created);
	}
	
	@JsonProperty("Date Modified")
	String getModified() {
		return getDate(modified);
	}

	public void setModified(Date modified) {
		this.modified = modified;
	}

	public void setSize(int width, int height) {
		this.width = width;
		this.height = height;
	}
	
	@JsonProperty("Height")
	Integer getHeight() {
		return height;
	}

	@JsonProperty("Width")
	Integer getWidth() {
		return width;
	}
	
	@JsonProperty("Size")
	Long getSize() {
		return size;
	}

	void setSize(long size) {
		this.size = size;
	}
	
	private String getDate(Date date) {
		if(date == null)
			return null;
		Locale locale = RequestData.getLocale();
		DateFormat df = DateFormat.getDateInstance(DateFormat.SHORT, locale);
		String dateStr = df.format(date);
		return dateStr;
	}
}
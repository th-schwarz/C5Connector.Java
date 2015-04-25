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
package codes.thischwa.c5c.requestcycle.response.mode;

import java.text.DateFormat;
import java.util.Date;
import java.util.Locale;

import codes.thischwa.c5c.requestcycle.RequestData;
import codes.thischwa.c5c.util.VirtualFile;
import codes.thischwa.c5c.util.VirtualFile.Type;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

/**
 * Holds the properties of a file, which are relevant for building the response for the filemanager.
 * Just for internal use.
 */
@JsonPropertyOrder(alphabetic=true)
public class FileInfoProperties {
	/** The base name WITHOUT the path, - just for internal use. */
	private String name;
	
	private VirtualFile.Type type = Type.file;
	private Date created = null;
	private Date modified;
	private Integer height = null;
	private Integer width = null;
	private Long size = null;
	private boolean isProtected;

	private FileInfoProperties(VirtualFile.Type type, String name, boolean isProtected, Date modified) {
		if(name == null || modified == null)
			throw new IllegalArgumentException("Parameter 'name' or 'modified' shouldn't be null!");
		this.type = type;
		this.name = name;
		this.isProtected = isProtected;
		this.modified = modified;
	}
	
	public FileInfoProperties(String name, boolean isProtected, int width, int height, long size, Date modified) {
		this(name, isProtected, size, modified);
		this.width = width;
		this.height = height;		
	}

	public FileInfoProperties(String name, boolean isProtected, long size, Date modified) {
		this(Type.file, name, isProtected, modified);
		this.size = size;
	}

	/**
	 * Just for directories 
	 * 
	 * @param name
	 * @param modified
	 */
	public FileInfoProperties(String name, boolean isProtected, Date modified) {
		this(Type.directory, name, isProtected, modified);
	}

	@JsonIgnore
	public VirtualFile.Type getType() {
		return type;
	}
	
	@JsonIgnore
	public String getName() {
		return name;
	}

	/**
	 * Returns the string representation of 'created' which depends on the current locale of the request.
	 * 
	 * @return The string representation of 'created'.
	 */
	@JsonProperty("Date Created")
	String getCreated() {
		return getDate(created);
	}
	
	/**
	 * Returns the string representation of 'modified' which depends on the current locale of the request.
	 * 
	 * @return The string representation of 'modified'.
	 */
	@JsonProperty("Date Modified")
	String getModified() {
		return getDate(modified);
	}

	@JsonIgnore
	public Date getRawModified() {
		return modified;
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

	@JsonIgnore
	public boolean isProtected() {
		return isProtected;
	}
	
	private String getDate(Date date) {
		Locale locale = RequestData.getLocale();
		if(date == null || locale == null)
			return null;
		DateFormat df = DateFormat.getDateInstance(DateFormat.SHORT, locale);
		String dateStr = df.format(date);
		return dateStr;
	}

	@JsonIgnore
	@Override
	public int hashCode() {
		int result = 1;
		result += (isProtected ? 1231 : 1237);
		result += ((modified == null) ? 0 : modified.hashCode());
		result += ((name == null) ? 0 : name.hashCode());
		result += ((type == null) ? 0 : type.hashCode());
		return result;
	}

	@JsonIgnore
	@Override
	public boolean equals(Object obj) {
		if(this == obj)
			return true;
		if(obj == null)
			return false;
		if(getClass() != obj.getClass())
			return false;
		FileInfoProperties other = (FileInfoProperties) obj;
		if(isProtected != other.isProtected)
			return false;
		if(modified == null) {
			if(other.modified != null)
				return false;
		} else if(!modified.equals(other.modified))
			return false;
		if(name == null) {
			if(other.name != null)
				return false;
		} else if(!name.equals(other.name))
			return false;
		if(type != other.type)
			return false;
		return true;
	}
	
}
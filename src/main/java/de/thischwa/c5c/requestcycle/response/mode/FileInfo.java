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

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import de.thischwa.c5c.Constants;
import de.thischwa.c5c.requestcycle.FilemanagerCapability;
import de.thischwa.c5c.requestcycle.response.GenericResponse;
import de.thischwa.c5c.util.Path;
import de.thischwa.c5c.util.StringUtils;
import de.thischwa.c5c.util.VirtualFile;
import de.thischwa.c5c.util.VirtualFile.Type;

/**
 * Holds the data for a FileInfo response.
 */
public final class FileInfo extends GenericResponse {
	
	private static final String type_unknown = "txt";
	
	private static final String type_dir = "dir";
	
	private FileInfoProperties fileProperties;
	
	private String path;
	
	private VirtualFile virtualFile;
	
	private List<String> capabilities = null;

	private String previewPath;

	public FileInfo(String path, boolean isDir) {
		capabilities = new ArrayList<>();
		virtualFile = new VirtualFile(path, isDir);
		this.path = path;
		if (isDir && !this.path.endsWith(Constants.defaultSeparator))
			this.path += Constants.defaultSeparator;
	}

	@JsonProperty("Path")
	public String getPath() {
		if(fileProperties == null)
			throw new IllegalArgumentException("No properties are set.");
		Path p = new Path(path);
		String path = p.toString();
		if(path.endsWith(Constants.defaultSeparator))
			path = path.substring(0, path.length()-1);
		if(!path.endsWith(fileProperties.getName()))
			path = p.addFile(fileProperties.getName());
		if(!getType().equals(type_dir) && path.endsWith(Constants.defaultSeparator))
			path = path.substring(0, path.length()-1);
		else if(getType().equals(type_dir) && !path.endsWith(Constants.defaultSeparator))
			path += Constants.defaultSeparator;
		return path;
	}

	@JsonProperty("Filename")
	public String getName() {
		if(fileProperties == null)
			throw new IllegalArgumentException("No properties are set.");
		 return fileProperties.getName();
	}

	@JsonProperty("File Type")
	public String getType() {
		if (fileProperties.getType() == Type.directory)
			return type_dir;
		
		VirtualFile vf = new VirtualFile(fileProperties.getName()); 
		if (!StringUtils.isNullOrEmptyOrBlank(vf.getExtension()))
			return vf.getExtension();
		return type_unknown;
	}

	@JsonProperty("Preview")
	public String getPreviewPath() {
		return previewPath;
	}

	public void setPreviewPath(String previewPath) {
		this.previewPath = previewPath;
	}

	@JsonProperty("Properties")
	public FileInfoProperties getFileProperties() {
		return fileProperties;
	}

	public void setFileProperties(FileInfoProperties fileProperties) {
		this.fileProperties = fileProperties;
	}

	@JsonIgnore
	public boolean isDir() {
		return (fileProperties.getType() == Type.directory);
	}

	@JsonIgnore
	public VirtualFile getVirtualFile() {
		return virtualFile;
	}

	@JsonIgnore
	public void setCapabilities(FilemanagerCapability.Capability[] capabilities) {
		if (capabilities != null && capabilities.length > 0) {
			this.capabilities = new ArrayList<>(capabilities.length);
			for (FilemanagerCapability.Capability capability : capabilities) {
				this.capabilities.add(capability.toString().toLowerCase());
			}
		}
	}

	@JsonProperty("Capabilities")
	public List<String> getCapabilities() {
		return capabilities;
	}
}

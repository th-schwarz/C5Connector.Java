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
package de.thischwa.c5c.requestcycle.response.mode;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import de.thischwa.c5c.Constants;
import de.thischwa.c5c.requestcycle.C5FileCapability;
import de.thischwa.c5c.requestcycle.response.Response;
import de.thischwa.c5c.util.StringUtils;
import de.thischwa.c5c.util.VirtualFile;
import de.thischwa.c5c.util.VirtualFile.Type;

/**
 * TODO document me
 */
public class FileInfo extends Response {
	
	private static final String type_unknown = "txt";
	
	private static final String type_dir = "dir";
	
	private FileProperties fileProperties;
	
	private String path;
	
	private VirtualFile virtualFile;
	
	private List<String> capabilities = null;

	private String previewPath;

	FileInfo(String path, boolean isDir) {
		virtualFile = new VirtualFile(path, isDir);
		this.path = path;
		if (isDir && !this.path.endsWith(Constants.separator))
			this.path += Constants.separator;
		capabilities = new ArrayList<String>();
	}

	@JsonProperty("Path")
	public String getPath() {
		return path;
	}

	@JsonProperty("Filename")
	public String getName() {
		return virtualFile.getName();
	}

	@JsonProperty("File Type")
	public String getType() {
		if (virtualFile.getType() == Type.DIRECTORY)
			return type_dir;
		if (!StringUtils.isNullOrEmptyOrBlank(virtualFile.getExtension()))
			return virtualFile.getExtension();
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
	public FileProperties getFileProperties() {
		return fileProperties;
	}

	public void setFileProperties(FileProperties fileProperties) {
		this.fileProperties = fileProperties;
	}

	public void setFileProperties(int height, int with, long size, String modified) {
		this.fileProperties = new FileProperties(height, with, size, modified);
	}

	public void setFileProperties(long size, String modified) {
		this.fileProperties = new FileProperties(size, modified);
	}
	
	public void setFolderProperties(String modified) {
		this.fileProperties = new FileProperties(modified);
	}

	@JsonIgnore
	public boolean isDir() {
		return (virtualFile.getType() == Type.DIRECTORY);
	}

	@JsonIgnore
	public VirtualFile getVirtualFile() {
		return virtualFile;
	}

	@JsonIgnore
	public void setCapabilities(C5FileCapability.CAPABILITY[] capabilities) {
		if (capabilities != null && capabilities.length > 0) {
			this.capabilities = new ArrayList<String>(capabilities.length);
			for (C5FileCapability.CAPABILITY capability : capabilities) {
				this.capabilities.add(capability.toString().toLowerCase());
			}
		}
	}

	@JsonProperty("Capabilities")
	public List<String> getCapabilities() {
		return capabilities;
	}

	/**
	 * TODO document me
	 */
	public class FileProperties {
		private String created = null;
		private String modified;
		private Integer height = null;
		private Integer with = null;
		private Long size = null;

		FileProperties(int height, int with, long size, String modified) {
			this.height = height;
			this.with = with;
			this.size = size;
			this.modified = modified;
		}

		FileProperties(long size, String modified) {
			this.size = size;
			this.modified = modified;
		}

		FileProperties(String modified) {
			this.modified = modified;
		}

		@JsonProperty("Date Created")
		String getCreated() {
			return created;
		}
		
		void setCreated(String created) {
			this.created = created;
		}
		
		@JsonProperty("Date Modified")
		String getModified() {
			return modified;
		}

		void setModified(String modified) {
			this.modified = modified;
		}

		@JsonProperty("Height")
		Integer getHeight() {
			return height;
		}

		void setHeight(int height) {
			this.height = height;
		}

		@JsonProperty("Width")
		Integer getWith() {
			return with;
		}

		void setWith(int with) {
			this.with = with;
		}

		@JsonProperty("Size")
		Long getSize() {
			return size;
		}

		void setSize(long size) {
			this.size = size;
		}
	}
}

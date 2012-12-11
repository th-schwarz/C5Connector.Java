package de.thischwa.c5c.requestcycle.response.mode;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import de.thischwa.c5c.util.VirtualFile;
import de.thischwa.c5c.util.VirtualFile.Type;

/**
 * Holds the properties of a file.
 */
public class FileInfoProperties {
	/** The base name WITHOUT the path, - just for internal use. */
	private String name;
	private VirtualFile.Type type = Type.file;
	private String created = null;
	private String modified;
	private Integer height = null;
	private Integer width = null;
	private Long size = null;

	public FileInfoProperties(String name, int width, int height, long size, String modified) {
		this.name = name;
		this.width = width;
		this.height = height;
		this.size = size;
		this.modified = modified;
	}

	public FileInfoProperties(String name, long size, String modified) {
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
	public FileInfoProperties(String name, String modified) {
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
}
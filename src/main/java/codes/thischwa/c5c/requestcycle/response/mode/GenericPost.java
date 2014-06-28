package codes.thischwa.c5c.requestcycle.response.mode;

import codes.thischwa.c5c.Constants;
import codes.thischwa.c5c.FilemanagerAction;
import codes.thischwa.c5c.requestcycle.response.GenericResponse;
import codes.thischwa.c5c.util.StringUtils;

import com.fasterxml.jackson.annotation.JsonProperty;

public class GenericPost extends GenericResponse {

	private String path;
	
	private String name;
	
	GenericPost(FilemanagerAction action, String path, String name) {
		super(action);
		this.path = path;
		this.name = name;
		if(!StringUtils.isNullOrEmpty(this.path) && !this.path.endsWith(Constants.defaultSeparator))
			this.path += Constants.defaultSeparator;
	}
	
	GenericPost(FilemanagerAction action, String errorMessage, int errorCode) {
		super(action);
		setError(errorMessage, errorCode);
	}

	@JsonProperty("Path")
	public String getPath() {
		return path;
	}
	
	public void setPath(String path) {
		this.path = path;
	}

	@JsonProperty("Name")
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	@Override
	public String toString() {
		return super.toString();
	}
}

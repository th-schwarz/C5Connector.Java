package codes.thischwa.c5c.filemanager;

import java.util.Set;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Represents the type <code>pdfs</code> of the JSON configuration of the filemanager.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Pdfs {

	@JsonProperty("showPdfReader")
	private boolean showReader;
	
	@JsonProperty("pdfsExt")
	private Set<String> extensions;
	
	@JsonProperty("pdfsReaderHeight")
	private int heigth;

	@JsonProperty("pdfsReaderWidth")
	private int with;
	
	Pdfs() {
	}

	public boolean isShowReader() {
		return showReader;
	}

	public void setShowReader(boolean showReader) {
		this.showReader = showReader;
	}

	public Set<String> getExtensions() {
		return extensions;
	}

	public void setExtensions(Set<String> extensions) {
		this.extensions = extensions;
	}

	public int getHeigth() {
		return heigth;
	}

	public void setHeigth(int heigth) {
		this.heigth = heigth;
	}

	public int getWith() {
		return with;
	}

	public void setWith(int with) {
		this.with = with;
	}
}

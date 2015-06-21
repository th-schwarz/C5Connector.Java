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
package codes.thischwa.c5c.filemanager;

import java.util.Set;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Represents the type <code>options</code> of the JSON configuration of the filemanager.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Options {

	public enum FILE_SORTING {
		DEFAULT, NAME_ASC, NAME_DESC, TYPE_ASC, TYPE_DESC, MODIFIED_ASC, MODIFIED_DESC;
	};
	
	public enum VIEW_MODE {
		LIST, GRID;
	}

	private String culture;
	private String lang;
	private String theme;
	private VIEW_MODE defaultViewMode;
	private boolean autoload;
	private boolean showFullPath;
	private boolean showTitleAttr;
	private boolean browseOnly;
	private boolean showConfirmation;
	private boolean showThumbs;
	private boolean generateThumbnails;
	private boolean searchBox;
	private boolean listFiles;
	private FILE_SORTING fileSorting;

	@JsonProperty("chars_only_latin")
	private boolean charsOnlyLatin;

	private String dateFormat;
	private boolean serverRoot;
	private Object fileRoot;
	private Object relPath;
	private Object baseUrl;
	private boolean logger;
	private Set<String> capabilities;
	private Set<String> plugins;

	Options() {
	}

	public String getCulture() {
		return culture;
	}

	public void setCulture(String culture) {
		this.culture = culture;
	}

	public String getLang() {
		return lang;
	}

	public void setLang(String lang) {
		this.lang = lang;
	}
	
	public String getTheme() {
		return theme;
	}
	
	public void setTheme(String theme) {
		this.theme = theme;
	}

	public VIEW_MODE getDefaultViewMode() {
		return defaultViewMode;
	}

	public void setDefaultViewMode(VIEW_MODE defaultViewMode) {
		this.defaultViewMode = defaultViewMode;
	}
	
	@JsonProperty("defaultViewMode")
	public void setDefaultViewMode(String defaultViewMode) {
		for(VIEW_MODE fs : VIEW_MODE.values()) {
			if(fs.toString().equalsIgnoreCase(defaultViewMode)) {
				this.defaultViewMode = fs;
				return;
			}
		}
		throw new IllegalArgumentException(String.format("Unknown defaultViewMode value: %s", defaultViewMode));
	}

	public boolean isAutoload() {
		return autoload;
	}

	public void setAutoload(boolean autoload) {
		this.autoload = autoload;
	}

	public boolean isShowFullPath() {
		return showFullPath;
	}

	public void setShowFullPath(boolean showFullPath) {
		this.showFullPath = showFullPath;
	}

	public boolean isShowTitleAttr() {
		return showTitleAttr;
	}

	public void setShowTitleAttr(boolean showTitleAttr) {
		this.showTitleAttr = showTitleAttr;
	}

	public boolean isBrowseOnly() {
		return browseOnly;
	}

	public void setBrowseOnly(boolean browseOnly) {
		this.browseOnly = browseOnly;
	}

	public boolean isShowConfirmation() {
		return showConfirmation;
	}

	public void setShowConfirmation(boolean showConfirmations) {
		this.showConfirmation = showConfirmations;
	}

	public boolean isShowThumbs() {
		return showThumbs;
	}

	public void setShowThumbs(boolean showThumbs) {
		this.showThumbs = showThumbs;
	}

	public boolean isGenerateThumbnails() {
		return generateThumbnails;
	}

	public void setGenerateThumbnails(boolean generateThumbnails) {
		this.generateThumbnails = generateThumbnails;
	}

	public boolean isSearchBox() {
		return searchBox;
	}

	public void setSearchBox(boolean searchBox) {
		this.searchBox = searchBox;
	}

	public boolean isListFiles() {
		return listFiles;
	}

	public void setListFiles(boolean listFiles) {
		this.listFiles = listFiles;
	}

	public FILE_SORTING getFileSorting() {
		return fileSorting;
	}

	public void setFileSorting(FILE_SORTING fileSorting) {
		this.fileSorting = fileSorting;
	}

	@JsonProperty("fileSorting")
	public void setFileSorting(String fileSorting) {
		for(FILE_SORTING fs : FILE_SORTING.values()) {
			if(fs.toString().equalsIgnoreCase(fileSorting)) {
				this.fileSorting = fs;
				return;
			}
		}
		throw new IllegalArgumentException(String.format("Unknown fileSorting value: %s", fileSorting));
	}

	public boolean isCharsOnlyLatin() {
		return charsOnlyLatin;
	}

	public void setCharsOnlyLatin(boolean charsOnlyLatin) {
		this.charsOnlyLatin = charsOnlyLatin;
	}

	public String getDateFormat() {
		return dateFormat;
	}

	public void setDateFormat(String dateFormat) {
		this.dateFormat = dateFormat;
	}

	public boolean isServerRoot() {
		return serverRoot;
	}

	public void setServerRoot(boolean serverRoot) {
		this.serverRoot = serverRoot;
	}

	public Object getFileRoot() {
		return fileRoot;
	}

	public void setFileRoot(Object fileRoot) {
		this.fileRoot = fileRoot;
	}

	public Object getRelPath() {
		return relPath;
	}

	public void setRelPath(Object relPath) {
		this.relPath = relPath;
	}
	
	public Object getBaseUrl() {
		return baseUrl;
	}
	
	public void setBaseUrl(Object baseUrl) {
		this.baseUrl = baseUrl;
	}

	public boolean isLogger() {
		return logger;
	}

	public void setLogger(boolean logger) {
		this.logger = logger;
	}

	public Set<String> getCapabilities() {
		return capabilities;
	}

	public void setCapabilities(Set<String> capabilities) {
		this.capabilities = capabilities;
	}

	public Set<String> getPlugins() {
		return plugins;
	}

	public void setPlugins(Set<String> plugins) {
		this.plugins = plugins;
	}

}
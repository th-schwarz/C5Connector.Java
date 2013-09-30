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
package de.thischwa.c5c.resource.filemanager;

import java.util.Set;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Represents the type <code>extras</code> of the JSON configuration of the filemanager. 
 */
public class Extras {

	@JsonProperty("extra_js_async")
	private boolean jsAsync;
	
	@JsonProperty("extra_js")
	private Set<String> js;
	
	Extras() {
	}

	public boolean isJsAsync() {
		return jsAsync;
	}

	public void setJsAsync(boolean jsAsync) {
		this.jsAsync = jsAsync;
	}

	public Set<String> getJs() {
		return js;
	}

	public void setJs(Set<String> js) {
		this.js = js;
	}
}

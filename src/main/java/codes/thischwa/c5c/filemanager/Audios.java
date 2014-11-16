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

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Represents the type <code>audios</code> of the JSON configuration of the filemanager. 
 */
public class Audios {

	@JsonProperty("showAudioPlayer")
	private boolean showPlayer;
	
	@JsonProperty("audiosExt")
	private Set<String> extensions;
	
	Audios() {
	}

	public boolean isShowPlayer() {
		return showPlayer;
	}

	public void setShowPlayer(boolean showPlayer) {
		this.showPlayer = showPlayer;
	}

	public Set<String> getExtensions() {
		return extensions;
	}

	public void setExtensions(Set<String> extensions) {
		this.extensions = extensions;
	}
	
}

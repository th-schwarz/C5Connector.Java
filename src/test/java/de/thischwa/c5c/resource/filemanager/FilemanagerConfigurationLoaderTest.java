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
package de.thischwa.c5c.resource.filemanager;

import static org.junit.Assert.*;

import org.junit.Test;

import com.fasterxml.jackson.databind.ObjectMapper;

public class FilemanagerConfigurationLoaderTest {

	@Test
	public void testObject() throws Exception {
		FilemanagerConfig conf = new FilemanagerConfig();
		conf.setComment("test");
		conf.getOptions().setLang("java");
		conf.getOptions().setRelPath(Boolean.FALSE);

		ObjectMapper mapper = new ObjectMapper();
		String actual = mapper.writeValueAsString(conf);
		String expected = "{\"options\":{\"culture\":null,\"lang\":\"java\",\"defaultViewMode\":null,\"autoload\":false,\"showFullPath\":false,\"showTitleAttr\":false,\"browseOnly\":false,\"showConfirmation\":false,\"showThumbs\":false,\"generateThumbnails\":false,\"searchBox\":false,\"listFiles\":false,\"fileSorting\":null,\"dateFormat\":null,\"serverRoot\":false,\"fileRoot\":null,\"relPath\":false,\"logger\":false,\"capabilities\":null,\"plugins\":null,\"chars_only_latin\":false},\"security\":{\"uploadPolicy\":null,\"uploadRestrictions\":null},\"upload\":{\"overwrite\":false,\"imagesOnly\":false,\"fileSizeLimit\":-1},\"exclude\":{\"unallowed_files\":null,\"unallowed_dirs\":null,\"unallowed_files_REGEXP\":null,\"unallowed_dirs_REGEXP\":null},\"images\":{\"imagesExt\":null},\"videos\":{\"showVideoPlayer\":false,\"videosExt\":null,\"videosPlayerHeight\":0,\"videosPlayerWidth\":0},\"audios\":{\"showAudioPlayer\":false,\"audiosExt\":null},\"edit\":{\"enabled\":false,\"lineNumbers\":false,\"lineWrapping\":false,\"codeHighlight\":false,\"theme\":null,\"editExt\":null},\"extras\":{\"extra_js_async\":false,\"extra_js\":null},\"icons\":{\"path\":null,\"directory\":null,\"default\":null},\"_comment\":\"test\"}";
		assertEquals(expected, actual);
	}

	@Test
	public void testFile() throws Exception {
		ObjectMapper mapper = new ObjectMapper();
		FilemanagerConfig conf = mapper.readValue(this.getClass().getResourceAsStream("filemanager.config.js.default"), FilemanagerConfig.class);
		
		assertEquals("php", conf.getOptions().getLang());
		assertEquals(Options.FILE_SORTING.DEFAULT, conf.getOptions().getFileSorting());
		assertTrue(conf.getOptions().isListFiles());
		
		assertEquals(Security.UPLOAD_POLICY.DISALLOW_ALL, conf.getSecurity().getUploadPolicy());
		assertTrue(conf.getSecurity().getAllowedExtensions().contains("doc"));
		
		assertFalse(conf.getUpload().isImagesOnly());
		assertEquals(16, conf.getUpload().getFileSizeLimit());
		
		assertTrue(conf.getExclude().getDisallowedDirs().contains("_thumbs"));
		
		assertTrue(conf.getImages().getExtensions().contains("png"));
		
		assertEquals("default.png", conf.getIcons().getDefaultIcon());
		
		assertTrue(conf.getOptions().getRelPath() instanceof Boolean);
		assertEquals(Boolean.FALSE, conf.getOptions().getRelPath());
		
		assertEquals(5, conf.getOptions().getCapabilities().size());
	}

	@Test
	public void testBool() throws Exception {
		ObjectMapper mapper = new ObjectMapper();
		FilemanagerConfig conf = mapper.readValue(this.getClass().getResourceAsStream("filemanager.config.js.default"), FilemanagerConfig.class);
		assertEquals(Boolean.FALSE, conf.getOptions().getRelPath());
	}
}

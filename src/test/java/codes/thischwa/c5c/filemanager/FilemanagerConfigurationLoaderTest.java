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

import static org.junit.Assert.*;

import org.junit.Test;

import codes.thischwa.c5c.filemanager.FilemanagerConfig;
import codes.thischwa.c5c.filemanager.Options;
import codes.thischwa.c5c.filemanager.Resize;
import codes.thischwa.c5c.filemanager.Security;

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
		String expected = "{\"options\":{\"culture\":null,\"lang\":\"java\",\"theme\":null,\"autoload\":false,\"showFullPath\":false,\"showTitleAttr\":false,\"browseOnly\":false,\"showConfirmation\":false,\"showThumbs\":false,\"generateThumbnails\":false,\"searchBox\":false,\"listFiles\":false,\"splitterWidth\":0,\"splitterMinWidth\":0,\"dateFormat\":null,\"serverRoot\":false,\"fileRoot\":null,\"relPath\":false,\"baseUrl\":null,\"logger\":false,\"capabilities\":null,\"plugins\":null,\"defaultViewMode\":null,\"fileSorting\":null,\"chars_only_latin\":false},\"security\":{\"uploadPolicy\":null,\"uploadRestrictions\":null},\"upload\":{\"overwrite\":false,\"imagesOnly\":false,\"fileSizeLimit\":-1},\"exclude\":{\"unallowed_files\":null,\"unallowed_dirs\":null},\"images\":{\"resize\":{\"enabled\":false,\"maxHeight\":0,\"maxWidth\":0},\"imagesExt\":null},\"videos\":{\"showVideoPlayer\":false,\"videosExt\":null,\"videosPlayerHeight\":0,\"videosPlayerWidth\":0},\"audios\":{\"showAudioPlayer\":false,\"audiosExt\":null},\"pdfs\":{\"showPdfReader\":false,\"pdfsExt\":null,\"pdfsReaderHeight\":0,\"pdfsReaderWidth\":0},\"edit\":{\"enabled\":false,\"lineNumbers\":false,\"lineWrapping\":false,\"codeHighlight\":false,\"theme\":null,\"editExt\":null},\"extras\":{\"extra_js_async\":false,\"extra_js\":null},\"icons\":{\"path\":null,\"directory\":null,\"default\":null},\"customScrollbar\":{\"enabled\":false,\"button\":false,\"theme\":null},\"url\":null,\"version\":null,\"_comment\":\"test\"}";
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
		assertTrue(conf.getOptions().isGenerateThumbnails());
		assertEquals(Boolean.FALSE, conf.getOptions().getRelPath());
		
		assertEquals(5, conf.getOptions().getCapabilities().size());
		
		Resize resize = conf.getImages().getResize();
		assertEquals(1280, resize.getMaxWidth());
		assertEquals(1024, resize.getMaxHeight());
	}

	@Test
	public void testBool() throws Exception {
		ObjectMapper mapper = new ObjectMapper();
		FilemanagerConfig conf = mapper.readValue(this.getClass().getResourceAsStream("filemanager.config.js.default"), FilemanagerConfig.class);
		assertEquals(Boolean.FALSE, conf.getOptions().getRelPath());
		assertTrue(conf.getOptions().isServerRoot());
	}
}

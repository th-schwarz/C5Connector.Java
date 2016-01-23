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
package codes.thischwa.c5c.impl;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletContext;

import org.apache.commons.io.Charsets;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;

import com.fasterxml.jackson.databind.ObjectMapper;

import codes.thischwa.c5c.MessageResolver;
import codes.thischwa.c5c.PropertiesLoader;
import codes.thischwa.c5c.util.PathBuilder;

/**
 * This implementation of the {@link MessageResolver} works like the default implementation {@link FilemanagerMessageResolver},
 * but it uses the filemanager files inside the resource folder.
 */
public class FilemanagerMessageLibResolver extends AbstractFilemanagerMessageResolver {

	protected String langPath = "/filemanager/scripts/languages/";
	
	protected FilenameFilter jsFilter = new FilenameFilter() {
		@Override
		public boolean accept(File dir, String name) {
			return name.endsWith(".js");
		}
	};

	@Override
	public void setServletContext(ServletContext servletContext) throws RuntimeException {
		super.setServletContext(servletContext);
		ObjectMapper mapper = new ObjectMapper();
		InputStream inLang = null;
		try {
			inLang = FilemanagerMessageLibResolver.class.getResourceAsStream(langPath);
			List<String> files = IOUtils.readLines(inLang,
					Charsets.toCharset(PropertiesLoader.getDefaultEncoding()));

			for(String f : files){
				if(!f.endsWith(".js"))
					continue;
				String lang = FilenameUtils.getBaseName(f);
				String res = new PathBuilder(langPath).addFile(f);
				InputStream in = FilemanagerMessageLibResolver.class.getResourceAsStream(res);
			  
		        @SuppressWarnings("unchecked")
				Map<String, String> langData = mapper.readValue(in, Map.class);
				collectLangData(lang, langData);
			}
		} catch (IOException e) {
			throw new RuntimeException(e);
		} finally {
			IOUtils.closeQuietly(inLang);
		}
	}
	
}

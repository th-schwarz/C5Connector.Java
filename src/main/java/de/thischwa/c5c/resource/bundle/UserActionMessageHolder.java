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
package de.thischwa.c5c.resource.bundle;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.thischwa.c5c.requestcycle.UserAction;

/**
 * It provides localized messages for failed user actions of the {@link UserAction} implementation. 
 */
public class UserActionMessageHolder {
	private static Logger logger = LoggerFactory.getLogger(UserActionMessageHolder.class);

	private static String baseName = "userActionMessages";
	private static Map<String, Properties> bundleData = new HashMap<String, Properties>();
	
	static {
		Pattern baseNamePattern = Pattern.compile(String.format(".*%s_?([a-z]{2})?\\.properties$", baseName));
		Map<String, InputStream> data = new HashMap<String, InputStream>();
		List<String> langs = new ArrayList<String>(Arrays.asList(Locale.getISOLanguages()));
		langs.add(null);

		// read the library bundles
		String pckg = ClassPathResourceList.class.getPackage().getName().replace('.', File.separatorChar).concat(File.separator );
		if(pckg.contains("\\"))
			pckg = pckg.replace("\\", "\\\\");
		String search = String.format(".*%s%s.*.properties", pckg, baseName);
		ClassPathResourceList resourceList = new ClassPathResourceList(search);
		for(String item : resourceList.getEntries()) {
			Matcher matcher = baseNamePattern.matcher(item);
			if(!matcher.matches())
				continue;
			String lang = matcher.group(1);
			if(langs.contains(lang)) {
				data.put(lang, resourceList.getInputStream(item));
				logger.debug("library-bundle-search: lang#{} found", lang);
			}
		}

		// read user bundles
		File dir = new File(System.getProperty("user.dir"));
		for(File file : dir.listFiles(new FilenameFilter() {
			@Override
			public boolean accept(File dir, String name) {
				return name.startsWith(baseName) && name.endsWith(".properties");
			}
		})) {
			String path = file.getAbsolutePath();
			Matcher matcher = baseNamePattern.matcher(path);
			if(!matcher.matches())
				continue;
			String lang = matcher.group(1);
			if(langs.contains(lang)) {
				try {
					boolean replaced = data.containsKey(lang);
					data.put(lang, new BufferedInputStream(new FileInputStream(file)));
					if(replaced) 
						logger.debug("user-bundle-search: lang#{} found, lang in library-bundle replaced", lang);
					else
						logger.debug("user-bundle-search: lang#{} found", lang);
				} catch (FileNotFoundException e) {
				}
			}
		}
		
		// build the bundles and cache it
		for(String lang : data.keySet()) {
			try {
				Properties props = new Properties();
				props.load(data.get(lang));
				bundleData.put(lang, props);
			} catch (IOException e) {
				throw new RuntimeException(String.format("A ResourceBundle couldn't be initialized for %s", lang));
			}
		}
		
		logger.info("*** bundle-control successful initialized!");
	}

	public static String get(Locale locale, Key key) {
		String lang = locale.getLanguage();
		try {
			if(!bundleData.containsKey(lang))
				return bundleData.get(null).getProperty(key.getPropertyName());
			return bundleData.get(lang).getProperty(key.getPropertyName());
		} catch (MissingResourceException e) {
			logger.error("Missinig key for locale [{}]: {}", locale.toString(), key);
			return String.format("MISSING KEY | %s |", key);
		}
	} 

	public enum Key {
		UploadNotAllowed("upload.notallowed"),
		CreateFolderNotAllowed("createfolder.notallowed");
		
		private String propertyName;
		
		private Key(String propertyName) {
			this.propertyName = propertyName;
		}
		
		public String getPropertyName() {
			return propertyName;
		}
	}
}


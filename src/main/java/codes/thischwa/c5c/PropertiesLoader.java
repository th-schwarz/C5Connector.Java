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
package codes.thischwa.c5c;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.util.Locale;
import java.util.Properties;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Static object for managing the default and user-defined properties files.<br/>
 * The files are loaded in the following order:
 * <ol>
 * <li>The default properties.</li>
 * <li>The user-defined properties <code>c5connector.properties</code> if present.</li>
 * </ol> 
 * Static wrapper methods are provided for all properties.<br/>
 * Please note: The user-defined properties <em>override</em> the default one.<br/> 
 * Moreover, you can set properties programmatically too ({@link #setProperty(String, String)}), 
 * but make sure to override them <em>before</em> the first call of any property.
 */
public class PropertiesLoader {
	
	private static final Logger logger = LoggerFactory.getLogger(PropertiesLoader.class);
	
	private static final String default_filename = "default.properties";
	
	private static final String local_properties = "/c5connector.properties";
	
	private static Properties properties = new Properties();
	
	private static Locale defaultLocale;

	static {
		// 1. load library defaults
		InputStream inDefault = PropertiesLoader.class.getResourceAsStream(default_filename);

		if (inDefault == null) {
			logger.error("{} not found", default_filename);
			throw new RuntimeException(default_filename + " not found");
		} else {
			if (!(inDefault instanceof BufferedInputStream))
				inDefault = new BufferedInputStream(inDefault);
			try {
				properties.load(inDefault);
				logger.info("{} successful loaded", default_filename);
			} catch (Exception e) {
				String msg = "Error while processing " + default_filename;
				logger.error(msg);
				throw new RuntimeException(msg, e);
			} finally {
				IOUtils.closeQuietly(inDefault);
			}
		}

		// 2. load user defaults if present
		InputStream inUser = PropertiesLoader.class.getResourceAsStream(local_properties);
		if (inUser == null) {
			logger.info("{} not found", local_properties);
		} else {
			if (!(inUser instanceof BufferedInputStream))
				inUser = new BufferedInputStream(inUser);
			try {
				properties.load(inUser);
				inUser.close();
				logger.info("{} successful loaded", local_properties);
			} catch (Exception e) {
				String msg = "Error while processing " + local_properties;
				logger.error(msg);
				throw new RuntimeException(msg, e);
			} finally {
				IOUtils.closeQuietly(inUser);
			}
		}
		
		// 3. build the default locale
		defaultLocale = new Locale(properties.getProperty("default.language"));
	}

	/**
	 * Searches for the property with the specified key in this property list.
	 *
	 * @param key the key
	 * @return the property
	 * @see Properties#getProperty(String)
	 */
	public static String getProperty(final String key) {
		return properties.getProperty(key);
	}

	/**
	 * Sets the property with the specified key in this property list.
	 *
	 * @param key the key
	 * @param value the value
	 * @see Properties#setProperty(String, String)
	 */
	public static void setProperty(final String key, final String value) {
		properties.setProperty(key, value);
	}

	/**
	 * Returns <code>default.encoding</code> property.
	 *
	 * @return the default character encoding
	 */
	public static String getDefaultEncoding() {
		return properties.getProperty("default.encoding");
	}
	
	/**
	 * Returns <code>connector.defaultEncoding</code> property.
	 *
	 * @return the default character encoding of the connector (http response)
	 */
	public static String getConnectorDefaultEncoding() {
		return properties.getProperty("connector.defaultEncoding");
	}
	
	/**
	 * Obtains the default locale dependent on the <code>default.language</code> property.
	 * 
	 * @return the default locale
	 */
	public static Locale getDefaultLocale() {
		return defaultLocale;
	}

	/**
	 * Returns <code>connector.forceSingleExtension</code> property
	 *
	 * @return true, if is force single extension
	 */
	public static boolean isForceSingleExtension() {
		return Boolean.valueOf(properties.getProperty("connector.forceSingleExtension"));
	}
	
	/**
	 * Returns <code>connector.filemanagerPath</code> property
	 *
	 * @return the filemanager path
	 */
	public static String getFilemanagerPath() {
		return properties.getProperty("connector.filemanagerPath");
	}

	/**
	 * Gets the default capacity.
	 *
	 * @return <code>connector.capabilities</code> property
	 */
	public static String getDefaultCapacity() {
		return properties.getProperty("connector.capabilities");
	} 
	
	/**
	 * Gets the maximum allowed file size in MB for upload. 
	 * 
	 * @return <code>connector.maxUploadSize</code> property, or null if not set
	 * @throws NumberFormatException if <code>connector.maxUploadSize</code> is not a valid number
	 */
	static Integer getMaxUploadSize() throws NumberFormatException {
		try {
			return Integer.valueOf(properties.getProperty("connector.maxUploadSize"));
		} catch(Exception e) {
			return null;
		}
	}

	/**
	 * Gets the dimension for thumbnails.
	 *
	 * @return <code>connector.thumbnail.dimension</code> property
	 */
	static String getThumbnailDimension() {
		return properties.getProperty("connector.thumbnail.dimension");
	} 

	/**
	 * Gets the dimension for preview.
	 *
	 * @return <code>connector.preview.dimension</code> property
	 */
	static String getPreviewDimension() {
		return properties.getProperty("connector.preview.dimension");
	} 

	/**
	 * Gets the regex to exclude folders by name.
	 *
	 * @return <code>connector.regex.exclude.folders</code> property
	 */
	static String getRegexToExcludeFolders() {
		return properties.getProperty("connector.regex.exclude.folders");
	} 
	
	/**
	 * Gets the regex to exclude files by name.
	 *
	 * @return <code>connector.regex.exclude.files</code> property
	 */
	static String getRegexToExcludeFiles() {
		return properties.getProperty("connector.regex.exclude.files");
	} 
	
	/**
	 * Returns <code>connector.iconResolverImpl</code> property
	 *
	 * @return the icon resolver impl
	 */
	static String getIconResolverImpl() {
		return properties.getProperty("connector.iconResolverImpl");
	}

	/**
	 * Returns <code>jii.impl</code> property
	 *
	 * @return the dimension provider impl
	 */
	static String getDimensionProviderImpl() {
		return properties.getProperty("jii.impl");
	}

	/**
	 * Returns <code>connector.userPathBuilderImpl</code> property
	 *
	 * @return the user action impl
	 */
	static String getUserPathBuilderImpl() {
		return properties.getProperty("connector.userPathBuilderImpl");
	}

	/**
	 * Returns <code>connector.messageResolverImpl</code> property
	 *
	 * @return the message resolver impl
	 */
	static String getMessageResolverImpl() {
		return properties.getProperty("connector.messageResolverImpl");
	}

	/**
	 * Gets the connector impl.
	 *
	 * @return <code>connector.impl</code> property
	 */
	static String getConnectorImpl() {
		return properties.getProperty("connector.impl");
	}
		
	/**
	 * Gets the file capability impl.
	 *
	 * @return <code>connector.fileCapabilityImpl</code> property
	 */
	static String getFileCapabilityImpl() {
		return properties.getProperty("connector.fileCapabilityImpl");
	} 

	/**
	 * Gets the file config impl.
	 *
	 * @return <code>connector.filemanagerConfigImpl</code> property
	 */
	static String getFilemanagerConfigImpl() {
		return properties.getProperty("connector.filemanagerConfigImpl");
	} 
	
	/**
	 * Gets the EXIF data remover impl.
	 *
	 * @return <code>connector.exifRemoverImpl</code> property
	 */
	static String getExifRemoverImpl() {
		return properties.getProperty("connector.exifRemoverImpl");
	} 

	/**
	 * Gets the default configuration resolver impl.
	 *
	 * @return <code>connector.defaultConfigResolverImpl</code> property
	 */
	static String getDefaultConfigResolverImpl() {
		return properties.getProperty("connector.defaultConfigResolverImpl");
	} 
}

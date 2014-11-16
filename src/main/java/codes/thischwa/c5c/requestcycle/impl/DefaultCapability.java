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
package codes.thischwa.c5c.requestcycle.impl;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import codes.thischwa.c5c.PropertiesLoader;
import codes.thischwa.c5c.requestcycle.Context;
import codes.thischwa.c5c.requestcycle.FilemanagerCapability;
import codes.thischwa.c5c.util.StringUtils;

/**
 * The default implementation of {@link FilemanagerCapability}. All capabilities
 * which set in the property <code>connector.capabilities</code> will be set for each files. 
 */
public class DefaultCapability implements FilemanagerCapability {
	private static Logger logger = LoggerFactory.getLogger(DefaultCapability.class);

	private FilemanagerCapability.Capability[] defaultC5FileCapability;
	
	public DefaultCapability() {
		String capabilitiesStr = PropertiesLoader.getDefaultCapacity();
		defaultC5FileCapability = buildDefaultCapabilities(capabilitiesStr);
	}
	
	@Override
	public Capability[] getCapabilities(Context ctx) { // TODO add type based on extension
		return defaultC5FileCapability;
	}

	/**
	 * Builds the default capabilities.
	 */
	static FilemanagerCapability.Capability[] buildDefaultCapabilities(String capabilitiesStr) {
		if(StringUtils.isNullOrEmptyOrBlank(capabilitiesStr)) 
			return null;
		
		String[] caps = capabilitiesStr.split(",");
		List<FilemanagerCapability.Capability> capList = new ArrayList<>(caps.length);
		for (String cap : caps) {
			FilemanagerCapability.Capability capability = FilemanagerCapability.Capability.valueOf(cap.trim().toLowerCase());
			if(capability == null) {
				logger.warn("Couldn't interprete [{}] as FilemanagerCapability!", cap);
			} else {
				capList.add(capability);
			}
		}
		return (capList.isEmpty()) ? null : capList.toArray(new FilemanagerCapability.Capability[capList.size()]);
	}
}

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

import static org.junit.Assert.*;

import org.junit.Test;

import codes.thischwa.c5c.requestcycle.FilemanagerCapability;
import codes.thischwa.c5c.requestcycle.impl.DefaultCapability;

public class DefaultCapabilityTest {

	@Test
	public void testBuildDefaultCapability() {
		assertNull(DefaultCapability.buildDefaultCapabilities(null));
		assertNull(DefaultCapability.buildDefaultCapabilities(" "));
		
		assertArrayEquals(new FilemanagerCapability.Capability[] { FilemanagerCapability.Capability.select }, 
				DefaultCapability.buildDefaultCapabilities("select"));

		assertArrayEquals(new FilemanagerCapability.Capability[] { FilemanagerCapability.Capability.select, FilemanagerCapability.Capability.delete }, 
				DefaultCapability.buildDefaultCapabilities("select, dElEtE"));
	}

}

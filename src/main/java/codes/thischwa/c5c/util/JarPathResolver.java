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
package codes.thischwa.c5c.util;

import java.io.File;
import java.net.URL;

/**
 * Utility class to resolve a folder or a path inside a jar file. 
 */
public class JarPathResolver {

    public static boolean isJar(String path) {
        File file = new File(JarPathResolver.class.getClassLoader().getResource(path).getFile());
        return (file.listFiles() == null);
    }

    private static URL getJarFolderResource(String folderPath) {
        return JarPathResolver.class.getResource(folderPath);
    }

    public static boolean insideJar(String folderPath) {
        URL folderResource = getJarFolderResource(folderPath);
        return folderResource != null && folderResource.toString().startsWith("jar:");
    }

    public static File getFolder(String folderPath) {
        URL folderResource = getJarFolderResource(folderPath);
        if (folderResource != null) {
            return new File(folderResource.getFile());
        }
        return null;
    }
}

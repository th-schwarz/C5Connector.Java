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

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Paths;
import java.security.CodeSource;
import java.util.HashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import codes.thischwa.c5c.PropertiesLoader;
import codes.thischwa.c5c.impl.JarFilemanagerMessageResolver;
import codes.thischwa.c5c.requestcycle.IconRequestResolver;
import codes.thischwa.c5c.util.JarPathResolver;
import codes.thischwa.c5c.util.PathBuilder;

/**
 * Resolves the icons of the filemanager which is inside a jar file (e.g. when Spring Boot is used). <br/>
 * <br/>
 * Thanks to Vladimir Yushkevic
 */
public class JarIconResolver extends FilemanagerIconResolver {
    private static Logger logger = LoggerFactory.getLogger(JarIconResolver.class);

    private static final String IMAGE_FOLDER = "images";
    private static final String ICONS_FOLDER = "fileicons";

    @Override
    public IconRequestResolver initRequest(String iconPath, String defaultIcon, String directoryIcon) {
        if (!iconCache.containsKey(iconPath))
            buildCache(iconPath, defaultIcon, directoryIcon);
        return iconCache.get(iconPath);
    }

    private void buildCache(String iconPath, String defaultIcon, String directoryIcon) {
        this.defaultIcon = defaultIcon;
        this.directoryIcon = directoryIcon;

        PathBuilder fileSystemIconPath = null;
        if (JarPathResolver.isJar(PropertiesLoader.getFilemanagerPath()))
            fileSystemIconPath = new PathBuilder(IMAGE_FOLDER).addFolder(ICONS_FOLDER);
        else
            fileSystemIconPath = new PathBuilder(iconPath);

        PathBuilder urlPath = new PathBuilder(fileSystemIconPath.toString());
        java.nio.file.Path imageFolder = null;

        try {
            logger.info("getRootPath() {}: " + getRootPath());
            if (JarPathResolver.insideJar(String.valueOf(getRootPath()))) {
                CodeSource src = JarFilemanagerMessageResolver.class.getProtectionDomain().getCodeSource();
                URI uri = src.getLocation().toURI();
                logger.info("Icon folder is inside jar: {}", uri);
                try (FileSystem jarFS = FileSystems.newFileSystem(uri, new HashMap<String, String>())) {
                    if (jarFS.getRootDirectories().iterator().hasNext()) {
                        java.nio.file.Path rootDirectory = jarFS.getRootDirectories().iterator().next();

                        imageFolder = rootDirectory.resolve(getIconFolderPath());
                        if (imageFolder != null) {
                            collectIcons(iconPath, imageFolder, urlPath);
                        } else {
                            throw new RuntimeException("Folder in jar " + imageFolder + " does not exists.");
                        }
                    }
                }
            } else {
                imageFolder = Paths.get(getImageFolderPath());
                logger.info("Icon folder is resolved to: {}", imageFolder);
                collectIcons(iconPath, imageFolder, urlPath);
            }
        } catch (URISyntaxException
                 | IOException e) {
            e.printStackTrace();
        }
    }

    private String getIconFolderPath() {
        return "/" + String.valueOf(new PathBuilder(PropertiesLoader.getFilemanagerPath()).addFolder(IMAGE_FOLDER).addFolder(ICONS_FOLDER));
    }

    private String getImageFolderPath() {
        return JarPathResolver.class.getClassLoader().getResource(String.valueOf(new PathBuilder(PropertiesLoader.getFilemanagerPath()).addFolder(IMAGE_FOLDER))).getPath().substring(1);
    }

    private String getRootPath() {
        return "/" + String.valueOf(new codes.thischwa.c5c.util.PathBuilder(PropertiesLoader.getFilemanagerPath()));
    }
}

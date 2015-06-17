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
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.DirectoryStream;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.CodeSource;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletContext;

import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import codes.thischwa.c5c.PropertiesLoader;
import codes.thischwa.c5c.util.JarPathResolver;
import codes.thischwa.c5c.util.PathBuilder;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Resolves the messages of the filemanager which is inside a jar file (e.g. when Spring Boot is used).  <br/>
 * <br/>
 * Thanks to Vladimir Yushkevic
 */
public class JarFilemanagerMessageResolver extends FilemanagerMessageResolver {
    private static Logger logger = LoggerFactory.getLogger(JarFilemanagerMessageResolver.class);

    private static final String JS_FILE_MASK = "*.js";

    @Override
    public void setServletContext(ServletContext servletContext) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            if (JarPathResolver.insideJar(getMessagesFolderPath())) {
                CodeSource src = JarFilemanagerMessageResolver.class.getProtectionDomain().getCodeSource();
                URI uri = src.getLocation().toURI();
                logger.info("Message folder is inside jar: {}", uri);

                try (FileSystem jarFS = FileSystems.newFileSystem(uri, new HashMap<String, String>())) {
                    if (jarFS.getRootDirectories().iterator().hasNext()) {
                        Path rootDirectory = jarFS.getRootDirectories().iterator().next();

                        Path langFolder = rootDirectory.resolve(getMessagesFolderPath());
                        if (langFolder != null) {
                            try (DirectoryStream<Path> langFolderStream = Files.newDirectoryStream(langFolder, JS_FILE_MASK)) {
                                for(Path langFile : langFolderStream) {
                                    String lang = langFile.getFileName().toString();
                                    InputStream is = Files.newInputStream(langFile);
                                    Map<String, String> langData = mapper.readValue(is, new TypeReference<HashMap<String,String>>(){});
                                    collectLangData(lang, langData);
                                }
                            }
                        } else {
                            throw new RuntimeException("Folder in jar " + langFolder + " does not exists.");
                        }
                    }
                }
            } else {
                File messageFolder = JarPathResolver.getFolder(getMessagesFolderPath());
                logger.info("Message folder resolved to: {}", messageFolder);

                if (messageFolder == null || !messageFolder.exists()) {
                    throw new RuntimeException("Folder " + getMessagesFolderPath() + " does not exist");
                }

                for(File file : messageFolder.listFiles(jsFilter)) {
                    String lang = FilenameUtils.getBaseName(file.getName());
                    Map<String, String> langData = mapper.readValue(file, new TypeReference<HashMap<String,String>>(){});
                    collectLangData(lang, langData);
                }
            }
        } catch (URISyntaxException | IOException e) {
			throw new RuntimeException(e);
        }
    }

    private String getMessagesFolderPath() {
        return "/" + String.valueOf(new PathBuilder(PropertiesLoader.getFilemanagerPath()).addFolder(langPath));
    }
}
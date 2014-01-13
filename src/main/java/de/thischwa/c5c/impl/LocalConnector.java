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
package de.thischwa.c5c.impl;

import java.awt.Dimension;
import java.io.BufferedInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.DirectoryStream;
import java.nio.file.FileSystemAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;

import de.thischwa.c5c.FilemanagerAction;
import de.thischwa.c5c.GenericConnector;
import de.thischwa.c5c.UserObjectProxy;
import de.thischwa.c5c.exception.C5CException;
import de.thischwa.c5c.exception.FilemanagerException;
import de.thischwa.c5c.exception.FilemanagerException.Key;
import de.thischwa.c5c.util.StringUtils;
import de.thischwa.jii.IDimensionProvider;
import de.thischwa.jii.exception.ReadException;

/**
 * The default implementation of the connector servlet.
 * It's a real local filesystem backend connector. The file access is translated as-is
 * to the local filesystem. A servlet context is respected, if it exists.
 */
public class LocalConnector extends GenericConnector {
	
	@Override
	public List<GenericConnector.FileProperties> getFolder(String backendPath, boolean needSize, boolean showThumbnailsInGrid, Set<String> imageExtensions) throws C5CException {
		Path folder = buildAndCheckFolder(backendPath);
		return constructFromDirRequest(folder, needSize, showThumbnailsInGrid, imageExtensions);
	}
	
	@Override
	public GenericConnector.FileProperties getInfo(String backendPath, boolean needSize, boolean showThumbnailsInGrid, Set<String> imageExtensions) throws C5CException {
		Path path = buildRealPath(backendPath);
		if(!Files.exists(path)) {
			logger.error("Requested file not exits: {}", path.toAbsolutePath());
			throw new FilemanagerException(FilemanagerAction.INFO, FilemanagerException.Key.FileNotExists, backendPath);
		}
		return constructFileInfo(path, needSize, showThumbnailsInGrid, imageExtensions);
	}
	
	@Override
	public boolean rename(String oldBackendPath, String sanitizedName) throws C5CException {
		Path src = buildRealPath(oldBackendPath);
		boolean isDirectory = Files.isDirectory(src);
		if(!Files.exists(src)) {
			logger.error("Source file not found: {}", src.toString());
			FilemanagerException.Key key = (isDirectory) ? FilemanagerException.Key.DirectoryNotExist : FilemanagerException.Key.FileNotExists;
			throw new FilemanagerException(FilemanagerAction.RENAME, key, FilenameUtils.getName(oldBackendPath));
		}
		
		Path dest = src.resolveSibling(sanitizedName);
		try {
			Files.move(src, dest);
		} catch (SecurityException | IOException e) {
			logger.warn(String.format("Error while renaming [%s] to [%s]", src.getFileName().toString(), dest.getFileName().toString()), e);
			FilemanagerException.Key key = (Files.isDirectory(src)) ? FilemanagerException.Key.ErrorRenamingDirectory : FilemanagerException.Key.ErrorRenamingFile;
			throw new FilemanagerException(FilemanagerAction.RENAME, key, FilenameUtils.getName(oldBackendPath), sanitizedName);
		} catch (FileSystemAlreadyExistsException e) {
			logger.warn("Destination file already exists: {}", dest.toAbsolutePath());
			FilemanagerException.Key key = (Files.isDirectory(dest)) ? FilemanagerException.Key.DirectoryAlreadyExists : FilemanagerException.Key.FileAlreadyExists;
			throw new FilemanagerException(FilemanagerAction.RENAME, key, sanitizedName);
		}
		return isDirectory;
	}
	
	@Override
	public void createFolder(String backendDirectory, String sanitizedFolderName) throws C5CException {
		Path parentFolder = buildAndCheckFolder(backendDirectory);
		Path newFolder = parentFolder.resolve(sanitizedFolderName);
		try {
			Files.createDirectories(newFolder);
		} catch (FileSystemAlreadyExistsException e) {
			logger.warn("Destination file already exists: {}", newFolder.toAbsolutePath());
			throw new FilemanagerException(FilemanagerAction.CREATEFOLDER, Key.DirectoryAlreadyExists, sanitizedFolderName);
		} catch (SecurityException | IOException e) {
			throw new FilemanagerException(FilemanagerAction.RENAME, FilemanagerException.Key.UnableToCreateDirectory, sanitizedFolderName);
		}
	}
	
	/**
	 * Builds the and check folder.
	 *
	 * @param backendPath the url path
	 * @return the file
	 * @throws FilemanagerException the known exception
	 */
	private Path buildAndCheckFolder(String backendPath) throws FilemanagerException {
		Path parentFolder = buildRealPath(backendPath);
		if(!Files.exists(parentFolder)) {
			logger.error("Source file not found: {}", parentFolder.toAbsolutePath());
			FilemanagerException.Key key = (Files.isDirectory(parentFolder)) ? FilemanagerException.Key.DirectoryNotExist : FilemanagerException.Key.FileNotExists;
			throw new FilemanagerException(FilemanagerAction.CREATEFOLDER, key, parentFolder.getFileName().toString());
		}		
		return parentFolder;
	}

	@Override
	public boolean delete(String backendPath) throws C5CException {
		Path file = buildRealPath(backendPath);
		boolean isDir = Files.isDirectory(file);
		if(!Files.exists(file)) {
			logger.error("Requested file not exits: {}", file.toAbsolutePath());
			FilemanagerException.Key key = (isDir) ? FilemanagerException.Key.DirectoryNotExist : FilemanagerException.Key.FileNotExists;
			throw new FilemanagerException(FilemanagerAction.DELETE, key, file.getFileName().toString());
		}
		boolean success = false;
		if(isDir) {
			try {
				FileUtils.deleteDirectory(file.toFile());
				success = true;
			} catch (IOException e) {
			}
		} else {
			try {
				Files.delete(file);
				success = true;
			} catch (IOException e) {
			}
		}
		if(!success) 
			throw new FilemanagerException(FilemanagerAction.DELETE, FilemanagerException.Key.InvalidDirectoryOrFile, FilenameUtils.getName(backendPath));
		return isDir;
	}

	/**
	 * Builds the real file.
	 *
	 * @param backendPath the url path
	 * @return the file
	 */
	private Path buildRealPath(String backendPath) {
		return Paths.get(backendPath);
	}
	
	/**
	 * Construct file info.
	 * 
	 * @param path the file
	 * @param needSize the need size
	 * @param showThumbnailsInGrid the show thumbnails in grid, can null
	 * @param imageExtensions allowed extensions for images
	 * @return the file info
	 * @throws C5CException the connector exception
	 */
	private FileProperties constructFileInfo(Path path, boolean needSize, boolean showThumbnailsInGrid, Set<String> imageExtensions) throws C5CException {
		try {
			GenericConnector.FileProperties fileProperties;
			Date lastModified = new Date(Files.getLastModifiedTime(path).toMillis());
			// 'needsize' isn't implemented in the filemanager yet, so the dimension is set if we have an image.
			String fileName = path.getFileName().toString();
			String ext = FilenameUtils.getExtension(fileName.toString());
			long size = Files.size(path);
			if(imageExtensions!=null && !StringUtils.isNullOrEmptyOrBlank(ext) && imageExtensions.contains(ext)) {
				IDimensionProvider dp = UserObjectProxy.getImageDimensionProvider();
				dp.set(path.toFile());
				Dimension dim = dp.getDimension();
				fileProperties = GenericConnector.buildForImage(fileName, dim.width, dim.height, size, lastModified);
			} else {
				 fileProperties = (Files.isDirectory(path)) ? buildForDirectory(fileName, lastModified)
						 : buildForFile(fileName, size, lastModified);
			}
			return fileProperties;
		} catch (FileNotFoundException e) {
			throw new C5CException(String.format("File not found: %s", path.toAbsolutePath().toString()));
		} catch (SecurityException | IOException e) {
			throw new C5CException(String.format("Error while analysing %s: %s", path.toAbsolutePath().toString(), e.getMessage()));
		} catch (ReadException e) {
			throw new C5CException(String.format("Error while getting the dimension of the image %s: %s", path.toAbsolutePath().toString(), e.getMessage()));			
		}
	}

	/**
	 * Construct from dir request.
	 * @param dir the dir
	 * @param needSize the need size
	 * @param showThumbnailsInGrid the show thumbnails in grid
	 * @param imageExtensions allowed extensions for images
	 * @return the folder info
	 * @throws C5CException the connector exception
	 */
	private List<FileProperties> constructFromDirRequest(Path dir, boolean needSize, boolean showThumbnailsInGrid, Set<String> imageExtensions) throws C5CException {
		List<FileProperties> props = new ArrayList<>();
		
		// add dirs
		try {
			for(Path d : Files.newDirectoryStream(dir, new DirectoryStream.Filter<Path>() {
				@Override
				public boolean accept(Path entry) throws IOException {
					return Files.isDirectory(entry);
				}})) {
				FileProperties fp = buildForDirectory(d.getFileName().toString(), new Date(Files.getLastModifiedTime(d).toMillis()));
				props.add(fp);
			}
		} catch (IOException | SecurityException e) {
			throw new C5CException(String.format("Error while fetching sub-directories from [%s]: %s", dir.toAbsolutePath().toString(), e.getMessage()));
		}

		// add files
		try {
			for(Path f : Files.newDirectoryStream(dir, new DirectoryStream.Filter<Path>() {
				@Override
				public boolean accept(Path entry) throws IOException {
					return Files.isRegularFile(entry);
				}})) {
				props.add(constructFileInfo(f, needSize, showThumbnailsInGrid, imageExtensions));
			}
		} catch (IOException | SecurityException e) {
			throw new C5CException(String.format("Error while fetching files from [%s]: %s", dir.toAbsolutePath().toString(), e.getMessage()));
		}
		
		return props;
	}
	
	@Override
	public void upload(String urlDirectory, String sanitizedName, InputStream in) throws C5CException {
		Path parentFolder = buildAndCheckFolder(urlDirectory);
		Path fileToSave = parentFolder.resolve(sanitizedName);
		try {
			Files.deleteIfExists(fileToSave);
			Files.copy(in, fileToSave, StandardCopyOption.REPLACE_EXISTING);
		} catch (IOException e) {
			throw new FilemanagerException(FilemanagerAction.UPLOAD, FilemanagerException.Key.InvalidFileUpload, sanitizedName);
		}
	}

	@Override
	public DownloadInfo download(String urlPath) throws C5CException {
		Path file = buildRealPath(urlPath);
		try {
			InputStream in = new BufferedInputStream(Files.newInputStream(file, StandardOpenOption.READ));
			return buildDownloadInfo(in, Files.size(file));
		} catch (FileNotFoundException e) {
			logger.error("Requested file not exits: {}", file.toAbsolutePath());
			throw new FilemanagerException(FilemanagerAction.DOWNLOAD, FilemanagerException.Key.FileNotExists, urlPath);
		} catch (IOException | SecurityException e) {
			String msg = String.format("Error while downloading {}: {}", file.getFileName().toFile(), e.getMessage());
			logger.error(msg, e);
			throw new C5CException(FilemanagerAction.DOWNLOAD, msg);
		}
	}	
}

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

import java.awt.Dimension;
import java.awt.image.BufferedImage;
import java.awt.image.ImagingOpException;
import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
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

import javax.imageio.ImageIO;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.imgscalr.Scalr;

import codes.thischwa.c5c.FilemanagerAction;
import codes.thischwa.c5c.GenericConnector;
import codes.thischwa.c5c.PropertiesLoader;
import codes.thischwa.c5c.UserObjectProxy;
import codes.thischwa.c5c.exception.C5CException;
import codes.thischwa.c5c.exception.FilemanagerException;
import codes.thischwa.c5c.exception.FilemanagerException.Key;

/**
 * The default implementation of the connector servlet.
 * It's a real local filesystem backend connector. The file access is translated as-is
 * to the local filesystem. A servlet context is respected, if it exists. <br/>
 * <br/>
 * For resizing images the 
 * <a href="http://www.thebuzzmedia.com/software/imgscalr-java-image-scaling-library/">imgscalr – Java Image Scaling Library</a> is used.
 */
public class LocalConnector extends GenericConnector {
	
	@Override
	public List<GenericConnector.FileProperties> getFolder(String backendPath, boolean needSize) throws C5CException {
		Path folder = buildAndCheckFolder(backendPath);
		return constructFromDirRequest(folder, needSize);
	}
	
	@Override
	public GenericConnector.FileProperties getInfo(String backendPath, boolean needSize) throws C5CException {
		Path path = buildRealPath(backendPath);
		if(!Files.exists(path)) {
			logger.error("Requested file not exits: {}", path.toAbsolutePath());
			throw new FilemanagerException(FilemanagerAction.INFO, FilemanagerException.Key.FileNotExists, backendPath);
		}
		return constructFileInfo(path, needSize);
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
	 * @return the file info
	 * @throws C5CException the connector exception
	 */
	private FileProperties constructFileInfo(Path path, boolean needSize) throws C5CException {
		InputStream imageIn = null;
		try {
			FileProperties fileProperties;
			Date lastModified = new Date(Files.getLastModifiedTime(path).toMillis());
			// 'needsize' isn't implemented in the filemanager yet, so the dimension is set if we have an image.
			String fileName = path.getFileName().toString();
			String ext = FilenameUtils.getExtension(fileName.toString());
			long size = Files.size(path);
			if(isImageExtension(ext)) {
				imageIn = new BufferedInputStream(Files.newInputStream(path));
				Dimension dim = UserObjectProxy.getDimension(imageIn);
				fileProperties = buildForImage(fileName, dim.width, dim.height, size, lastModified);
			} else {
				 fileProperties = (Files.isDirectory(path)) ? buildForDirectory(fileName, lastModified)
						 : buildForFile(fileName, size, lastModified);
			}
			return fileProperties;
		} catch (FileNotFoundException e) {
			throw new C5CException(String.format("File not found: %s", path.getFileName().toString()));
		} catch (SecurityException | IOException e) {
			logger.warn("Error while analyzing an image!", e);
			throw new C5CException(String.format("Error while getting the dimension of the image %s: %s", path.getFileName().toString(), e.getMessage()));			
		} finally {
			IOUtils.closeQuietly(imageIn);
		}
	}

	/**
	 * Construct from dir request.
	 * @param dir the dir
	 * @param needSize the need size
	 * @return the folder info
	 * @throws C5CException the connector exception
	 */
	private List<FileProperties> constructFromDirRequest(Path dir, boolean needSize) throws C5CException {
		List<FileProperties> props = new ArrayList<>();
		
		// add dirs
		try {
			for(Path d : Files.newDirectoryStream(dir, new DirectoryStream.Filter<Path>() {
				@Override
				public boolean accept(Path entry) throws IOException {
					return Files.isDirectory(entry) && checkFolderName(entry.getFileName().toString());
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
					return Files.isRegularFile(entry) && checkFilename(entry.getFileName().toString());
				}})) {
				props.add(constructFileInfo(f, needSize));
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
	public StreamContent download(String backendPath) throws C5CException {
		Path file = buildRealPath(backendPath);
		try {
			InputStream in = new BufferedInputStream(Files.newInputStream(file, StandardOpenOption.READ));
			return buildStreamContent(in, Files.size(file));
		} catch (FileNotFoundException e) {
			logger.error("Requested file not exits: {}", file.toAbsolutePath());
			throw new FilemanagerException(FilemanagerAction.DOWNLOAD, FilemanagerException.Key.FileNotExists, backendPath);
		} catch (IOException | SecurityException e) {
			String msg = String.format("Error while downloading {}: {}", file.getFileName().toFile(), e.getMessage());
			logger.error(msg, e);
			throw new C5CException(FilemanagerAction.DOWNLOAD, msg);
		}
	}	
	
	@Override
	public StreamContent buildThumbnail(String backendPath, Dimension dim) throws C5CException {
		Path file = buildRealPath(backendPath);
		String ext = FilenameUtils.getExtension(backendPath);

		InputStream in = null;
		try {
			in = Files.newInputStream(file);
			return resize(in, ext, dim);
		} catch (IllegalArgumentException | ImagingOpException | IOException e) {
			throw new C5CException(FilemanagerAction.THUMBNAIL, e.getMessage());
		} finally { 
			IOUtils.closeQuietly(in);
		}
	}
	
	@Override
	public StreamContent preview(String backendPath, Dimension maxPreviewDim) throws C5CException {
		Path file = buildRealPath(backendPath);
		try {
			Dimension currentDim = UserObjectProxy.getDimension(Files.newInputStream(file));
			if(maxPreviewDim != null && (currentDim.width > maxPreviewDim.width || currentDim.height > maxPreviewDim.height)) {
				return resize(new BufferedInputStream(Files.newInputStream(file)), FilenameUtils.getExtension(backendPath), maxPreviewDim);
			}
			return buildStreamContent(Files.newInputStream(file), Files.size(file));
		} catch (IOException e) {
			throw new C5CException(FilemanagerAction.PREVIEW, e.getMessage());
		}
	}

	@Override
	public StreamContent resize(InputStream imageIn, String imageExt, Dimension dim) throws IOException {
		BufferedImage img = null;
		BufferedImage newImg = null;
		try {
			img = ImageIO.read(imageIn);
			newImg = Scalr.resize(img, Scalr.Method.BALANCED, Scalr.Mode.AUTOMATIC, dim.width, dim.height);
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			ImageIO.write(newImg, imageExt, baos);
			baos.flush();
			return buildStreamContent(new ByteArrayInputStream(baos.toByteArray()), baos.size());
		} catch (IllegalArgumentException | ImagingOpException e) {
			throw new IOException(e);
		} finally { 
			if(img != null)
				img.flush();
			if(newImg != null)
				newImg.flush();
		}
	}

	@Override
	public String editFile(String backendPath) throws C5CException {
		Path file = buildRealPath(backendPath);
		InputStream in = null;
		try {
			in = Files.newInputStream(file, StandardOpenOption.READ);
			return IOUtils.toString(in, PropertiesLoader.getDefaultEncoding());
		} catch (IOException e) {
			throw new C5CException(FilemanagerAction.EDITFILE, e.getMessage());
		} finally {
			IOUtils.closeQuietly(in);
		}
	}
	
	@Override
	public void saveFile(String backendPath, String content) throws C5CException {
		Path file = buildRealPath(backendPath);
		OutputStream out = null;
		try {
			out = Files.newOutputStream(file, StandardOpenOption.CREATE, StandardOpenOption.WRITE);
			IOUtils.write(content, out, PropertiesLoader.getDefaultEncoding());
		} catch (IOException e) {
			throw new C5CException(FilemanagerAction.SAVEFILE, e.getMessage());
		} finally {
			IOUtils.closeQuietly(out);
		}
	}

	@Override
	public void replace(String backendPath, InputStream in) throws C5CException {
		Path file = buildRealPath(backendPath);
		try {
			Files.copy(in, file, StandardCopyOption.REPLACE_EXISTING);
		} catch (IOException e) {
			throw new FilemanagerException(FilemanagerAction.REPLACE, FilemanagerException.Key.InvalidFileUpload, file.getFileName().toString());
		}
	}
}

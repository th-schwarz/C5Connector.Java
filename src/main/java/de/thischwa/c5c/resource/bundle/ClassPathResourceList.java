package de.thischwa.c5c.resource.bundle;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import org.apache.commons.io.IOUtils;

/**
 * 
 * 
 * Inspired by an article of 'stoughto': http://forums.devx.com/showthread.php?153784-how-to-list-resources-in-a-package
 */
public class ClassPathResourceList {
	private final Collection<String> entries = new HashSet<String>();
	private Pattern entriesPattern;

	/**
	 * for all elements of java.class.path get a Collection of resources Pattern pattern = Pattern.compile(".*"); gets all resources
	 * 
	 * @param pattern
	 *            the pattern to match
	 */
	public ClassPathResourceList(final String pattern) {
		entriesPattern = Pattern.compile(pattern);
		String classPath = System.getProperty("java.class.path", ".");
		String[] classPathElements = classPath.split(":");
		for (String element : classPathElements) {
			getResources(element);
		}
	}

	private void getResources(final String classPathElement) {
		File file = new File(classPathElement);
		if (file.isDirectory()) {
			getResourcesFromDirectory(file);
		} else {
			getResourcesFromJarFile(file);
		}
	}

	private void getResourcesFromJarFile(final File file) {
		ZipFile zf;
		try {
			zf = new ZipFile(file);
		} catch ( Exception e) {
			throw new Error(e);
		}
		Enumeration<? extends ZipEntry> e = zf.entries();
		while (e.hasMoreElements()) {
			ZipEntry ze = e.nextElement();
			String fileName = ze.getName();
			boolean accept = entriesPattern.matcher(fileName).matches();
			if (accept) {
				entries.add(fileName);
			}
		}
		try {
			zf.close();
		} catch (IOException ioe) {
			throw new Error(ioe);
		}
	}

	private void getResourcesFromDirectory(final File directory) {
		File[] fileList = directory.listFiles();
		for (File file : fileList) {
			if (file.isDirectory()) {
				getResourcesFromDirectory(file);
			} else {
				try {
					String fileName = file.getCanonicalPath();
					boolean accept = entriesPattern.matcher(fileName).matches();
					if (accept) {
						entries.add(fileName);
					}
				} catch (IOException e) {
					throw new Error(e);
				}
			}
		}
	}

	public Collection<String> getEntries() {
		return entries;
	}

	public InputStream getInputStream(String path) {
		try {
			@SuppressWarnings("resource")
			InputStream in = (path.startsWith(File.separator)) ? new FileInputStream(path) : ClassPathResourceList.class
					.getResourceAsStream(File.separator + path);
			if (!(in instanceof BufferedInputStream))
				in = new BufferedInputStream(in);
			return in;
		} catch(IOException e) {
			return null;
		}
	}

	/**
	 * list the resources that match args[0]
	 * 
	 * @param args
	 *            args[0] is the pattern to match, or list all resources if there are no args
	 */
	public static void main(final String[] args) {
		String pckg = ClassPathResourceList.class.getPackage().getName().replace('.', File.separatorChar).concat(File.separator );
		if(pckg.contains("\\"))
			pckg = pckg.replace("\\", "\\\\");
		ClassPathResourceList cprl = new ClassPathResourceList(".*" + pckg + "userActionMessages.*properties");
		for (String name : cprl.getEntries()) {
			System.out.println(name);
			InputStream in = null;
			try {
				in = cprl.getInputStream(name);
				//System.out.println(IOUtils.toString(in));
			} finally {
				IOUtils.closeQuietly(in);
			}
		}
	}
}
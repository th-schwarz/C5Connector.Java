package de.thischwa.c5c.resource;

import static org.junit.Assert.*;

import java.util.Locale;

import org.junit.Test;

public class UserActionMessageHolderTest {

	@Test
	public void testGetDefault() {
		String expected = "You are not authorized to upload files!";
		assertEquals(expected, UserActionMessageHolder.get(Locale.ITALIAN, UserActionMessageHolder.Key.UploadNotAllowed));

		expected = "You are not authorized to create folders!";
		assertEquals(expected, UserActionMessageHolder.get(Locale.ITALIAN, UserActionMessageHolder.Key.CreateFolderNotAllowed));
	}

	@Test
	public void testGetDE() {
		String expected = "Sie sind nicht berechtigt Dateien hochzuladen!";
		assertEquals(expected, UserActionMessageHolder.get(Locale.GERMAN, UserActionMessageHolder.Key.UploadNotAllowed));

		expected = "Sie sind nicht berechtigt Verzeichnisse zu erstellen!";
		assertEquals(expected, UserActionMessageHolder.get(Locale.GERMAN, UserActionMessageHolder.Key.CreateFolderNotAllowed));
	}
}

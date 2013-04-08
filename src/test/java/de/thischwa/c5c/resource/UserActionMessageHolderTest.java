package de.thischwa.c5c.resource;

import static org.junit.Assert.*;

import java.util.Locale;

import org.junit.Test;

public class UserActionMessageHolderTest {

	@Test
	public void testGetDefault() {
		String expected = "Upload is not allowed.";
		assertEquals(expected, UserActionMessageHolder.get(Locale.ITALIAN, UserActionMessageHolder.Key.UploadNotAllowed));

		expected = "The creation of a folder is not allowed.";
		assertEquals(expected, UserActionMessageHolder.get(Locale.ITALIAN, UserActionMessageHolder.Key.CreateFolderNotAllowed));
	}

	@Test
	public void testGetDE() {
		String expected = "Das Hochladen ist nicht erlaubt.";
		assertEquals(expected, UserActionMessageHolder.get(Locale.GERMAN, UserActionMessageHolder.Key.UploadNotAllowed));

		expected = "Das Anlegen eines Verzeichnisses ist nicht erlaubt.";
		assertEquals(expected, UserActionMessageHolder.get(Locale.GERMAN, UserActionMessageHolder.Key.CreateFolderNotAllowed));
	}

}

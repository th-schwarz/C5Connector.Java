package de.thischwa.c5c.resource;

import static org.junit.Assert.*;

import java.util.Locale;

import org.junit.Test;

public class PropertiesLoaderTest {

	@Test
	public void testGetDefaultLocale() {
		Locale expected = Locale.ENGLISH;
		assertEquals(expected, PropertiesLoader.getDefaultLocale());
	}

}

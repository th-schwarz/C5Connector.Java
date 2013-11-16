package de.thischwa.c5c.resource.filemanager;

import static org.junit.Assert.*;

import org.junit.Test;

public class UploadTest {

	@Test
	public void testIsFileSizeLimitAuto() {
		Upload u = new Upload();
		u.setFileSizeLimit(20);
		assertFalse(u.isFileSizeLimitAuto());
		assertEquals(20, u.getFileSizeLimit());
		
		u.setFileSizeLimit();
		assertTrue(u.isFileSizeLimitAuto());
		assertEquals(-1, u.getFileSizeLimit());
	}

}

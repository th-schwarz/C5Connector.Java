/*
 * C5Connector.Java - The Java backend for the filemanager of corefive.
 * It provides a simple object for creating an editor instance.
 * Copyright (C) Thilo Schwarz
 * 
 * == BEGIN LICENSE ==
 * 
 * Licensed under the terms of any of the following licenses at your
 * choice:
 * 
 *  - GNU General Public License Version 2 or later (the "GPL")
 *    http://www.gnu.org/licenses/gpl.html
 * 
 *  - GNU Lesser General Public License Version 2.1 or later (the "LGPL")
 *    http://www.gnu.org/licenses/lgpl.html
 * 
 *  - Mozilla Public License Version 1.1 or later (the "MPL")
 *    http://www.mozilla.org/MPL/MPL-1.1.html
 * 
 * == END LICENSE ==
 */
package de.thischwa.c5c.util;

import static org.junit.Assert.*;

import org.junit.Test;

import de.thischwa.c5c.util.FileUtils;

public class FileUtilsTest {

	@Test
    public void isSingleExtension01() {
    	boolean condition = FileUtils.isSingleExtension("hacked.txt");
    	assertTrue(condition);
    }

	@Test
    public void isSingleExtension02() {
    	boolean condition = FileUtils.isSingleExtension("hacked.php_txt");
    	assertTrue(condition);
    }

	@Test
    public void isSingleExtension03() {
    	boolean condition = FileUtils.isSingleExtension("hacked.php.txt");
    	assertFalse(condition);
    }

	@Test
    public void isSingleExtension04() {
    	boolean condition = FileUtils.isSingleExtension("hacked.txt.");
    	assertFalse(condition);
    }

	@Test
    public void isSingleExtension05() {
    	boolean condition = FileUtils.isSingleExtension("hacked..txt");
    	assertFalse(condition);
    }

	@Test
    public void forceSingleExtension01() {
    	String actual = FileUtils.forceSingleExtension("hacked.txt");
    	assertEquals("hacked.txt", actual);
    }
	
	@Test
    public void forceSingleExtension02() {
    	String actual = FileUtils.forceSingleExtension("hacked.php_txt");
    	assertEquals("hacked.php_txt", actual);
    }

	@Test
    public void forceSingleExtension03() {
    	String actual = FileUtils.forceSingleExtension("hacked.php.txt");
    	assertEquals("hacked_php.txt", actual);
    }

	@Test
    public void forceSingleExtension04() {
    	String actual = FileUtils.forceSingleExtension("hacked..txt");
    	assertEquals("hacked_.txt", actual);
    }

	@Test
    public void sanitizeName01() {
    	assertEquals("a.b_c_d_e_f_g_h_i_", FileUtils.sanitizeName("a.b|c<d>e:f?g*h<i>"));
    }
	
	@Test
	public void sanitizeName02() {
		assertEquals("a.b_c_d_e_f_g_h_i_", FileUtils.sanitizeName("a.b|c\u007Fd>e:f\u0005g*h<i>"));
	}

	@Test
    public void sanitizeName03() {
    	assertEquals("b_c_d_e_f_g_h_i_", FileUtils.sanitizeName("b|c<d>e:f?g*h<i>"));
    }

	@Test
    public void sanitizeName04() {
    	assertEquals("name.ext1.ext2", FileUtils.sanitizeName("name.ext1.ext2"));
    }
	
	@Test
    public void sanitizeName05() {
    	assertEquals("name.ext1", FileUtils.sanitizeName("name.ext1"));
    }
}

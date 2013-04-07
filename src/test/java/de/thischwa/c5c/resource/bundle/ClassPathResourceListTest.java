package de.thischwa.c5c.resource.bundle;

import static org.junit.Assert.*;
import static org.junit.matchers.JUnitMatchers.*;

import java.util.Collection;
import java.util.HashSet;

import org.junit.Test;

import de.thischwa.c5c.resource.bundle.ClassPathResourceList;

public class ClassPathResourceListTest {

	/**
	 * Cuts the application dir of all entries. This is needed in order to run these tests on other systems.
	 * 
	 * @param entries
	 * @return
	 */
	static Collection<String> cleanEntries(Collection<String> entries) {
		String appDir = System.getProperty("user.dir");
		Collection<String> newEntries = new HashSet<String>();
		for (String entry : entries) {
			if (entry.startsWith(appDir))
				entry = entry.substring(appDir.length(), entry.length());
			newEntries.add(entry);
		}
		return newEntries;
	}

	@Test
	public void testGetEntries() {
		ClassPathResourceList cprl = new ClassPathResourceList(".*userActionMessages.*properties");
		assertEquals(2, cprl.getEntries().size());

		Collection<String> entries = cleanEntries(cprl.getEntries());
		assertThat(
				entries,
				hasItems("/target/classes/de/thischwa/c5c/resource/userActionMessages_de.properties",
						"/target/classes/de/thischwa/c5c/resource/userActionMessages.properties"));
	}

}

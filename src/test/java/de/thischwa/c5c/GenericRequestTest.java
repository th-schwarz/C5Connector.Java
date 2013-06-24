package de.thischwa.c5c;

import org.eclipse.jetty.testing.ServletTester;
import org.junit.After;

public class GenericRequestTest {

	protected ServletTester tester;

	@After
	public void tearDown() throws Exception {
		tester.stop();
	}

	String cleanResponse(String response) {
		String actual = response.replaceAll("\\\"Date Modified\\\":\\\"\\d*/\\d*/\\d*\\\",", "");
		return actual;
	}
}

package codes.thischwa.c5c.util.jackson;

import static org.junit.Assert.*;

import org.junit.Test;

import com.fasterxml.jackson.databind.ObjectMapper;

public class ExtendedJacksonTest {

	@Test
	public void testSerialization() throws Exception {
		C c = new C();
		c.setKey("c");
		B b = new B();
		b.setKey("b");
		b.setObj(c);
		A a = new A();
		a.setKey("a");
		a.setObj(b);

		ObjectMapper mapper = new ObjectMapper(); 
		String actual = mapper.writeValueAsString(a);
		assertEquals("{\"key\":\"a\",\"obj\":{\"key\":\"b\",\"obj\":{\"key\":\"c\"}}}", actual);
	}

	@Test
	public void testDeserialization() throws Exception {
		ObjectMapper mapper = new ObjectMapper();
		A a = mapper.readValue("{\"key\":\"a\",\"obj\":{\"key\":\"b\",\"obj\":{\"key\":\"c\"}}}", A.class);

		assertEquals("a", a.getKey());
		assertEquals("b", a.getObj().getKey());
		assertEquals("c", a.getObj().getObj().getKey());
	}
}

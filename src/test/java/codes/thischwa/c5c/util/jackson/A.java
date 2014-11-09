package codes.thischwa.c5c.util.jackson;


class A {
	private String key;
	private B obj = new B();
	
	public A() {
	}
	
	public String getKey() {
		return key;
	}
	public void setKey(String key) {
		this.key = key;
	}
	public B getObj() {
		return obj;
	}
	public void setObj(B obj) {
		this.obj = obj;
	}
}
package codes.thischwa.c5c.util.jackson;


class B {
	private String key;
	private C obj = new C();

	public B() {
	}
	
	public String getKey() {
		return key;
	}
	public void setKey(String key) {
		this.key = key;
	}
	public C getObj() {
		return obj;
	}
	public void setObj(C obj) {
		this.obj = obj;
	}
}
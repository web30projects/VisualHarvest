package visualharvester.service;

public class Pair {
	String key, value;

	public Pair(PairKey key, String value) {
		this.key = key.getKey();
		this.value = value;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

}

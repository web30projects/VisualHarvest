package visualharvester.service;

import java.util.ArrayList;
import java.util.List;

public class ServiceConfig {

	List<Pair> pairs = new ArrayList<>();

	public void put(String key, String value) {
		pairs.add(new Pair(key, value));
	}

	public void put(Pair newPair) {
		for (Pair pair : pairs) {
			if (pair.getKey().equals(newPair.getKey())) {
				return;
			}
		}
		pairs.add(newPair);
	}

	public String get(String key) {
		for (Pair pair : pairs) {
			if (pair.getKey().equals(key)) {
				return pair.getValue();
			}
		}
		return null;
	}

}

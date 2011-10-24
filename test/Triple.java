package test;

/**
 * This class represents a generic data structure version of a pair
 *
 * @author Markus Köppen, Andreas Hasselberg
 *
 * @param <K> type of the key
 * @param <V> type of the value
 */
public class Triple<K,V1,V2> {

	K key;
	V1 value1;
	V2 value2;

	/**
	 * Constructor which initialize key and value with null
	 */
	public Triple() {
		key = null;
		value1 = null;
		value2 = null;
	}

	/**
	 * Constructor which initialize key and value with specific content
	 * @param key value of the key
	 * @param value value of the value
	 */
	public Triple(K key, V1 value1, V2 value2) {
		this.key = key;
		this.value1 = value1;
		this.value2 = value2;
	}

	/**
	 * returns a String representation of the pair
	 */
	public String toString() {
		return key.toString() + ":" + value1.toString() + ":" + value2.toString();
	}

	/**
	 * returns the key
	 * @return key
	 */
	public K getKey() {
		return key;
	}

	/**
	 * sets the key
	 * @param key new key
	 */
	public void setKey(K key) {
		this.key = key;
	}

	/**
	 * returns the value
	 * @return value
	 */
	public V1 getValue1() {
		return value1;
	}
	public V2 getValue2() {
		return value2;
	}

	/**
	 * sets the value
	 * @param value new value
	 */
	public void setValue1(V1 value1) {
		this.value1 = value1;
	}
	public void setValue2(V2 value2) {
		this.value2 = value2;
	}
}
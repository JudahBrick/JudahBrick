package tables;

import java.util.ArrayList;

//import java.Lang.Comparable;
public class Key<K, V extends Comparable<K>> implements Comparable {

	private K key;		//The key for these values
	private ArrayList<V> value = new ArrayList<>();		//The list of values
	private Node child;		// The Node child of this Key

	/**
	 * A new BTree Key to be inserted into the BTree
	 * @param key The value to be the key
	 * @param value The value you would like to add to this key
	 * @param child The child of this Key
	 */
	public Key(K key, V value, Node child) {
		this.setKey(key);
		this.addValue(value);
		this.child = child;
	}

	/**
	 * Returns the ArrayList of the values in this Key
	 * @return The list of values in this key
	 */
	public ArrayList<V> getValue() {
		return value;
	}

	/**
	 * Adds a value to this key
	 * @param value The value you wish to add
	 */
	public void addValue(V value) {
		if(value == null){
			this.value.clear();
			return;
		}
		this.value.add(value);
	}

	/**
	 * Deletes the values in this key
	 */
	public void setNull()
	{
		value = new ArrayList<V>();
	}
	
	/**
	 * Returns the value held as the key
	 * @return The value that is the Key
	 */
	public K getKey() {
		return key;
	}

	/**
	 * Sets the Key for this key
	 * @param key  The value to be key
	 */
	public void setKey(K key) {
		this.key = key;
	}

	/**
	 * Returns the Child of this key
	 * @return The child
	 */
	public Node getChild() {
		return child;
	}

	/**
	 * Sets the key's child
	 * @param child
	 */
	public void setChild(Node child) {
		this.child = child;
		this.value = null;
	}


	public int compareTo(Object obj) {
		if(this.getKey().equals(BTree.getSentinel().getKey())){
			return -1;
		}
		if(((Key) obj).getKey().equals(BTree.getSentinel().getKey())){
			return 1;
		}
		if (this.getKey().equals((((Key) obj).getKey()))) {
			return 0;
		}
		return ((Comparable<K>) this.getKey()).compareTo((K) ((Key) obj).getKey());
	}

	/**
	 * checks if it is equal to another Key
	 * @param key the Key we are testing for
	 * @return True or false
	 */
	public boolean equals(Key key) {
		return this.getKey() == key.getKey();
	}

}
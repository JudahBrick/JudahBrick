package tables;

import java.lang.IllegalArgumentException;
import java.util.ArrayList;

public class BTree<K, V extends Comparable<Key>> {
	
	public enum DataType{INT,VARCHAR,DECIMAL,BOOLEAN};

	private static final Object sentinelKey = "*";
	//private Key second;
	private static final Key sentinel =  new Key(sentinelKey, null, null);
	private static final int M = 6;
	private Node root;		//The root of the Tree
	private Node originalRoot;		// The first root the BTree had
	private int size = 0;		//The amount of keys in the BTree
	private int height = 0;		//The height of the Tree
	private String name;	//The name of the BTree
	private String dataType;	//The data type for the key
	
	/**
	 * Creates a new BTree for the Data Type you are using as the keys
	 * @param dataType The data type of the keys
	 */
	public BTree(String dataType) {
		root = new Node(M);
		root.put(sentinel);
		this.dataType = dataType;
		originalRoot = root;
		size++;
	}

	/**
	 * Adds a new Key and VAlue to the BTree
	 * @param key The key you are using for this value
	 * @param value The value you wish to add to the BTree under this Key
	 */
	public void add(K key, V value){// throws IllegalArgumentException{
		//System.out.println(key.getClass());
		if(!key.getClass().getName().equals(dataType)){
			throw new IllegalArgumentException("All keys in the BTree need be of the same type");
		}
		Key theNewKey = new Key(key, value, null);
		Node child = add(root, theNewKey, height);
		if (child != null) {
			Node newRoot = new Node(M);
			Key firstKey = new Key(root.getFirstKey(), null, root);
			Key secondKey = new Key(child.getFirstKey(), null, child);
			newRoot.put(firstKey);
			newRoot.put(secondKey);
			// Node testNode = newRoot.get(secondKey.getKey()).getChild();
			root = newRoot;
			height++;
		}
		size++;
	}

	/**
	 * Recursively climbs down the tree to find the right spot to add the New Key
	 * @param node	The root or the current node we are visiting
	 * @param key The key we are using 
	 * @param hieght The height of the tree we are currently at
	 * @return A new node if a new node was created from a split
	 */
	private Node add(Node node, Key key, int hieght) {
		if (hieght == 0) {
			return node.put(key);
		}

		if (hieght > 0) {
			Node x = add(node.findSpot(key, hieght), key, hieght - 1);

			if (x == null) {
				return null;
			}
			Key newChild = new Key(x.getFirstKey(), null, x);
			return node.put(newChild);
		}

		return null;
	}

	/**
	 * Returns the Key with its value
	 * @param key a key with just it's key as a value in order to search
	 * @return The
	 */
	public Key get(Key key) {
		return getKey(root, key, height);
	}

	/**
	 * Returns the list of values under a certain key
	 * @param key The value we are using as the key
	 * @return The list of values
	 */
	public ArrayList<V> get(K key) {
		Key theKey = new Key(key, null, null);
		Key theKeyValue =  getKey(root, theKey, height);
		return theKeyValue.getValue();
	}

	/**
	 * Recursively climbs down the tree to find the Key Value we are looking for
	 * @param root The current Node we are at
	 * @param key The value we are using as the key
	 * @param hieght The height of the tree we are currently at
	 * @return The Key with its ArrayList of values
	 */
	private Key getKey(Node root, Key key, int hieght) {
		if (hieght == 0) {
			return root.get(key);
		} else {
			Node newNode = root.findSpot(key, hieght);
			return getKey(newNode, key, hieght - 1);
		}
	}

	/**
	 * The size of the BTree
	 * @return The size of the BTree
	 */
	public int size() {
		return size;
	}

	/**
	 * Deletes the values of a Key
	 * @param key the value we are using as the key
	 */
	public void delete(K key) {
		add(key, null);
	}
	
	/**
	 * Return the Sentinel
	 * @return The sentinal
	 */
	public static Key getSentinel()
	{
		return sentinel;
	}
	
	/**
	 * Returns all of the values in the BTree
	 * @return The list of values
	 */
	public ArrayList<V> getAll()
	{
		return getAll(originalRoot);
	}

	/**
	 * Returns the list of all of the values in this BTree
	 * @param root The root of the tree
	 * @return The list of all of the values
	 */
	private ArrayList<V> getAll(Node root)
	{
		ArrayList<V> allKeys = new ArrayList<>();
		//root.getAll();
		if(root.hasRight()){
			allKeys.addAll(root.getAll());
			allKeys.addAll(getAll(root.getRight()));//root.getRight().getAll();
			//allKeys.addAll(rightKeys);
		}
		if(!root.hasRight()){
			allKeys = root.getAll();
		}
		return allKeys;
	}
	
	/**
	 * Returns the list of values greater or equal to a certain key
	 * @param value The value being used as the key
	 * @return The list of values under the keys greater or equal to the inserted key
	 */
	public ArrayList<V> greaterOrEqual(K value)
	{
		ArrayList<V> theKeys = new ArrayList<V>();
		if(this.get(value) != null){
			theKeys.addAll(this.get(value));
		}
		theKeys.addAll(getGreater(value));
		return theKeys;
	}
	
	/**
	 * Returns all of the values under the keys that are greater than inserted key
	 * @param value The value we are using as the key
	 * @return The list of the values who's key is greater than the key inserted
	 */
	public ArrayList<V> getGreater(K value)
	{
		return getGreater(originalRoot,value);
	}

	/**
	 * Returns all of the values under the keys that are greater than inserted key
	 * @param root The root of the tree
	 * @param value The value we are using as the key
	 * @return The list of the values who's key is greater than the key inserted
	 */
	private ArrayList<V> getGreater(Node root, K value)
	{
		Key greaterKey = new Key(value, null, null);
		ArrayList<V> allKeys = new ArrayList<>();
		//root.getAll();
		if(root.hasRight()){
			allKeys.addAll(root.getAllGreater(greaterKey));		
			allKeys.addAll(getGreater(root.getRight(), value));//root.getRight().getAll();
			//allKeys.addAll(rightKeys);
		}
		if(!root.hasRight()){
			allKeys = root.getAllGreater(greaterKey);
		}
		return allKeys;
	}
	
	/**
	 * Returns all of the values under the keys that are less than or equal to the inserted key
	 * @param value The value we are using as the key
	 * @return All of the values under the keys that are less than or equal to the inserted key
	 */
	public ArrayList<V> lessOrEqual(K value)
	{
		ArrayList<V> theKeys = new ArrayList<>();
		
		theKeys.addAll(getLess(value));
		ArrayList<V> theVal = this.get(value);
		if(theVal != null){
			theKeys.addAll(theVal);
		}
		return theKeys;
	}
	
	/**
	 * Returns all of the values under the keys that are less than the inserted key
	 * @param value The value we are using as the key
	 * @return Returns all of the values under the keys that are less than the inserted key
	 */
	public ArrayList<V> getLess(K value)
	{
		return getLess(originalRoot,value);
	}

	/**
	 * Returns all of the values under the keys that are less than the inserted key
	 * @param root The root of the BTree
	 * @param value The value we are using as the key
	 * @return Returns all of the values under the keys that are less than the inserted key
	 */
	private ArrayList<V> getLess(Node root, K value)
	{
		Key lessKey = new Key(value, null, null);
		ArrayList<V> allKeys = new ArrayList<>();
		//root.getAll();
		if(root.hasRight()){
			allKeys.addAll(root.getAllLess(lessKey));		
			allKeys.addAll(getLess(root.getRight(), value));//root.getRight().getAll();
			//allKeys.addAll(rightKeys);
		}
		if(!root.hasRight()){
			allKeys = root.getAllLess(lessKey);
		}
		return allKeys;
	}
	
	/**
	 * Returns the name of the BTree
	 * @return The name of the BTree
	 */
	public String getName() {
		return name;
	}

	/**
	 * Sets the name of the BTree
	 * @param name The name to be set to the BTree
	 */
	public void setName(String name) {
		this.name = name;
	}

	
	public static <V> void main(String args[]) {

		String a = "a";
		BTree BTree = new BTree(a.getClass().getName());
		/*
		for (int i = 1; i <= 50; i++) {
			BTree.add(i, i * 2);
		}
		
		BTree.delete(6);
		System.out.println(BTree.get(5).getValue());
		System.out.println(BTree.get(48).getValue());
*/
		BTree.add("A", 1);
		BTree.add("B", 2);
		BTree.add("C", 3);
		BTree.add("D", 4);
		BTree.add("E", 5);
		BTree.add("F", 6);
		BTree.add("G", 7);
		BTree.add("H", 8);
		BTree.add("I", 9);
		BTree.add("J", 10);
		BTree.add("K", 11);
		BTree.add("L", 12);
		BTree.add("M", 13);
		BTree.add("N", 14);
		BTree.add("O", 15);
		BTree.add("P", 16);
		BTree.add("Q", 17);
		BTree.add("Q", 1);
		BTree.add("Q", 2);
		BTree.add("Q", 3);
		BTree.add("Q", 4);
		BTree.add("Q", 5);
		BTree.add("A", 1);
		BTree.add("B", 2);
		BTree.add("C", 3);
		BTree.add("D", 4);
		BTree.add("E", 5);
		BTree.add("F", 6);
		BTree.add("G", 7);
		BTree.add("H", 8);
		BTree.add("I", 9);
		BTree.add("J", 10);
		BTree.add("K", 11);
		BTree.add("L", 12);
		BTree.add("M", 13);
		BTree.add("N", 14);
		BTree.add("O", 15);
		BTree.add("P", 16);
		BTree.add("Q", 17);
		BTree.add("Q", 1);
		BTree.add("Q", 2);
		BTree.add("Q", 3);
		BTree.add("Q", 4);
		BTree.add("Q", 5);
		

		
		ArrayList<V> keys = BTree.lessOrEqual("K");
		for(int i =0; i < keys.size(); i++){
			System.out.println(keys.get(i));
		}
		
		System.out.println(BTree.get("B"));
		System.out.println(BTree.get("F"));
		System.out.println(BTree.get("Q"));
		/*
		System.out.println();
		System.out.println();
		System.out.println();
		V theQ = BTree.get("Q");
		ArrayList<Object> theValues = theQ.getValue();
		for(int i = 0; i < theValues.size(); i++){
			System.out.println(theValues.get(i));
		}
		*/
	}

	
}

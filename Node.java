package tables;

import java.util.ArrayList;

public class Node<K, V> {// extends Comparable<Key>> {

	private Key[] theNode;		//The actual node where the Keys are stored
	private Node parent;	//The parent of the Node
	private int size = 0;
	private int sizeCap;	//The limit of key entries
	private Node left = null;	// The node to its left
	private Node right = null;	//The node to its right

	/**
	 * Creates a new Node to be placed in a BTree
	 * @param size The Max size of the node
	 */
	public Node(int size) {
		theNode = new Key[size];
		sizeCap = size;
		parent = null;
	}

	/**
	 * returns how manyKeys are in this Node
	 * @return The number of keys in this node
	 */
	public int size() {
		return size;
	}

	/**
	 * Sets the size of the node
	 * @param size The number you would like to set the size to
	 */
	private void setSize(int size) {
		this.size = size;
	}

	/**
	 * Returns the parent of this node
	 * @return the parent
	 */
	public Node<K, V> getParent() {
		return parent;
	}

	/**
	 * Sets the parent of this Node
	 * @param parentNode The Node to become a Parent  Mazel Tov
	 */
	public void setParent(Node<K, V> parentNode) {
		this.parent = parentNode;
	}

	/**
	 * Returns the max size of this Node
	 * @return The Max size of the Node
	 */
	private int getSizeCap() {
		return sizeCap;
	}

	/**
	 * Inserts a new key into the Node
	 * @param theKey The Key to be inserted
	 * @return Returns a new Node if this Node reached it's size cap and split
	 * */
	public Node<K, V> put(Key theKey) {

		for (int i = 0; i <= size; i++) {
			if (theNode[i] == null) {
				theNode[i] = theKey;
				size++;
				break;
			}
			int compare = theKey.compareTo(theNode[i]);
			if (compare == 0) {
				if(theKey.getValue().isEmpty()){
					theNode[i].setNull();
					return null;
				}
				theNode[i].addValue((Comparable) theKey.getValue().get(0));
				break;
			} else if (compare < 0) {
				int theSpot = i;
				int n = this.size();
				// System.out.println(n);
				for (int j = n - 1; j >= theSpot; j--) {
					theNode[j + 1] = theNode[j];
				}
				theNode[theSpot] = theKey;
				size++;
				break;
			}
		}

		return checkNeedToSplit();
	}

	/**
	 * Checks if this node needs to be split
	 * @return A new Node if it did split
	 */
	private Node checkNeedToSplit() {
		if (size == sizeCap) {
			return this.split();
		}
		return null;
	}

	/**
	 * Splits the Node into 2 and returns the New Node back to the caller
	 * @return The new Node created
	 */
	protected Node<K, V> split() {
		Node<K, V> node2 = new Node<>(sizeCap);
		int n = sizeCap / 2;
		for (int i = n; i < sizeCap; i++) {
			node2.put(theNode[i]);
		}
		this.setSize(n);
		if(this.hasRight()){
			Node node3 = this.right;
			node3.setLeft(node2);
			node2.setRight(node3);
		}
		this.setRight(node2);
		node2.setLeft(this);
		return node2;
	}

	/**
	 * Deletes a Key
	 * @param theKey The key we would like to delete
	 */
	public void delete(Key theKey) {
		int theSize = this.size();
		for (int i = 0; i < theSize; i++) {
			if (theNode[i].equals(theKey)) {
				theNode[i].addValue(null);
				return;
			}
		}
	}

	/**
	 * Used to get the value held as the Key of the first key in this Node
	 * @return The value held as key
	 */
	protected K getFirstKey() {
		return (K) theNode[0].getKey();
	}

	/**
	 * Finds the location where we need to climb further down the BTree and returns its child
	 * @param key The key we are searching for
	 * @param height The height of the tree we are currently at
	 * @return The child Node
	 */
	public Node<K, V> findSpot(Key key, int height) {
		for (int i = 0; i <= size; i++) {
			int compare = key.compareTo(theNode[i]);

			if (compare == 0) {
				return ((Node<K, V>) theNode[i].getChild());
			}
			if (compare < 0) {
				return ((Node<K, V>) theNode[i - 1].getChild());
			}
			if (theNode[i + 1] == null) { // if the next value in the node array
				return (Node) theNode[i].getChild();		// is null
			}
			if (i == size()) {
				return (Node) theNode[i].getChild();
			} 

		}
		return null;
	}

	/**
	 * Returns a Key from the Node
	 * @param key The Key we are using to search for our Key
	 * @return The desired Key
	 */
	public Key get(Key key) {
		for (int i = 0; i < this.size(); i++) {
			if (theNode[i].equals(key)) {
				return theNode[i];
			}
		}
		return null;
	}
	
	/**
	 * Returns all of the Values in this Node
	 * @returnall of the Values in this Node
	 */
	public ArrayList<V> getAll()
	{
		ArrayList<V> theKeys = new ArrayList<>();
		for(int i = 0; i < this.size; i++){
			theKeys.addAll(theNode[i].getValue());
		}
		return theKeys;
	}

	/**
	 * Returns all of the Values held in the Keys who's Key value is greater than the inserted Key
	 * @param key The Key who's value we are comparing from
	 * @return All of the Values in this Node that fit the terms
	 */
	public ArrayList<V> getAllGreater(Key key)
	{
		ArrayList<V> theKeys = new ArrayList<>();
		for(int i = 0; i < this.size; i++){
			if(theNode[i].compareTo(key) > 0){
				theKeys.addAll(theNode[i].getValue());
			}
		}
		return theKeys;
	}
	
	/**
	 * Returns all of the Values held in the Keys who's Key value is less than the inserted Key
	 * @param key The Key who's value we are comparing from
	 * @return All of the Values in this Node that fit the terms
	 */
	public ArrayList<V> getAllLess(Key key)
	{
		ArrayList<V> theKeys = new ArrayList<>();
		for(int i = 0; i < this.size; i++){
			if(theNode[i].compareTo(key) < 0  && !(theNode[i].equals(BTree.getSentinel()))){
				theKeys.addAll(theNode[i].getValue());
			}
		}
		return theKeys;
	}
	
	/**
	 * returns the Node's left sibling
	 * @return It's left sibling
	 */
	public Node getLeft() {
		return left;
	}

	/**
	 * Sets it's left sibling
	 * @param left The Node to be set as it's sibling
	 */
	public void setLeft(Node left) {
		this.left = left;
	}

	/**
	 * returns the Node's right sibling
	 * @return It's right sibling
	 */
	public Node getRight() {
		return right;
	}

	/**
	 * Sets it's right sibling
	 * @param left The Node to be set as it's sibling
	 */
	public void setRight(Node right) {
		this.right = right;
	}
	
	/**
	 * Checks if this Node has a right sibling
	 * @return True or false if it has a sibling
	 */
	public boolean hasRight()
	{
		if(right != null){
			return true;
		}
		return false;
	}
	
	/**
	 * Checks if this Node has a left sibling
	 * @return True or false if it has a sibling
	 */
	public boolean hasLeft()
	{
		if(left != null){
			return true;
		}
		return false;
	}
	
	public static void main(String args[]) {

		Key key1 = new Key(1, "hi", null);
		Key key2 = new Key(2, "hello", null);
		Node theNode = new Node(4);
		theNode.put(key1);
		theNode.put(key2);
		System.out.println(theNode.get(key2).getKey());
		System.out.println(theNode.get(key2).getValue());

	}

	
}

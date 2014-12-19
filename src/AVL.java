import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class AVL<T extends Comparable<? super T>> implements BSTInterface<T> {
	//Add instance variables here
	private Node<T> root;
	private int size;
	
	
	public AVL() {
		root = null;
		size = 0;
	}
	
	@Override
	public String toString() {
		if (root == null) {
		return "()";
		}
		return root.toString();
	}


	@Override
	public void add(T data) {
		if (data == null) {
			throw new IllegalArgumentException("data is null");
		}

		if (root == null) {
			root = new Node<T>(data);
			root.setHeight(1);
			root.setBalanceFactor(0);
			size++;
		} else {
			add(data, root);
			updateHeight(root);
			
			updateBF(root);
			root = rotate(root);
			updateHeight(root);
			updateBF(root);
		}
	}

	/**
	 * adds the data to the BST by recursive methods
	 * 
	 * @param data to be added
	 * @param root
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private void add(T data, Node<T> root) {
		Comparable nodeData = (Comparable) root.getData();
		Comparable myData = (Comparable) data;

		if (myData.compareTo(nodeData) < 0) {
			if (root.getLeft() == null) {
				root.setLeft(new Node<T>(data));
				root.getLeft().setHeight(1);
				root.getLeft().setBalanceFactor(0);
				size++;
			} else {
				add(data, root.getLeft());
				updateHeight(root);
				updateBF(root);
				root.setLeft(rotate(root.getLeft()));
				updateHeight(root);
				updateBF(root);
			}
		} else if (myData.compareTo(nodeData) > 0) {
			if (root.getRight() == null) {
				root.setRight(new Node<T>(data));
				root.getRight().setHeight(1);
				root.getRight().setBalanceFactor(0);
				size++;
			} else {
				add(data, root.getRight());
				updateHeight(root);
				updateBF(root);
				root.setRight(rotate(root.getRight()));
				updateHeight(root);
				updateBF(root);
			}
		}
		
	}


	@SuppressWarnings("unchecked")
	@Override
	public void addAll(Collection<T> c) {

		if (c == null) {
			throw new IllegalArgumentException("data is null");
		}

		for (Object obj: c.toArray()) {
			T data = (T) obj;

			if (data == null) {
				throw new IllegalArgumentException("data is null");
			}

			add(data);
		}
	}

	@Override
	public T remove(T data) {
		if (data == null) {
			throw new IllegalArgumentException("data is null");
		}
		
		if (root == null) {
			return null;
		}
		
		ReturnObject old = new ReturnObject(null);
		Node<T> newRoot = remove(root, data, old);
		root = newRoot;
		
		if (old.get() != null) {
			size--;
		}
		
		updateHeight(root);
		updateBF(root);
		root = rotate(root);
		updateHeight(root);
		updateBF(root);
		
		return old.get();
	}

	/**
	 * recursively searches through the tree to find the node to be removed and stores the data for it in old before removing it.
	 * 
	 * @param node
	 * @param data
	 * @param old
	 * @return the new tree
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private Node<T> remove(Node<T> node, T data, ReturnObject old) {
		if (node != null) {
			Comparable nodeData = (Comparable) node.getData();
			Comparable myData = (Comparable) data;

			int comparison = myData.compareTo(nodeData);

			if (comparison == 0) {
				old.set(node.getData());
				node = removeHelper(node);
			} else if (comparison < 0) {
				Node<T> subtree = remove(node.getLeft(), data, old);
				node.setLeft(subtree);
				updateHeight(node);
				updateBF(node);
				node.setLeft(rotate(node.getLeft()));
				updateHeight(node);
				updateBF(node);
			} else {
				node.setRight(remove(node.getRight(), data, old));
				updateHeight(node);
				updateBF(node);
				node.setRight(rotate(node.getRight()));
				updateHeight(node);
				updateBF(node);
			}
		}
		
		return node;
	}

	/**
	 * goes through all cases of removing the node, if it has two nodes, one, or zero and 
	 * uses appropriate algorithm to remove it.
	 * 
	 * @param rootNode
	 * @return the new subtree
	 */
	private Node<T> removeHelper(Node<T> rootNode) {
		if (rootNode.getLeft() != null && rootNode.getRight() != null) {
			Node<T> right = rootNode.getRight();
			Node<T> smallest = findSmallest(right);
			rootNode.setData(smallest.getData());
			rootNode.setRight(removeSmallest(right));
		} else if (rootNode.getRight() != null) {
			rootNode = rootNode.getRight();
		} else {
			rootNode = rootNode.getLeft();
		}

		return rootNode;
	}

	/**
	 * removes the smallest element in that subtree
	 * 
	 * @param rootNode
	 * @return the subtree
	 */
	private Node<T> removeSmallest(Node<T> rootNode) {
		if (rootNode.getLeft() != null) {
			Node<T> left = rootNode.getLeft();
			Node<T> root = removeSmallest(left);
			rootNode.setLeft(root);
		} else {
			rootNode = rootNode.getRight();
		}

		return rootNode;
	}

	/**
	 * finds the smallest element in the subtree
	 * 
	 * @param rootNode
	 * @return the smallest element
	 */
	private Node<T> findSmallest(Node<T> rootNode) {
		if (rootNode.getLeft() != null) {
			rootNode = findSmallest(rootNode.getLeft());
		}

		return rootNode;
	}

	@Override
	public T get(T data) {
		if (data == null) {
			throw new IllegalArgumentException("data is null");
		}

		if (root == null) {
			return null;
		} else {
			return get(data, root);
		}
	}

	/**
	 * returns the value that is being searched for.
	 * 
	 * @param data
	 * @param node
	 * @return the data to be found
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private T get(T data, Node<T> node) {
		Comparable nodeData = (Comparable) node.getData();
		Comparable myData = (Comparable) data;

		if (myData.compareTo(nodeData) == 0) {
			return node.getData();
		}

		if (myData.compareTo(nodeData) < 0) {
			if (node.getLeft() == null) {
				return null;
			} else {
				return get(data, node.getLeft());
			}
		} else {
			if (node.getRight() == null) {
				return null;
			} else {
				return get(data, node.getRight());
			}
		}
	}

	@Override
	public boolean contains(T data) {
		if (data == null) {
			throw new IllegalArgumentException("data is null");
		}

		if (root == null) {
			return false;
		} else {
			return contains(data, root);
		}
	}

	/**
	 * recursively conducts binary search through data to see if data is contained within the data structure
	 * 
	 * @param data
	 * @param node
	 * @return whether of not data is contained within the tree
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private boolean contains(T data, Node<T> node) {
		Comparable nodeData = (Comparable) node.getData();
		Comparable myData = (Comparable) data;

		if (myData.compareTo(nodeData) == 0) {
			return true;
		}

		if (myData.compareTo(nodeData) < 0) {
			if (node.getLeft() == null) {
				return false;
			} else {
				return contains(data, node.getLeft());
			}
		} else {
			if (node.getRight() == null) {
				return false;
			} else {
				return contains(data, node.getRight());
			}
		}
	}

	@Override
	public List<T> preOrder() {
		List<T> list = new ArrayList<T>(size);
		if (size == 0) {
			return list;
		}

		list.addAll(preOrder(root));
		return list;
	}

	/**
	 * returns a list in pre-order
	 * 
	 * @param node
	 * @return list in pre-order
	 */
	private List<T> preOrder(Node<T> node) {
		List<T> list = new ArrayList<T>(size);

		list.add(node.getData());

		if (node.getLeft() != null) {
			list.addAll(preOrder(node.getLeft()));
		}

		if (node.getRight() != null) {
			list.addAll(preOrder(node.getRight()));
		}

		return list;
	}

	@Override
	public List<T> inOrder() {
		List<T> list = new ArrayList<T>(size);
		if (size == 0) {
			return list;
		}
		list.addAll(inOrder(root));
		return list;
	}

	/**
	 * returns a list in in-order
	 * 
	 * @param node
	 * @return list in in-order
	 */
	private List<T> inOrder(Node<T> node) {
		List<T> list = new ArrayList<T>(size);

		if (node.getLeft() != null) {
			list.addAll(inOrder(node.getLeft()));
		}

		list.add(node.getData());

		if (node.getRight() != null) {
			list.addAll(inOrder(node.getRight()));
		}

		return list;
	}

	@Override
	public List<T> postOrder() {
		List<T> list = new ArrayList<T>(size);
		if (size == 0) {
			return list;
		}
		list.addAll(postOrder(root));
		return list;
	}

	/**
	 * returns a list in post-order
	 * 
	 * @param node
	 * @return list in post-order
	 */
	private List<T> postOrder(Node<T> node) {
		List<T> list = new ArrayList<T>(size);

		if (node.getLeft() != null) {
			list.addAll(postOrder(node.getLeft()));
		}

		if (node.getRight() != null) {
			list.addAll(postOrder(node.getRight()));
		}

		list.add(node.getData());

		return list;
	}

	@Override
	public List<T> levelOrder() {
		if (size == 0) {
			return new ArrayList<T>();
		}

		List<T> list = levelOrder(root);
		return list;
	}

	/**
	 * returns a list in level-order
	 * 
	 * @param node
	 * @return list in level-order
	 */
	private List<T> levelOrder(Node<T> root) {
		Queue<Node<T>> level = new LinkedList<Node<T>>();
		level.add(root);
		List<T> list = new ArrayList<T>();
		while (!level.isEmpty()) {
			Node<T> node = level.poll();
			list.add(node.getData());
			if (node.getLeft() != null)
				level.add(node.getLeft());
			if (node.getRight() != null)
				level.add(node.getRight());
		}
		return list;
	}

	@Override
	public boolean isEmpty() {
		return size == 0;
	}

	@Override
	public int size() {
		return size;
	}

	@Override
	public void clear() {
		root = null;
		size = 0;
	}

	
	/**
	 * updates the balance factor in the tree
	 * 
	 * @param root
	 */
	private void updateBF(Node<T> root) {
		if (root.getLeft() == null && root.getRight() == null) {
			root.setBalanceFactor(0);
		} else if (root.getLeft() == null || root.getRight() == null) {
			if (root.getLeft() == null) {
				root.setBalanceFactor(0 - root.getRight().getHeight());
			}
			
			if (root.getRight() == null) {
				root.setBalanceFactor(root.getLeft().getHeight());
			}
		} else {
			root.setBalanceFactor(root.getLeft().getHeight() - root.getRight().getHeight());
		}
		
		if (root.getLeft() != null) {
			updateBF(root.getLeft());
		}
		
		if (root.getRight() != null) {
			updateBF(root.getRight());
		}
	}
	
	/**
	 * updates the height in the tree
	 * 
	 * @param root
	 * @return
	 */
	private int updateHeight(Node<T> root) {
		if (root == null) {
			return 0;
		} else {
			int height = Math.max(updateHeight(root.getLeft()), updateHeight(root.getRight())) + 1;
			root.setHeight(height);
			return height;
		}
	}
	
	/**
	 * A suggested private helper method
	 * You do not have to use this if you don't want to
	 * @param unbalanced Node of subtree that is unbalanced
	 * @return the balanced subtree
	 */
	private Node<T> rotate(Node<T> unbalanced) {
		if (unbalanced == null) {
			return null;
		}
		
		Node<T> balanced = null;
		if (unbalanced.getBalanceFactor() < -1) {
			if (unbalanced.getRight() != null && unbalanced.getRight().getBalanceFactor() > 0) {
				unbalanced.setRight(rightRotation(unbalanced.getRight()));
			}
				
			balanced = leftRotation(unbalanced);
			return balanced;
			
		} else if (unbalanced.getBalanceFactor() > 1) {
			if (unbalanced.getLeft() != null && unbalanced.getLeft().getBalanceFactor() < 0) {
				unbalanced.setLeft(leftRotation(unbalanced.getLeft()));
			}
				
			balanced = rightRotation(unbalanced);
			return balanced;
		}
		
		return unbalanced;
	}
	
	/**
	 * performs a left rotation
	 * 
	 * @param unbalanced
	 * @return
	 */
	private Node<T> leftRotation(Node<T> unbalanced) {
		Node<T> balanced = unbalanced.getRight();
		unbalanced.setRight(balanced.getLeft());
		balanced.setLeft(unbalanced);
		updateHeight(balanced);
		return balanced;
	}
	
	/**
	 * performs a right rotation
	 * 
	 * @param unbalanced
	 * @return
	 */
	private Node<T> rightRotation(Node<T> unbalanced) {
		Node<T> balanced = unbalanced.getLeft();
		unbalanced.setLeft(balanced.getRight());
		balanced.setRight(unbalanced);
		updateHeight(balanced);
		return balanced;
	}
	
	
	/**
	 * Return object used in remove method to store the object to be returned
	 * 
	 * @author Tanmay
	 */
	private class ReturnObject {

		private T data;

		/**
		 * constructor for ReturnObject
		 * 
		 * @param aData
		 */
		public ReturnObject(T aData) {
			set(aData);
		}

		/**
		 * getter for data
		 * 
		 * @return data
		 */
		public T get() {
			return data;
		}

		/**
		 * setter for data
		 * 
		 * @param data
		 */
		public void set(T data) {
			this.data = data;
		}
	}
	
	//DO NOT MODIFY OR USE ANY OF THE METHODS BELOW IN YOUR IMPLEMENTATION
	//These methods are for testing purposes only
	public Node<T> getRoot() {
		return root;
	}

	public void setRoot(Node<T> root) {
		this.root = root;
	}
	
	
}

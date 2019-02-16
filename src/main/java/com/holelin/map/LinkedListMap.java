package com.holelin.map;

/**
 * ClassName: LinkedListMap
 * 基于链表实现的Map
 * @author HoleLin
 * @version 1.0
 * @date 2019/2/11
 */

public class LinkedListMap<K, V> implements Map<K, V> {
	private Node dummyHead;
	private int size;

	public LinkedListMap() {
		dummyHead = new Node();
		size = 0;
	}

	private Node getNode(K key) {
		Node cur = dummyHead.next;
		while (cur != null) {
			if (cur.key.equals(key)) {
				return cur;
			}
			cur = cur.next;
		}
		return null;
	}

	@Override
	public void add(K key, V value) {
		Node node = getNode(key);
		if (node == null) {
			dummyHead.next = new Node(key, value, dummyHead.next);
			size++;
		} else {
			// 如果重复的key,则修改当前的key对应value
			node.value = value;
		}
	}

	@Override
	public V remove(K key) {
		Node prev = dummyHead;
		while (prev.next != null) {
			if (prev.next.key.equals(key)) {
				break;
			}
			prev = prev.next;
		}
		if (prev.next != null) {
			Node delNode = prev.next;
			prev.next = delNode.next;
			delNode.next = null;
			size--;
			return delNode.value;
		}
		return null;

	}

	@Override
	public boolean contains(K key) {
		return get(key) != null;
	}

	@Override
	public V get(K key) {
		Node node = getNode(key);
		return node == null ? null : node.value;
	}

	@Override
	public void set(K key, V value) {
		Node node = getNode(key);
		if (node == null) {
			throw new IllegalArgumentException(key + " doesn't exist");
		}
		node.value = value;
	}

	@Override
	public int getSize() {
		return 0;
	}

	@Override
	public boolean isEmpty() {
		return false;
	}

	private class Node {
		/**
		 * 键
		 */
		K key;
		/**
		 * 值
		 */
		V value;
		/**
		 * 指针域
		 */
		Node next;

		public Node(K key, V value, Node next) {
			this.key = key;
			this.value = value;
			this.next = next;
		}

		public Node(K key, V value) {
			this.key = key;
			this.value = value;
			this.next = null;
		}

		public Node() {
			this(null, null);
		}

		@Override
		public String toString() {
			return key.toString() + " : " + value.toString();
		}
	}
}

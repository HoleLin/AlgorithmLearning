package com.holelin.heap;

import com.holelin.array.Array;

/**
 * ClassName: MaxHeap
 * 基于数组的大顶推
 *
 * @author HoleLin
 * @version 1.0
 * @date 2019/2/13
 */

public class MaxHeap<E extends Comparable<E>> {
	private Array<E> data;

	/**
	 * Heapify
	 * 将数组转换为大顶堆
	 * @param arr 待转换的数组
	 */
	public MaxHeap(E[] arr){
		data=new Array<>(arr);
		// 找到最后一个非叶子节点进行siftDown操作,然后向前推进直至根
		for (int i = parent(arr.length-1); i >=0 ; i--) {
			siftDown(i);
		}

	}

	public MaxHeap(int capacity) {
		data = new Array<>(capacity);
	}

	public MaxHeap() {
		data = new Array<>();
	}

	/**
	 * 返回堆中的元素个数
	 *
	 * @return 堆中的元素个数
	 */
	public int size() {
		return data.getSize();
	}

	/**
	 * 判断堆中是否为空
	 *
	 * @return 为空返回true;反之返回false;
	 */
	public boolean isEmpty() {
		return data.isEmpty();
	}

	/**
	 * 返回完全二叉树的数组表示中,一个索引所表示的元素的父亲节点的索引
	 *
	 * @param index 查找父亲节点的索引
	 * @return 所查节点的父亲节点的索引
	 */
	private int parent(int index) {
		if (index == 0) {
			throw new IllegalArgumentException("index-0 doesn't have parent.");
		}
		return (index - 1) / 2;
	}

	/**
	 * 返回完全二叉树的数组表示中,一个索引表示的元素的左孩子节点的索引
	 * tips: 索引从0开始
	 * @param index 查找左孩子节点的索引
	 * @return 所查节点的左孩子节点的索引
	 */
	private int leftChild(int index) {
		return index * 2 + 1;
	}

	/**
	 * 返回完全二叉树的数组表示中,一个索引表示的元素的右孩子节点的索引
	 * tips: 索引从0开始
	 * @param index 查找右孩子节点的索引
	 * @return 所查节点的右孩子节点的索引
	 */
	private int rightChild(int index) {
		return index * 2 + 2;
	}

	/**
	 * 添加元素e
	 *
	 * @param e 元素e
	 */
	public void add(E e) {
		// 在末尾添加元素e
		data.addLast(e);
		// 上浮元素-- 即重新构建大顶堆
		siftUp(data.getSize() - 1);
	}

	/**
	 * 上浮k所在位置的元素(使结构满足大顶推)
	 *
	 * @param k k索引
	 */
	private void siftUp(int k) {
		// k所在的元素不能小于0 且 父亲元素小于k所在位置的元素
		while (k > 0 && data.get(parent(k)).compareTo(data.get(k)) < 0) {
			// k位置上的元素与k元素父亲节点交换位置
			data.swap(k, parent(k));
			// 改变k的索引,继续比较
			k = parent(k);
		}
	}

	/**
	 * 查看堆中最大值
	 * @return 堆中最大值
	 */
	public E findMax() {
		if (data.getSize() == 0) {
			throw new IllegalArgumentException("Can not findMax when heap is empty.");
		}
		return data.get(0);
	}

	/**
	 * 取出堆中最大值(删除最大值) -- 需要进行重新构建堆
	 * @return 堆中最大值
	 */
	public E extractMax() {
		E ret = findMax();
		// 交换首部和尾部元素
		data.swap(0, data.getSize() - 1);
		// 删除尾部元素
		data.removeLast();
		// 进行下沉操作
		siftDown(0);
		return ret;
	}

	/**
	 * 下沉k所在元素
	 * @param k k索引
	 */
	private void siftDown(int k) {
		while (leftChild(k) < data.getSize()) {
			// 以下操作: 将k所在位置元素的值与k的左右孩子的值进行比较
			//          -- 将k所在位置元素的值与其中较大的值进行交换
			// j保存左右孩子中最大值
			int j = leftChild(k);
			// j+1 -- 右孩子所在的位置
			// j+1<data.getSize() -- 表示存在右孩子
			if (j + 1 < data.getSize() && data.get(j + 1).compareTo(data.get(j)) > 0) {
				j = rightChild(k);
			}
			// 当k所在元素的值大于左右孩子中最大值 -- 即满足大顶堆结构,则停止操作
			if (data.get(k).compareTo(data.get(j)) >= 0) {
				break;
			}
			// 否则交换k和j位置的值
			data.swap(k, j);
			// 移动k的索引位置,以便下一次比较
			k = j;
		}

	}

	/**
	 * 取出堆中的最大元素,并且替换成元素e
	 * @param e 元素e
	 * @return 最大元素
	 */
	public E replace(E e){
		E ret =findMax();
		// 将堆顶元素与e进行替换
		data.set(0,e);
		// 可能替换完成后会改变堆结构,重新构建堆结构
		siftDown(0);
		return ret;
	}


}

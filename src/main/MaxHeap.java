package main;

import java.util.*;

public class MaxHeap {

	List<VertexNode> maxHeap;
	Map<Integer, Integer> pos;
	
	int[] heap;
	int[] heapD;
	int[] heapP;
//	Could not get across some edge cases to use H[], D[] and P[] so a different approach to heap is used.

//    Constructor to add list of fringes 
	MaxHeap(List<VertexNode> fringes) {
		maxHeap = new ArrayList<>(fringes.size());
		pos = new HashMap<>(fringes.size());
		for (int i = 0; i < fringes.size(); i++) {
			maxHeap.add(fringes.get(i));
			pos.put(fringes.get(i).vertex, i);
		}
		for (int i = maxHeap.size() / 2; i >= 0; i--) {
			heapify(i);
		}
	}

//    Pop max element of Heap
	public VertexNode popMax() {
		VertexNode max = maxHeap.get(0);
		delete(max.vertex);

		return max;
	}

//	Insert vertex into Heap
	public void insert(VertexNode fringe) {
		maxHeap.add(fringe);
		int i = maxHeap.size() - 1;
		pos.put(fringe.vertex, i);
		
//		bottom-up heapify
		while (i > 0 && i < maxHeap.size() && maxHeap.get(i / 2).edgeWt - maxHeap.get(i).edgeWt < 0) {
			swap(i / 2, i);
			i = i / 2;
		}
	}

//	Delete vertex from Heap 
	public void delete(int vertex) {
		int p = pos.get(vertex);
		swap(p, maxHeap.size() - 1);
		maxHeap.remove(maxHeap.size() - 1);
		pos.remove(vertex);
		
//		Bottom-Up Heapify
		while (p > 0 && p < maxHeap.size() && maxHeap.get(p / 2).edgeWt - maxHeap.get(p).edgeWt < 0) {
			swap(p / 2, p);
			p = p / 2;
		}
		
//		Top-Down Heapify
		heapify(p);
	}

//	Top-Down Heapify
	private void heapify(int n) {
		int leftChild = 2 * n;
		int rightChild = 2 * n + 1;
		int largeIndex = n;
		if (leftChild < maxHeap.size() && maxHeap.get(leftChild).edgeWt - maxHeap.get(n).edgeWt > 0) {
			largeIndex = leftChild;
		}
		if (rightChild < maxHeap.size() && maxHeap.get(rightChild).edgeWt - maxHeap.get(largeIndex).edgeWt > 0) {
			largeIndex = rightChild;
		}
		if (largeIndex != n) {
			swap(n, largeIndex);
			heapify(largeIndex);
		}
	}

//	Swap position of vertices in Heap
	private void swap(int pos1, int pos2) {
		VertexNode fringe1 = maxHeap.get(pos1);
		VertexNode fringe2 = maxHeap.get(pos2);

		maxHeap.set(pos1, fringe2);
		maxHeap.set(pos2, fringe1);

		pos.put(fringe1.vertex, pos2);
		pos.put(fringe2.vertex, pos1);
	}
}

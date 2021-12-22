package main;

// VertexNode LinkedList which stores edge of each vertex
public class VertexNode {
	int vertex;
	int edgeWt;
	VertexNode next;
	int index;


	public VertexNode(int vertex, int edgeWt, int index, VertexNode next) {
		this.vertex = vertex;
		this.edgeWt = edgeWt;
		this.index = index;
		this.next = next;
	}
	
	public int getIndex() {
		return index;
	}
	public void setIndex(int index) {
		this.index = index;
	}
	public int getVertex() {
		return vertex;
	}
	public void setVertex(int vertex) {
		this.vertex = vertex;
	}
	public int getEdgeWt() {
		return edgeWt;
	}
	public void setEdgeWt(int edgeWt) {
		this.edgeWt = edgeWt;
	}
	public VertexNode getNext() {
		return next;
	}
	public void setNext(VertexNode next) {
		this.next = next;
	}
	
}

package main;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Main {

	public static int n = 5000; // No. of vertices
	public static int weightLimit = 50; // max edge weight
	public static int[] status = new int[n]; // status of unseen, in-tree and fringe
	public static int[] bw = new int[n]; // bandwidth of vertex from source
	public static int[] dadMaxSpan = new int[n]; // dad array of max spanning tree
	public static Random random = new Random(); // random number generator

	public static void main(String args[]) {

		long startTime = System.nanoTime();
		long endTime = System.nanoTime();
		long time;

		GraphTimeStorage[] graph1TimeStorage = new GraphTimeStorage[5];
		GraphTimeStorage[] graph2TimeStorage = new GraphTimeStorage[5];

		VertexNode[] graph1;
		VertexNode[] graph2;

		int s = random.nextInt(n);
		int t = s;
		while (s == t)
			t = random.nextInt(n);

		System.out.println("s: " + s + ",t: " + t);

		int avgDegree = 6; // Avg. degree
		float avgDegreePercentage = 0.2f; // Avg. neighbor percentage

//		Testing for 5 graphs
		for (int i = 0; i < 5; i++) {
			graph1 = new VertexNode[n];
			graph2 = new VertexNode[n];

			graph1TimeStorage[i] = new GraphTimeStorage();
			graph2TimeStorage[i] = new GraphTimeStorage();

			s = random.nextInt(n);
			t = s;
			while (s == t)
				t = random.nextInt(n);
			System.out.println("s: " + s + ";t: " + t);

//			Graph 1
			graph1 = generateConnectedGraph(graph1, n, weightLimit);
			graph1 = generateGraph1(graph1, n, weightLimit, avgDegree);
			System.out.println("====================================");
			startTime = System.nanoTime();
			dijskstraWithoutHeap(graph1, s, t, n);
			endTime = System.nanoTime();
			time = endTime - startTime;
			graph1TimeStorage[i].graphDijkstraWithoutHeap = time / 1000000;

			startTime = System.nanoTime();
			dijkstraWithHeap(graph1, s, t, n);
			endTime = System.nanoTime();
			time = endTime - startTime;

			graph1TimeStorage[i].graphDijkstraWithHeap = time / 1000000;

			startTime = System.nanoTime();
			kruskal(graph1, s, t, n);
			endTime = System.nanoTime();
			time = endTime - startTime;

			graph1TimeStorage[i].graphKruskal = time / 10000000;
			System.out.println("====================================");

//			Graph 2
			graph2 = generateConnectedGraph(graph2, n, weightLimit);
			graph2 = generateGraph2(graph2, n, weightLimit, avgDegreePercentage);

			System.out.println("====================================");
			startTime = System.nanoTime();
			dijskstraWithoutHeap(graph2, s, t, n);
			endTime = System.nanoTime();
			time = endTime - startTime;
			graph2TimeStorage[i].graphDijkstraWithoutHeap = time / 1000000;

			startTime = System.nanoTime();
			dijkstraWithHeap(graph2, s, t, n);
			endTime = System.nanoTime();
			time = endTime - startTime;

			graph2TimeStorage[i].graphDijkstraWithHeap = time / 1000000;

			startTime = System.nanoTime();
			kruskal(graph2, s, t, n);
			endTime = System.nanoTime();
			time = Math.abs(endTime - startTime);

			graph2TimeStorage[i].graphKruskal = time / 100000000;
			System.out.println("====================================");
		}

		System.out.println("Graph\tDwithout\tDwith\tK");
		for (int i = 0; i < 5; i++) {
			System.out.println("G1-" + (i + 1) + "\t|" + graph1TimeStorage[i].graphDijkstraWithoutHeap + "\t\t|"
					+ graph1TimeStorage[i].graphDijkstraWithHeap + "\t|" + graph1TimeStorage[i].graphKruskal);
			System.out.println("G2-" + (i + 1) + "\t|" + graph2TimeStorage[i].graphDijkstraWithoutHeap + "\t\t|"
					+ graph2TimeStorage[i].graphDijkstraWithHeap + "\t|" + graph2TimeStorage[i].graphKruskal);
			System.out.println("+==================================+");
		}

	}

//	Kruskal's algorithm
	public static void kruskal(VertexNode[] graph, int s, int t, int n) {
		UnionFind unionFind = new UnionFind(n);

		List<Edge> edgeList = getEdgesGraph(graph);
		MaxHeapEdge maxHeapEdge = new MaxHeapEdge(edgeList);

//		Collections.sort(edgeList, (e1, e2) -> (e2.edgeWt - e1.edgeWt));

		VertexNode[] maxSpanningTree = new VertexNode[n];

		while (!maxHeapEdge.maxHeap.isEmpty()) {
			Edge edge = maxHeapEdge.popMax();
			int v1 = edge.v1;
			int v2 = edge.v2;

			int r1 = unionFind.find(v1);
			int r2 = unionFind.find(v2);

			if (r1 != r2) {
				unionFind.union(r1, r2);

				if (maxSpanningTree[v1] == null) {
					maxSpanningTree[v1] = new VertexNode((v2) % n, edge.edgeWt, 1, null);
				} else {
					maxSpanningTree[v1] = new VertexNode((v2) % n, edge.edgeWt, maxSpanningTree[v1].index + 1,
							maxSpanningTree[v1]);
				}

				if (maxSpanningTree[v2] == null) {
					maxSpanningTree[v2] = new VertexNode((v1) % n, edge.edgeWt, 1, null);
				} else {
					maxSpanningTree[v2] = new VertexNode((v1) % n, edge.edgeWt, maxSpanningTree[v2].index + 1,
							maxSpanningTree[v2]);
				}
			}
		}

		boolean[] isVisited = new boolean[n];

		dfs(maxSpanningTree, s, isVisited);
		int maxBw = Integer.MAX_VALUE;
		System.out.print("s-t path: ");
		while (dadMaxSpan[t] != s) {
			maxBw = Math.min(getEdgeWeight(t, dadMaxSpan[t], graph), maxBw);
			System.out.print(t + " <-- ");
			t = dadMaxSpan[t];
		}
		System.out.println(t);
		System.out.println("The max bandwidth of graph with Kruskal is: " + maxBw);
	}

//	Get the edge weight between 2 given vertices
	public static int getEdgeWeight(int v1, int v2, VertexNode[] graph) {
		VertexNode vw = graph[v1];
		while (vw != null) {
			if (vw.vertex == v2) {
//				System.out.println(v1 + "-" + vw.vertex + "<->" + vw.edgeWt);
				return vw.edgeWt;
			}
			vw = vw.next;
		}
		return Integer.MAX_VALUE;
	}

//	DFS traversal
	public static void dfs(VertexNode[] graph, int v, boolean visited[]) {
		visited[v] = true;
		VertexNode vw = graph[v];
		while (vw != null) {
			if (visited[vw.vertex] == false) {
				dadMaxSpan[vw.vertex] = v;
				dfs(graph, vw.vertex, visited);
			}
			vw = vw.next;
		}
//		System.out.print(v + "-->");
//		visited[v] = true;
	}

//	Check if there is an edge (Used in random edge additions to the graph)
	public static Boolean ContainsEdge(VertexNode vertexNode, int dest) {
		while (vertexNode != null) {
			if (vertexNode.vertex == dest)
				return true;
			vertexNode = vertexNode.next;
		}
		return false;
	}

//	Generate a connected graph -> Each vertex is connected to it's next vertex forming a circular graph
	public static VertexNode[] generateConnectedGraph(VertexNode[] graph, int n, int weightLimit) {
		for (int i = 0; i < n; i++) {
			int randWeight = random.nextInt(weightLimit + 1 - weightLimit / 2) + weightLimit / 2; // random edge
																									// weight generator
																									// variable
			if (graph[i] == null) {
				graph[i] = new VertexNode((i + 1) % n, randWeight, 1, null);
			} else {
//				System.out.println(graph1[i].index);
				graph[i] = new VertexNode((i + 1) % n, randWeight, graph[i].index + 1, graph[i]);
			}

			if (graph[(i + 1) % n] == null) {
				graph[(i + 1) % n] = new VertexNode(i, randWeight, 1, null);
			} else {
				graph[(i + 1) % n] = new VertexNode(i, randWeight, graph[(i + 1) % n].index + 1, graph[(i + 1) % n]);
			}
		}
		return graph;
	}

//	Generate Sparse Graph with an average degree
	public static VertexNode[] generateGraph1(VertexNode[] graph1, int n, int weightLimit, int avgDegree) {
		int randSrc; // random src vertex variable
		int randDest; // random destination variable
		int randWeight; // random edge weight generator variable

		int avgEdgeSum = n * avgDegree / 2;
		int currEdgeSum = n;

		while (currEdgeSum < avgEdgeSum) {
			randSrc = random.nextInt(n);
			randDest = random.nextInt(n);

			if (!ContainsEdge(graph1[randSrc], randDest) && randSrc != randDest) {
				randWeight = random.nextInt(weightLimit + 1 - weightLimit / 2) + weightLimit / 2;
//				System.out.println(randSrc + ", " + randDest + ", " + randWeight);
				graph1[randSrc] = new VertexNode(randDest, randWeight, graph1[randSrc].index + 1, graph1[randSrc]);
				graph1[randDest] = new VertexNode(randSrc, randWeight, graph1[randDest].index + 1, graph1[randDest]);
				currEdgeSum++;
			}
		}

		return graph1;
	}

//	Generate Dense Graph with a percentage of vertex being its neighbors
	public static VertexNode[] generateGraph2(VertexNode[] graph2, int n, int weightLimit, float percentage) {
		int randDest; // random destination variable
		int randWeight; // random edge weight generator variable

		int currentVertexPercentage = (int) Math.round(n * percentage) / 2;
		int currVertexSum;

		for (int i = 0; i < n; i++) {
			currVertexSum = 1;
			while (currVertexSum < currentVertexPercentage) {
				randDest = random.nextInt(n);

				if (!ContainsEdge(graph2[i], randDest) && i != randDest
						&& graph2[randDest].getIndex() != currentVertexPercentage) {
					randWeight = random.nextInt(weightLimit + 1 - weightLimit / 2) + weightLimit / 2;
					graph2[i] = new VertexNode(randDest, randWeight, graph2[i].index + 1, graph2[i]);
					graph2[randDest] = new VertexNode(i, randWeight, graph2[randDest].index + 1, graph2[randDest]);
					currVertexSum++;
				}
			}
		}

		return graph2;
	}

//	Print the given graph
	public static void printGraph(VertexNode[] graph, int numVertices) {
		for (int i = 0; i < numVertices; i++) {
			VertexNode temp = graph[i];
			System.out.print(i + ":-");
			while (temp != null) {
				System.out.print("->(" + temp.vertex + ":" + temp.edgeWt + ")");
				temp = temp.next;
			}
			System.out.println();
		}
	}

//	Print the Avg. degree of the graph
	public static void printAvgDegree(VertexNode[] graph, int n) {
		int edgeCounter = 0;
		for (int i = 0; i < n; i++) {
			edgeCounter += graph[i].index;
		}

		System.out.println("The average Degree of the graph is " + edgeCounter / n);
	}

//	Run Dijstra's algorithm on the graph without using a heap - O(n^2)
	public static void dijskstraWithoutHeap(VertexNode[] graph, int s, int t, int noOfNodes) {
		int unseen = 0, fringe = 1, intree = 2;
		int[] status = new int[noOfNodes];
		int[] bw = new int[noOfNodes];
		int[] dad = new int[noOfNodes];
		List<VertexNode> fringes = new ArrayList<>();

		status[s] = intree;

		// add nodes from s
		VertexNode head = graph[s];
		while (head != null) {
			status[head.vertex] = fringe;
			dad[head.vertex] = s;
			bw[head.vertex] = head.edgeWt;
			fringes.add(head);
			head = head.next;
		}

		// add nodes for unseen and update status, dad and bw for unseen and fringe
		while (!fringes.isEmpty()) {
			VertexNode maxFringe = getMaxFringe(fringes);
			status[maxFringe.vertex] = intree;
			VertexNode node = graph[maxFringe.vertex];
			while (node != null) {
				if (status[node.vertex] == unseen) {
					status[node.vertex] = fringe;
					dad[node.vertex] = maxFringe.vertex;
					bw[node.vertex] = Math.min(bw[maxFringe.vertex], node.edgeWt);
					fringes.add(new VertexNode(node.vertex, bw[node.vertex], 0, null) {
					});
				} else if (status[node.vertex] == fringe
						&& bw[node.vertex] < Math.min(bw[maxFringe.vertex], node.edgeWt)) {
					dad[node.vertex] = maxFringe.vertex;
					bw[node.vertex] = Math.min(bw[maxFringe.vertex], node.edgeWt);
					updateFringe(fringes, node.vertex, bw[node.vertex]);
				}
				node = node.next;
			}
		}

		// get max bw and path from t to s
		System.out.println("Max bandwidth without heap using Dijkstra is: " + bw[t]);
		System.out.print("s-t path: ");
		while (t != s) {
			System.out.print(t + " <-- ");
			t = dad[t];
		}
		System.out.println(t);
	}

//	Get the Max Fringe from a list of Fringes
	public static VertexNode getMaxFringe(List<VertexNode> fringes) {
		VertexNode maxFringe = new VertexNode(-1, -1, 0, null);
		int id = -1;
		for (int i = 0; i < fringes.size(); i++) {
			if (fringes.get(i).getEdgeWt() > maxFringe.getEdgeWt()) {
				maxFringe = fringes.get(i);
				id = i;
			}
		}
		fringes.remove(id);
		return maxFringe;
	}

//	Update the value in a Fringe
	public static void updateFringe(List<VertexNode> fringes, int vertex, int updatedWt) {
		for (int i = 0; i < fringes.size(); i++) {
			if (fringes.get(i).vertex == vertex) {
				fringes.get(i).edgeWt = updatedWt;
			}
		}
	}

	public static void dijkstraWithHeap(VertexNode[] graph, int s, int t, int noOfNodes) {
		int unseen = 0, fringe = 1, intree = 2;
		int[] status = new int[noOfNodes];
		int[] bw = new int[noOfNodes];
		int[] dad = new int[noOfNodes];
		List<VertexNode> fringes = new ArrayList<>();

		status[s] = intree;

		// add nodes from src
		VertexNode head = graph[s];
		while (head != null) {
			status[head.vertex] = fringe;
			dad[head.vertex] = s;
			bw[head.vertex] = head.edgeWt;
			fringes.add(head);
			head = head.next;
		}
		MaxHeap maxHeap = new MaxHeap(fringes);

		// add nodes for unseen in heap and update status, dad and bw for unseen and
		// fringe
		while (!maxHeap.maxHeap.isEmpty()) {
			VertexNode maxFringe = maxHeap.popMax();
			status[maxFringe.vertex] = intree;
			VertexNode node = graph[maxFringe.vertex];
			while (node != null) {
				if (status[node.vertex] == unseen) {
					status[node.vertex] = fringe;
					dad[node.vertex] = maxFringe.vertex;
					bw[node.vertex] = Math.min(bw[maxFringe.vertex], node.edgeWt);
					maxHeap.insert(new VertexNode(node.vertex, bw[node.vertex], 0, null));
				} else if (status[node.vertex] == fringe
						&& bw[node.vertex] < Math.min(bw[maxFringe.vertex], node.edgeWt)) {
					maxHeap.delete(node.vertex);
					dad[node.vertex] = maxFringe.vertex;
					bw[node.vertex] = Math.min(bw[maxFringe.vertex], node.edgeWt);
					maxHeap.insert(new VertexNode(node.vertex, bw[node.vertex], 0, null));
				}
				node = node.next;
			}
		}

		// get max bw and path from dest to src
		System.out.println("Max bandwidth with heap using Dijkstra is: " + bw[t]);
		System.out.print("s-t path: ");
		while (t != s) {
			System.out.print(t + " <-- ");
			t = dad[t];
		}
		System.out.println(t);
	}

	public static List<Edge> getEdgesGraph(VertexNode[] graph) {
		int n = graph.length;
		List<Edge> edgeList = new ArrayList<>();
		for (int i = 0; i < n; i++) {
			VertexNode vw = graph[i];
			while (vw != null) {
				int v = i;
				int w = vw.vertex;
				int e = vw.edgeWt;
				edgeList.add(new Edge(v, w, e));
				vw = vw.next;
			}
		}
		return edgeList;
	}

}

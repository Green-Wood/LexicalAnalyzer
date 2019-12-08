package FA;

import java.util.HashSet;
import java.util.Set;

public class Graph {
    private final int V;                // number of vertices in this digraph
    private int E;                      // number of edges in this digraph
    private Set<DirectedEdge>[] outEdges;    // adj[v] = adjacency list for vertex v
    private Set<DirectedEdge>[] inEdges;

    /**
     * Initializes an empty edge-weighted digraph with {@code V} vertices and 0 edges.
     *
     * @param  V the number of vertices
     * @throws IllegalArgumentException if {@code V < 0}
     */
    public Graph(int V) {
        if (V < 0) throw new IllegalArgumentException("Number of vertices in a Digraph must be nonnegative");
        this.V = V;
        this.E = 0;
        outEdges = (Set<DirectedEdge>[]) new Set[V];
        inEdges = (Set<DirectedEdge>[]) new Set[V];
        for (int v = 0; v < V; v++) {
            outEdges[v] = new HashSet<>();
            inEdges[v] = new HashSet<>();
        }
    }

    /**
     * Returns the number of vertices in this edge-weighted digraph.
     *
     * @return the number of vertices in this edge-weighted digraph
     */
    public int V() {
        return V;
    }

    /**
     * Returns the number of edges in this edge-weighted digraph.
     *
     * @return the number of edges in this edge-weighted digraph
     */
    public int E() {
        return E;
    }

    // throw an IllegalArgumentException unless {@code 0 <= v < V}
    private void validateVertex(int v) {
        if (v < 0 || v >= V)
            throw new IllegalArgumentException("vertex " + v + " is not between 0 and " + (V-1));
    }

    /**
     * Adds the directed edge {@code e} to this edge-weighted digraph.
     *
     * @param  e the edge
     * @throws IllegalArgumentException unless endpoints of edge are between {@code 0}
     *         and {@code V-1}
     */
    public void addEdge(DirectedEdge e) {
        int v = e.from();
        int w = e.to();
        validateVertex(v);
        validateVertex(w);
        outEdges[v].add(e);
        inEdges[w].add(e);
        E++;
    }


    /**
     * Returns the directed edges incident from vertex {@code v}.
     *
     * @param  v the vertex
     * @return the directed edges incident from vertex {@code v} as an Iterable
     * @throws IllegalArgumentException unless {@code 0 <= v < V}
     */
    public Iterable<DirectedEdge> outEdges(int v) {
        validateVertex(v);
        return outEdges[v];
    }

    public Iterable<DirectedEdge> inEdges(int v) {
        validateVertex(v);
        return inEdges[v];
    }

    public Iterable<Label> connectedLabel(Iterable<Integer> set) {
        Set<Label> labelSet= new HashSet<>();
        for (int v: set) {
            for (DirectedEdge e: outEdges(v)) {
                if (e.label() != null) labelSet.add(e.label());
            }
        }
        return labelSet;
    }



    /**
     * Returns all directed edges in this edge-weighted digraph.
     * To iterate over the edges in this edge-weighted digraph, use foreach notation:
     * {@code for (DirectedEdge e : G.edges())}.
     *
     * @return all edges in this edge-weighted digraph, as an iterable
     */
    public Iterable<DirectedEdge> edges() {
        Set<DirectedEdge> list = new HashSet<>();
        for (int v = 0; v < V; v++) {
            for (DirectedEdge e : outEdges(v)) {
                list.add(e);
            }
        }
        return list;
    }

    /**
     * Returns a string representation of this edge-weighted digraph.
     *
     * @return the number of vertices <em>V</em>, followed by the number of edges <em>E</em>,
     *         followed by the <em>V</em> adjacency lists of edges
     */
    public String toString() {
        StringBuilder s = new StringBuilder();
        s.append(String.format("Vertex: %d Edges: %d\n", V, E));
        for (int v = 0; v < V; v++) {
            s.append(String.format("V%d: ", v));
            for (DirectedEdge e : outEdges[v]) {
                s.append(e).append("  ");
            }
            s.append("\n");
        }
        return s.toString();
    }
}

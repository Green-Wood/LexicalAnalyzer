package FA;

import java.util.HashSet;
import java.util.Set;

public class EpsilonDFS {
    private Set<Integer> reachable;
    /**
     * Computes the vertices in digraph {@code G} that are
     * reachable from the source vertex {@code s}.
     * @param G the digraph
     * @param s the source vertex
     * @throws IllegalArgumentException unless {@code 0 <= s < V}
     */
    public EpsilonDFS(Graph G, int s) {
        reachable = new HashSet<Integer>();
        dfs(G, s);
    }

    public EpsilonDFS(Graph G, Iterable<Integer> sources) {
        reachable = new HashSet<Integer>();
        for (int v : sources) {
            if (!reachable.contains(v)) dfs(G, v);
        }
    }

    private void dfs(Graph G, int v) {
        reachable.add(v);
        for (DirectedEdge e : G.adj(v)) {
            if (!reachable.contains(e.to()) && e.label() == '_')
                dfs(G, e.to());
        }
    }

    /**
     * return vertex that are reachable from source
     * @return vertex
     */
    public Iterable<Integer> closure() {
        return reachable;
    }
}

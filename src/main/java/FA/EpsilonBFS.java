package FA;

import java.util.*;

public class EpsilonBFS {
    private Set<Integer> reachable;
    /**
     * Computes the vertices in digraph {@code G} that are
     * reachable from the source vertex {@code s}.
     * @param G the digraph
     * @param s the source vertex
     * @throws IllegalArgumentException unless {@code 0 <= s < V}
     */
    public EpsilonBFS(Graph G, int s) {
        reachable = new HashSet<>();
        bfs(G, Collections.singleton(s));
    }

    public EpsilonBFS(Graph G, Iterable<Integer> sources) {
        reachable = new HashSet<>();
        bfs(G, sources);
    }

    private void bfs(Graph G, Iterable<Integer> sources) {
        Queue<Integer> queue = new ArrayDeque<>();
        for (int v: sources) {
            queue.add(v);
        }

        while (!queue.isEmpty()) {
            int v = queue.poll();
            reachable.add(v);
            for (DirectedEdge e: G.outEdges(v)) {
                if (e.isEpsilon() && !reachable.contains(e.to())) queue.add(e.to());
            }
        }
    }

    /**
     * return vertex that are reachable from source
     * @return vertex
     */
    public Set<Integer> closure() {
        return reachable;
    }

    public boolean isEmpty() {
        return reachable.isEmpty();
    }
}

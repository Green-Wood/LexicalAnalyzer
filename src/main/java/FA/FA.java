package FA;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public abstract class FA {

    Graph graph;
    // for single finalState NFA, finalState index always equals to V-1
    Map<Integer, String> finalStateMap;
    Set<Label> labelSet;

    FA(Label label) {

        assert label != null;

        this.graph = new Graph(2);
        this.finalStateMap = new HashMap<>();
        this.labelSet = new HashSet<>();

        addEdge(0, 1, label);
        labelSet.add(label);
    }

    FA(int V) {
        this.graph = new Graph(V);
        this.finalStateMap = new HashMap<>();
        this.labelSet = new HashSet<>();
    }

    int V() {
        return graph.V();
    }

    int E() {
        return graph.E();
    }

    Iterable<DirectedEdge> edges() {
        return graph.edges();
    }

    Iterable<DirectedEdge> outEdges(int v) {
        return graph.outEdges(v);
    }

    Iterable<DirectedEdge> inEdges(int v) {
        return graph.inEdges(v);
    }

    String patternName(int stateId) {
        // TODO add exception
        return finalStateMap.get(stateId);
    }

    void addEdge(int from, int to, Label label) {
        if (label != null) labelSet.add(label);
        DirectedEdge e = new DirectedEdge(from, to, label);
        graph.addEdge(e);
    }

    private void validateRegExp(String regExp) {
        // TODO add validation in the future
    }

    /**
     * set pattern name for Token usage
     * @param name pattern name
     */
    void setPatternName(String name) {
        finalStateMap.put(V() - 1, name);
    }

    /**
     * set pattern name for Token usage given stateId,
     * which can be used to assign multi pattern name with multi finalState
     * @param stateId finalState id
     * @param name pattern name
     */
    void setPatternName(int stateId, String name) {
        finalStateMap.put(stateId, name);
    }

    public abstract String recognize(String text);
}

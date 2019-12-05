package FA;

public class DirectedEdge {
    private final int from;
    private final int to;
    private final Label label;

    /**
     * Initializes a directed edge from vertex {@code from} to vertex {@code to} with
     * the given {@code weight}.
     * @param from the tail vertex
     * @param to the head vertex
     * @param label the weight of the directed edge
     * @throws IllegalArgumentException if either {@code from} or {@code to}
     *    is a negative integer
     */
    public DirectedEdge(int from, int to, Label label) {
        if (from < 0) throw new IllegalArgumentException("Vertex names must be nonnegative integers");
        if (to < 0) throw new IllegalArgumentException("Vertex names must be nonnegative integers");
        this.from = from;
        this.to = to;
        this.label = label;
    }

    /**
     * Returns the tail vertex of the directed edge.
     * @return the tail vertex of the directed edge
     */
    public int from() {
        return from;
    }

    /**
     * Returns the head vertex of the directed edge.
     * @return the head vertex of the directed edge
     */
    public int to() {
        return to;
    }

    /**
     * Returns the label of the directed edge.
     * @return the label of the directed edge
     */
    public Label label() {
        return label;
    }

    /**
     * Returns a string representation of the directed edge.
     * @return a string representation of the directed edge
     */
    public String toString() {
        return from + "->" + to + " Label: " + label.c;
    }
}

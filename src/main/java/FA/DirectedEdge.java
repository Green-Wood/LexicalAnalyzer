package FA;

public class DirectedEdge {
    private final int from;
    private final int to;
    private Label label;

    public DirectedEdge(int from, int to, Label label) {
        if (from < 0) throw new IllegalArgumentException("Vertex names must be nonnegative integers");
        if (to < 0) throw new IllegalArgumentException("Vertex names must be nonnegative integers");
        this.from = from;
        this.to = to;
        this.label = label;
    }

    public boolean isEpsilon() {
        return label == null;
    }

    public int from() {
        return from;
    }

    public int to() {
        return to;
    }

    public Label label() {
        return label;
    }

    public String toString() {
        return from + "->" + to + " Label: " + (label == null? "null": label.c);
    }
}

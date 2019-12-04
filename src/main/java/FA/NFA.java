package FA;

import java.util.*;

public class NFA {
    private Graph graph;
    private int finalState;

    private void validateRegExp(String regExp) {
        // TODO add validation in the future
    }

    /**
     * build NFA based on regular expression
     * @param regExp regular expression
     * @return NFA
     */
    public static NFA builder(String regExp) {
        String postfixRegExp = PostFix.infixToPostfix(regExp);

        char[] re = postfixRegExp.toCharArray();
        Deque<NFA> operands = new ArrayDeque<NFA>();

        for(char c: re) {
            if (!PostFix.precedenceMap.containsKey(c)) {
                operands.push(new NFA(c));
            } else if (c == '*') {
                NFA nfa = operands.pop();
                NFA kleeneNfa = nfa.kleene();
                operands.push(kleeneNfa);
            } else if (c == '|') {
                NFA secondNfa = operands.pop();
                NFA firstNfa = operands.pop();
                NFA unionNfa = firstNfa.union(secondNfa);
                operands.push(unionNfa);
            } else if (c == '《') {
                //concat two NFA
                NFA secondNfa = operands.pop();
                NFA firstNfa = operands.pop();
                NFA concatNfa = firstNfa.concat(secondNfa);
                operands.push(concatNfa);
            }
        }
        return operands.pop();
    }

    /**
     *
     * @param text text that you need to recognize
     * @return if this text matches regular expression
     */
    public boolean recognize(String text) {
        EpsilonDFS epsilonDfs = new EpsilonDFS(graph, 0);
        char[] textArr = text.toCharArray();

        for (char c: textArr) {
            if (PostFix.precedenceMap.containsKey(c))
                throw new IllegalArgumentException("text contains the metacharacter '" + c + "'");

            Set<Integer> matchSet = new HashSet<Integer>();
            for (int v: epsilonDfs.closure()) {
                for (DirectedEdge e: adj(v)) {
                    if (e.label() == c || e.label() == '.') matchSet.add(e.to());
                }
            }

            epsilonDfs = new EpsilonDFS(graph, matchSet);
        }
        for (int v: epsilonDfs.closure())
            if (v == finalState) return true;
        return false;
    }

    private NFA(char c) {
        this.graph = new Graph(2);
        finalState = 1;
        addEdge(0, 1, c);
    }

    private NFA(int V) {
        this.graph = new Graph(V);
        finalState = V - 1;
    }

    public int finalState() {
        return finalState;
    }
    public int V() {
        return graph.V();
    }

    public int E() {
        return graph.E();
    }

    public Iterable<DirectedEdge> edges() {
        return graph.edges();
    }

    public Iterable<DirectedEdge> adj(int v) {
        return graph.adj(v);
    }

    private void addEdge(int from, int to, char label) {
        DirectedEdge e = new DirectedEdge(from, to, label);
        graph.addEdge(e);
    }

    /**
     * make new (NFA)* and return
     * @return new (NFA)*
     */
    public NFA kleene() {
        NFA resNFA = new NFA(V() + 2);
        // '_' is the wildcard character
        resNFA.addEdge(0, 1, '_');

        // copy edges from original NFA
        for (DirectedEdge e: edges()) {
            resNFA.addEdge(e.from() + 1, e.to() + 1, e.label());
        }

        // add edge to final vertex
        resNFA.addEdge(V(), V() + 1, '_');

        // Loop back from last state of n to initial state of n.
        resNFA.addEdge(V(), 1, '_');

        // Add empty transition from new initial state to new final state.
        resNFA.addEdge(0, V() + 1, '_');

        return resNFA;
    }

    /**
     * (this《another), which means concat another NFA to the end of this NFA
     * @param another NFA as second operand
     * @return new NFA that concat two NFA
     */
    public NFA concat(NFA another) {
        NFA resNFA = new NFA(this.V() + another.V());
        // copy edges from first NFA
        for (DirectedEdge e: this.edges()) {
            resNFA.addEdge(e.from(), e.to(), e.label());
        }

        // add epsilon edge to concat two NFA
        resNFA.addEdge(this.finalState, this.V(), '_');

        // copy edges from second NFA
        for (DirectedEdge e: another.edges()) {
            resNFA.addEdge(e.from() + this.V(), e.to() + this.V(), e.label());
        }

        return resNFA;
    }

    /**
     * (this | another)
     * @param another NFA as second operand
     * @return new NFA that union two NFA
     */
    public NFA union(NFA another) {
        NFA resNFA = new NFA(this.V() + another.V() + 2);

        resNFA.addEdge(0, 1, '_');

        // copy from first NFA
        for (DirectedEdge e: this.edges()) {
            resNFA.addEdge(e.from() + 1, e.to() + 1, e.label());
        }

        // add epsilon edge to new final state
        resNFA.addEdge(this.finalState + 1, resNFA.finalState, '_');

        resNFA.addEdge(0, this.V() + 1, '_');

        for (DirectedEdge e: another.edges()) {
            int from = e.from() + this.V() + 1;
            int to = e.to() + this.V() + 1;
            resNFA.addEdge(from, to, e.label());
        }

        resNFA.addEdge(another.finalState + this.V() + 1, resNFA.finalState, '_');

        return resNFA;
    }

    @Override
    public String toString() {
        return "NFA{" +
                "graph=" + graph +
                ", finalState=" + finalState +
                '}';
    }
}

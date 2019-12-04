package FA;

import java.util.*;

public class NFA {
    private Graph graph;
    // for single finalState NFA, finalState index always equals to V-1
    private Map<Integer, String> finalStateMap;

    private void validateRegExp(String regExp) {
        // TODO add validation in the future
    }

    /**
     * set pattern name for Token usage
     * @param name
     */
    public void setPatternName(String name) {
        finalStateMap.put(V() - 1, name);
    }

    /**
     * build NFA based on regular expression
     * @param regExp regular expression
     * @return NFA
     */
    public static NFA builder(String regExp, String pattern) {
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
        NFA nfa =  operands.pop();
        nfa.setPatternName(pattern);
        return nfa;
    }

    /**
     * determine whether text matches regular expression pattern
     * @param text text that you need to recognize
     * @return if this text matches regular expression, return pattern name. else return ""
     */
    public String recognize(String text) {
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
            // optimization
            if(epsilonDfs.isEmpty()) return "";
        }
        // TreeSet take the advantage of sorting keys automatically
        TreeSet<Integer> finalStates = new TreeSet<Integer>();
        for (int v: epsilonDfs.closure()) {
            if (finalStateMap.containsKey(v))
                finalStates.add(v);
        }
        if (finalStates.isEmpty()) return "";
        // choose pattern name corresponding to the minimum id when multi finalState encountered
        return finalStateMap.get(finalStates.first());
    }

    private NFA(char c) {
        this.graph = new Graph(2);
        this.finalStateMap = new HashMap<Integer, String>();
        finalStateMap.put(1, "");
        addEdge(0, 1, c);
    }

    private NFA(int V) {
        this.graph = new Graph(V);
        this.finalStateMap = new HashMap<Integer, String>();
        finalStateMap.put(V - 1, "");
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
        resNFA.addEdge(this.V() - 1, this.V(), '_');

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
        int newFinalState = this.V() + another.V() + 1;

        resNFA.addEdge(0, 1, '_');

        // copy from first NFA
        for (DirectedEdge e: this.edges()) {
            resNFA.addEdge(e.from() + 1, e.to() + 1, e.label());
        }

        // add epsilon edge to new final state
        resNFA.addEdge(this.V(), newFinalState, '_');

        resNFA.addEdge(0, this.V() + 1, '_');

        for (DirectedEdge e: another.edges()) {
            int from = e.from() + this.V() + 1;
            int to = e.to() + this.V() + 1;
            resNFA.addEdge(from, to, e.label());
        }

        resNFA.addEdge(another.V() + this.V(), newFinalState, '_');

        return resNFA;
    }

    @Override
    public String toString() {
        return "NFA{" +
                "graph=" + graph +
                ", finalState=" + finalStateMap +
                '}';
    }
}

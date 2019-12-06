package FA;

import java.util.*;

public class NFA {
    private Graph graph;
    // for single finalState NFA, finalState index always equals to V-1
    private Map<Integer, String> finalStateMap;
    private static Set<Character> operatorSet = new HashSet<>(Arrays.asList('*', '|', '《', '+', '?'));

    private void validateRegExp(String regExp) {
        // TODO add validation in the future
    }

    /**
     * set pattern name for Token usage
     * @param name pattern name
     */
    private void setPatternName(String name) {
        finalStateMap.put(V() - 1, name);
    }

    /**
     * set pattern name for Token usage given stateId,
     * which can be used to assign multi pattern name with multi finalState
     * @param stateId finalState id
     * @param name pattern name
     */
    private void setPatternName(int stateId, String name) {
        finalStateMap.put(stateId, name);
    }

    /**
     * build NFA based on regular expression
     * @param regExp regular expression
     * @return NFA
     */
    public static NFA builder(String regExp, String pattern) {
        List<Label> postfixRegExp = Preprocess.toPostfix(regExp);

        Deque<NFA> operands = new ArrayDeque<>();

        for(Label label : postfixRegExp) {
            if (label.isMeta || !operatorSet.contains(label.c)) {
                operands.push(new NFA(label));

            } else if (label.c == '*') {
                NFA nfa = operands.pop();
                NFA kleeneNfa = nfa.kleene();
                operands.push(kleeneNfa);

            } else if (label.c == '|') {
                NFA secondNfa = operands.pop();
                NFA firstNfa = operands.pop();
                NFA unionNfa = firstNfa.union(secondNfa);
                operands.push(unionNfa);

            } else if (label.c == '《') {
                //concat two NFA
                NFA secondNfa = operands.pop();
                NFA firstNfa = operands.pop();
                NFA concatNfa = firstNfa.concat(secondNfa);
                operands.push(concatNfa);

            } else if (label.c == '+') {
                NFA nfa = operands.pop();
                NFA plusNfa = nfa.plus();
                operands.push(plusNfa);

            } else if (label.c == '?') {
                NFA nfa = operands.pop();
                NFA quesNfa = nfa.question();
                operands.push(quesNfa);
            } else {
                System.out.println("Encountered with known operator!!!! " +
                        "Please make sure your regular expression only contains (* | + ?)");
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
            Set<Integer> matchSet = new HashSet<>();
            for (int v: epsilonDfs.closure()) {
                for (DirectedEdge e: adj(v)) {
                    if (!e.isEpsilon() && e.label().isMatch(c))
                        matchSet.add(e.to());
                }
            }

            epsilonDfs = new EpsilonDFS(graph, matchSet);
            // optimization
            if(epsilonDfs.isEmpty()) return "";
        }
        // TreeSet take the advantage of sorting keys automatically
        TreeSet<Integer> finalStates = new TreeSet<>();
        for (int v: epsilonDfs.closure()) {
            if (finalStateMap.containsKey(v))
                finalStates.add(v);
        }
        if (finalStates.isEmpty()) return "";
        // choose pattern name corresponding to the minimum id when multi finalState encountered
        return finalStateMap.get(finalStates.first());
    }

    private NFA(Label label) {
        this.graph = new Graph(2);
        this.finalStateMap = new HashMap<>();
        finalStateMap.put(1, "");
        addEdge(0, 1, label);
    }

    private NFA(int V) {
        this.graph = new Graph(V);
        this.finalStateMap = new HashMap<>();
        finalStateMap.put(V - 1, "");
    }

    int V() {
        return graph.V();
    }

    int E() {
        return graph.E();
    }

    private Iterable<DirectedEdge> edges() {
        return graph.edges();
    }

    private Iterable<DirectedEdge> adj(int v) {
        return graph.adj(v);
    }

    public String patternName(int stateId) {
        // TODO add exception
        return finalStateMap.get(stateId);
    }

    private void addEdge(int from, int to, Label label) {
        DirectedEdge e = new DirectedEdge(from, to, label);
        graph.addEdge(e);
    }

    /**
     * make new (NFA)* and return
     * @return new (NFA)*
     */
    private NFA kleene() {
        NFA resNFA = new NFA(V() + 2);
        resNFA.addEdge(0, 1, null);

        // copy edges from original NFA
        for (DirectedEdge e: edges()) {
            resNFA.addEdge(e.from() + 1, e.to() + 1, e.label());
        }

        // add edge to final vertex
        resNFA.addEdge(V(), V() + 1, null);

        // Loop back from last state of n to initial state of n.
        resNFA.addEdge(V(), 1, null);

        // Add empty transition from new initial state to new final state.
        resNFA.addEdge(0, V() + 1, null);

        return resNFA;
    }

    /**
     * (a|b)?  occur zero or one time
     * @return new nfa
     */
    private NFA question() {
        NFA resNfa = new NFA(V() + 2);

        resNfa.addEdge(0, 1, null);
        resNfa.addEdge(0, V() + 1, null);
        for (DirectedEdge e: edges()) {
            resNfa.addEdge(e.from() + 1, e.to() + 1, e.label());
        }
        resNfa.addEdge(V(), V() + 1, null);

        return resNfa;
    }

    /**
     * (a|b)+   occur one and more times
     * @return new nfa
     */
    private NFA plus() {
        NFA copyNfa = new NFA(V());
        // copy self
        for (DirectedEdge e: edges()) {
            copyNfa.addEdge(e.from(), e.to(), e.label());
        }

        NFA kleeneNfa = kleene();
        return copyNfa.concat(kleeneNfa);
    }

    /**
     * (this《another), which means concat another NFA to the end of this NFA
     * @param another NFA as second operand
     * @return new NFA that concat two NFA
     */
    private NFA concat(NFA another) {
        NFA resNFA = new NFA(this.V() + another.V());
        // copy edges from first NFA
        for (DirectedEdge e: this.edges()) {
            resNFA.addEdge(e.from(), e.to(), e.label());
        }

        // add epsilon edge to concat two NFA
        resNFA.addEdge(this.V() - 1, this.V(), null);

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
    private NFA union(NFA another) {
        NFA resNFA = new NFA(this.V() + another.V() + 2);
        int newFinalState = this.V() + another.V() + 1;

        resNFA.addEdge(0, 1, null);

        // copy from first NFA
        for (DirectedEdge e: this.edges()) {
            resNFA.addEdge(e.from() + 1, e.to() + 1, e.label());
        }

        // add epsilon edge to new final state
        resNFA.addEdge(this.V(), newFinalState, null);

        resNFA.addEdge(0, this.V() + 1, null);

        for (DirectedEdge e: another.edges()) {
            int from = e.from() + this.V() + 1;
            int to = e.to() + this.V() + 1;
            resNFA.addEdge(from, to, e.label());
        }

        resNFA.addEdge(another.V() + this.V(), newFinalState, null);

        return resNFA;
    }

    /**
     * merge two NFA into one NFA with single source and multi finalState
     * @param another NFA
     * @return one NFA with single source and multi finalState. Notice that first NFA will have smaller finalState id.
     */
    public NFA merge(NFA another) {
        NFA resNfa = new NFA(this.V() + another.V() + 1);

        // add edge from source to first NFA
        resNfa.addEdge(0, 1, null);
        // copy first NFA
        for (DirectedEdge e: this.edges()) {
            resNfa.addEdge(e.from() + 1, e.to() + 1, e.label());
        }

        // add edge from source to second NFA
        resNfa.addEdge(0, this.V() + 1, null);
        // copy second NFA
        for (DirectedEdge e: another.edges()) {
            int from = e.from() + this.V() + 1;
            int to  = e.to() + this.V() + 1;
            resNfa.addEdge(from, to, e.label());
        }

        // copy first finalState and patter name
        for (Map.Entry<Integer, String> entry: this.finalStateMap.entrySet()) {
            resNfa.setPatternName(entry.getKey() + 1, entry.getValue());
        }

        // copy second finalState and patter name
        for (Map.Entry<Integer, String> entry: another.finalStateMap.entrySet()) {
            resNfa.setPatternName(entry.getKey() + this.V() + 1, entry.getValue());
        }

        return resNfa;
    }

    @Override
    public String toString() {
        return "NFA{" +
                "graph=" + graph +
                ", finalState=" + finalStateMap +
                '}';
    }
}

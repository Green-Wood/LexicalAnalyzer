package FA;

import java.util.*;
import java.util.stream.Collectors;

public class DFA extends FA{

    private DFA(int V) {
        super(V);
    }

    public static DFA builder(NFA nfa) {
        Map<Set<Integer>, Integer> stateIdMap = new HashMap<>();
        Map<Integer, Set<DirectedEdge>> adjMap = new HashMap<>();
        Queue<Set<Integer>> queue = new ArrayDeque<>();
        // push source
        int id = 0;
        queue.add(Collections.singleton(0));
        while (!queue.isEmpty()) {
            EpsilonDFS dfs = new EpsilonDFS(nfa.graph, queue.poll());
            Set<Integer> epsilonClosure = dfs.closure();
            if (!stateIdMap.containsKey(epsilonClosure)) {
                stateIdMap.put(epsilonClosure, id);
                id++;
            }
            for (Label label: nfa.labelSet) {
                Set<Integer> reachable = new HashSet<>();
                for (int v: epsilonClosure) {
                    for (DirectedEdge e: nfa.graph.adj(v)) {
                        if (label.equals(e.label())) reachable.add(e.to());
                    }
                }
                // no edge matches
                if (reachable.isEmpty()) continue;

                EpsilonDFS reachDfs = new EpsilonDFS(nfa.graph, reachable);
                reachable = reachDfs.closure();

                if (!stateIdMap.containsKey(reachable)) {
                    stateIdMap.put(reachable, id);
                    queue.add(reachable);
                    id++;
                }
                int from = stateIdMap.get(epsilonClosure);
                int to = stateIdMap.get(reachable);
                DirectedEdge edge = new DirectedEdge(from, to, label);
                Set<DirectedEdge> adjv = adjMap.getOrDefault(from, new HashSet<>());
                adjv.add(edge);
                adjMap.put(from, adjv);
            }
        }

        // construct DFA edge
        DFA dfa = new DFA(stateIdMap.size());
        for (Map.Entry<Integer, Set<DirectedEdge>> entry: adjMap.entrySet()) {
            for (DirectedEdge e: entry.getValue()) {
                dfa.addEdge(e.from(), e.to(), e.label());
            }
        }

        // set finalSet pattern name
        for (Set<Integer> set: stateIdMap.keySet()) {
            TreeSet<Integer> finalStates = new TreeSet<>();
            for (int v: set) {
                if (nfa.finalStateMap.containsKey(v)) {
                    finalStates.add(v);
                }
            }
            if (finalStates.isEmpty()) continue;
            int finalState = finalStates.first();
            dfa.setPatternName(stateIdMap.get(set), nfa.finalStateMap.get(finalState));
        }

        return dfa;
    }

    @Override
    public String recognize(String text) {
        int currentState = 0;
        char[] charArr = text.toCharArray();
        for (char c: charArr) {
            boolean isMatch = false;
            for (DirectedEdge e: graph.adj(currentState)) {
                // not consider wildcards
                if (e.label().isMatch(c)) {
                    currentState = e.to();
                    isMatch = true;
                    break;
                }
            }
            if (!isMatch) return "";
        }
        if (!finalStateMap.containsKey(currentState)) return "";
        return finalStateMap.get(currentState);
    }

    public DFA minimize() {
        List<Set<Integer>> P = new ArrayList<>();
        Queue<Set<Integer>> W = new ArrayDeque<>();
        Set<Integer> finalSet = new HashSet<>();
        Set<Integer> nonFinal = new HashSet<>();
        for (int v = 0; v < V(); v++) {
            if (finalStateMap.containsKey(v)) {
                finalSet.add(v);
            } else {
                nonFinal.add(v);
            }
        }

        P.add(finalSet);
        P.add(nonFinal);
        W.add(finalSet);

        while (!W.isEmpty()) {
            Set<Integer> A = W.poll();
            for (Label label: labelSet) {
                Set<Integer> X = new HashSet<>();
                for (DirectedEdge e: edges()) {
                    if (label.equals(e.label()) && A.contains(e.to())) X.add(e.from());
                }
                List<Set<Integer>> newP = new ArrayList<>();
                for (Set<Integer> Y: P) {
                    Set<Integer> intersect = X.stream().filter(Y::contains).collect(Collectors.toSet());
                    Set<Integer> minus = Y.stream().filter(x -> !X.contains(x)).collect(Collectors.toSet());
                    if (!intersect.isEmpty() && !minus.isEmpty()) {
                        newP.add(intersect);
                        newP.add(minus);
                        if (W.contains(Y)) {
                            W.remove(Y);
                            W.add(intersect);
                            W.add(minus);
                        } else {
                            if (intersect.size() <= minus.size()) {
                                W.add(intersect);
                            } else {
                                W.add(minus);
                            }
                        }
                    } else {
                        newP.add(Y);
                    }
                }
                P = newP;
            }
        }

        DFA dfa = new DFA(P.size());
        int firstIndex = findInitSet(P);
        List<Set<Integer>> sortP = new ArrayList<>();
        for (int i = firstIndex; i < P.size(); i++) {
            sortP.add(P.get(i));
        }
        for (int i = 0; i < firstIndex; i++) {
            sortP.add(P.get(i));
        }

        for (int i = 0; i < sortP.size(); i++) {
            int represent = findRepresent(sortP.get(i));
            for (DirectedEdge e: adj(represent)) {
                int to = findSetByVertex(e.to(), sortP);
                dfa.addEdge(i, to, e.label());
            }
        }

        for (Map.Entry<Integer, String> entry: finalStateMap.entrySet()) {
            int setId = findSetByVertex(entry.getKey(), sortP);
            dfa.setPatternName(setId, entry.getValue());
        }

        return dfa;
    }

    private int findRepresent(Set<Integer> set) {
        int represent = -1;
        for (int v: set) {
            represent = v;
        }
        return represent;
    }

    private int findInitSet(List<Set<Integer>> setList) {
        int i = 0;
        for (Set<Integer> s: setList) {
            if (s.contains(0)) return i;
            i++;
        }
        return i;
    }

    private int findSetByVertex(int v, List<Set<Integer>> setList) {
        for (int i = 0; i < setList.size(); i++) {
            if (setList.get(i).contains(v)) return i;
        }
        return -1;
    }
}

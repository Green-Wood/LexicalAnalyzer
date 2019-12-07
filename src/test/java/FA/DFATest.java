package FA;

import org.junit.Test;

import static org.junit.Assert.*;

public class DFATest {

    @Test
    public void convertFromNFA() {
        String re = "a|b";
        String pattern = "re";
        NFA nfa = NFA.builder(re, pattern);
        DFA dfa = DFA.builder(nfa);
        assertEquals(2, dfa.E());
        assertEquals(3, dfa.V());
        for (DirectedEdge e: dfa.edges()) {
            assertFalse(e.isEpsilon());
        }
    }

    @Test
    public void kleene() {
        String re = "a**";
        NFA nfa = NFA.builder(re, "");
        DFA dfa = DFA.builder(nfa);
        assertEquals(2, dfa.E());
        assertEquals(2, dfa.V());
        for (DirectedEdge e: dfa.edges()) {
            assertFalse(e.isEpsilon());
        }
    }

    @Test
    public void concat() {
        String re = "a|b|c|d";
        NFA nfa = NFA.builder(re, "");
        DFA dfa = DFA.builder(nfa);
        assertEquals(4, dfa.E());
        assertEquals(5, dfa.V());
        for (DirectedEdge e: dfa.edges()) {
            assertFalse(e.isEpsilon());
        }
    }

    @Test
    public void union() {
        String re = "abcd";
        NFA nfa = NFA.builder(re, "");
        assertEquals(8, nfa.V());
        assertEquals(7, nfa.E());
    }

    @Test
    public void recognizePlus() {
        String re = "a(a|b)+";
        String pattern = "re";
        String text1 = "a";
        String text2 = "ababb";
        NFA nfa = NFA.builder(re, pattern);
        DFA dfa = DFA.builder(nfa);
        assertEquals("", dfa.recognize(text1));
        assertEquals(pattern, dfa.recognize(text2));
    }

    @Test
    public void recognizeQuestion() {
        String re = "a(a|b)?";
        String pattern = "re";
        String text1 = "a";
        String text2 = "ab";
        String text3 = "abb";
        NFA nfa = NFA.builder(re, pattern);
        DFA dfa = DFA.builder(nfa);
        assertEquals(pattern, dfa.recognize(text1));
        assertEquals(pattern, dfa.recognize(text2));
        assertEquals("", dfa.recognize(text3));
    }

    @Test
    public void recognize() {
        String pattern = "re";
        String re = "(a|b)*a";
        String text1 = "ababab";
        String text2 = "bbbbba";
        NFA nfa = NFA.builder(re, pattern);
        DFA dfa = DFA.builder(nfa);
        assertEquals("", dfa.recognize(text1));
        assertEquals(pattern, dfa.recognize(text2));
    }


    @Test
    public void recognizeWS() {
        String pattern = "re";
        String re = "[\\t\\n\\|]+";
        String text1 = "\t";
        String text2 = "\n";
        String text4 = "|";
        String text3 = " ";
        NFA nfa = NFA.builder(re, pattern);
        DFA dfa = DFA.builder(nfa);
        assertEquals(pattern, dfa.recognize(text1));
        assertEquals(pattern, dfa.recognize(text2));
        assertEquals(pattern, dfa.recognize(text4));
        assertEquals("", dfa.recognize(text3));
    }

    @Test
    public void recognizeMultiRe() {
        String pattern1 = "re1";
        String re1 = "a(a|b)*a";
        String pattern2 = "re2";
        String re2 = "ab*";

        String text1 = "abbababba";
        String text2 = "abb";

        NFA nfa1 = NFA.builder(re1, pattern1);
        NFA nfa2 = NFA.builder(re2, pattern2);
        NFA nfa = nfa1.merge(nfa2);

        DFA dfa = DFA.builder(nfa);
        assertEquals(pattern1, dfa.recognize(text1));
        assertEquals(pattern2, dfa.recognize(text2));
    }

    @Test
    public void recognizeMultiReInOrder() {
        String pattern1 = "re1";
        String re1 = "a(a|b)*";
        String pattern2 = "re2";
        String re2 = "ab*";

        String text1 = "abbb";    //should be recognized as "a(a|b)*"
        String text2 = "a";

        NFA nfa1 = NFA.builder(re1, pattern1);
        NFA nfa2 = NFA.builder(re2, pattern2);
        NFA nfa = nfa1.merge(nfa2);

        DFA dfa = DFA.builder(nfa);
        assertEquals(pattern1, dfa.recognize(text1));
        assertEquals(pattern1, dfa.recognize(text2));
    }

    @Test
    public void recognizeMetaChar() {
        String re = "\\(0\\)\\*2";
        String pattern = "re";
        NFA nfa = NFA.builder(re, pattern);
        DFA dfa = DFA.builder(nfa);
        String text1 = "(0)*2";
        String text2 = "(0)";
        assertEquals(pattern, dfa.recognize(text1));
        assertEquals("", dfa.recognize(text2));
    }

    @Test
    public void recognizeDoubleSlash() {
        String re = "\\\\|a";
        String pattern = "re";
        NFA nfa = NFA.builder(re, pattern);
        DFA dfa = DFA.builder(nfa);
        String text1 = "\\";
        String text2 = "a";
        String text3 = "<\\a()>";
        assertEquals(pattern, dfa.recognize(text1));
        assertEquals(pattern, dfa.recognize(text2));
        assertEquals("", dfa.recognize(text3));
    }

    @Test
    public void recognizeSlash() {
        String re = "green_wood";
        String pattern = "re";
        NFA nfa = NFA.builder(re, pattern);
        DFA dfa = DFA.builder(nfa);
        String text1 = "green_wood";
        String text2 = "greenwood";
        assertEquals(pattern, dfa.recognize(text1));
        assertEquals("", dfa.recognize(text2));
    }

    @Test
    public void recognizeAZ() {
        String re = "[A-Za-z]";
        String pattern = "re";
        NFA nfa = NFA.builder(re, pattern);
        DFA dfa = DFA.builder(nfa);
        String text1 = "a";
        String text2 = "!";
        assertEquals(pattern, dfa.recognize(text1));
        assertEquals("", dfa.recognize(text2));
    }

    @Test
    public void recognizeBracket() {
        String re = "(a|b)*\\([b-zB-Z]\\.[0-9]\\)";
        String pattern = "re";
        NFA nfa = NFA.builder(re, pattern);
        DFA dfa = DFA.builder(nfa);
        String text1 = "abba(T.2)";
        String text2 = "abba(a.1)";
        String text3 = "abba(a11)";
        assertEquals(pattern, dfa.recognize(text1));
        assertEquals("", dfa.recognize(text2));
        assertEquals("", dfa.recognize(text3));
    }

    @Test
    public void recognizeUnionPriority() {
        String re = "aa|bc";
        String pattern = "re";
        NFA nfa = NFA.builder(re, pattern);
        DFA dfa = DFA.builder(nfa);
        String text1 = "aa";
        String text2 = "abc";
        assertEquals(pattern, dfa.recognize(text1));
        assertEquals("", dfa.recognize(text2));
    }

    @Test
    public void minimizeUnion() {
        String re = "a|b|c|d|e";
        String pattern = "re";
        NFA nfa = NFA.builder(re, pattern);
        DFA dfa = DFA.builder(nfa);
        dfa = dfa.minimize();
        assertEquals(5, dfa.E());
        assertEquals(2, dfa.V());
    }
}
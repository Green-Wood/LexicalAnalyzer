package FA;

import org.junit.Test;

import static org.junit.Assert.*;

public class NFATest {

    @org.junit.Test
    public void builder() {
        String re = "((a.b)|c)*";
        NFA nfa = NFA.builder(re, "");
        assertEquals(12, nfa.V());
        assertEquals(14, nfa.E());
    }

    @org.junit.Test
    public void kleene() {
        String re = "a**";
        NFA nfa = NFA.builder(re, "");
        assertEquals(6, nfa.V());
        assertEquals(9, nfa.E());
    }

    @org.junit.Test
    public void concat() {
        String re = "a|b|c|d";
        NFA nfa = NFA.builder(re, "");
        assertEquals(14, nfa.V());
        assertEquals(16, nfa.E());
    }

    @org.junit.Test
    public void union() {
        String re = "abcd";
        NFA nfa = NFA.builder(re, "");
        assertEquals(8, nfa.V());
        assertEquals(7, nfa.E());
    }

    @Test
    public void recognize() {
        String pattern = "re";
        String re = "(a|b)*a";
        String text1 = "ababab";
        String text2 = "bbbbba";
        NFA nfa = NFA.builder(re, pattern);
        assertEquals("", nfa.recognize(text1));
        assertEquals(pattern, nfa.recognize(text2));
    }

    @Test
    public void recognizeAnyChar() {
        String pattern = "re";
        String re = "a.*b";
        String text1 = "ab";
        String text2 = "asdasdasfdsfiojasiob";
        String text3 = "a";
        NFA nfa = NFA.builder(re, pattern);
        assertEquals(pattern, nfa.recognize(text1));
        assertEquals(pattern, nfa.recognize(text2));
        assertEquals("", nfa.recognize(text3));
    }

    @Test
    public void recognizeWS() {
        String pattern = "re";
        String re = "( |\n|\r|\t)( |\n|\r|\t)*";
        String text1 = "     ";
        String text2 = "  \t   \n  \r ";
        String text3 = "a\t";
        NFA nfa = NFA.builder(re, pattern);
        assertEquals(pattern, nfa.recognize(text1));
        assertEquals(pattern, nfa.recognize(text2));
        assertEquals("", nfa.recognize(text3));
    }
}
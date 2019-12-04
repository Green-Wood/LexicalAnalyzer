package FA;

import org.junit.Test;

import static org.junit.Assert.*;

public class NFATest {

    @org.junit.Test
    public void builder() {
        String re = "((a.b)|c)*";
        NFA nfa = NFA.builder(re);
        assertEquals(12, nfa.V());
        assertEquals(14, nfa.E());
        assertEquals(11, nfa.finalState());
    }

    @org.junit.Test
    public void kleene() {
        String re = "a**";
        NFA nfa = NFA.builder(re);
        assertEquals(6, nfa.V());
        assertEquals(9, nfa.E());
        assertEquals(5, nfa.finalState());
    }

    @org.junit.Test
    public void concat() {
        String re = "a|b|c|d";
        NFA nfa = NFA.builder(re);
        assertEquals(14, nfa.V());
        assertEquals(16, nfa.E());
        assertEquals(13, nfa.finalState());
    }

    @org.junit.Test
    public void union() {
        String re = "abcd";
        NFA nfa = NFA.builder(re);
        assertEquals(8, nfa.V());
        assertEquals(7, nfa.E());
        assertEquals(7, nfa.finalState());
    }

    @Test
    public void recognize() {
        String re = "(a|b)*a";
        String text1 = "ababab";
        String text2 = "bbbbba";
        NFA nfa = NFA.builder(re);
        assertFalse(nfa.recognize(text1));
        assertTrue(nfa.recognize(text2));
    }

    @Test
    public void recognizeAnyChar() {
        String re = "a.*b";
        String text1 = "ab";
        String text2 = "asdasdasfdsfiojasiob";
        String text3 = "a";
        NFA nfa = NFA.builder(re);
        assertTrue(nfa.recognize(text1));
        assertTrue(nfa.recognize(text2));
        assertFalse(nfa.recognize(text3));
    }

    @Test
    public void recognizeWS() {
        String re = "( |\n|\r|\t)( |\n|\r|\t)*";
        String text1 = "     ";
        String text2 = "  \t   \n  \r ";
        String text3 = "a\t";
        NFA nfa = NFA.builder(re);
        assertTrue(nfa.recognize(text1));
        assertTrue(nfa.recognize(text2));
        assertFalse(nfa.recognize(text3));
    }
}
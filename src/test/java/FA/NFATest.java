package FA;

import org.junit.Test;

import static org.junit.Assert.*;

public class NFATest {

    @Test
    public void builder() {
        String re = "((a.b)|c)*";
        NFA nfa = NFA.builder(re, "");
        assertEquals(12, nfa.V());
        assertEquals(14, nfa.E());
    }

    @Test
    public void kleene() {
        String re = "a**";
        NFA nfa = NFA.builder(re, "");
        assertEquals(6, nfa.V());
        assertEquals(9, nfa.E());
    }

    @Test
    public void concat() {
        String re = "a|b|c|d";
        NFA nfa = NFA.builder(re, "");
        assertEquals(14, nfa.V());
        assertEquals(16, nfa.E());
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
        assertEquals("", nfa.recognize(text1));
        assertEquals(pattern, nfa.recognize(text2));
    }

    @Test
    public void recognizeQuestion() {
        String re = "a(a|b)?";
        String pattern = "re";
        String text1 = "a";
        String text2 = "ab";
        String text3 = "abb";
        NFA nfa = NFA.builder(re, pattern);
        assertEquals(pattern, nfa.recognize(text1));
        assertEquals(pattern, nfa.recognize(text2));
        assertEquals("", nfa.recognize(text3));
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

        assertEquals(pattern1, nfa.recognize(text1));
        assertEquals(pattern2, nfa.recognize(text2));
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

        assertEquals(pattern1, nfa.recognize(text1));
        assertEquals(pattern1, nfa.recognize(text2));
    }

    @Test
    public void recognizeMetaChar() {
        String re = "\\(0\\)\\*2";
        String pattern = "re";
        NFA nfa = NFA.builder(re, pattern);
        String text1 = "(0)*2";
        String text2 = "(0)";
        assertEquals(pattern, nfa.recognize(text1));
        assertEquals("", nfa.recognize(text2));
    }

    @Test
    public void recognizeDoubleSlash() {
        String re = "<\\\\a\\((a|b)\\)>";
        String pattern = "re";
        NFA nfa = NFA.builder(re, pattern);
        String text1 = "<\\a(b)>";
        String text2 = "<\\a(b)>";
        String text3 = "<\\a()>";
        assertEquals(pattern, nfa.recognize(text1));
        assertEquals(pattern, nfa.recognize(text2));
        assertEquals("", nfa.recognize(text3));
    }

    @Test
    public void recognizeSlash() {
        String re = "green_wood";
        String pattern = "re";
        NFA nfa = NFA.builder(re, pattern);
        String text1 = "green_wood";
        String text2 = "greenwood";
        assertEquals(pattern, nfa.recognize(text1));
        assertEquals("", nfa.recognize(text2));
    }
}
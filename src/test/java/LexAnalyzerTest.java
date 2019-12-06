import org.junit.Test;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.Assert.*;

public class LexAnalyzerTest {
    private static List<String> regs = new ArrayList<>();
    static {
        regs.add("delim [\\t\\n]");
        regs.add("ws      {delim}+");
        regs.add("letter   [A-Za-z]");
        regs.add("digit    [0-9]");
        regs.add("id        {letter}({letter}|{digit})*");
    }


    @Test
    public void parse() {
        LexAnalyzer lexAnalyzer = new LexAnalyzer(regs);
        assertEquals("[\\t\\n]", lexAnalyzer.patternReMap.get("delim"));
        assertEquals("[\\t\\n]+", lexAnalyzer.patternReMap.get("ws"));
        assertEquals("[A-Za-z]", lexAnalyzer.patternReMap.get("letter"));
        assertEquals("[0-9]", lexAnalyzer.patternReMap.get("digit"));
        assertEquals("[A-Za-z]([A-Za-z]|[0-9])*", lexAnalyzer.patternReMap.get("id"));
    }

    @Test
    public void parseFile() throws FileNotFoundException {
        LexAnalyzer lexAnalyzer = new LexAnalyzer("pattern.txt");
        assertEquals("[\\t\\n]", lexAnalyzer.patternReMap.get("delim"));
        assertEquals("[\\t\\n]+", lexAnalyzer.patternReMap.get("ws"));
        assertEquals("[A-Za-z]", lexAnalyzer.patternReMap.get("letter"));
        assertEquals("[0-9]", lexAnalyzer.patternReMap.get("digit"));
        assertEquals("[A-Za-z]([A-Za-z]|[0-9])*", lexAnalyzer.patternReMap.get("id"));
    }

    @Test
    public void testNFA() {
        LexAnalyzer lexAnalyzer = new LexAnalyzer(regs);
        String text1 = "\n";
        String text2 = "a";
        String text3 = "2";
        String text4 = "text4";
        assertEquals("delim", lexAnalyzer.nfa.recognize(text1));
        assertEquals("letter", lexAnalyzer.nfa.recognize(text2));
        assertEquals("digit", lexAnalyzer.nfa.recognize(text3));
        assertEquals("id", lexAnalyzer.nfa.recognize(text4));
    }
}
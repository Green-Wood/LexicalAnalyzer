import exception.GrammarException;
import exception.RegExpException;
import org.junit.Test;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.*;

import static org.junit.Assert.*;

public class LexAnalyzerTest {
    private static List<String> regs = new ArrayList<>();
    static {
        regs.add("delim [\\t\\n ]");
        regs.add("ws      {delim}+");
        regs.add("letter   [A-Za-z]");
        regs.add("digit    [0-9]");
        regs.add("id        {letter}({letter}|{digit})*");
    }

    @Test
    public void parseFile() throws FileNotFoundException, RegExpException {
        LexAnalyzer lexAnalyzer = new LexAnalyzer("resources/REJava.l");
        assertEquals("[\\t\\r\\n ]", lexAnalyzer.patternReMap.get("delim"));
        assertEquals("([\\t\\r\\n ])+", lexAnalyzer.patternReMap.get("ws"));
        assertEquals("[A-Za-z_]", lexAnalyzer.patternReMap.get("letter_"));
        assertEquals("[0-9]", lexAnalyzer.patternReMap.get("digit"));
        assertEquals("([A-Za-z_])(([A-Za-z_])|([0-9]))*", lexAnalyzer.patternReMap.get("id"));
    }

    @Test
    public void testNFA() throws RegExpException {
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

    boolean isEqual(Token token, String pattern, String lexeme, int id) {
        return token.pattern.equals(pattern) && token.lexeme.equals(lexeme) && token.id == id;
    }

    @Test
    public void testFile1() throws IOException, GrammarException, RegExpException {
        LexAnalyzer lexAnalyzer = new LexAnalyzer("resources/REJava.l");
        List<Token> tokenList = lexAnalyzer.analyze("resources/input1.txt");
        assertTrue(isEqual(tokenList.get(0), "key", "public", 0));
        assertTrue(isEqual(tokenList.get(27), "id", "a", 4));
        assertTrue(isEqual(tokenList.get(39), "number", "2e-10", 2));
    }

    @Test
    public void testFile2() throws IOException, RegExpException {
        LexAnalyzer lexAnalyzer = new LexAnalyzer("resources/REJava.l");
        try {
            lexAnalyzer.analyze("resources/input2.txt");
        } catch (GrammarException e) {
            assertEquals("Regular expressions fail to match at line: 4\n" +
                    "        String s = `hello`;\n" +
                    "                   ^", e.getMessage());
        }
    }

    @Test
    public void testFile3() throws IOException, GrammarException, RegExpException {
        LexAnalyzer lexAnalyzer = new LexAnalyzer("resources/REJava.l");
        List<Token> tokenList = lexAnalyzer.analyze("resources/input3.txt");
    }
}
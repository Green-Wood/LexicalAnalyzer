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

    private static List<String> javaRegs = Arrays.asList(
            "delim -> [\\t\\r\\n ]",
            "ws -> {delim}+",
            "digit -> 0|1|2|3|4|5|6|7|8|9",
            "digits -> {digit}+",
            "optionalFraction -> .{digits}|ε",
            "optionalExponent -> (E(+|-|ε){digits})|ε",
            "number -> {digits}{optionalFraction}{optionalExponent}",
            "key -> abstract|assert|boolean|break|byte|case|catch|char|class|const|continue|default|do|double|else|enum|extends|final|finally|float|for|goto|if|implements|import|instanceof|int|interface|long|native|new|package|private|protected|public|return|strictfp|short|static|super|switch|synchronized|this|throw|throws|transient|try|void|volatile|while",
            "letter_ -> [A-Za-z_]",
            "id -> {letter_}({letter_}|{digit})*",
            "operation -> ~|!|-|++|--"+
            "|+|-|\\*|/|%"+
            "|?|:"+
            "|==|!=|>|<|>=|<="+
            "|&|\\||^"+
            "|&&|\\|\\|"+
            "|=|+=|-=|\\*=|/=|%=|&=|^=|\\|=|<<=|>>="+
            "|<<|>>|>>>",
            "punctuation -> \\(|\\)|\\{|\\}|[|]|;|\"|'|,|."
    );

    @Test
    public void parseFile() throws FileNotFoundException, RegExpException {
        LexAnalyzer lexAnalyzer = new LexAnalyzer("pattern.txt");
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

    boolean isEqual(Token token, String pattern, String text, int id) {
        return token.pattern.equals(pattern) && token.text.equals(text) && token.id == id;
    }

    @Test
    public void testFile1() throws IOException, GrammarException, RegExpException {
        LexAnalyzer lexAnalyzer = new LexAnalyzer("pattern.txt");
        List<Token> tokenList = lexAnalyzer.analyze("test1.txt");
        assertTrue(isEqual(tokenList.get(0), "key", "public", 0));
        assertTrue(isEqual(tokenList.get(27), "id", "a", 4));
        assertTrue(isEqual(tokenList.get(39), "number", "2e-10", 2));
    }

    @Test
    public void testFile12() throws IOException, RegExpException {
        LexAnalyzer lexAnalyzer = new LexAnalyzer("pattern.txt");
        try {
            lexAnalyzer.analyze("test2.txt");
        } catch (GrammarException e) {
            assertEquals("Grammar seem to be wrong at line: 4\n" +
                    "        String s = `hello`;\n" +
                    "                   ^", e.getMessage());
        }
    }
}
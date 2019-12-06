import org.junit.Test;

import java.util.HashSet;
import java.util.Set;

import static org.junit.Assert.*;

public class LexAnalyzerTest {
    @Test
    public void parse() {
        Set<String> regs = new HashSet<>();
        regs.add("delim [\t\n]");
        regs.add("ws      {delim}+");
        regs.add("letter   [A-Za-z]");
        regs.add("digit    [0-9]");
        regs.add("id        {letter}({letter}|{digit})*");
        LexAnalyzer lexAnalyzer = new LexAnalyzer(regs);
        assertEquals("[\t\n]", lexAnalyzer.patternReMap.get("delim"));
        assertEquals("[\t\n]+", lexAnalyzer.patternReMap.get("ws"));
        assertEquals("[A-Za-z]", lexAnalyzer.patternReMap.get("letter"));
        assertEquals("[0-9]", lexAnalyzer.patternReMap.get("digit"));
        assertEquals("[A-Za-z]([A-Za-z]|[0-9])*", lexAnalyzer.patternReMap.get("id"));
    }
}
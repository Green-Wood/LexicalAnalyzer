package FA;

import org.junit.Test;

import java.util.List;

import static org.junit.Assert.*;

public class PostFixTest {
    @Test
    public void addConcatMetaChar() {
        String s = "\\(0\\)";
        String res = PostFix.addConcat(s);
        assertEquals("\\(《0《\\)", res);
    }

    @Test
    public void addConcat() {
        String s = "(aa|b)*(ab|aa)*";
        String res = PostFix.addConcat(s);
        assertEquals("(a《a|b)*《(a《b|a《a)*", res);
    }

    @Test
    public void addConcatDoubleSlash() {
        String s = "<\\\\a\\((a|b)\\)>";
        String res = PostFix.addConcat(s);
        assertEquals("<《\\\\《a《\\(《(a|b)《\\)《>", res);
    }

    @Test
    public void toPostMetaChar() {
        String s = "\\(0\\)";
        List<Label> res = PostFix.toPostfix(s);
        String expected = "(0《)《";
        assertEquals(expected.length(), res.size());
        for (int i = 0; i < res.size(); i++) {
            assertEquals(expected.charAt(i), res.get(i).c);
        }
    }

    @Test
    public void toPostfix() {
        String s = "(aa|b)*(ab|aa)*";
        List<Label> res = PostFix.toPostfix(s);
        String expected = "aa《b|*ab《aa《|*《";
        assertEquals(expected.length(), res.size());
        for (int i = 0; i < res.size(); i++) {
            assertEquals(expected.charAt(i), res.get(i).c);
        }
    }

    @Test
    public void toPostDoubleSlash() {
        String s = "<\\\\a\\((a|b)\\)>";
        List<Label> res = PostFix.toPostfix(s);
        String expected = "<\\《a《(《ab|《)《>《";
        assertEquals(expected.length(), res.size());
        for (int i = 0; i < res.size(); i++) {
            assertEquals(expected.charAt(i), res.get(i).c);
        }
    }
}
package FA;

import javafx.geometry.Pos;
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
    public void addUnionNumber() {
        String s = "[0-9]";
        String res = PostFix.addUnion(s);
        assertEquals("(0|1|2|3|4|5|6|7|8|9)", res);
    }

    @Test
    public void addUnionHybrid() {
        String s = "(a|b)*\\([a-zA-Z]\\.[0-9]\\)";
        String res = PostFix.addUnion(s);
        assertEquals("(a|b)*\\((a|b|c|d|e|f|g|h|i|j|k|l|m|n|o|p|q|r|s|t|u|v|w|x|y|z|" +
                "A|B|C|D|E|F|G|H|I|J|K|L|M|N|O|P|Q|R|S|T|U|V|W|X|Y|Z)\\.(0|1|2|3|4|5|6|7|8|9)\\)", res);
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

    @Test
    public void toPostSpecial() {
        String s = "[\t\n]+";
        List<Label> res = PostFix.toPostfix(s);
        String expected = "\t\n|+";
        assertEquals(expected.length(), res.size());
        for (int i = 0; i < res.size(); i++) {
            assertEquals(expected.charAt(i), res.get(i).c);
        }
    }
}
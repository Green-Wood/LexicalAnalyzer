import java.util.HashMap;
import java.util.Map;

public class Token {
    public String pattern;
    public String lexeme;
    public int id;
    // used as a counter to record id
    private static Map<String, Integer> patternIdMap = new HashMap<>();

    public Token(String pattern, String lexeme) {
        this.id = patternIdMap.getOrDefault(pattern, 0);
        patternIdMap.put(pattern, this.id + 1);
        this.pattern = pattern;
        this.lexeme = lexeme;
    }

    @Override
    public String toString() {
        return "Token{" +
                "pattern='" + pattern + '\'' +
                ", lexeme='" + lexeme + '\'' +
                ", id=" + id +
                '}';
    }
}

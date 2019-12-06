import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class Token {
    public String pattern;
    public String text;
    public int id;
    private static Map<String, Integer> patternIdMap = new HashMap<String, Integer>();

    public Token(String pattern, String text) {
        this.id = patternIdMap.getOrDefault(pattern, 0);
        patternIdMap.put(pattern, this.id + 1);
        this.pattern = pattern;
        this.text = text;
    }

    @Override
    public String toString() {
        return "Token{" +
                "pattern='" + pattern + '\'' +
                ", text='" + text + '\'' +
                ", id=" + id +
                '}';
    }
}

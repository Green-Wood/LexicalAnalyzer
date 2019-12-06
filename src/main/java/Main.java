import java.io.IOException;
import java.util.List;

public class Main {
    public static void main(String[] args) throws IOException {
        LexAnalyzer analyzer = new LexAnalyzer("pattern.txt");
        try {
            List<Token> tokenList = analyzer.analyze("text.txt");
        } catch (GrammarException e) {
            System.out.println(e.getMessage());
        }
    }
}

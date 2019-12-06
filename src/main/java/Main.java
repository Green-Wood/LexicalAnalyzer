import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

public class Main {
    public static void main(String[] args) throws IOException, RegExpException, GrammarException {
        LexAnalyzer analyzer = new LexAnalyzer("pattern.txt");
        List<Token> tokenList = analyzer.analyze("test1.txt");
        List<String> tokenStr = tokenList.stream()
                .filter(token -> !token.pattern.equals("ws") && !token.pattern.equals("delim"))
                .map(Token::toString)
                .collect(Collectors.toList());
        Path file = Paths.get("result1.txt");
        Files.write(file, tokenStr, StandardCharsets.UTF_8);
    }
}

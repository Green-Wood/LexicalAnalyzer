import exception.MatchingException;
import exception.RegExpException;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;

public class Main {
    public static void main(String[] args) throws IOException, RegExpException, MatchingException {
        LexAnalyzer analyzer = new LexAnalyzer("resources/REJava.l");  // read pattern and regExp
        List<Token> tokenList = analyzer.analyze("resources/input3.txt");   // read file to analyze
        List<String> tokenStr = tokenList.stream()
                .filter(token -> !token.pattern.equals("ws") && !token.pattern.equals("delim"))
                .map(Token::toString)
                .collect(Collectors.toList());
        Path file = Paths.get("resources/output3.txt");
        Files.write(file, tokenStr, StandardCharsets.UTF_8);
    }
}

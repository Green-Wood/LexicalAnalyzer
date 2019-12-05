import FA.NFA;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

public class LexAnalyzer {
    public static final String ANSI_RESET = "\u001B[0m";
    public static final String ANSI_BLACK = "\u001B[30m";
    public static final String ANSI_RED = "\u001B[31m";
    public static final String ANSI_GREEN = "\u001B[32m";
    public static final String ANSI_YELLOW = "\u001B[33m";
    public static final String ANSI_BLUE = "\u001B[34m";
    public static final String ANSI_PURPLE = "\u001B[35m";
    public static final String ANSI_CYAN = "\u001B[36m";
    public static final String ANSI_WHITE = "\u001B[37m";

    private Map<String, String> rawPatternReMap;
    private List<String> patternList;
    Map<String, String> patternReMap;
    private NFA nfa;

    public LexAnalyzer(String regFilename) throws FileNotFoundException {
        rawPatternReMap = new HashMap<>();
        patternReMap = new HashMap<>();
        patternList = new ArrayList<>();

        File file = new File(regFilename);
        Scanner in = new Scanner(file);
        while (in.hasNextLine()) {
            String line = in.nextLine();
            String[] lineArr = line.split("\\s+", 2);
            rawPatternReMap.put(lineArr[0], lineArr[1]);
            patternList.add(lineArr[0]);
        }
        parseRegExp();
        initNFA();
    }

    /**
     * For testing
     * @param regs to build lex analyzer
     */
    public LexAnalyzer(Set<String> regs) {
        rawPatternReMap = new HashMap<>();
        patternReMap = new HashMap<>();
        patternList = new ArrayList<>();

        for (String line: regs) {
            String[] lineArr = line.split("\\s+", 2);
            rawPatternReMap.put(lineArr[0], lineArr[1]);
            patternList.add(lineArr[0]);
        }
        parseRegExp();
        initNFA();
    }

    /**
     * parse raw regexp like {pattern} to real regexp
     */
    private void parseRegExp() {
        for (Map.Entry<String, String> entry: rawPatternReMap.entrySet()) {
            String pattern = entry.getKey();
            parseRecursive(pattern);
        }
    }

    /**
     * parse raw to pure regular expression recursively
     * @param pattern
     * @return pure regular expression
     */
    private String parseRecursive(String pattern) {
        if (patternReMap.containsKey(pattern))
            return patternReMap.get(pattern);

        String regExp = rawPatternReMap.get(pattern);
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < regExp.length(); i++) {
            char c = regExp.charAt(i);
            if (c == '{' && (i == 0 || regExp.charAt(i-1) != '\\')) {
                int j = i + 1;
                String realReg = null;
                for (; j < regExp.length(); j++) {
                    if (regExp.charAt(j) == '}') {
                        realReg = parseRecursive(regExp.substring(i + 1, j));
                        break;
                    }
                }
                // TODO if realReg is null throw exception
                sb.append(realReg);
                i = j;
            } else {
                sb.append(c);
            }
        }
        String pureReg = sb.toString();
        patternReMap.put(pattern, pureReg);

        return pureReg;
    }

    /**
     * construct NFA for each regular expression and merge them
     */
    private void initNFA() {
        String firstPattern = patternList.get(0);
        nfa = NFA.builder(patternReMap.get(firstPattern), firstPattern);

        for (int i = 1; i < patternList.size(); i++) {
            String pattern = patternList.get(i);
            String regExp = patternReMap.get(pattern);
            NFA nextNfa = NFA.builder(regExp, pattern);
            nfa = nfa.merge(nextNfa);
        }
    }

    private String readFile(String path, Charset encoding) throws IOException {
        byte[] encoded = Files.readAllBytes(Paths.get(path));
        return new String(encoded, encoding);
    }

    public List<Token> analyze(String textFileName) throws IOException, GrammarException {
        String content = readFile(textFileName, StandardCharsets.UTF_8);
        List<Token> tokenList = new ArrayList<>();

        int lineNum = 1;
        int i = 0;
        while (i < content.length()) {
            int j = i + 1;
            String lastMatchesText = null;
            String lastMatchesPattern = null;
            int lastJ = j;
            while (j <= content.length()) {
                String text = content.substring(i, j);
                String pattern = nfa.recognize(text);
                if (!pattern.equals("")) {
                    lastMatchesPattern = pattern;
                    lastMatchesText = text;
                    lastJ = j;
                }
                j += 1;
            }

            if (lastMatchesText == null) {
                // in the end, we cannot match any pattern
                int[] tup = findSingleLine(i, content);
                String msg = String.format("Grammar seem to be wrong at line: %d\n", lineNum);
                msg += content.substring(tup[0], i) + ANSI_RED + content.charAt(i) + ANSI_RESET + content.substring(i + 1, tup[1]);
                throw new GrammarException(msg);
            }
            // record lineNumber
            if (lastMatchesText.contains("\n")) lineNum++;

            tokenList.add(new Token(lastMatchesPattern, lastMatchesText));
            i = lastJ;
        }

        return tokenList;
    }

    private int[] findSingleLine(int i, String content) {
        int begin = i, end = i;
        for (; begin >= 0; begin--) {
            if (content.charAt(begin) == '\n') break;
        }
        for (; end < content.length(); end++) {
            if (content.charAt(end) == '\n') break;
        }
        return new int[]{begin, end};
    }
}

import FA.DFA;
import FA.FA;
import FA.NFA;
import exception.MatchingException;
import exception.RegExpException;

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
    FA fa;

    public LexAnalyzer(String regFilename) throws FileNotFoundException, RegExpException {
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

        initFA();
    }

    /**
     * For testing
     * @param regs to build lex analyzer
     */
    public LexAnalyzer(List<String> regs) throws RegExpException {
        rawPatternReMap = new HashMap<>();
        patternReMap = new HashMap<>();
        patternList = new ArrayList<>();

        for (String line: regs) {
            String[] lineArr = line.split("\\s+", 2);
            rawPatternReMap.put(lineArr[0], lineArr[1]);
            patternList.add(lineArr[0]);
        }
        initFA();
    }

    private void initFA() throws RegExpException {
        parseRegExp();
        NFA nfa = initNFA();
        DFA dfa = DFA.builder(nfa);
        dfa = dfa.minimize();
        fa = dfa;
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
                        String needPattern = regExp.substring(i + 1, j);
                        if (needPattern.equals(pattern))
                            throw new StackOverflowError("Recursive definition" + pattern + "can not exist!");
                        realReg = parseRecursive(needPattern);
                        break;
                    }
                }
                // TODO if realReg is null throw exception
                sb.append("(").append(realReg).append(")");
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
    private NFA initNFA() throws RegExpException {
        String firstPattern = patternList.get(0);
        NFA nfa = NFA.builder(patternReMap.get(firstPattern), firstPattern);

        for (int i = 1; i < patternList.size(); i++) {
            String pattern = patternList.get(i);
            String regExp = patternReMap.get(pattern);
            NFA nextNfa;
            try {
                nextNfa = NFA.builder(regExp, pattern);
            } catch (NoSuchElementException e) {
                throw new RegExpException("Parsing failed, please check your \nPattern: " +
                        pattern + "\nRegExp: " + regExp + "\n");
            }

            nfa = nfa.merge(nextNfa);
        }

        return nfa;
    }

    private String readFile(String path, Charset encoding) throws IOException {
        byte[] encoded = Files.readAllBytes(Paths.get(path));
        return new String(encoded, encoding);
    }

    public List<Token> analyze(String textFileName) throws IOException, MatchingException {
        String content = readFile(textFileName, StandardCharsets.UTF_8);
        List<Token> tokenList = new ArrayList<>();

        int lineNum = 1;
        int i = 0;
        while (i < content.length()) {
            int j = i + 1;
            String lastMatchesLexeme = null;
            String lastMatchesPattern = null;
            int lastJ = j;
            while (j <= content.length()) {
                String text = content.substring(i, j);
                String pattern = fa.recognize(text);
                if (!pattern.equals("")) {
                    lastMatchesPattern = pattern;
                    lastMatchesLexeme = text;
                    lastJ = j;
                }
                j += 1;
            }

            if (lastMatchesLexeme == null) {
                // in the end, we cannot match any pattern
                int[] tup = findSingleLine(i, content);
                String msg = String.format("Regular expressions fail to match at line: %d\n", lineNum);
                msg += content.substring(tup[0], tup[1]) + "\n" + getHint(i - tup[0]);
                throw new MatchingException(msg);
            }
            // record lineNumber
            if (lastMatchesLexeme.contains("\n")) lineNum++;

            tokenList.add(new Token(lastMatchesPattern, lastMatchesLexeme));
            i = lastJ;
        }

        return tokenList;
    }

    /**
     * get a string contains '^' to show where is wrong
     * @param size size of blank space
     * @return
     */
    private String getHint(int size) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < size; i++) {
            sb.append(' ');
        }
        sb.append('^');
        return sb.toString();
    }

    private int[] findSingleLine(int i, String content) {
        int begin = i, end = i;
        for (; begin > 0; begin--) {
            if (content.charAt(begin) == '\n') break;
        }
        for (; end < content.length(); end++) {
            if (content.charAt(end) == '\n') break;
        }
        if (begin > 0) begin++;
        return new int[]{begin, end};
    }
}

import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;

public class LexAnalyzer {
    private Map<String, String> rawPatternReMap;
    public Map<String, String> patternReMap;

    public LexAnalyzer(String fileName) {
        rawPatternReMap = new HashMap<>();
        patternReMap = new HashMap<>();

        File file = new File(fileName);
        try {
            Scanner in = new Scanner(file);
            while (in.hasNextLine()) {
                String line = in.nextLine();
                String[] lineArr = line.split("\\s+", 2);
                rawPatternReMap.put(lineArr[0], lineArr[1]);
            }
            parseRegExp();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    /**
     * For testing
     * @param regs to build lex analyzer
     */
    public LexAnalyzer(Set<String> regs) {
        rawPatternReMap = new HashMap<>();
        patternReMap = new HashMap<>();

        for (String line: regs) {
            String[] lineArr = line.split("\\s+", 2);
            rawPatternReMap.put(lineArr[0], lineArr[1]);
        }
        parseRegExp();
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


}

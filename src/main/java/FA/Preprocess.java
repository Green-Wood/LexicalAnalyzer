package FA;

import java.util.*;

public class Preprocess {

    /** Operators precedence map. */
    public static final Map<Character, Integer> precedenceMap = new HashMap<Character, Integer>();
    static {
        precedenceMap.put('(', 1);
        precedenceMap.put('|', 2);
        precedenceMap.put('《', 3); // explicit concatenation operator
        precedenceMap.put('?', 4);
        precedenceMap.put('*', 4);
        precedenceMap.put('+', 4);
        precedenceMap.put('^', 5);
    }

    /**
     * Get character precedence.
     *
     * @param label character and isMeta
     * @return corresponding precedence
     */
    private static int getPrecedence(Label label) {
        if (label.isMeta) return 6;
        Integer precedence = precedenceMap.get(label.c);
        return precedence == null ? 6 : precedence;
    }

    /**
     * transform elements in bracket
     * @param elements in brackets
     * @return
     */
    private static String handleBracket(String elements) {
        StringBuilder sb = new StringBuilder();
        char[] charArr = elements.toCharArray();
        for (int i = 0; i < charArr.length; i++) {
            if (charArr[i] == '\\') {
                sb.append('\\').append(charArr[i+1]).append('|');
                i += 1;
            } else if (charArr[i] == '-') {
                for (char c = (char)(charArr[i-1] + 1); c < charArr[i+1]; c++) {
                    sb.append(c).append('|');
                }
            } else {
                sb.append(charArr[i]).append('|');
            }
        }
        sb.deleteCharAt(sb.length() - 1);
        return "(" + sb.toString() + ")";
    }

    /**
     * handle bracket in regular expression
     * @param regex
     * @return regExp without bracket
     */
    public static String addUnion(String regex) {
        StringBuilder sb = new StringBuilder();
        char[] charArr = regex.toCharArray();
        for (int i = 0; i < charArr.length; i++) {
            if (charArr[i] == '[' && (i == 0 || charArr[i-1] != '\\')) {
                String parsed = null;
                int j = i + 1;
                for (; j < charArr.length; j++) {
                    if (charArr[j] == ']' && charArr[j-1] != '\\') {
                        String elems = regex.substring(i + 1, j);
                        parsed = handleBracket(elems);
                        break;
                    }
                }
                sb.append(parsed);
                i = j;
            } else {
                sb.append(charArr[i]);
            }
        }
        return sb.toString();
    }

    /**
     * Transform regular expression by inserting a '《' as explicit concatenation
     * operator.
     */
    public static String addConcat(String regex) {
        StringBuilder sb = new StringBuilder();
        List<Character> allOperators = Arrays.asList('|', '?', '+', '*', '^', ')');
        List<Character> binaryOperators = Collections.singletonList('|');

        for (int i = 0; i < regex.length() - 1; i++) {
            char c1 = regex.charAt(i);
            char c2 = regex.charAt(i + 1);
            sb.append(c1);

            // can we put concat operator between c1 and c2 ?
            boolean notMeta = i == 0 || regex.charAt(i-1) != '\\';
            boolean isFirstSlash = c1 == '\\' && notMeta;
            boolean isC1Operator = (c1 == '(' || binaryOperators.contains(c1)) && notMeta;
            boolean isC2Operator = allOperators.contains(c2) || c2 == ')';
            boolean cannotConcat = isC1Operator || isC2Operator || isFirstSlash;
            if (!cannotConcat) {
                sb.append('《');
            }
        }
        sb.append(regex.charAt(regex.length() - 1));

        return sb.toString();
    }

    /**
     * Convert regular expression from infix to postfix notation using
     * Shunting-yard algorithm.
     *
     * @param regex infix notation
     * @return postfix notation
     */
    public static List<Label> toPostfix(String regex) {
        List<Label> postfix = new ArrayList<>();

        Deque<Label> stack = new ArrayDeque<>();

        String withoutBracket = addUnion(regex);
        String formattedRegEx = addConcat(withoutBracket);
        char[] charArr = formattedRegEx.toCharArray();
        for (int i = 0; i < charArr.length; i++) {
            char c = charArr[i];
            switch (c) {
                case '(':
                    stack.push(new Label(c, false));
                    break;

                case ')':
                    while (stack.peek().c != '(') {
                        postfix.add(stack.pop());
                    }
                    stack.pop();
                    break;

                default:
                    while (stack.size() > 0) {
                        Label peekedChar = stack.peek();

                        int peekedCharPrecedence = getPrecedence(peekedChar);
                        int currentCharPrecedence = getPrecedence(new Label(c, false));

                        if (peekedCharPrecedence >= currentCharPrecedence) {
                            postfix.add(stack.pop());
                        } else {
                            break;
                        }
                    }
                    if (c == '\\') {
                        // encountered first back-slash, TODO check i < charArr.length - 1
                        stack.push(new Label(charArr[i + 1], true));
                        i += 1;
                    } else {
                        stack.push(new Label(c, false));
                    }
                    break;
            }
        }

        while (stack.size() > 0)
            postfix.add(stack.pop());

        return postfix;
    }
}

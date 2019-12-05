package FA;

import java.util.*;

public class PostFix {

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
     * Transform regular expression by inserting a '《' as explicit concatenation
     * operator.
     */
    // TODO  1、识别[]
    public static String addConcat(String regex) {
        StringBuilder sb = new StringBuilder();
        List<Character> allOperators = Arrays.asList('|', '?', '+', '*', '^');
        List<Character> binaryOperators = Collections.singletonList('|');

        for (int i = 0; i < regex.length() - 1; i++) {
            char c1 = regex.charAt(i);
            char c2 = regex.charAt(i + 1);
            sb.append(c1);

            // can we put concat operator between c1 and c2 ?
            boolean isWithinMeta = c1 == '\\';
            boolean isParentheses = c1 == '(' || c2 == ')';
            boolean cannotConcat = isParentheses || allOperators.contains(c2) || binaryOperators.contains(c1) || isWithinMeta;
            boolean isSecondMeta = i > 0 && regex.charAt(i - 1) == '\\';
            if (isSecondMeta || !cannotConcat) {
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

        String formattedRegEx = addConcat(regex);
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

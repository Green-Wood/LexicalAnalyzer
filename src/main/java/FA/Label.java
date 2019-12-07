package FA;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * this class is used to record a character and whether it is a metaChar
 * Example:  char * is not meta but an operator
 *          char  \*  is a char that need to be matched
 */
public class Label {
    private static Set<Character> operatorSet = new HashSet<>(Arrays.asList('*', '|', '《', '+', '?'));
    char c;
    boolean isEscape;

    // TODO 有时间可以分成多个类，做\d之类的
    Label(char c, boolean isEscape) {
        if (c == 'n' && isEscape) {
            this.c = '\n';
            this.isEscape = false;
        } else if (c == 't' && isEscape) {
            this.c = '\t';
        } else {
            this.c = c;
        }
        this.isEscape = isEscape;
    }

    boolean isMatch(char c) {
        if (this.c == '.' && !isEscape) {
            // all matches
            return true;
        }
        return this.c == c;
    }

    boolean isOperator() {
        return !isEscape && operatorSet.contains(c);
    }
}

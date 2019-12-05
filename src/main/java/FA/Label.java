package FA;

/**
 * this class is used to record a character and weather it is a metaChar
 * Example:  char * is not meta but an operator
 *          char  \*  is a char that need to be matched
 */
public class Label {
    char c;
    boolean isMeta;
    boolean isEpsilon;

    static Label buildEpsilon() {
        Label label = new Label();
        label.isEpsilon = true;
        return label;
    }

    Label(){}

    // TODO 有时间可以分成多个类，做\d之类的
    Label(char c, boolean isMeta) {
        this.c = c;
        this.isMeta = isMeta;
        this.isEpsilon = false;
    }

    boolean isMatch(char c) {
        if (this.c == '.' && !isMeta) {
            // all matches
            return true;
        }
        return this.c == c;
    }
}

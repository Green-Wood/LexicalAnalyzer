import FA.PostFix;

public class Main {
    public static void main(String[] args) {
        String regx = "((a.b)|c)*";
        String post = PostFix.infixToPostfix(regx);
        System.out.println(post);
    }
}

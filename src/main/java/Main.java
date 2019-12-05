import FA.PostFix;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        File file = new File("test.txt");
        String test = "\\(\\)";
        try {
            Scanner in = new Scanner(file);
            while (in.hasNextLine()) {
                String s = in.nextLine();
                assert test.equals(s);
                System.out.println(s);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

    }
}

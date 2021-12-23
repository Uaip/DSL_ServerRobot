package mysocketRobot;

import java.util.Scanner;

public class test {
    public static void main(String[] args) {
        new test().Integerset();
    }

    public void Integerset() {
        try {
            Scanner sc = new Scanner(System.in);
            String s = sc.nextLine();
            if (s.equals("is")) {
                System.out.println(s);
            }
        } catch (NumberFormatException e) {
            System.out.println("格式错误");
        }
    }
}

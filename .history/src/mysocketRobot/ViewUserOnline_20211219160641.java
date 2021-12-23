package mysocketRobot;

import java.util.NoSuchElementException;
import java.util.Scanner;

class ViewUserOnline implements Runnable {

    @Override
    public void run() {
        while (true) {
            Scanner sc = new Scanner(System.in);
            try {
                String input = sc.nextLine();
                if (input.equals(DSL_SERVER.USERLOOK)) {
                    System.out.println("<< USER LIST ONLINE >>");
                    for (String user : DSL_SERVER.userList)
                        System.out.println("USER:\t" + user);
                }
            } catch (NoSuchElementException e) {

            } finally {
                sc.close();
            }
        }
    }

}

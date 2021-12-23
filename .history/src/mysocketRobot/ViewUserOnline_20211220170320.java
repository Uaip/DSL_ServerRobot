package mysocketRobot;

import java.util.NoSuchElementException;
import java.util.Scanner;

/**
 * This class implements the runnable interface
 * Asynchronous processing of commands from the server itself
 */
class curInterfacesProvided implements Runnable {
    @Override
    public void run() {
        Scanner sc;
        try {
            while (true) {
                sc = new Scanner(System.in);
                String input = sc.nextLine();
                if (input.equals(DSL_SERVER.USERLOOK)) {
                    System.out.println("<< USER ONLINE LIST >>");
                    System.out.println("=================");
                    for (String user : DSL_SERVER.userList)
                        System.out.println("USER:\t" + user);
                    System.out.println("=================");
                }
            }
        } catch (NoSuchElementException e) {
            e.printStackTrace();
        }
    }
}
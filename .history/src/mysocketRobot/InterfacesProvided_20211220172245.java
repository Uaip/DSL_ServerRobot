package mysocketRobot;

import java.util.NoSuchElementException;
import java.util.Scanner;

/**
 * This class implements the runnable interface
 * Asynchronous processing of commands from the server itself
 */
class InterfacesProvided implements Runnable {
    @Override
    public void run() {
        Scanner sc;
        try {
            while (true) {
                sc = new Scanner(System.in);
                String input = sc.nextLine();
                if (input.equalsIgnoreCase(DSL_SERVER.USERLOOK)) {
                    System.out.println("<< USER ONLINE LIST >>");
                    System.out.println("=================");
                    for (String user : DSL_SERVER.userList)
                        System.out.println("USER:\t" + user);
                    System.out.println("=================");
                } else if (input.equalsIgnoreCase("viewparser")) {
                    DSL_SERVER.p.showparser();
                } else if (input.equalsIgnoreCase("sip")) {

                } else if (input.equalsIgnoreCase("ms")) {

                } else if (input.equalsIgnoreCase("sa")) {

                } else {
                    System.out.println("WARNING:instruction \"" + input + "\" is not supported by current version");
                }
            }
        } catch (NoSuchElementException e) {
            e.printStackTrace();
        }
    }
}
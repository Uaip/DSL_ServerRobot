package mysocketRobot;

import java.util.NoSuchElementException;
import java.util.Scanner;

import javax.annotation.processing.SupportedSourceVersion;

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
                } else if (input.equalsIgnoreCase(DSL_SERVER.PARSERLOOK)) {
                    if (DSL_SERVER.p != null)
                        DSL_SERVER.p.showparser();
                    else {
                        System.out.println("p is null");
                    }
                } else if (input.equalsIgnoreCase(DSL_SERVER.ROBOTVERSION)) {
                    System.out.println("Version:" + DSL_SERVER.Version);
                } else if (input.equalsIgnoreCase("currentstate")) {
                    System.out.println("current step:" + Intepreter.curStep);
                }
                /**
                 * The following interface is not yet implemented!!!!
                 */
                else if (input.equalsIgnoreCase(DSL_SERVER.SIPLOOK)) {
                    System.out.println("SIP INFORMATION");
                } else if (input.equalsIgnoreCase(DSL_SERVER.SALOOK)) {
                    System.out.println("SA INFORMATION");
                } else if (input.equalsIgnoreCase(DSL_SERVER.MSLOOK)) {
                    System.out.println("MS INFORMATION");
                } else {
                    System.out.println("WARNING:instruction \"" + input + "\" is not supported by current version");
                }
            }
        } catch (NoSuchElementException e) {
            e.printStackTrace();
        }
    }
}
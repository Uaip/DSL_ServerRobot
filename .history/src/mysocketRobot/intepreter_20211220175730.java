package mysocketRobot;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.net.SocketException;
import java.sql.Date;
import java.text.SimpleDateFormat;

/**
 * The interpreter class implements the runnable interface. This class is used
 * to process user requests, interpret and execute script files. Script files
 * are generated in the parser file and passed parameters through object
 * attributes. Whenever a user ends the operation, the thread automatically
 * closes
 */
class Intepreter implements Runnable {

    private Socket socket;

    public BufferedReader buff;
    public BufferedWriter writer;
    public String userName;
    public String linkedPort;
    public int amount;
    public boolean isinput = false;
    public static String filename = "script.txt";
    public static String curStep;
    public static user a = null;
    public static String action = "";
    public static int stepID = 0;
    public String input = "";
    public int msTos = 1000;

    /**
     * construtor
     * 
     * @param socket
     */
    public Intepreter(Socket socket) {
        this.socket = socket;
        this.linkedPort = String.valueOf(socket.getPort());
        try {
            this.buff = new BufferedReader(new InputStreamReader(
                    socket.getInputStream(), "UTF-8"));
            this.writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream(), "UTF-8"));
        } catch (Exception e) {
            e.printStackTrace();
        }
        sendMsg("\t与客服" + socket.getPort() + "连接成功["
                + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date(System.currentTimeMillis())) + "]");
        System.out.println("用户[PORT:" + socket.getPort() + "]与服务器连接成功");
        DSL_SERVER.userList.add(linkedPort);
    }

    /**
     * main process of processing script
     */
    @Override
    public void run() {
        try {
            try {
                sendMsg("\t为了让您与客服更好的交流,请输入你的名字:");
                this.userName = buff.readLine();
                sendMsg("\t请为您的账户充值:(要求输入整数)");
            } catch (SocketException e) {
                // To reduce memory utilization, no exceptions are thrown when the server is in
                // use
                // e.printStackTrace();
            }
            try {
                /**
                 * The user is allowed to enter account incorrectly at most twice, otherwise the
                 * connection with the user will be closed
                 */
                this.amount = Integer.parseInt(buff.readLine());
            } catch (NumberFormatException e) {
                sendMsg("\t格式错误,请重新输入");
                try {
                    this.amount = Integer.parseInt(buff.readLine());
                } catch (NumberFormatException u) {
                    /**
                     * Before exiting, we need to free up resources
                     */
                    sendMsg("\t输入多次错误, 请重新连接");
                    writer.close();
                    buff.close();
                    socket.close();
                    return;
                }
            }
            /**
             * This is a step to declare a parser object
             */
            DSL_SERVER.p = new parser();

            // Create user object
            this.a = new user(this.userName, this.amount);
            // read script file
            DSL_SERVER.p.parseFile(filename, a);
            // Set current step as script entry
            parser p = DSL_SERVER.p;
            curStep = p.entry;
            while (!action.equalsIgnoreCase("exit")) {
                if (stepID < p.step.get(curStep).size())
                    action = p.step.get(curStep).get(stepID);
                else {
                    action = "exit";
                    sendMsg("\t服务不支持");
                }
                switch (action) {
                    case "Speak":
                        sendMsg("\t" + p.answer.get(curStep).get(stepID++));
                        break;
                    case "Listen":
                        // Every time you enter the listen step, you need to reset the value of isinput
                        // to prevent it from being affected by the last step
                        isinput = false;
                        String[] time = p.answer.get(curStep).get(stepID++).split(",");
                        long SysStartTime = System.currentTimeMillis();
                        int time1 = Integer.parseInt(time[0]);
                        int time2 = Integer.parseInt(time[1]);
                        long SysEndTime = System.currentTimeMillis();
                        new Thread() {
                            @Override
                            public void run() {
                                /**
                                 * This thread is used for read user's input
                                 * This allows us to detect whether the user has timed out in another thread
                                 */
                                try {
                                    input = buff.readLine();
                                    isinput = true;
                                } catch (IOException e) {
                                    // TODO Auto-generated catch block
                                    // e.printStackTrace();
                                }
                            }
                        }.start();

                        while ((SysEndTime - SysStartTime) / msTos < time1 + time2) {
                            SysEndTime = System.currentTimeMillis();
                            if (isinput)
                                break;
                            Thread.currentThread();
                            /**
                             * input = buff.readLine(); this operation would cause blocking and
                             * The serverwaits until there is an input
                             */
                            Thread.sleep(100);
                            /**
                             * The main thread makes concessions so that the input actions of the child
                             * threads can be executed
                             */
                        }
                        break;
                    case "Branch":
                        String curAct = p.answer.get(curStep).get(stepID++);
                        int index1 = curAct.indexOf("\"");
                        int index2 = curAct.lastIndexOf("\"");
                        String option = curAct.substring(index1 + 1, index2);
                        /**
                         * If the user has entered and meets the current branch conditions, the branch
                         * will skip to corresponding branch
                         */
                        if (isinput && input != null && input.contains(option)) {
                            String stepSwithTo = curAct.substring(index2 + 1);
                            curStep = stepSwithTo;
                            stepID = 0;
                        }
                        break;
                    case "Silence":
                        String stepSwithTo = p.answer.get(curStep).get(stepID);
                        curStep = stepSwithTo;
                        stepID = 0;
                        break;
                    case "Default":
                        String stepSwithTo1 = p.answer.get(curStep).get(stepID);
                        curStep = stepSwithTo1;
                        stepID = 0;
                        break;
                    case "Exit":
                        break;
                    default:
                        if (input.contains("退出"))
                            return;
                        break;
                }
            }
        } catch (Exception e) {
            // System.out.println("Connection reset by peer");
        } finally {
            try {
                writer.close();
                buff.close();
                socket.close();
            } catch (Exception e) {

            }
            // after one user disconnected, we need to remove the user from userlist and
            // output disconnection info
            DSL_SERVER.userList.remove(this.linkedPort);
            System.out.println("user " + userName + " [port:" + this.linkedPort + "]" + "与服务器断开了连接");
        }
    }

    private void sendMsg(String msg) {
        try {
            if (!socket.isOutputShutdown()) {
                writer.write(msg);
                writer.write("\n");
                writer.flush();
            }
        } catch (Exception e) {
            System.out.println("The connection is disconnected abnormally");
        }
    }

}
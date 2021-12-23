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

    private BufferedReader buff;

    private BufferedWriter writer;
    private String userName;
    private String linkedPort;
    private int amount;
    private boolean isinput = false;
    private String filename = "script.txt";
    private String curStep;
    private user a = null;
    private String action = "";
    private int stepID = 0;
    private String input = "";
    private int msTos = 1000;

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
        sendMsg("与客服" + socket.getPort() + "连接成功["
                + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date(System.currentTimeMillis())) + "]");
        System.out.println("用户[PORT:" + socket.getPort() + "]与服务器连接成功");
        DSL_SERVER.userList.add(linkedPort);
    }

    @Override
    public void run() {
        try {
            try {
                sendMsg("为了让您与客服更好的交流,请输入你的名字:");
                this.userName = buff.readLine();
                sendMsg("请为您的账户充值:(要求输入整数)");
            } catch (SocketException e) {
                // To reduce memory utilization, no exceptions are thrown when the server is in
                // use
                // e.printStackTrace();
            }
            try {
                this.amount = Integer.parseInt(buff.readLine());
            } catch (NumberFormatException e) {
                sendMsg("格式错误,请重新输入");
                try {
                    this.amount = Integer.parseInt(buff.readLine());
                } catch (NumberFormatException u) {
                    sendMsg("输入多次错误, 请重新连接");
                    writer.close();
                    buff.close();
                    socket.close();
                    return;
                }
            }

            parser p = new parser();

            this.a = new user(this.userName, this.amount);
            p.parseFile(filename, a);
            curStep = p.entry;
            while (!action.equalsIgnoreCase("exit")) {
                if (stepID < p.step.get(curStep).size())
                    action = p.step.get(curStep).get(stepID);
                else {
                    action = "exit";
                    sendMsg("服务不支持");
                }
                switch (action) {
                    case "Speak":
                        sendMsg(p.answer.get(curStep).get(stepID++));
                        break;
                    case "Listen":
                        isinput = false;
                        String[] time = p.answer.get(curStep).get(stepID++).split(",");
                        long SysStartTime = System.currentTimeMillis();
                        int time1 = Integer.parseInt(time[0]);
                        int time2 = Integer.parseInt(time[1]);
                        long SysEndTime = System.currentTimeMillis();
                        new Thread() {
                            @Override
                            public void run() {
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
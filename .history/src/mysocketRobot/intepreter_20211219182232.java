package mysocketRobot;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.Socket;
import java.net.SocketException;
import java.sql.Date;
import java.text.SimpleDateFormat;

/**
 * 处理客户端发送来的消息
 */
class Intepreter implements Runnable {

    private Socket socket;

    private BufferedReader buff;

    private Writer writer;
    // private int timeoutTime = 2;
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
            this.writer = new OutputStreamWriter(socket.getOutputStream(),
                    "UTF-8");
        } catch (Exception e) {
            e.printStackTrace();
        }
        sendMsg("与客服" + socket.getPort() + "连接成功"
                + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date(System.currentTimeMillis())));
        System.out.println("用户[PORT:" + socket.getPort() + "]与服务器连接成功");
        DSL_SERVER.userList.add(linkedPort);
    }

    @Override
    public void run() {
        try {
            sendMsg("请输入你的名字:");
            try {
                this.userName = buff.readLine();
            } catch (SocketException e) {

            }
            sendMsg("请为您的账户充值:(要求输入整数)");
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
                            // input = buff.readLine(); // 阻塞
                            Thread.sleep(100); // 主线程休眠
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
            System.out.println("Connection reset by peer");
        } finally { // 关闭资源，聊天室移除成员
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

    /**
     * 发送消息
     * 
     * @param msg
     */
    private void sendMsg(String msg) {
        try {
            writer.write(msg);
            writer.write("\n");
            writer.flush();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
package mysocketRobot;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.CopyOnWriteArrayList;

import javax.naming.ldap.SortKey;

import java.sql.Date;
import java.text.SimpleDateFormat;

/**
 * Java客服机器人
 * 
 * @author Asspomina
 * @version 1.0
 * @time 2021/12/16
 */
public class DLSserver extends ServerSocket {

    private static final int SERVER_PORT = 8899; // 服务端端口
    private static final String USERLOOK = "useronline"; // 查看在线用户

    public static List<String> userList = new CopyOnWriteArrayList<String>();

    public DLSserver() throws Exception {
        super(SERVER_PORT);
    }

    /**
     * 启动向客户端发送消息的线程，使用线程处理每个客户端发来的消息
     * 
     * @throws Exception
     */
    public void load() throws Exception {
        new Thread(new ViewUserOnline()).start(); // 开启向客户端发送消息的线程

        while (true) {
            // server尝试接收其他Socket的连接请求，server的accept方法是阻塞式的
            Socket socket = this.accept();
            /**
             * 我们的服务端处理客户端的连接请求是同步进行的， 每次接收到来自客户端的连接请求后，
             * 都要先跟当前的客户端通信完之后才能再处理下一个连接请求。 这在并发比较多的情况下会严重影响程序的性能，
             * 为此，我们可以把它改为如下这种异步处理与客户端通信的方式
             */
            // 每接收到一个Socket就建立一个新的线程来处理它
            new Thread(new Intepreter(socket)).start();

        }
    }

    /**
     * 
     */
    class ViewUserOnline implements Runnable {

        @Override
        public void run() {
            while (true) {
                Scanner sc = new Scanner(System.in);
                String input = sc.nextLine();
                if (input.equals(USERLOOK)) {
                    System.out.println("<< USER LIST ONLINE >>");
                    for (String user : userList)
                        System.out.println("USER:\t" + user);
                }
            }
        }

    }

    /**
     * 入口
     * 
     * @param args
     */
    public static void main(String[] args) {
        try {
            DLSserver server = new DLSserver(); // 启动服务端
            server.load();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
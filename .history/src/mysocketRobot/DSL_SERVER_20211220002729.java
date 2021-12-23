package mysocketRobot;

import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Java客服机器人
 * 
 * @author Asspomina
 * @version 1.0
 * @time 2021/12/16
 */
public class DSL_SERVER extends ServerSocket {

    protected static final int SERVER_PORT = 8080; // server port
    protected static final String USERLOOK = "useronline"; // whether view all users online
    // CopyOnWriteArrayList is a data structure created to be used in a concurrent
    // environment.
    public static List<String> userList = new CopyOnWriteArrayList<String>();

    public DSL_SERVER() throws Exception {
        super(SERVER_PORT);
    }

    /**
     * Start the thread that sends messages to the client and use the thread to
     * process messages from each client
     * 
     * @throws Exception
     */
    public void load() throws Exception {
        new Thread(new ViewUserOnline()).start(); // 开启服务器查询在线用户线程

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
     * 入口
     * 
     * @param args
     */
    public static void main(String[] args) {
        DSL_SERVER server;
        try {
            server = new DSL_SERVER(); // 启动服务端
            server.load();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
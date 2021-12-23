package mysocketRobot;

import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * A Simple Server Robot --java
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
        new Thread(new ViewUserOnline()).start(); // this thread is used for view user online.It's done asynchronously

        while (true) {
            // The server attempts to receive connection requests from other sockets. The
            // accept method of the server is blocking
            Socket socket = this.accept();
            /**
             * Our server processes the connection request of the client synchronously.
             * Every time we receive the connection request from the client, we must
             * communicate with the current client before processing the next connection
             * request. This will seriously affect the performance of the program in the
             * case of more concurrency. Therefore, it is necessary to adopt the form of
             * asynchronous processing and open a new thread for each client
             */
            // Each time a socket is received, a new thread is created to process it
            new Thread(new Intepreter(socket)).start();
        }
    }

    /**
     * entrance
     * 
     * @param args
     * @throws Exception
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
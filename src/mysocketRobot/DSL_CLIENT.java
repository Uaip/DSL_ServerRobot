package mysocketRobot;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.Socket;

/**
 * A Simple Server Robot --java
 * 
 * Client
 * 
 * @author Asspomina
 * @version 1.0
 * @time 2021/12/16
 * @codeformat UTF-8
 */
public class DSL_CLIENT extends Socket {
    private static final String SERVER_IP = "127.0.0.1";
    private static int SERVER_PORT = 8080;
    private Socket client;
    private BufferedReader br;
    private BufferedWriter writer;
    private static int defaultValue = 1;
    private static int testMaxUsers = defaultValue; // This is used to test the concurrency ability of the server

    public DSL_CLIENT() throws Exception {
        super(SERVER_IP, SERVER_PORT);
        this.client = this;
        // System.out.println("USER[PORT:" + client.getLocalPort() + "]您与客服连接成功");
    }

    public void load() {
        try {
            this.writer = new BufferedWriter(new OutputStreamWriter(this.getOutputStream(), "UTF-8"));
        } catch (UnsupportedEncodingException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        } catch (IOException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }
        new Thread(new receiveMsg()).start();
        while (true) {
            br = new BufferedReader(new InputStreamReader(System.in));
            String inputMsg;
            try {
                inputMsg = br.readLine();
                inputMsg = NLP(inputMsg);
                writer.write(inputMsg);
                writer.write('\n');
                writer.flush();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                System.out.println("连接断开");
                return;
            }

        }
    }

    /**
     * This method is used for natural language process.
     * 
     * @param str
     * @return
     */
    String NLP(String str) {
        return str;
    }

    class receiveMsg implements Runnable {
        private BufferedReader buff;

        @Override
        public void run() {

            try {
                this.buff = new BufferedReader(new InputStreamReader(client.getInputStream(), "UTF-8"));
                while (true) {
                    String result;

                    result = buff.readLine();
                    if (result == null)
                        break;
                    System.out.println(result);
                }
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } finally {
                try {
                    buff.close();
                    client.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

        }
    }

    public static void main(String[] args) throws Exception {
        DSL_CLIENT client[] = new DSL_CLIENT[testMaxUsers];

        for (int i = 0; i < client.length; i++) {
            client[i] = new DSL_CLIENT();
        }
        for (int i = 0; i < client.length; i++)
            client[i].load();
    }
}

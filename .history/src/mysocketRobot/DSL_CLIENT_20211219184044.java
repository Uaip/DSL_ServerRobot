package mysocketRobot;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.lang.ref.Cleaner.Cleanable;
import java.net.Socket;

public class DSL_CLIENT extends Socket {
    private static final String SERVER_IP = "127.0.0.1";
    private static int SERVER_PORT = 8899;
    private Socket client;
    private BufferedReader br;
    private Writer writer;

    public DSL_CLIENT() throws Exception {
        super(SERVER_IP, SERVER_PORT);
        this.client = this;
        // System.out.println("USER[PORT:" + client.getLocalPort() + "]您与客服连接成功");
    }

    public void load() {
        try {
            this.writer = new OutputStreamWriter(this.getOutputStream(), "UTF-8");
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
        DSL_CLIENT client[] = new DSL_CLIENT[680];

        for (int i = 0; i < client.length; i++) {
            client[i] = new DSL_CLIENT();
        }
        for (int i = 0; i < client.length; i++)
            client[i].load();
        // DSL_CLIENT client;
        // try {
        // client = new DSL_CLIENT();
        // client.load();
        // } catch (Exception e) {
        // // TODO Auto-generated catch block
        // e.printStackTrace();
        // }
    }
}

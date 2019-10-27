package upt.pcbe.messaging.server;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.Socket;

public class Client {
    private static final int port = 1338;

    public static void main(String[] args) {
        try {
            InetAddress ip = InetAddress.getByName("localhost");
            final Socket socket = new Socket(ip, port);

            DataInputStream in = new DataInputStream(socket.getInputStream());
            DataOutputStream out = new DataOutputStream(socket.getOutputStream());

            new Thread() { // reading thread
                public void run() {
                    BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
                    String line;
                    while (true) {
                        try {
                            line = br.readLine();

                            if (line.equals("exit")) {
                                socket.close();
                                break;
                            } else if (line.startsWith("message:")) {
//                                Message msg = Message.contructMessage(line);
                                out.writeUTF(line);
                                out.flush();
                                // System.out.println("Send msg to server: " + msg);
                            }
                        } catch (Throwable t) {
                            System.out.println("Error in read thread");
                            t.printStackTrace();
                        }
                    }
                }
            }.start();

            while (true) {
                String received = in.readUTF();
                // System.out.println("Server sent);
                System.out.println(received);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

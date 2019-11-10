package upt.pcbe.messaging.test;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicInteger;

import upt.pcbe.messaging.shared.Message;

public class ClientThreadTest implements Runnable {
    private static final String FILE_PATH = "D:\\Other Projects\\pcbe-messaging-sockets\\src\\upt\\pcbe\\messaging\\test\\others_message.data";
    private final int port = 1338;
    private BufferedReader br;
    private Integer clientId;
    private CopyOnWriteArrayList<String> msgList = new CopyOnWriteArrayList<String>();
    private AtomicInteger messagesSent = new AtomicInteger(0);

    public ClientThreadTest(Integer clientId) {
        this.clientId = clientId;
    }

    @Override
    public void run() {
        try {
            

            prepareData();

            InetAddress ip = InetAddress.getByName("localhost");
            final Socket socket = new Socket(ip, port);

            DataInputStream in = new DataInputStream(socket.getInputStream());
            DataOutputStream out = new DataOutputStream(socket.getOutputStream());

            // Thread t = new Thread() { // reading thread
            // public void run() {
            String line;
            while (true) {
                try {
                    line = send();

                    if (line == null || line.equals("exit")) {
                        // socket.close();
                        System.out.println("Client " + clientId + " shutdown");
                        return;
                    } else if (line.startsWith("message:")) {
                        out.writeUTF(line);
                        out.flush();
                    }
                } catch (Throwable t) {
                    System.out.println("Error in read thread");
                    t.printStackTrace();
                }
            }
            // }
            // };
            // t.setName("Reading Thread Client " + clientId);
            // t.start();

            // while (true) {
            // String received = in.readUTF();
            // receive(received);
            // if (received.equals("last")) {
            // System.out.println("Successfully send " + messagesSent + " messages.");
            // System.out.println("MessageStack size:" + msgList.size());
            // socket.close();
            // break;
            // }
            // }
        } catch (Throwable e) {
            System.out.println("Client test error");
            e.printStackTrace();
        }

    }

    private void prepareData() {
        try {
            br = new BufferedReader(new FileReader(new File(FILE_PATH)));
        } catch (FileNotFoundException e) {
            System.out.println("File not found");
        }
    }

    // private void receive(String line) {
    // if (line.equals("Hi new Client")) return;
    // boolean success = msgList.remove(line);
    // if (success) {
    // messagesSent.incrementAndGet();
    // } else {
    // System.out.println("Could not delete message:" + line);
    // }
    // }

    private String send() {
        String line;
        try {
            line = br.readLine();
            if (line != null && line.startsWith("message")) {
                Message m = Message.contructMessage(line);
                m.setReceiver(ClientsTest.CLIENT_TEST);
                if (m.getReceiver() == clientId) {
                    msgList.add(m.getMessage());
                }
                return m.toString();
            }
        } catch (IOException e) {
            System.out.println("Can not read from file");
            return null;
        }
        return line;
    }

}

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

public class ClientMainTest {
    private static final int port = 1338;
    private static BufferedReader br;
    private static Integer clientId;
    private static CopyOnWriteArrayList<String> selfMsgList = new CopyOnWriteArrayList<String>();
    private static AtomicInteger selfSendMsg = new AtomicInteger(0);
    private static AtomicInteger messagesReceived = new AtomicInteger(0);
    private static final String FILE_PATH = "D:\\Other Projects\\pcbe-messaging-sockets\\src\\upt\\pcbe\\messaging\\test\\self_message.data";
    
    public static void main(String[] args) {
        try {
            Thread.sleep(1000);
            clientId = Integer.valueOf(args[0]);
            prepareData();

            InetAddress ip = InetAddress.getByName("localhost");
            final Socket socket = new Socket(ip, port);

            DataInputStream in = new DataInputStream(socket.getInputStream());
            DataOutputStream out = new DataOutputStream(socket.getOutputStream());

            Thread t = new Thread() { // reading thread
                public void run() {
                    String line;
                    while (true) {
                        try {
                            line = send();

                            if (line == null || line.equals("exit")) {
                                // socket.close();
                                break;
                            } else if (line.startsWith("message:")) {
                                out.writeUTF(line);
                                out.flush();
                            }
                        } catch (Throwable t) {
                            System.out.println("Error in read thread");
                            t.printStackTrace();
                        }
                    }
                }
            };
            t.setName("Reading Thread Client " + clientId);
            t.start();

            while (true) {
                String received = in.readUTF();
                receive(received);
                System.out.println("received "+received);
                if (received.equals("last")) {
                    System.out.println("Successfully self sent " + selfSendMsg + " messages.");
                    System.out.println("Not self received messages size:" + selfMsgList.size());
                    System.out.println("Received " + messagesReceived + " messages from others.");

                    socket.close();
                    break;
                }
            }
        } catch (Throwable e) {
            System.out.println("Client test error");
            e.printStackTrace();
        }
    }

    private static void prepareData() {
        try {
            br = new BufferedReader(new FileReader(new File(FILE_PATH)));
        } catch (FileNotFoundException e) {
            System.out.println("File not found");
        }
    }

    private static void receive(String line) {
        if (line.equals("Hi new Client")) return;
        boolean success = selfMsgList.remove(line);
        if (success) {
            selfSendMsg.incrementAndGet();
        } else {
            messagesReceived.incrementAndGet();           
            System.out.println("Not self sent message:" + line);
        }
    }

    private static String send() {
        String line;
        try {
            line = br.readLine();
            if (line != null && line.startsWith("message")) {
                Message m = Message.contructMessage(line);
                m.setReceiver(ClientsTest.CLIENT_TEST);
                if (m.getReceiver() == clientId) {
                    selfMsgList.add(m.getMessage());
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
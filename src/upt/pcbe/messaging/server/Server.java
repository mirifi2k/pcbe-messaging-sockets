package upt.pcbe.messaging.server;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;

import upt.pcbe.messaging.shared.Message;
import upt.pcbe.messaging.shared.TopicMessage;

public class Server {
    private static final int port = 1338;
    private static int counter = 0;

    private static HashMap<Integer, DataOutputStream> clients = new HashMap<Integer, DataOutputStream>();
    public static ConcurrentLinkedQueue<Message> queue = new ConcurrentLinkedQueue<Message>();
    public static HashMap<String, List<TopicMessage>> topics = new HashMap<>();
    public static final int maxMessageDuration = 60000;

    public static void main(String[] args) {
        ServerSocket serverSocket = null;
        Socket socket = null;

        try {
            serverSocket = new ServerSocket(port);
            new MessageConsumer().start();
            while (true) {
                try {
                    socket = serverSocket.accept(); // accept client on the server

                    DataOutputStream out = new DataOutputStream(socket.getOutputStream());
                    clients.put(counter++, out);
                    out.writeUTF("Hi new Client");
                    out.flush();
                    System.out.println("New client connected. Id:" + (counter - 1));
                    new ClientThread(socket).start();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        // serverSocket.close();
    }

    public static synchronized HashMap<Integer, DataOutputStream> getClients() {
        return clients;
    }
}

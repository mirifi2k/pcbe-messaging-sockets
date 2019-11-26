package upt.pcbe.messaging.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

import upt.pcbe.messaging.shared.Message;
import upt.pcbe.messaging.shared.TopicMessage;

public class Server {
    private static final int port = 1338;
    private static int counter = 0;

    private static HashMap<Integer, Socket> clients = new HashMap<>();
    public static ConcurrentLinkedQueue<Message> queue = new ConcurrentLinkedQueue<Message>();
    public static ConcurrentHashMap<String, List<TopicMessage>> topics = new ConcurrentHashMap<>();
    public static final int maxMessageDuration = 60000;
    
    public static boolean isReceiverConnected(int receiver) {
    	if (!clients.containsKey(receiver)) {
    		return false;
    	}
    	
    	return !clients.get(receiver).isClosed() && clients.get(receiver).isConnected();
    }
    
    public static void removeClient(int id) {
    	System.out.println("[debug] client ID " + id + " left.");
    	clients.remove(id);
    }

    public static void main(String[] args) {
        ServerSocket serverSocket = null;
        Socket socket = null;

        try {
            serverSocket = new ServerSocket(port);
            new MessageConsumer().start();
            
            while (true) {
                try {
                	// accept client on the server
                    socket = serverSocket.accept();
                    clients.put(++counter, socket);
                    
                    System.out.println("[debug] new client connected (id: " + counter + ") " + socket);
                    new ClientThread(socket, counter).start();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        // serverSocket.close();
    }

    public static synchronized HashMap<Integer, Socket> getClients() {
        return clients;
    }
}

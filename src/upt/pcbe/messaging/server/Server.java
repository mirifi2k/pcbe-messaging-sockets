package upt.pcbe.messaging.server;

import java.net.*;
import java.io.*;

public class Server {
	private static final int port = 1338;
	
	public static void main(String[] args) {
		ServerSocket serverSocket = null;
		Socket socket = null;
		
		try {
			serverSocket = new ServerSocket(port);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		while (true) {
			try {
				System.out.println("New client connected");
				socket = serverSocket.accept(); // accept client on the server
				new ClientThread(socket).start();
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			
		}
		
		// serverSocket.close();
	}
}

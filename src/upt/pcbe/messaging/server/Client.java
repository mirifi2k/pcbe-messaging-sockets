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
		Socket socket = null;
		try {
			InetAddress ip = InetAddress.getByName("localhost");
			socket = new Socket(ip, port);
			
			DataInputStream in = new DataInputStream(socket.getInputStream()); 
            DataOutputStream out = new DataOutputStream(socket.getOutputStream());
            
            BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
            String line;
            
            while (true) {
            	line = br.readLine();
            	out.writeUTF(line);
            	
            	if (line.equals("exit")) {
            		socket.close();
            		break;
            	}
            	
            	String received = in.readUTF();
            	System.out.println("Server sent " + received);
            }
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}

package upt.pcbe.messaging.client;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.Socket;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

                            if (line.startsWith("/")) {
                            	if (line.equals("/exit")) {
                            		socket.close();
                            		break;
                            	} else {
                            		if (line.startsWith("/message")) {
                            			Pattern p = Pattern.compile("^\\/message \\d+ .+$");
                            			Matcher m = p.matcher(line);
                            			
                            			if (!m.find()) {
                            				System.out.println("[syntax] Correct usage: /message <receiver id> <message>");
                            			} else {
                            				out.writeUTF(line);
                            				out.flush();
                            			}
                            		} else if (line.startsWith("/topic")) {
                            			Pattern p = Pattern.compile("^\\/topic [^ ]+( \\d+ .+)*$");
                            			Matcher m = p.matcher(line);
                            			
                            			if (!m.find()) {
                            				System.out.println("[syntax] Correct usage: /topic <topic name> {<delay> <message>}");
                            			} else {
                            				out.writeUTF(line);
                            				out.flush();
                            			}
                            		}
                            	}
                            }
                        } catch (Throwable t) {
                            t.printStackTrace();
                        }
                    }
                }
            }.start();

            while (true) {
            	try {
	                String received = in.readUTF();
	                System.out.println(received);
            	} catch (IOException e) {
            		in.close();
            		System.out.println("[server] Server closed the connection.");
            		break;
            	}
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

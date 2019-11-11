package upt.pcbe.messaging.server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import upt.pcbe.messaging.shared.Message;
import upt.pcbe.messaging.shared.TopicMessage;

public class ClientThread extends Thread {
    private Socket socket;
    private int id;

    public ClientThread(Socket socket, int id) {
        this.socket = socket;
        this.id = id;
    }

    @Override
    public void run() {
        // DataOutputStream out = null;
        DataInputStream in = null;
        DataOutputStream out = null;

        try {
            in = new DataInputStream(socket.getInputStream());
            out = new DataOutputStream(socket.getOutputStream());
        } catch (IOException e) {
            return;
        }

        String line;
        while (true) {
            try {
            	// get the message from the client
                line = in.readUTF();

                if (line == null) {
                	break;
                } else if (line.startsWith("/")) {
                	if (line.equals("/exit")) {
                		socket.close();
                        break;
                	} else if (line.startsWith("/message")) {
                		String[] commandArgs = line.split(" ", 3);
                    	
                    	int receiver = Integer.valueOf(commandArgs[1]);
                    	String message = commandArgs[2];
                    	
                    	if (!Server.isReceiverConnected(receiver)) {
                    		out.writeUTF("[error] message could not be sent because the specified client is not connected to the server!");
                    	} else {
    	                    Message msg = Message.contructMessage(message, this.id, receiver);
    	                    Server.queue.add(msg);
                    	}
                	} else if (line.startsWith("/topic")) {
                		String[] commandArgs = line.split(" ", 4);
                		String mainTopic = commandArgs[1];
                		
                		// use case of topic reading
                		if (commandArgs.length == 2) { 
                			List<TopicMessage> messages = Server.topics.get(mainTopic);
                			
                			if (null == messages) {
                				out.writeUTF("[error] given topic is not created yet.");
                				out.writeUTF("[error] you can create the topic using the following syntax: /topic <topic name> <expiration delay> <message>");
                			} else {
                				StringBuilder sb = new StringBuilder();
                				
	                			messages = messages
	                					.stream()
	                					.filter(m -> !m.isTopicExpired())
	                					.collect(Collectors.toList());
	                			
	                			// replace Server global list with new list (without expired topics)
	                			Server.topics.put(mainTopic, messages);
	                			
	                			sb.append("Following messages were found in the '" + mainTopic + "' topic:");
	                			for (TopicMessage message : messages) {
	                				sb.append("\n\t > " + message.getMessage() + " (posted on: " + message.getCreationTime().toString() + ")");
	                			}
	                			
	                			out.writeUTF(sb.toString());
                			}
                			
                			out.flush(); //inside or outside for loop NOT SURE
                		}
                		// use case of topic posting
                		else if (commandArgs.length == 4) {
                			int delay = Integer.valueOf(commandArgs[2]);
                			String message = commandArgs[3];
                			
                			TopicMessage topic = TopicMessage.constructTopic(mainTopic, delay, message);
                			List<TopicMessage> messages;
                		
                			if (Server.topics.containsKey(topic.getType())) {
                				messages = Server.topics.get(topic.getType());
                			} else {
                				messages = new ArrayList<>();
                      		}
                			
                			Server.topics.put(topic.getType(), messages);
            				messages.add(topic);
            				
            				out.writeUTF("[success] Your message has been successfully saved to the '" + mainTopic + "' topic!");
                		}
                    }
                }
            } catch (IOException e) {
            	// client left the server
            	Server.removeClient(this.id);
                break;
            }
        }
        try {
            socket.close();
            in.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

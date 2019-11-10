package upt.pcbe.messaging.server;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import upt.pcbe.messaging.shared.Message;
import upt.pcbe.messaging.shared.TopicMessage;

public class ClientThread extends Thread {
    private Socket socket;

    public ClientThread(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        DataOutputStream out = null;
        DataInputStream in = null;
        BufferedReader br = null;

        try {
            br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            in = new DataInputStream(socket.getInputStream());
            out = new DataOutputStream(socket.getOutputStream());
        } catch (IOException e) {
            return;
        }

        String line;
        while (true) {
            try {
                line = in.readUTF(); // get the message from the client

                if (line == null || line.equals("exit")) {
                    socket.close();
                    break;
                } else if (line.startsWith("message:")) {
                    Message msg = Message.contructMessage(line);
                    Server.queue.add(msg);
                } else if (line.startsWith("topic:")){
                    String mainTopic = line.split(":")[1];
                    if(line.split(":").length == 4) {
                        TopicMessage topic = TopicMessage.constructTopic(line);

                        if (Server.topics.containsKey(topic.getType())) {
                            List<TopicMessage> messages = Server.topics.get(topic.getType());
                            messages.add(topic);
                            Server.topics.put(topic.getType(), messages);
                        }
                        else {
                            List<TopicMessage> messages = new ArrayList<>();
                            messages.add(topic);
                            Server.topics.put(topic.getType(), messages);
                        }
                    }
                    else if(line.split(":").length == 2){
                        List<TopicMessage> messages = Server.topics.get(mainTopic);
                        messages = messages.stream().filter(m -> !m.isExpired()).collect(Collectors.toList());
                        Server.topics.put(mainTopic, messages);
                        for (TopicMessage message: messages) {
                            out.writeUTF(message.getMessage());
                            out.flush(); //inside or outside for loop NOT SURE
                        }
                        //out.flush();
                    }
                }

                // System.out.println("From client: " + line);
                // out.writeUTF(line);
                // out.flush();
            } catch (IOException e) {
                return;
            }
        }
        try {
            socket.close();
            in.close();
            // out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

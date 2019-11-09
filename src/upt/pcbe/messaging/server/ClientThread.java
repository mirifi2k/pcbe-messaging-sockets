package upt.pcbe.messaging.server;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

import upt.pcbe.messaging.shared.Message;

public class ClientThread extends Thread {
    private Socket socket;

    public ClientThread(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        // DataOutputStream out = null;
        DataInputStream in = null;
        BufferedReader br = null;

        try {
            br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            in = new DataInputStream(socket.getInputStream());
            // out = new DataOutputStream(socket.getOutputStream());
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

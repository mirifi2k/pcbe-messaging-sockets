package upt.pcbe.messaging.server;

import java.io.DataOutputStream;
import java.io.IOException;

import upt.pcbe.messaging.shared.Message;

public class MessageConsumer extends Thread {

    @Override
    public void run() {
        System.out.println("Message consummer start");
        while (true) {
            try {
                Message msg = Server.queue.poll();
                if (msg != null) {
                    System.out.println("Consumming " + msg.toString());
                    consumeMessage(msg);
                }
//                Thread.sleep(1000);
            } catch (Throwable e) {
                System.out.println("Could not sleep");
                e.printStackTrace();
            }
        }
    }

    private void consumeMessage(Message msg) throws InterruptedException {
        try {
            DataOutputStream receiver = Server.clients.get(msg.getReceiver());
            if (receiver != null) {
                receiver.writeUTF(msg.getMessage());
                receiver.flush();
            } else {
                System.out.println("Client " + msg.getReceiver() + " not found. Sleep 1s");
                Thread.sleep(1000);
                receiver = Server.clients.get(msg.getReceiver());
                if (receiver != null) {
                    receiver.writeUTF(msg.getMessage());
                    receiver.flush();
                } else
                    System.out.println("Client not found." + msg.toString());
            }
        } catch (IOException e) {
            System.out.println("Could not sent message");
            e.printStackTrace();
        }
    }
}

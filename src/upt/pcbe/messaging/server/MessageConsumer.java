package upt.pcbe.messaging.server;

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
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private void consumeMessage(Message msg) {
        try {
            Server.getClients().get(msg.getReceiver()).writeUTF(msg.getMessage());
            Server.getClients().get(msg.getReceiver()).flush();
        } catch (IOException e) {
            System.out.println("Could not sent message");
            e.printStackTrace();
        }
    }
}

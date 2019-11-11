package upt.pcbe.messaging.server;

import java.io.DataOutputStream;
import java.io.IOException;

import upt.pcbe.messaging.shared.Message;

public class MessageConsumer extends Thread {

    @Override
    public void run() {
        System.out.println("[debug] message consumer thread started.");
        
        while (true) {
            try {
                Message msg = Server.queue.poll();
                
                if (msg != null) {
                    System.out.println("[debug] consuming " + msg.toString());
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
        	int receiver = msg.getReceiver();
        	
        	// safe to use here, receiver check if alive was done in Server class
        	DataOutputStream out = new DataOutputStream(Server.getClients().get(receiver).getOutputStream());
        	
        	out.writeUTF(msg.returnMessageFormat());
        	out.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

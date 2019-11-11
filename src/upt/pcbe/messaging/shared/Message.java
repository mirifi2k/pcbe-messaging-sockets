package upt.pcbe.messaging.shared;

public class Message {

    private String message;
    private int receiver;
    private int sender;

    public Message(String message, int sender, int receiver) {
        this.message = message;
        this.receiver = receiver;
        this.sender = sender;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getReceiver() {
        return receiver;
    }
    
    public int getSender() {
    	return this.sender;
    }

    public void setReceiver(int receiver) {
        this.receiver = receiver;
    }

    public String toString() {
        return "message:" + String.valueOf(receiver) + ":" + message;
    }
    
    public String returnMessageFormat() {
    	return "You've received a new message from client ID " + sender + ": " + message;
    }

    public static Message contructMessage(String message, int sender, int receiver) {
        try {
        	return new Message(message, sender, receiver);
        } catch (Throwable t) {
            System.err.println("[debug] probably incorrect syntax is used!");
            t.printStackTrace();
        }
        
        return null;
    }
}
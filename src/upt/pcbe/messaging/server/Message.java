package upt.pcbe.messaging.server;

public class Message {

    private String message;
    private int receiver;

    public Message(String message, int receiver) {
        this.message = message;
        this.receiver = receiver;
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

    public void setReceiver(int receiver) {
        this.receiver = receiver;
    }

    public String toString() {
        return "message:" + String.valueOf(receiver) + ":" + message;
    }

    public static Message contructMessage(String line) {
        try {
            // do not use : in message, it will get trimmed
            int receiver = Integer.valueOf(line.split(":")[1]);
            String message = line.split(":")[2];
            return new Message(message, receiver);
        } catch (Throwable t) {
            System.out.println("Message Error. Syntax->message:<number>:<message>");
            return null;
        }
    }
}
package upt.pcbe.messaging.shared;

import java.time.LocalTime;
import java.util.Date;

public class TopicMessage {

    private String message, type;
    private int delay;
    private Date time;

    public TopicMessage(String type, int delay, String message){
        this.type = type;
        this.delay = delay;
        this.message = message;
        this.time = new Date();
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getDelay() {
        return delay;
    }

    public void setDelay(int delay) {
        this.delay = delay;
    }

    public Date getTime(){
        return time;
    }

    public static TopicMessage constructTopic(String line){
        try {
            // do not use : in message, it will get trimmed
            String type = line.split(":")[1];
            int delay = Integer.valueOf(line.split(":")[2]);
            String message = line.split(":")[3];

            return new TopicMessage(type, delay, message);
        } catch (Throwable t) {
            System.out.println("Message Error. Syntax->message:<type>:<delay>:<message>");
            return null;
        }
    }

    public boolean isExpired(){
        long startTime = this.time.getTime();
        long nowTime = new Date().getTime();
        long timePassed = nowTime - startTime;

        if(delay > timePassed)
            return true;
        return false;
    }
}

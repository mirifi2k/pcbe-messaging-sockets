package upt.pcbe.messaging.shared;

import upt.pcbe.messaging.server.Server;

import java.util.Date;

public class TopicMessage {

    private String message, type;
    private int delay;
    private Date creationTime;

    public TopicMessage(String type, int delay, String message) {
        this.type = type;
        this.delay = delay;
        this.message = message;
        this.creationTime = new Date();
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

    public Date getCreationTime() {
        return creationTime;
    }

    public static TopicMessage constructTopic(String type, int delay, String message) {
        try {
        	return new TopicMessage(type, delay, message);
        } catch (Throwable t) {
            System.out.println("[debug] possible syntax error occured!");
        }
        return null;
    }

    public boolean isTopicExpired() {
        long time = new Date().getTime() - this.creationTime.getTime();
        return (delay < time || Server.maxMessageDuration < time);
    }
}
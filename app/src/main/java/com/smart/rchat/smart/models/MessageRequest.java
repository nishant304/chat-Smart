package com.smart.rchat.smart.models;

/**
 * Created by nishant on 05.02.17.
 */

public class MessageRequest {

    private String to;

    private String from;

    private String message;

    private int type;

    public MessageRequest(String to,String from,String message,int type){
        this.to = to;
        this.from = from;
        this.message = message;
        this.type = type;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

}

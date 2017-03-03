package com.smart.rchat.smart.models;

import com.google.firebase.database.Exclude;
import com.google.firebase.database.ServerValue;

import java.util.HashMap;

/**
 * Created by nishant on 05.02.17.
 */

public class MessageRequest {

    private String to;

    private String from;

    private String message;

    private int type;

    private HashMap<String, Object> dateCreated;

    public MessageRequest(String to,String from,String message,int type){
        this.to = to;
        this.from = from;
        this.message = message;
        this.type = type;
        this.dateCreated = new HashMap<>();
        dateCreated.put("date", ServerValue.TIMESTAMP);
    }

    public MessageRequest(){

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

    public HashMap<String, Object> getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(HashMap<String, Object> dateCreated) {
        this.dateCreated = dateCreated;
    }


}

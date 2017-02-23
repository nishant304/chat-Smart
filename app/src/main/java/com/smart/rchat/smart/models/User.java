package com.smart.rchat.smart.models;

import java.util.ArrayList;

/**
 * Created by nishant on 14.02.17.
 */

public class User {

    private String userId;

    private String profilePic;

    private String phone;

    private String name;

    private String status;

    private String typingTo;

    private ArrayList<String> groups = new ArrayList<>();

    public User(String userId, String profileUrl, String phone, String name) {
        this.phone = phone;
        this.userId = userId;
        this.profilePic = profileUrl;
        this.name = name;
    }

    public User(){

    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getProfilePic() {
        return profilePic;
    }

    public void setProfilePic(String profilePic) {
        this.profilePic = profilePic;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ArrayList<String> getGroups() {
        return groups;
    }

    public void setGroups(ArrayList<String> groups) {
        this.groups = groups;
    }

    public String getTypingTo() {
        return typingTo;
    }

    public void setTypingTo(String typingTo) {
        this.typingTo = typingTo;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

}

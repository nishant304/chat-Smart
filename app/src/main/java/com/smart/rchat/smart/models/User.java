package com.smart.rchat.smart.models;

/**
 * Created by nishant on 14.02.17.
 */

public class User {

    private String userId;

    private String profileUrl;

    private String phone;

    private String name;

    public User(String userId, String profileUrl, String phone, String status) {
        this.phone = phone;
        this.userId = userId;
        this.profileUrl = profileUrl;
        this.name = status;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getProfileUrl() {
        return profileUrl;
    }

    public void setProfileUrl(String profileUrl) {
        this.profileUrl = profileUrl;
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


}

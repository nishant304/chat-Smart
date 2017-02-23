package com.smart.rchat.smart.models;

import java.util.ArrayList;

/**
 * Created by nishant on 21.02.17.
 */

public class Group {

    private ArrayList<String> members;

    private String url;

    private String name;

    public ArrayList<String> getMembers() {
        return members;
    }

    public void setMembers(ArrayList<String> members) {
        this.members = members;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

}

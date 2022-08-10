package com.sinamekidev.socialapp.models;

import java.util.ArrayList;

public class User {
    private UserInfo userInfo;
    private ArrayList<String> user_postList = new ArrayList<>();
    private ArrayList<String> friends_list = new ArrayList<>();
    private ArrayList<String> friend_request_list = new ArrayList<>();
    public User(UserInfo userInfo) {
        this.userInfo = userInfo;
    }
    public UserInfo getUserInfo(){
        return  this.userInfo;
    }
    public ArrayList<String> getUser_postList() {
        return this.user_postList;
    }
    public ArrayList<String> getFriends_list() {
        return this.friends_list;
    }
    public ArrayList<String> getFriend_request_list(){ return this.friend_request_list;}

    public User(){

    }
}

package com.sinamekidev.socialapp.models;

import java.util.ArrayList;

public class MessageParent {
    private ArrayList<MessageChild> message_List;
    private ArrayList<String> user_list = new ArrayList<>();

    public MessageParent() {
    }

    public MessageParent(String user1,String user2) {
        message_List = new ArrayList<MessageChild>();
        user_list.add(user1);
        user_list.add(user2);
    }

    public ArrayList<MessageChild> getMessage_List() {
        return message_List;
    }

    public void setMessage_List(ArrayList<MessageChild> message_List) {
        this.message_List = message_List;
    }

    public ArrayList<String> getUser_list() {
        return user_list;
    }

    public void setUser_list(ArrayList<String> user_list) {
        this.user_list = user_list;
    }
}

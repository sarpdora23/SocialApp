package com.sinamekidev.socialapp.models;

import com.google.firebase.firestore.FieldValue;

import java.util.ArrayList;

public class Post {
    private String author;
    private String text;
    private ArrayList<Comment> commentList = new ArrayList<>();
    private String post_uid;
    private ArrayList<String> likeList = new ArrayList<>();

    public Post(String author, String text,String post_uid) {
        this.author = author;
        this.text = text;
        this.post_uid = post_uid;
    }
    public Post(){

    }

    public ArrayList<String> getLikeList() {
        return this.likeList;
    }

    public void setLikeList(ArrayList<String> likeList) {
        this.likeList = likeList;
    }

    public ArrayList<Comment> getCommentList() {
        return this.commentList;
    }

    public void setCommentList(ArrayList<Comment> commentList) {
        this.commentList = commentList;
    }

    public String getAuthor() {
        return this.author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getText() {
        return this.text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getPost_uid() {
        return post_uid;
    }

    public void setPost_uid(String post_uid) {
        this.post_uid = post_uid;
    }
}

package com.sinamekidev.socialapp.models;

public class MessageChild {
    private String author;
    private String message;

    public MessageChild() {
    }

    public MessageChild(String author, String message) {
        this.author = author;
        this.message = message;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}

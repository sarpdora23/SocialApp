package com.sinamekidev.socialapp.models;

public class Comment {
    private String person_uid;
    private String comment;

    public Comment(){

    }
    public Comment(String person_uid,String comment){
        this.person_uid = person_uid;
        this.comment = comment;
    }
    public String getPerson_uid(){
        return this.person_uid;
    }
    public String getComment(){
        return this.comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public void setPerson_uid(String person_uid) {
        this.person_uid = person_uid;
    }
}

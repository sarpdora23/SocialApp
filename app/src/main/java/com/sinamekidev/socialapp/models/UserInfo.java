package com.sinamekidev.socialapp.models;

public class UserInfo {
    private String username;
    private String email;
    private String profile_url;
    private String password;

    public UserInfo(String username, String email, String profile_url, String password) {
        this.username = username;
        this.email = email;
        this.profile_url = profile_url;
        this.password = password;
    }
    public UserInfo(){

    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setProfile_url(String profile_url) {
        this.profile_url = profile_url;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getUsername() {
        return this.username;
    }

    public String getEmail() {
        return this.email;
    }

    public String getProfile_url() {
        return this.profile_url;
    }

    public String getPassword() {
        return this.password;
    }
}

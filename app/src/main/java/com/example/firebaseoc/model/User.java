package com.example.firebaseoc.model;

import androidx.annotation.Nullable;

/**
 * a class that will represent our user data with this following attribute:
 * uid,username,isMentor,urlPicture.
 */
public class User {
    private String uid;
    private String username;
    private Boolean isMentor;
    @Nullable private String urlPicture;

    public User() {
    }

    public User(String uid, String username, Boolean isMentor, @Nullable String urlPicture) {
        this.uid = uid;
        this.username = username;
        this.isMentor = isMentor;
        this.urlPicture = urlPicture;
    }

    public User(String uid, String username, @Nullable String urlPicture) {
        this.uid = uid;
        this.username = username;
        this.urlPicture = urlPicture;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Boolean getIsMentor() {
        return isMentor;
    }

    public void setIsMentor(Boolean mentor) {
        isMentor = mentor;
    }

    @Nullable
    public String getUrlPicture() {
        return urlPicture;
    }

    public void setUrlPicture(@Nullable String urlPicture) {
        this.urlPicture = urlPicture;
    }
}

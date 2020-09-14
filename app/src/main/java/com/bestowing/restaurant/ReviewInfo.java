package com.bestowing.restaurant;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;

public class ReviewInfo implements Serializable {
    private String title;
    private String userComment;
    private ArrayList<String> photos;
    private UserInfo userInfo;
    private String writer;
    private Date createdAt;
    private String id;

    public ReviewInfo(String title, String userComment, ArrayList<String> photos, UserInfo userInfo, String writer, Date createdAt, String id) {
        this.title = title;
        this.userComment = userComment;
        this.photos = photos;
        this.userInfo = userInfo;
        this.writer = writer;
        this.createdAt = createdAt;
        this.id = id;
    }

    public ReviewInfo(String title, String userComment, ArrayList<String> photos, String writer, Date createdAt, String id) {
        this.title = title;
        this.userComment = userComment;
        this.photos = photos;
        this.writer = writer;
        this.createdAt = createdAt;
        this.id = id;
    }

    public ReviewInfo(String title, String userComment, ArrayList<String> photos, String writer, Date createdAt) {
        this.title = title;
        this.userComment = userComment;
        this.photos = photos;
        this.writer = writer;
        this.createdAt = createdAt;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setUserInfo(UserInfo userInfo) { this.userInfo = userInfo; }

    public UserInfo getUserInfo() {
        return userInfo;
    }

    public String getUserComment() {
        return userComment;
    }

    public void setUserComment(String userComment) {
        this.userComment = userComment;
    }

    public ArrayList<String> getPhotos() {
        return photos;
    }

    public void setPhotos(ArrayList<String> photos) {
        this.photos = photos;
    }

    public String getWriter() {
        return writer;
    }

    public void setWriter(String writer) {
        this.writer = writer;
    }

    public Date getCreatedAt() {
        return this.createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public String getId() {
        return this.id;
    }
}

package com.bestowing.restaurant;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class ReviewInfo implements Serializable {
    private String userComment;
    private ArrayList<String> photos;
    private String writer;
    private Date createdAt;
    private String id;

    public ReviewInfo(String userComment, ArrayList<String> photos, String writer, Date createdAt) {
        this.userComment = userComment;
        this.photos = photos;
        this.writer = writer;
        this.createdAt = createdAt;
    }

    public ReviewInfo(String userComment, ArrayList<String> photos, String writer, Date createdAt, String id) {
        this.userComment = userComment;
        this.photos = photos;
        this.writer = writer;
        this.createdAt = createdAt;
        this.id = id;
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
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}

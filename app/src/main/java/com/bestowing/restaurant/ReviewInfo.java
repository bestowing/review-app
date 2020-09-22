package com.bestowing.restaurant;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class ReviewInfo implements Serializable {
    private String title;
    private String userComment;
    private ArrayList<String> photos;
    private UserInfo userInfo;
    private String writer;
    private Date createdAt;
    private String id;
    private Map<String, Boolean> like;
    private Long likeNum;
    private ArrayList<String> tags;
    private double rating;
    private String restaurantId;

    // 생성자
    public ReviewInfo(String title, String userComment, ArrayList<String> photos, UserInfo userInfo, String writer, Date createdAt, String id, Map<String, Boolean> like, Long likeNum) {
        this.title = title;
        this.userComment = userComment;
        this.photos = photos;
        this.userInfo = userInfo;
        this.writer = writer;
        this.createdAt = createdAt;
        this.id = id;
        this.like = like;
        this.likeNum = likeNum;
    }

    public ReviewInfo(String title, String userComment, ArrayList<String> photos, UserInfo userInfo, String writer, Date createdAt, String id, Map<String, Boolean> like, Long likeNum, ArrayList<String> tags, double rating, String restaurantId) {
        this.title = title;
        this.userComment = userComment;
        this.photos = photos;
        this.userInfo = userInfo;
        this.writer = writer;
        this.createdAt = createdAt;
        this.id = id;
        this.like = like;
        this.likeNum = likeNum;
        this.tags = tags;
        this.rating = rating;
        this.restaurantId = restaurantId;
    }

    // getter, setter
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
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

    public UserInfo getUserInfo() {
        return userInfo;
    }

    public void setUserInfo(UserInfo userInfo) {
        this.userInfo = userInfo;
    }

    public String getWriter() {
        return writer;
    }

    public void setWriter(String writer) {
        this.writer = writer;
    }

    public Date getCreatedAt() {
        return createdAt;
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

    public Map<String, Boolean> getLike() {
        return like;
    }

    public void setLike(Map<String, Boolean> like) {
        this.like = like;
    }

    public Long getLikeNum() {
        return likeNum;
    }

    public void setLikeNum(Long likeNum) {
        this.likeNum = likeNum;
    }

    public ArrayList<String> getTags() {
        return tags;
    }

    public void setTags(ArrayList<String> tags) {
        this.tags = tags;
    }

    public double getRating() {
        return rating;
    }

    public void setRating(double rating) {
        this.rating = rating;
    }

    public String getRestaurantId() {
        return restaurantId;
    }

    public void setRestaurantId(String restaurantId) {
        this.restaurantId = restaurantId;
    }

    // like 기능
    public void like(boolean is_like) {
        if (is_like)
            this.likeNum++;
        else
            this.likeNum--;
    }

    public void addLike(String myId, boolean is_like) {
        if (is_like) {
            if (this.like == null) {
                this.like = new HashMap<>();
            }
            this.like.put(myId, true);
        }
        else
            this.like.remove(myId);
    }
}

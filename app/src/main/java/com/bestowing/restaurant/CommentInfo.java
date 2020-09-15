package com.bestowing.restaurant;

import java.io.Serializable;
import java.util.Date;

public class CommentInfo implements Serializable {
    private String content;
    private UserInfo userInfo;
    private String writer;
    private Date createdAt;
    // private CommentInfo subcomments; // 이 댓글에 달린 댓글들
    private String id;

    public CommentInfo(String content, UserInfo userInfo, String writer, Date createdAt, String id) {
        this.content = content;
        this.userInfo = userInfo;
        this.writer = writer;
        this.createdAt = createdAt;
        this.id = id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
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
}

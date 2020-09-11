package com.bestowing.restaurant;

public class UserInfo {
    private String nickName;
    private String photoUrl;

    public UserInfo(String nickName, String photoUrl) {
        this.nickName = nickName;
        this.photoUrl = photoUrl;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }
}

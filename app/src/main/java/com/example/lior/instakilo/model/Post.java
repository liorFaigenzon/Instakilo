package com.example.lior.instakilo.model;

/**
 * Created by eliav.menachi on 25/03/2015.
 */
public class Post {
    String id;
    String userId;
    String photoId;
    String title;
    String content;
    int likeCounter;
    String lastUpdated;
    boolean checked;

    public String getLastUpdated() {
        return lastUpdated;
    }
    public void setLastUpdated(String lastUpdated) {
        this.lastUpdated = lastUpdated;
    }



    public Post(){

    }
    public Post(String id,String photoId,  String userId, String title, String content, int likeCounter,  boolean checked) {
        this.id = id;
        this.photoId = photoId;
        this.userId = userId;
        this.title = title;
        this.content = content;
        this.likeCounter = likeCounter;
        this.checked = checked;
    }

    public String getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getContent() {
        return content;
    }

    public int getLikeCounter() {
        return likeCounter;
    }

    public String getPhotoId() {
        return photoId;
    }

    public String getUserId() {
        return userId;
    }

    public boolean isChecked() {
        return checked;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void setLikeCounter(int likeCounter) {
        this.likeCounter = likeCounter;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public void setPhotoId(String photoId) {
        this.photoId = photoId;
    }

    public void setChecked(boolean checked) {
        this.checked = checked;
    }
}

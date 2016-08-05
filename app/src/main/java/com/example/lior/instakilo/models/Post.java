package com.example.lior.instakilo.models;

public class Post {
    String id;
    String authorId;
    String photoId; // TODO: For now the photoId is only content in words
    int likeCounter;
    String lastUpdated;

    public Post() {}

    public Post(String id,String photoId,  String userId, int likeCounter) {
        this.id = id;
        this.photoId = photoId;
        this.authorId = userId;
        this.likeCounter = likeCounter;
    }

    public String getId() {
        return id;
    }

    public int getLikeCounter() {
        return likeCounter;
    }

    public String getPhotoId() {
        return photoId;
    }

    public String getAuthorId() {
        return authorId;
    }

    public String getLastUpdated() {
        return lastUpdated;
    }

    public void setLikeCounter(int likeCounter) {
        this.likeCounter = likeCounter;
    }

    public void setAuthorId(String authorId) {
        this.authorId = authorId;
    }

    public void setPhotoId(String photoId) {
        this.photoId = photoId;
    }

    public void setLastUpdated(String lastUpdated) {
        this.lastUpdated = lastUpdated;
    }
}

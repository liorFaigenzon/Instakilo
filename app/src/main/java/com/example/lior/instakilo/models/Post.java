package com.example.lior.instakilo.models;

import com.google.firebase.database.Exclude;
import com.google.firebase.database.IgnoreExtraProperties;

import java.util.HashMap;
import java.util.Map;

@IgnoreExtraProperties
public class Post {
    private String id;
    private String authorId;
    private String authorName;
    private String photoId;
    private int likeCounter;
    private String lastUpdated;
    private Map<String, Boolean> likeUsers;

    public Post() {}

    public Post(String authorId, String authorName, String photoId) {
        this.authorId = authorId;
        this.authorName = authorName;
        this.photoId = photoId;
        this.likeCounter = 0;
        this.likeUsers = new HashMap<>();
    }

    public Post(String authorId, String authorName, String photoId, int likeCounter, Map<String, Boolean> likeUsers) {
        this.authorId = authorId;
        this.authorName = authorName;
        this.photoId = photoId;
        this.likeCounter = likeCounter;
        this.likeUsers = likeUsers;
    }

    public Post(String id, String authorId, String authorName, String photoId, int likeCounter, Map<String, Boolean> likeUsers) {
        this.id = id;
        this.authorId = authorId;
        this.authorName = authorName;
        this.photoId = photoId;
        this.likeCounter = likeCounter;
        this.likeUsers = likeUsers;
    }

    public String getId() {
        return id;
    }

    public String getAuthorId() {
        return authorId;
    }

    public String getAuthorName() {
        return authorName;
    }

    public String getPhotoId() {
        return photoId;
    }

    public int getLikeCounter() {
        return likeCounter;
    }

    public String getLastUpdated() {
        return lastUpdated;
    }

    public Map<String, Boolean> getLikeUsers() {
        return likeUsers;
    }

    public void setAuthorId(String authorId) {
        this.authorId = authorId;
    }

    public void setAuthorName(String authorName) {
        this.authorName = authorName;
    }

    public void setPhotoId(String photoId) {
        this.photoId = photoId;
    }

    public void incLikeCounter() {
        this.likeCounter++;
    }

    public void setLastUpdated(String lastUpdated) {
        this.lastUpdated = lastUpdated;
    }

    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("authorId", authorId);
        result.put("authorName", authorName);
        result.put("photoId", photoId);
        result.put("likeCounter", likeCounter);
        result.put("lastUpdated", lastUpdated);
        result.put("likeUsers", likeUsers);

        return result;
    }
}

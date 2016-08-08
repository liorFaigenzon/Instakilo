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

    public Post(String authorId, String authorName, String photoId, int likeCounter) {
        this(authorId, authorName, photoId);
        this.likeCounter = likeCounter;
        this.likeUsers = new HashMap<>();
    }

    public Post(String authorId, String authorName, String photoId, int likeCounter, Map<String, Boolean> likeUsers) {
        this(authorId, authorName, photoId, likeCounter);
        this.likeUsers = likeUsers;
    }

    public Post(String id, String authorId, String authorName, String photoId, int likeCounter, Map<String, Boolean> likeUsers) {
        this(authorId, authorName, photoId, likeCounter, likeUsers);
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public String getAuthorId() {
        return authorId;
    }

    public void setAuthorId(String authorId) {
        this.authorId = authorId;
    }

    public String getAuthorName() {
        return authorName;
    }

    public void setAuthorName(String authorName) {
        this.authorName = authorName;
    }

    public String getPhotoId() {
        return photoId;
    }

    public void setPhotoId(String photoId) {
        this.photoId = photoId;
    }

    public int getLikeCounter() {
        return likeCounter;
    }

    public void incLikeCounter() {
        this.likeCounter++;
    }

    public void decLikeCounter() {
        this.likeCounter--;
    }

    public String getLastUpdated() {
        return lastUpdated;
    }

    public void setLastUpdated(String lastUpdated) {
        this.lastUpdated = lastUpdated;
    }

    public Map<String, Boolean> getLikeUsers() {
        return likeUsers;
    }

    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();

        if (authorId != null) {
            result.put("authorId", authorId);
        }

        if (authorName != null) {
            result.put("authorName", authorName);
        }

        if (photoId != null) {
            result.put("photoId", photoId);
        }

        result.put("likeCounter", likeCounter);

        if (lastUpdated != null) {
            result.put("lastUpdated", lastUpdated);
        }

        if (likeUsers != null || likeUsers.size() != 0) {
            result.put("likeUsers", likeUsers);
        }

        return result;
    }
}

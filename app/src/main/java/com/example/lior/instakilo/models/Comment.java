package com.example.lior.instakilo.models;

import com.google.firebase.database.Exclude;
import com.google.firebase.database.IgnoreExtraProperties;

import java.util.HashMap;
import java.util.Map;

@IgnoreExtraProperties
public class Comment {
    private String id;
    private String authorId;
    private String authorName;
    private String postId;
    private String content;
    private String lastUpdated;

    public Comment() {}

    public Comment(String authorId, String authorName, String postId, String content) {
        this.authorId = authorId;
        this.authorName = authorName;
        this.postId = postId;
        this.content = content;
    }

    public Comment(String id, String authorId, String authorName, String postId, String content) {
        this(authorId, authorName, postId, content);
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    public String getPostId() {
        return postId;
    }

    public void setPostId(String postId) {
        this.postId = postId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getLastUpdated() {
        return lastUpdated;
    }

    public void setLastUpdated(String lastUpdated) {
        this.lastUpdated = lastUpdated;
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

        if (postId != null) {
            result.put("postId", postId);
        }

        if (content != null) {
            result.put("content", content);
        }

        if (lastUpdated != null) {
            result.put("lastUpdated", lastUpdated);
        }

        return result;
    }
}

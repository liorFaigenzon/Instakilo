package com.example.lior.instakilo.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.firebase.database.Exclude;
import com.google.firebase.database.IgnoreExtraProperties;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;

@IgnoreExtraProperties
public class Post implements Parcelable {

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

    public Post(String id, String authorId, String authorName, String photoId, int likeCounter, String creationDate) {
        this(authorId, authorName, photoId);
        this.id = id;
        this.likeCounter = likeCounter;
        this.likeUsers = new HashMap<>();
        this.lastUpdated = creationDate;
    }

    public Post(String authorId, String authorName, String photoId, int likeCounter, Map<String, Boolean> likeUsers) {
        this(authorId, authorName, photoId, likeCounter);
        this.likeUsers = likeUsers;
    }

    public Post(String id, String authorId, String authorName, String photoId, int likeCounter, Map<String, Boolean> likeUsers) {
        this(authorId, authorName, photoId, likeCounter, likeUsers);
        this.id = id;
    }

    public Post(String id, String authorId, String authorName, String photoId, int likeCounter, Map<String, Boolean> likeUsers, String creationDate) {
        this(authorId, authorName, photoId, likeCounter, likeUsers);
        this.id = id;
        this.lastUpdated = creationDate;
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

    public String getPhotoId() {
        return photoId;
    }

    public void setPhotoId(String photoId) {
        this.photoId = photoId;
    }

    public int getLikeCounter() {
        return likeCounter;
    }

    public boolean toggleLikeCounter(String userId) {
        if (this.likeUsers.containsKey(userId)) {
            this.likeUsers.remove(userId);
            this.likeCounter--;

            return false;
        } else {
            this.likeUsers.put(userId, true);
            this.likeCounter++;

            return true;
        }
    }

    public boolean isUserLiked(String userId) {
        return this.likeUsers.containsKey(userId);
    }

    public String getLastUpdated() {
        return lastUpdated;
    }

    public void setLastUpdated() {
        // Create date object with now's date to save as the last update of the post
        SimpleDateFormat dateFormatGmt = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        dateFormatGmt.setTimeZone(TimeZone.getTimeZone("GMT"));
        String date = dateFormatGmt.format(new Date()).toString();

        this.lastUpdated = date;
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

        if (likeUsers != null/* || likeUsers.size() != 0*/) {
            result.put("likeUsers", likeUsers);
        }

        return result;
    }


      /* everything below here is for implementing Parcelable */

    // 99.9% of the time you can just ignore this
    @Override
    public int describeContents() {
        return 0;
    }

    // write your object's data to the passed-in Parcel
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeStringArray(new String[] {
                this.id,
                this.authorId,
                this.authorName,
                this.photoId,
                Integer.toString(this.likeCounter),
                this.lastUpdated
        });

        dest.writeInt(this.likeUsers.size());
        for (Map.Entry<String, Boolean> entry : this.likeUsers.entrySet()) {
            dest.writeString(entry.getKey());
            dest.writeString("true");
        }
    }

    // this is used to regenerate your object. All Parcelables must have a CREATOR that implements these two methods
    public static final Parcelable.Creator<Post> CREATOR = new Parcelable.Creator<Post>() {
        public Post createFromParcel(Parcel in) {
            return new Post(in);
        }

        public Post[] newArray(int size) {
            return new Post[size];
        }
    };

    // example constructor that takes a Parcel and gives you an object populated with it's values
    private Post(Parcel in) {
        String[] data = new String[6];

        in.readStringArray(data);
        this.id = data[0];
        this.authorId = data[1];
        this.authorName = data[2];
        this.photoId = data[3];
        this.likeCounter = Integer.parseInt(data[4]);
        this.lastUpdated = data[5];

        int size = in.readInt();
        this.likeUsers =  new HashMap<>();
        for(int i = 0; i < size; i++){
            String key = in.readString();
            boolean value = Boolean.valueOf(in.readString());
            this.likeUsers.put(key,value);
        }

    }
}

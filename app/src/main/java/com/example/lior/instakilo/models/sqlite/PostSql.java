package com.example.lior.instakilo.models.sqlite;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.NonNull;

import com.example.lior.instakilo.models.Post;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class PostSql {
    final static String POST_TABLE = "posts";
    final static String POST_TABLE_ID = "_id";
    final static String POST_TABLE_AUTHOR_ID = "authorId";
    final static String POST_TABLE_AUTHOR_NAME = "authorName";
    final static String POST_TABLE_LIKE_COUNTER = "likeCounter";
    final static String POST_TABLE_PHOTO_ID = "photo_id";
    final static String POST_TABLE_DATE = "creationDate";
    final static String POST_LIKE_TABLE = "posts_likes";
    final static String POST_LIKE_POST_ID = "post_id";
    final static String POST_LIKE_USER_ID = "user_id";

    public static void create(SQLiteDatabase db) {
        db.execSQL("create table " +
                POST_TABLE + " (" +
                POST_TABLE_ID + " TEXT PRIMARY KEY," +
                POST_TABLE_AUTHOR_ID + " TEXT," +
                POST_TABLE_AUTHOR_NAME + " TEXT," +
                POST_TABLE_LIKE_COUNTER + " TEXT," +
                POST_TABLE_DATE + " TEXT," +
                POST_TABLE_PHOTO_ID + " TEXT);");

        db.execSQL("create table " +
                POST_LIKE_TABLE + " (" +
                POST_LIKE_POST_ID + " TEXT PRIMARY KEY," +
                POST_LIKE_USER_ID + " TEXT PRIMARY KEY);");
    }

    public static void drop(SQLiteDatabase db) {
        db.execSQL("drop table " + POST_TABLE);
        db.execSQL("drop table " + POST_LIKE_TABLE);
    }

    public static List<Object> getAllPosts(SQLiteDatabase db) {
        Cursor cursor = db.query(POST_TABLE, null, null , null, null, null, null);
        List<Object> posts = new LinkedList<Object>();

        if (cursor.moveToFirst()) {
            int idIndex = cursor.getColumnIndex(POST_TABLE_ID);
            int authorIdIndex = cursor.getColumnIndex(POST_TABLE_AUTHOR_ID);
            int authorNameIndex = cursor.getColumnIndex(POST_TABLE_AUTHOR_NAME);
            int likeCounterIndex = cursor.getColumnIndex(POST_TABLE_LIKE_COUNTER);
            int creationDateIndex = cursor.getColumnIndex(POST_TABLE_DATE);
            int photoIdIndex = cursor.getColumnIndex(POST_TABLE_PHOTO_ID);
            do {
                String id = cursor.getString(idIndex);
                String authorId = cursor.getString(authorIdIndex);
                String authorName = cursor.getString(authorNameIndex);
                int likeCounter = cursor.getInt(likeCounterIndex);
                String creationDate = cursor.getString(creationDateIndex);
                String photoId = cursor.getString(photoIdIndex);
                Post pst = new Post(id, authorId, authorName, photoId, likeCounter, getPostLikes(db, id), creationDate);
                posts.add(pst);
            } while (cursor.moveToNext());
        }

        return posts;
    }

    private static Map<String, Boolean> getPostLikes(SQLiteDatabase db, String id) {
        String where = POST_LIKE_POST_ID + " = ?";
        String[] args = {id};

        Cursor cursor = db.query(POST_TABLE, null, where, args, null, null, null);
        Map<String, Boolean> likes = new HashMap<>();

        if (cursor.moveToFirst()) {
            int userIdIndex = cursor.getColumnIndex(POST_LIKE_USER_ID);

            do {
                String userId = cursor.getString(userIdIndex);
                likes.put(userId, null);
            } while (cursor.moveToNext());
        }

        return likes;
    }

    public static Object getPostById(SQLiteDatabase db, String id) {
        String where = POST_TABLE_ID + " = ?";
        String[] args = {id};
        Cursor cursor = db.query(POST_TABLE, null, where, args, null, null, null);
        if (cursor.moveToFirst()) {
            //int idIndex = cursor.getColumnIndex(POST_TABLE_ID);
            int authorIdIndex = cursor.getColumnIndex(POST_TABLE_AUTHOR_ID);
            int authorNameIndex = cursor.getColumnIndex(POST_TABLE_AUTHOR_NAME);
            int likeCounterIndex = cursor.getColumnIndex(POST_TABLE_LIKE_COUNTER);
            int creationDateIndex = cursor.getColumnIndex(POST_TABLE_DATE);
            int photoIdIndex = cursor.getColumnIndex(POST_TABLE_PHOTO_ID);
            String authorId = cursor.getString(authorIdIndex);
            String authorName = cursor.getString(authorNameIndex);
            int likeCounter = cursor.getInt(likeCounterIndex);
            String creationDate = cursor.getString(creationDateIndex);
            String photoId = cursor.getString(photoIdIndex);
            Post pst = new Post(id, authorId, authorName, photoId, likeCounter, creationDate);
            return pst;
        }
        return null;
    }

    public static void add(SQLiteDatabase db, Object model) {
        // Cast the model to post
        Post pst = (Post)model;

        ContentValues values = new ContentValues();
        values.put(POST_TABLE_ID, pst.getId());
        values.put(POST_TABLE_AUTHOR_ID, pst.getAuthorId());
        values.put(POST_TABLE_AUTHOR_NAME, pst.getAuthorName());
        values.put(POST_TABLE_LIKE_COUNTER, pst.getLikeCounter());
        values.put(POST_TABLE_DATE, pst.getLastUpdated());
        values.put(POST_TABLE_PHOTO_ID, pst.getPhotoId());
        db.insertWithOnConflict(POST_TABLE, POST_TABLE_ID, values, SQLiteDatabase.CONFLICT_REPLACE);
    }

    private static void addLike(SQLiteDatabase db, String postId, String userId) {
        ContentValues values = new ContentValues();
        values.put(POST_LIKE_POST_ID, postId);
        values.put(POST_LIKE_USER_ID, userId);
        db.insertWithOnConflict(POST_TABLE, POST_TABLE_ID, values, SQLiteDatabase.CONFLICT_REPLACE);
    }


    public static void update(SQLiteDatabase db, Object model) {
        // Cast the model to post
        Post pst = (Post)model;

        ContentValues values = new ContentValues();
        //values.put(COMMENT_TABLE_ID, pst.getId());
        values.put(POST_TABLE_AUTHOR_ID, pst.getAuthorId());
        values.put(POST_TABLE_AUTHOR_NAME, pst.getAuthorName());
        values.put(POST_TABLE_LIKE_COUNTER, pst.getLikeCounter());
        values.put(POST_TABLE_PHOTO_ID, pst.getPhotoId());
        db.update(POST_TABLE, values, POST_TABLE_ID + " = ?", new String[]{ pst.getId() });

        // Delete all current likes
        deletePostLikes(db, pst.getId());

        // Add all likes including new likes
        for (String userId : pst.getLikeUsers().keySet())
        {
            addLike(db, pst.getId(), userId);
        }
    }

    public static void delete(SQLiteDatabase db, Object model) {
        // Cast the model to post
        Post pst = (Post)model;

        db.delete(POST_TABLE, POST_TABLE_ID + " = ?", new String[]{ pst.getId() });
        deletePostLikes(db, pst.getId());
    }

    private static void deletePostLikes(SQLiteDatabase db, String postId) {
        db.delete(POST_LIKE_TABLE, POST_LIKE_POST_ID + " = ?", new String[]{ postId });
    }

    public static String getLastUpdateDate(SQLiteDatabase db){
        return LastUpdateSql.getLastUpdate(db,POST_TABLE);
    }
    public static void setLastUpdateDate(SQLiteDatabase db, String date){
        LastUpdateSql.setLastUpdate(db,POST_TABLE, date);
    }
}

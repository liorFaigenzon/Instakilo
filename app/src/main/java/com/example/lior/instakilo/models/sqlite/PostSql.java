package com.example.lior.instakilo.models.sqlite;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.lior.instakilo.models.Post;

import java.util.LinkedList;
import java.util.List;

public class PostSql {
    final static String POST_TABLE = "posts";
    final static String POST_TABLE_ID = "_id";
    final static String POST_TABLE_AUTHOR_ID = "authorId";
    final static String POST_TABLE_AUTHOR_NAME = "authorName";
    final static String POST_TABLE_LIKE_COUNTER = "likeCounter";
    final static String POST_TABLE_PHOTO_ID = "photo_id";

    public static void create(SQLiteDatabase db) {
        db.execSQL("create table " +
                POST_TABLE + " (" +
                POST_TABLE_ID + " TEXT PRIMARY KEY," +
                POST_TABLE_AUTHOR_ID + " TEXT," +
                POST_TABLE_AUTHOR_NAME + " TEXT," +
                POST_TABLE_LIKE_COUNTER + " TEXT," +
                POST_TABLE_PHOTO_ID + " TEXT);");
    }

    public static void drop(SQLiteDatabase db) {
        db.execSQL("drop table " + POST_TABLE);
    }

    public static List<Object> getAllPosts(SQLiteDatabase db) {
        Cursor cursor = db.query(POST_TABLE, null, null , null, null, null, null);
        List<Object> posts = new LinkedList<Object>();

        if (cursor.moveToFirst()) {
            int idIndex = cursor.getColumnIndex(POST_TABLE_ID);
            int authorIdIndex = cursor.getColumnIndex(POST_TABLE_AUTHOR_ID);
            int authorNameIndex = cursor.getColumnIndex(POST_TABLE_AUTHOR_NAME);
            int likeCounterIndex = cursor.getColumnIndex(POST_TABLE_LIKE_COUNTER);
            int photoIdIndex = cursor.getColumnIndex(POST_TABLE_PHOTO_ID);
            do {
                String id = cursor.getString(idIndex);
                String authorId = cursor.getString(authorIdIndex);
                String authorName = cursor.getString(authorNameIndex);
                int likeCounter = cursor.getInt(likeCounterIndex);
                String photoId = cursor.getString(photoIdIndex);
                Post pst = new Post(id, authorId, authorName, photoId, likeCounter, null);
                posts.add(pst);
            } while (cursor.moveToNext());
        }

        return posts;
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
            int photoIdIndex = cursor.getColumnIndex(POST_TABLE_PHOTO_ID);
            String authorId = cursor.getString(authorIdIndex);
            String authorName = cursor.getString(authorNameIndex);
            int likeCounter = cursor.getInt(likeCounterIndex);
            String photoId = cursor.getString(photoIdIndex);
            Post pst = new Post(id, authorId, authorName, photoId, likeCounter, null);
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
        values.put(POST_TABLE_PHOTO_ID, pst.getPhotoId());
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
    }

    public static void delete(SQLiteDatabase db, Object model) {
        // Cast the model to post
        Post pst = (Post)model;

        db.delete(POST_TABLE, POST_TABLE_ID + " = ?", new String[]{ pst.getId() });
    }

    public static String getLastUpdateDate(SQLiteDatabase db){
        return LastUpdateSql.getLastUpdate(db,POST_TABLE);
    }
    public static void setLastUpdateDate(SQLiteDatabase db, String date){
        LastUpdateSql.setLastUpdate(db,POST_TABLE, date);
    }
}

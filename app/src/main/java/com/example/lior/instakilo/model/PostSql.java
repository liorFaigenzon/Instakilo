package com.example.lior.instakilo.model;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by eliav.menachi on 08/06/2016.
 */
public class PostSql {
    final static String POST_TABLE = "posts";
    final static String POST_TABLE_ID = "_id";
    final static String POST_TABLE_TITLE = "photoId";
    final static String POST_TABLE_CONTENT = "userId";
    final static String POST_TABLE_LIKE_COUNTER = "content";
    final static String POST_TABLE_USER_ID = "image_name";
    final static String POST_PHOTO_ID = "title";
    final static String POST_TABLE_CHECKABLE = "checkable";
    //final static String POST_TABLE_DELETED = "deleted";

    static public void create(SQLiteDatabase db) {
        db.execSQL("create table " + POST_TABLE + " (" +
                POST_TABLE_ID + " TEXT PRIMARY KEY," +
                POST_TABLE_TITLE + " TEXT," +
                POST_TABLE_CONTENT + " TEXT," +
                POST_TABLE_LIKE_COUNTER + " TEXT," +
                POST_TABLE_USER_ID + " TEXT," +
                POST_PHOTO_ID + " TEXT," +
                POST_TABLE_CHECKABLE + " BOOLEAN);");
    }

    public static void drop(SQLiteDatabase db) {
        db.execSQL("drop table " + POST_TABLE + ";");
    }

    public static List<Post> getAllPosts(SQLiteDatabase db) {
        Cursor cursor = db.query(POST_TABLE, null, null , null, null, null, null);
        List<Post> posts = new LinkedList<Post>();

        if (cursor.moveToFirst()) {
            int idIndex = cursor.getColumnIndex(POST_TABLE_ID);
            int titleIndex = cursor.getColumnIndex(POST_TABLE_TITLE);
            int contentIndex = cursor.getColumnIndex(POST_TABLE_CONTENT);
            int likeCounterIndex = cursor.getColumnIndex(POST_TABLE_LIKE_COUNTER);
            int userIdIndex = cursor.getColumnIndex(POST_TABLE_USER_ID);
            int phoneIdIndex = cursor.getColumnIndex(POST_PHOTO_ID);
            int checkableIndex = cursor.getColumnIndex(POST_TABLE_CHECKABLE);
            do {
                String id = cursor.getString(idIndex);
                String photoId = cursor.getString(phoneIdIndex);
                String userId = cursor.getString(userIdIndex);
                String content = cursor.getString(contentIndex);
                int likeCounter = cursor.getInt(likeCounterIndex);
                String title = cursor.getString(titleIndex);
                int checkable = cursor.getInt(checkableIndex); //0 false / 1 true
                Post pst = new Post(id, photoId,userId, title , content, likeCounter, checkable == 1);
                posts.add(pst);
            } while (cursor.moveToNext());
        }
        return posts;
    }

    public static Post getPostById(SQLiteDatabase db, String id) {
        String where = POST_TABLE_ID + " = ?";
        String[] args = {id};
        Cursor cursor = db.query(POST_TABLE, null, where, args, null, null, null);
        if (cursor.moveToFirst()) {
            int idIndex = cursor.getColumnIndex(POST_TABLE_ID);
            int titleIndex = cursor.getColumnIndex(POST_TABLE_TITLE);
            int contentIndex = cursor.getColumnIndex(POST_TABLE_CONTENT);
            int likeCounterIndex = cursor.getColumnIndex(POST_TABLE_LIKE_COUNTER);
            int userIdIndex = cursor.getColumnIndex(POST_TABLE_USER_ID);
            int phoneIdIndex = cursor.getColumnIndex(POST_PHOTO_ID);
            int checkableIndex = cursor.getColumnIndex(POST_TABLE_CHECKABLE);
            String _id = cursor.getString(idIndex);
            String photoId = cursor.getString(phoneIdIndex);
            String userId = cursor.getString(userIdIndex);
            String content = cursor.getString(contentIndex);
            int likeCounter = cursor.getInt(likeCounterIndex);
            String title = cursor.getString(titleIndex);
            int checkable = cursor.getInt(checkableIndex); //0 false / 1 true
            Post pst = new Post(_id, photoId, userId,title, content, likeCounter, checkable == 1);
            return pst;
        }
        return null;
    }

    public static void add(SQLiteDatabase db, Post pst) {
        ContentValues values = new ContentValues();
        values.put(POST_TABLE_ID, pst.getId());
        values.put(POST_TABLE_TITLE, pst.getTitle());
        values.put(POST_TABLE_CONTENT, pst.getContent());
        values.put(POST_TABLE_LIKE_COUNTER, pst.getLikeCounter());
        values.put(POST_TABLE_USER_ID, pst.getUserId());
        values.put(POST_PHOTO_ID, pst.getPhotoId());
        if (pst.isChecked()) {
            values.put(POST_TABLE_CHECKABLE, 1);
        } else {
            values.put(POST_TABLE_CHECKABLE, 0);
        }
        db.insertWithOnConflict(POST_TABLE, POST_TABLE_ID, values, SQLiteDatabase.CONFLICT_REPLACE);
    }

    public static String getLastUpdateDate(SQLiteDatabase db){
        return LastUpdateSql.getLastUpdate(db,POST_TABLE);
    }
    public static void setLastUpdateDate(SQLiteDatabase db, String date){
        LastUpdateSql.setLastUpdate(db,POST_TABLE, date);
    }
}

package com.example.lior.instakilo.models.sqlite;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.lior.instakilo.models.Comment;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by Alon on 10/08/2016.
 */
public class CommentSql {
    final static String COMMENT_TABLE = "comments";
    final static String COMMENT_TABLE_ID = "_id";
    final static String COMMENT_TABLE_AUTHOR_ID = "authorId";
    final static String COMMENT_TABLE_AUTHOR_NAME = "authorName";
    final static String COMMENT_TABLE_POST_ID = "likeCounter";
    final static String COMMENT_TABLE_CONTENT = "photo_id";

    public static void create(SQLiteDatabase db) {
        db.execSQL("create table " +
                COMMENT_TABLE + " (" +
                COMMENT_TABLE_ID + " TEXT PRIMARY KEY," +
                COMMENT_TABLE_AUTHOR_ID + " TEXT," +
                COMMENT_TABLE_AUTHOR_NAME + " TEXT," +
                COMMENT_TABLE_POST_ID + " TEXT," +
                COMMENT_TABLE_CONTENT + " TEXT);");
    }

    public static void drop(SQLiteDatabase db) {
        db.execSQL("drop table " + COMMENT_TABLE);
    }

    public static List<Object> getAllComments(SQLiteDatabase db) {
        Cursor cursor = db.query(COMMENT_TABLE, null, null , null, null, null, null);
        List<Object> comments = new LinkedList<Object>();

        if (cursor.moveToFirst()) {
            int idIndex = cursor.getColumnIndex(COMMENT_TABLE_ID);
            int authorIdIndex = cursor.getColumnIndex(COMMENT_TABLE_AUTHOR_ID);
            int authorNameIndex = cursor.getColumnIndex(COMMENT_TABLE_AUTHOR_NAME);
            int postIdIndex = cursor.getColumnIndex(COMMENT_TABLE_POST_ID);
            int contentIndex = cursor.getColumnIndex(COMMENT_TABLE_CONTENT);
            do {
                String id = cursor.getString(idIndex);
                String authorId = cursor.getString(authorIdIndex);
                String authorName = cursor.getString(authorNameIndex);
                String postId = cursor.getString(postIdIndex);
                String content = cursor.getString(contentIndex);
                Comment cmt = new Comment(id, authorId, authorName, postId, content);
                comments.add(cmt);
            } while (cursor.moveToNext());
        }
        return comments;
    }

    public static List<Object> getCommentsByPostId(SQLiteDatabase db, String postId) {
        String where  = COMMENT_TABLE_POST_ID + " = ?";
        String[] args = {postId};
        Cursor cursor = db.query(COMMENT_TABLE, null, where , args, null, null, null);
        List<Object> comments = new LinkedList<Object>();

        if (cursor.moveToFirst()) {
            int idIndex = cursor.getColumnIndex(COMMENT_TABLE_ID);
            int authorIdIndex = cursor.getColumnIndex(COMMENT_TABLE_AUTHOR_ID);
            int authorNameIndex = cursor.getColumnIndex(COMMENT_TABLE_AUTHOR_NAME);
            //int postIdIndex = cursor.getColumnIndex(COMMENT_TABLE_POST_ID);
            int contentIndex = cursor.getColumnIndex(COMMENT_TABLE_CONTENT);
            do {
                String id = cursor.getString(idIndex);
                String authorId = cursor.getString(authorIdIndex);
                String authorName = cursor.getString(authorNameIndex);
                //String postId = cursor.getString(postIdIndex);
                String content = cursor.getString(contentIndex);
                Comment cmt = new Comment(id, authorId, authorName, postId, content);
                comments.add(cmt);
            } while (cursor.moveToNext());
        }
        return comments;
    }

    public static Object getCommentById(SQLiteDatabase db, String id) {
        String where = COMMENT_TABLE_ID + " = ?";
        String[] args = {id};
        Cursor cursor = db.query(COMMENT_TABLE, null, where, args, null, null, null);
        if (cursor.moveToFirst()) {
            int idIndex = cursor.getColumnIndex(COMMENT_TABLE_ID);
            int authorIdIndex = cursor.getColumnIndex(COMMENT_TABLE_AUTHOR_ID);
            int authorNameIndex = cursor.getColumnIndex(COMMENT_TABLE_AUTHOR_NAME);
            int postIdIndex = cursor.getColumnIndex(COMMENT_TABLE_POST_ID);
            int contentIndex = cursor.getColumnIndex(COMMENT_TABLE_CONTENT);
            //String id = cursor.getString(idIndex);
            String authorId = cursor.getString(authorIdIndex);
            String authorName = cursor.getString(authorNameIndex);
            String postId = cursor.getString(postIdIndex);
            String content = cursor.getString(contentIndex);
            Comment cmt = new Comment(id, authorId, authorName, postId, content);
            return cmt;
        }
        return null;
    }

    public static void add(SQLiteDatabase db, Object model) {
        // Cast the model to comment
        Comment cmt = (Comment)model;

        ContentValues values = new ContentValues();
        values.put(COMMENT_TABLE_ID, cmt.getId());
        values.put(COMMENT_TABLE_AUTHOR_ID, cmt.getAuthorId());
        values.put(COMMENT_TABLE_AUTHOR_NAME, cmt.getAuthorName());
        values.put(COMMENT_TABLE_POST_ID, cmt.getPostId());
        values.put(COMMENT_TABLE_CONTENT, cmt.getContent());
        db.insertWithOnConflict(COMMENT_TABLE, COMMENT_TABLE_ID, values, SQLiteDatabase.CONFLICT_REPLACE);
    }

    public static void update(SQLiteDatabase db, Object model) {
        // Cast the model to comment
        Comment cmt = (Comment)model;

        ContentValues values = new ContentValues();
        //values.put(COMMENT_TABLE_ID, pst.getId());
        values.put(COMMENT_TABLE_ID, cmt.getId());
        values.put(COMMENT_TABLE_AUTHOR_ID, cmt.getAuthorId());
        values.put(COMMENT_TABLE_AUTHOR_NAME, cmt.getAuthorName());
        values.put(COMMENT_TABLE_POST_ID, cmt.getPostId());
        values.put(COMMENT_TABLE_CONTENT, cmt.getContent());
        db.update(COMMENT_TABLE, values, COMMENT_TABLE_ID + " = ?", new String[]{ cmt.getId() });
    }

    public static void delete(SQLiteDatabase db, Object model) {
        // Cast the model to comment
        Comment cmt = (Comment)model;

        db.delete(COMMENT_TABLE, COMMENT_TABLE_ID + " = ?", new String[]{ cmt.getId() });
    }

    public static String getLastUpdateDate(SQLiteDatabase db){
        return LastUpdateSql.getLastUpdate(db, COMMENT_TABLE);
    }
    public static void setLastUpdateDate(SQLiteDatabase db, String date){
        LastUpdateSql.setLastUpdate(db, COMMENT_TABLE, date);
    }
}


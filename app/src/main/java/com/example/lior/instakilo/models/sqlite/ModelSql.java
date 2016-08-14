package com.example.lior.instakilo.models.sqlite;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.lior.instakilo.models.Comment;
import com.example.lior.instakilo.models.Model;
import com.example.lior.instakilo.models.Post;

import java.util.List;

public class ModelSql {
    final static int VERSION = 31;

    Helper sqlDb;


    public ModelSql(Context context){
        sqlDb = new Helper(context);
    }

    public SQLiteDatabase getWritableDB(){
        return sqlDb.getWritableDatabase();
    }

    public SQLiteDatabase getReadbleDB(){
        return sqlDb.getReadableDatabase();
    }

    public List<Object> getAll(Model.ModelClass model) {
        SQLiteDatabase db = getReadbleDB();

        switch (model)
        {
            case POST:
                return PostSql.getAllPosts(db);
            case COMMENT:
                return CommentSql.getAllComments(db);
        }
        return null;
    }

    public List<Object> getCommentsByPostId(String PostId) {
        SQLiteDatabase db = getReadbleDB();
        return CommentSql.getCommentsByPostId(db, PostId);
    }

    public void add(Object model) {
        SQLiteDatabase db = getWritableDB();

        if (model instanceof  Post) {
            PostSql.add(db, model);
            PostSql.setLastUpdateDate(db, ((Post)model).getLastUpdated());
        }
        if (model instanceof  Comment) {
            CommentSql.add(db, model);
            CommentSql.setLastUpdateDate(db, ((Comment)model).getLastUpdated());
        }
    }

    public void update(Object model) {
        SQLiteDatabase db = getWritableDB();

        if (model instanceof  Post) {
            PostSql.update(db, model);
            PostSql.setLastUpdateDate(db, ((Post)model).getLastUpdated());
        }
        if (model instanceof  Comment) {
            CommentSql.update(db, model);
            CommentSql.setLastUpdateDate(db, ((Comment)model).getLastUpdated());

        }
    }

    public void delete(Object model) {
        SQLiteDatabase db = getWritableDB();

        if (model instanceof  Post) {
            PostSql.delete(db, model);
            PostSql.setLastUpdateDate(db, ((Post)model).getLastUpdated());
        }
        if (model instanceof  Comment) {
            CommentSql.delete(db, model);
            CommentSql.setLastUpdateDate(db, ((Comment)model).getLastUpdated());
        }
    }

    public String getLastUpdateData(Model.ModelClass model)
    {
        SQLiteDatabase db = getReadbleDB();

        switch (model)
        {
            case POST:
                return PostSql.getLastUpdateDate(db);
            case COMMENT:
                return CommentSql.getLastUpdateDate(db);
        }
        return null;
    }

    public void setLastUpdateData(Model.ModelClass model, String date)
    {
        SQLiteDatabase db = getWritableDB();

        switch (model)
        {
            case POST:
                PostSql.setLastUpdateDate(db, date);
                break;
            case COMMENT:
                CommentSql.setLastUpdateDate(db, date);
                break;
        }
    }

    class Helper extends SQLiteOpenHelper {
        public Helper(Context context) {
            super(context, "database.db", null, VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            PostSql.create(db);
            CommentSql.create(db);
            LastUpdateSql.create(db);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            PostSql.drop(db);
            CommentSql.drop(db);
            LastUpdateSql.drop(db);
            onCreate(db);
        }
    }
}

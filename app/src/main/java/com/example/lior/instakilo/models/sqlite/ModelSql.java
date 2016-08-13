package com.example.lior.instakilo.models.sqlite;

/**
 * Created by eliav.menachi on 08/06/2016.
 */
import android.content.Context;
import java.util.LinkedList;
import java.util.List;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.lior.instakilo.models.Comment;
import com.example.lior.instakilo.models.Model;
import com.example.lior.instakilo.models.Post;

/**
 * Created by eliav.menachi on 13/05/2015.
 */
public class ModelSql {
    final static int VERSION = 7;

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

    public void add(Object model) {
        SQLiteDatabase db = getWritableDB();

        if (model instanceof  Post)
            PostSql.add(db, model);
        if (model instanceof  Comment)
            CommentSql.add(db, model);
    }

    public void delete(Object model) {
        SQLiteDatabase db = getWritableDB();

        if (model instanceof  Post)
            PostSql.delete(db, model);
        if (model instanceof  Comment)
            CommentSql.delete(db, model);
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
            case COMMENT:
                CommentSql.setLastUpdateDate(db, date);
        }
    }

    class Helper extends SQLiteOpenHelper {
        public Helper(Context context) {
            super(context, "database.db", null, VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            PostSql.create(db);
            LastUpdateSql.create(db);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            PostSql.drop(db);
            LastUpdateSql.drop(db);
            onCreate(db);
        }
    }
}

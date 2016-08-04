package com.example.lior.instakilo.model;

/**
 * Created by eliav.menachi on 08/06/2016.
 */
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by eliav.menachi on 13/05/2015.
 */
public class ModelSql {
    final static int VERSION = 6;

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

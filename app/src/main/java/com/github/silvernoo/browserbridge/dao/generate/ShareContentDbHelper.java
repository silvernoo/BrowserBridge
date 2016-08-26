package com.github.silvernoo.browserbridge.dao.generate;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by saikou on 2016/8/19 0019.
 * Email uedeck@gmail.com .
 */
public class ShareContentDbHelper extends SQLiteOpenHelper {
    // If you change the database schema, you must increment the database version.
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "data.db";
    public static final String TABLE_NAME_URL_DATA = "url_data";
    public static final String TABLE_NAME_ITEM = "item";
    private static final String SQL_CREATE_ITEM = "CREATE TABLE " + TABLE_NAME_ITEM + "(" +
            "_id INTEGER    PRIMARY KEY," +
            "content        TEXT," +
            "package_name   TEXT" +
            ");";
    private static final String SQL_CREATE_URL_DATA = "CREATE TABLE " + TABLE_NAME_URL_DATA + "(" +
            "_id       INTEGER PRIMARY KEY," +
            "share_url INTEGER," +
            "item_id   INTEGER REFERENCES item (_id)" +
            ");";

    public ShareContentDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_ITEM);
        db.execSQL(SQL_CREATE_URL_DATA);
    }

    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // This database is only a cache for online data, so its upgrade policy is
        // to simply to discard the data and start over
        // db.execSQL(SQL_DELETE_ENTRIES);
        onCreate(db);
    }
}
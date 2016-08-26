package com.github.silvernoo.browserbridge.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.github.silvernoo.browserbridge.dao.generate.ShareContentDbHelper;

public class ShareContentDao {
    public SQLiteDatabase writableDatabase;
    private final ShareContentDbHelper blockListDbHelper;

    public ShareContentDao(Context context) {
        new ShareContentDbHelper(context);
        blockListDbHelper = new ShareContentDbHelper(context);
    }

    public long insert(ShareData data) {
        writableDatabase = blockListDbHelper.getWritableDatabase();
        writableDatabase.beginTransaction();
        ContentValues contentValues = new ContentValues();
        contentValues.put("content", data.content);
        contentValues.put("package_name", data.packageName);
        long insert = writableDatabase.insert(ShareContentDbHelper.TABLE_NAME_ITEM, null, contentValues);
        if (data.urls != null) {
            for (String s : data.urls) {
                ContentValues contentValues2 = new ContentValues();
                contentValues2.put("share_url", s);
                contentValues2.put("item_id", insert);
                writableDatabase.insert(ShareContentDbHelper.TABLE_NAME_URL_DATA, null, contentValues2);
            }
        }
        writableDatabase.setTransactionSuccessful();
        writableDatabase.endTransaction();
        return insert;
    }

    /**
     * SELECT i._id,
     * i.content,
     * u.share_url
     * FROM item i
     * INNER JOIN
     * url_data u
     * WHERE i._id = u.item_id;
     *
     * @return
     */
    public Cursor preLoad() {
        writableDatabase = blockListDbHelper.getWritableDatabase();
        return writableDatabase.query(ShareContentDbHelper.TABLE_NAME_ITEM,
                new String[]{"item._id", "item.content", "item.package_name"},
                null,
                null,
                null,
                null,
                null);
    }

    public Cursor queryUrlByItemId(int id) {
        writableDatabase = blockListDbHelper.getWritableDatabase();
        return writableDatabase.query(ShareContentDbHelper.TABLE_NAME_URL_DATA,
                new String[]{"url_data.share_url"},
                "url_data.item_id=?",
                new String[]{String.valueOf(id)},
                null,
                null,
                null);
    }
}
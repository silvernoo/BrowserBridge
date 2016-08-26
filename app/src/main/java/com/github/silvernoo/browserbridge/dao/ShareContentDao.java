package com.github.silvernoo.browserbridge.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.github.silvernoo.browserbridge.dao.generate.ShareContentDbHelper;

public class ShareContentDao {
    public SQLiteDatabase writableDatabase;
    private final ShareContentDbHelper blockListDbHelper;
    public int excludeId;

    public ShareContentDao(Context context) {
        new ShareContentDbHelper(context);
        blockListDbHelper = new ShareContentDbHelper(context);
        writableDatabase = blockListDbHelper.getWritableDatabase();
    }

    public long insert(ShareData data) {
        if (!writableDatabase.isOpen())
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
        if (!writableDatabase.isOpen())
            writableDatabase = blockListDbHelper.getWritableDatabase();
        return writableDatabase.query(ShareContentDbHelper.TABLE_NAME_ITEM,
                new String[]{"item._id", "item.content", "item.package_name"},
                null,
                null,
                "item._id",
                excludeId == -1 ? null : String.format("item._id != %s", excludeId),
                null);
    }

    public Cursor queryUrlByItemId(int id) {
        if (!writableDatabase.isOpen())
            writableDatabase = blockListDbHelper.getWritableDatabase();
        return writableDatabase.query(ShareContentDbHelper.TABLE_NAME_URL_DATA,
                new String[]{"url_data.share_url"},
                "url_data.item_id=?",
                new String[]{String.valueOf(id)},
                null,
                null,
                null);
    }

    public void flow() {
        if (!writableDatabase.isOpen())
            writableDatabase = blockListDbHelper.getWritableDatabase();
        writableDatabase.beginTransaction();
        try {
            writableDatabase.delete(ShareContentDbHelper.TABLE_NAME_ITEM, "item._id == ?", new String[]{String.valueOf(excludeId)});
            writableDatabase.delete(ShareContentDbHelper.TABLE_NAME_URL_DATA, "url_data.item_id == ?", new String[]{String.valueOf(excludeId)});
            writableDatabase.setTransactionSuccessful();
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            writableDatabase.endTransaction();
        }
        excludeId = -1;
    }
}
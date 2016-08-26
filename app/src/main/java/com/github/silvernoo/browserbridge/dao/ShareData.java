package com.github.silvernoo.browserbridge.dao;

import android.database.Cursor;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by saikou on 2016/8/23 0023.
 * Email uedeck@gmail.com .
 */
public class ShareData {
    public int _id;
    public List<String> urls = new ArrayList<>();
    public String content;
    public String packageName;

    public static ShareData fromCursor(Cursor cursor, ShareContentDao shareContentDao) {
        ShareData shareData = new ShareData();
        shareData._id = cursor.getInt(0);
        shareData.content = cursor.getString(1);
        shareData.packageName = cursor.getString(2);
        Cursor cursor1 = shareContentDao.queryUrlByItemId(shareData._id);
        while (cursor1.moveToNext()) {
            shareData.urls.add(cursor1.getString(0));
        }
        return shareData;
    }
}

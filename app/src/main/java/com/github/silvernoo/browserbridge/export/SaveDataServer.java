package com.github.silvernoo.browserbridge.export;

import android.app.ActivityManager;
import android.app.Service;
import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.github.silvernoo.browserbridge.dao.ShareContentDao;
import com.github.silvernoo.browserbridge.dao.ShareData;

import java.util.ArrayList;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;

/**
 * Created by saikou on 2016/8/25 0025.
 * Email uedeck@gmail.com .
 */
public class SaveDataServer extends Service {

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        ShareContentDao shareContentDao = new ShareContentDao(this);
        //noinspection unchecked
        if (intent != null && intent.hasExtra("data")) {
            ArrayList<String> strings = (ArrayList<String>) intent.getSerializableExtra("data");
            ShareData shareData = new ShareData();
            shareData.content = strings.get(0);
            if (strings.size() >= 2) {
                shareData.urls = strings.subList(1, strings.size());
            }
            String currentApp = null;
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                // intentionally using string value as Context.USAGE_STATS_SERVICE was
                // strangely only added in API 22 (LOLLIPOP_MR1)
                @SuppressWarnings("WrongConstant")
                UsageStatsManager usm = (UsageStatsManager) getSystemService("usagestats");
                long time = System.currentTimeMillis();
                List<UsageStats> appList = usm.queryUsageStats(UsageStatsManager.INTERVAL_DAILY,
                        time - 5 * 1000, time);
                if (appList != null && appList.size() > 0) {
                    SortedMap<Long, UsageStats> sortedMap = new TreeMap<>();
                    for (UsageStats usageStats : appList) {
                        sortedMap.put(usageStats.getLastTimeUsed(),
                                usageStats);
                    }
                    if (!sortedMap.isEmpty()) {
                        List<UsageStats> longs = new ArrayList<>();
                        longs.addAll(sortedMap.values());
                        if (longs.get(longs.size() - 2).getPackageName().trim().toLowerCase().equals("android"))
                            currentApp = longs.get(longs.size() - 3).getPackageName();
                        else
                            currentApp = longs.get(longs.size() - 2).getPackageName();
                    }
                }
            } else {
                ActivityManager am = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
                List<ActivityManager.RunningAppProcessInfo> tasks = am
                        .getRunningAppProcesses();
                currentApp = tasks.get(0).processName;
            }
            shareData.packageName = currentApp;
            shareContentDao.insert(shareData);
            shareContentDao.writableDatabase.close();
        }
        return super.onStartCommand(intent, flags, startId);
    }
}

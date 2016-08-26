package com.github.silvernoo.browserbridge.export;

import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.github.silvernoo.browserbridge.BaseActivity;
import com.github.silvernoo.browserbridge.R;
import com.github.silvernoo.browserbridge.widget.SpanTextView;
import com.github.silvernoo.browserbridge.widget.processor.TelProcessor;
import com.github.silvernoo.browserbridge.widget.processor.UrlProcessor;

import java.util.ArrayList;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by saikou on 2015/12/9 0009.
 * Email uedeck@gmail.com .
 */
public class Bridge extends BaseActivity {

    private ArrayList<String> strings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        strings = extractUrl(getIntent().getStringExtra(Intent.EXTRA_TEXT));
        if (strings != null && strings.size() >= 1) {

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                UsageStatsManager mUsageStatsManager = (UsageStatsManager) getSystemService(Context.USAGE_STATS_SERVICE);
                long time = System.currentTimeMillis();
                // We get usage stats for the last 10 seconds
                List<UsageStats> stats = mUsageStatsManager.queryUsageStats(UsageStatsManager.INTERVAL_DAILY, time - 1000 * 10, time);
                // Sort the stats by the last time used
                if (stats != null) {
                    SortedMap<Long, UsageStats> mySortedMap = new TreeMap<Long, UsageStats>();
                    for (UsageStats usageStats : stats) {
                        mySortedMap.put(usageStats.getLastTimeUsed(), usageStats);
                    }
                    if (mySortedMap != null && !mySortedMap.isEmpty()) {
                        String topPackageName = mySortedMap.get(mySortedMap.lastKey()).getPackageName();
                        Log.e("TopPackage Name", topPackageName);
                    }
                }
            }

            if (mPreferences.getBoolean(KEY_CLIPBOARD, true) && strings.size() >= 2 && !TextUtils.isEmpty(strings.get(1))) {
                writeToClipboard(strings.get(1));
                Toast.makeText(getBaseContext(), getString(R.string.write_to_clipboard),
                        Toast.LENGTH_LONG).show();
            }
            View inflate = View.inflate(this, R.layout.view_share_detail, null);
            SpanTextView message = (SpanTextView) inflate.findViewById(android.R.id.message);
            message.addTextProcessor(new TelProcessor());
            message.addTextProcessor(new UrlProcessor());
            message.setText(strings.get(0));
            message.setMovementMethod(LinkMovementMethod.getInstance());
            new AlertDialog.Builder(this)
                    .setView(inflate)
                    .setOnCancelListener(new DialogInterface.OnCancelListener() {
                        @Override
                        public void onCancel(DialogInterface dialog) {
                            finish();
                        }
                    })
                    .setPositiveButton(R.string.open, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            openUrlInBrowser();
                            finish();
                        }
                    }).setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    finish();
                }
            })
                    .setNeutralButton(R.string.save, new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Intent intent = new Intent(Bridge.this, SaveDataServer.class);
                            intent.putExtra("data", strings);
                            startService(intent);
                            finish();
                        }

                    }).show();
        } else
            Toast.makeText(getBaseContext(), getString(R.string.url_not_found),
                    Toast.LENGTH_LONG).show();
    }

    private void openUrlInBrowser() {
        for (int i = 1; i < strings.size(); i++) {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse(strings.get(i)));
            startActivity(intent);
        }
    }

    public static ArrayList<String> extractUrl(String extra) {
        ArrayList<String> result = new ArrayList<>();
        result.add(extra);
        if (extra == null) {
            return null;
        } else {
            Pattern pattern = Pattern.compile("https?://[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|]");
            Matcher matcher = pattern.matcher(extra);
            if (matcher.find()) {
                do {
                    String url = matcher.group();
                    if (!TextUtils.isEmpty(url)) {
                        result.add(url);
                    }
                } while (matcher.find());
                return result;
            } else {
                return result;
            }
        }
    }

    private void writeToClipboard(String url) {
        if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.HONEYCOMB) {
            android.text.ClipboardManager clipboard = (android.text.ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
            clipboard.setText(url);
        } else {
            android.content.ClipboardManager clipboard = (android.content.ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
            android.content.ClipData clip = android.content.ClipData.newPlainText("url", url);
            clipboard.setPrimaryClip(clip);
        }
    }
}

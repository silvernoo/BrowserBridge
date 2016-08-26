package com.github.silvernoo.browserbridge;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

/**
 * Created by saikou on 2016/8/19 0019.
 * Email uedeck@gmail.com .
 */
public class BaseActivity extends AppCompatActivity {

    protected SharedPreferences mPreferences;

    public static final String KEY_CLIPBOARD = "key_clipboard";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPreferences = getPreferences(MODE_PRIVATE);
    }
}

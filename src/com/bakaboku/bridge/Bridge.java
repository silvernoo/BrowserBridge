package com.bakaboku.bridge;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Window;

public class Bridge extends Activity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		String extra = getIntent().getStringExtra(Intent.EXTRA_TEXT);
		String url = "";
		Pattern pattern = Pattern.compile("htt(p|ps)[\\w\\d\\:\\/\\.]{3,}");
		Matcher matcher = pattern.matcher(extra);
		if (matcher.find()) {
			url = matcher.group();
		}
		if (TextUtils.isEmpty(url))
			finish();
		Log.i("BrowserBridge", url);
		Intent intent = new Intent(Intent.ACTION_VIEW);
		intent.setData(Uri.parse(url));
		startActivity(intent);
		finish();
	}
}

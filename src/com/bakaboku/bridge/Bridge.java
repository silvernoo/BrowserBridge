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
import android.widget.Toast;

public class Bridge extends Activity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		String extra = getIntent().getStringExtra(Intent.EXTRA_TEXT);
		if (extra == null || !extra.contains("http")) {
			Toast.makeText(
					getApplicationContext(),
					getResources().getString(
							R.string.msg_not_found_share_content),
					Toast.LENGTH_LONG).show();
			finish();
		} else {
			String url = "";
			Pattern pattern = Pattern.compile("htt(p|ps)[\\w\\d\\:\\/\\.]{3,}");
			Matcher matcher = pattern.matcher(extra);
			if (matcher.find()) {
				url = matcher.group();
			}
			if (TextUtils.isEmpty(url))
				finish();
			else {
				Log.i("BrowserBridge", url);
				Intent intent = new Intent(Intent.ACTION_VIEW);
				intent.setData(Uri.parse(url));
				startActivity(intent);
				finish();
			}
		}
	}
}

package silvernoo.github.com.browserbridge;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Toast;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by saikou on 2015/12/9 0009.
 * Email uedeck@gmail.com .
 */
public class Bridge extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        String extra = getIntent().getStringExtra(Intent.EXTRA_TEXT);
        if (extra == null) {
            Toast.makeText(getBaseContext(), getString(R.string.share_content_is_null),
                    Toast.LENGTH_LONG).show();
        } else {
            Pattern pattern = Pattern.compile("https?://[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|]");
            Matcher matcher = pattern.matcher(extra);
            if (matcher.find()) {
                do {
                    String url = matcher.group();
                    if (TextUtils.isEmpty(url))
                        Toast.makeText(getBaseContext(), getString(R.string.not_found_that_url),
                                Toast.LENGTH_LONG).show();
                    else {
                        Intent intent = new Intent(Intent.ACTION_VIEW);
                        intent.setData(Uri.parse(url));
                        writeToClipboard(url);
                        startActivity(intent);
                    }
                } while (matcher.find());
            } else {
                Toast.makeText(getBaseContext(), getString(R.string.not_found_that_url),
                        Toast.LENGTH_LONG).show();
            }
        }
        if (write) {
            Toast.makeText(getBaseContext(), getString(R.string.write_to_clipboard),
                    Toast.LENGTH_LONG).show();
        }
        finish();
    }

    boolean write = false;

    private void writeToClipboard(String url) {
        if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.HONEYCOMB) {
            android.text.ClipboardManager clipboard = (android.text.ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
            clipboard.setText(url);
        } else {
            android.content.ClipboardManager clipboard = (android.content.ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
            android.content.ClipData clip = android.content.ClipData.newPlainText("url", url);
            clipboard.setPrimaryClip(clip);
        }
        write = true;
    }
}

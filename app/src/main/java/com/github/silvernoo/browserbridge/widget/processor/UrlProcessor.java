package com.github.silvernoo.browserbridge.widget.processor;

import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.URLSpan;

import com.github.silvernoo.browserbridge.widget.TextProcessor;

import java.util.List;
import java.util.Map;

/**
 * Created by saikou on 2016/8/23 0023.
 * Email uedeck@gmail.com .
 */
public class UrlProcessor extends BaseProcessor implements TextProcessor {

    @SuppressWarnings("unchecked")
    @Override
    public CharSequence parseText(CharSequence charSequence) {
        SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder(charSequence);

        Map<String, List<?>> position = position(charSequence.toString(), "https?://[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|]");

        List<Integer> start = (List<Integer>) position.get("start");
        List<Integer> end = (List<Integer>) position.get("end");
        List<String> data = (List<String>) position.get("data");
        for (int i = 0; i < start.size(); i++) {
            spannableStringBuilder.setSpan(new URLSpan(data.get(i)), start.get(i), end.get(i), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
        return spannableStringBuilder;
    }
}

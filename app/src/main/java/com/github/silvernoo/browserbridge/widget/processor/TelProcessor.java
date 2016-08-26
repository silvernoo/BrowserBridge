package com.github.silvernoo.browserbridge.widget.processor;

import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.URLSpan;

import com.github.silvernoo.browserbridge.widget.TextProcessor;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by saikou on 2016/8/23 0023.
 * Email uedeck@gmail.com .
 */
public class TelProcessor extends BaseProcessor implements TextProcessor {

    private List<Integer> start;
    private List<Integer> end;
    private List<String> data;
    private List<Integer> seps;

    @SuppressWarnings("unchecked")
    @Override
    public CharSequence parseText(CharSequence charSequence) {
        SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder(charSequence);

        start = new ArrayList<>();
        end = new ArrayList<>();
        data = new ArrayList<>();
        matchTelephone(charSequence.toString());
        matchMobilePhone(charSequence.toString());

        for (int i = 0; i < start.size(); i++) {
            spannableStringBuilder.setSpan(new URLSpan("tel:" + data.get(i)), start.get(i), end.get(i), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
        return spannableStringBuilder;
    }

    private void matchMobilePhone(String extra) {
        extra = Preprocessor(extra);
        Pattern pattern = Pattern.compile("(86|0086)?(1[0-9][0-9])\\d{8}|400\\d{7}");
        Matcher matcher = pattern.matcher(extra);
        if (matcher.find()) {
            do {
                start.add(adjust(matcher.start()));
                end.add(adjust(matcher.end()));
                data.add(matcher.group());
            } while (matcher.find());
        }
    }

    private Integer adjust(int start) {
        for (int i = 0; i < seps.size(); i++) {
            if (seps.get(i) < start) {
                start++;
            }
        }
        return start;
    }

    private String Preprocessor(String extra) {
        seps = new ArrayList<>();
        char[] chars = extra.toCharArray();
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < chars.length; i++) {
            char c = chars[i];
            if (c != '(' && c != ')' && c != '-' && c != '+') {
                stringBuilder.append(c);
            } else {
                seps.add(i);
            }
        }
        return stringBuilder.toString();
    }

    private void matchTelephone(String extra) {
        Pattern pattern = Pattern.compile("(?<!\\d)(\\(\\d{3,4}\\)|\\d{3,4}(-|\\s))?\\d{7,8}(?!\\d)");
        Matcher matcher = pattern.matcher(extra);
        if (matcher.find()) {
            do {
                start.add(matcher.start());
                end.add(matcher.end());
                data.add(matcher.group());
            } while (matcher.find());
        }
    }
}

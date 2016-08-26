package com.github.silvernoo.browserbridge.widget.processor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by saikou on 2016/8/23 0023.
 * Email uedeck@gmail.com .
 */
public class BaseProcessor {
    public Map<String, List<?>> position(String extra, String regex) {
        List<Integer> start = new ArrayList<>();
        List<Integer> end = new ArrayList<>();
        List<String> data = new ArrayList<>();
        if (extra == null) {
            return null;
        } else {
            Pattern pattern = Pattern.compile(regex);
            Matcher matcher = pattern.matcher(extra);
            if (matcher.find()) {
                do {
                    start.add(matcher.start());
                    end.add(matcher.end());
                    data.add(matcher.group());
                } while (matcher.find());
                Map<String, List<?>> map = new HashMap<>();
                map.put("start", start);
                map.put("end", end);
                map.put("data", data);
                return map;
            } else {
                return null;
            }
        }
    }
}

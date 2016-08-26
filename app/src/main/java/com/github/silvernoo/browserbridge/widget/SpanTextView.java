package com.github.silvernoo.browserbridge.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by saikou on 2016/8/23 0023.
 * Email uedeck@gmail.com .
 */
public class SpanTextView extends TextView {

    List<TextProcessor> processors = new ArrayList<>();

    public SpanTextView(Context context) {
        super(context);
    }

    public SpanTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public SpanTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void addTextProcessor(TextProcessor textProcessor) {
        processors.add(textProcessor);
    }

    public void setText(String text) {
        CharSequence charSequence = text;
        for (TextProcessor processor : processors) {
            charSequence = processor.parseText(charSequence);
        }
        super.setText(charSequence);
    }

//    @Override
//    public void setText(CharSequence text, BufferType type) {
//        for (TextProcessor environment : processors) {
//            text = environment.parseText(text);
//        }
//        super.setText(text, type);
//    }
}

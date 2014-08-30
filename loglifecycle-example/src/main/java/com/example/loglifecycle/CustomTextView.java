package com.example.loglifecycle;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.TextView;

import com.github.stephanenicolas.loglifecycle.LogLifeCycle;

@LogLifeCycle
public class CustomTextView extends TextView {

    public CustomTextView(Context context) {

        super(context);
    }

    public CustomTextView(Context context, AttributeSet attrs) {

        super(context, attrs);
    }

    public CustomTextView(Context context, AttributeSet attrs, int defStyle) {

        super(context, attrs, defStyle);
    }
}

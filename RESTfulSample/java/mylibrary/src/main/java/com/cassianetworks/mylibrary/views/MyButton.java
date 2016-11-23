package com.cassianetworks.mylibrary.views;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.Button;

/**
 * Created by ZhangMin on 2016/5/27.
 */
public class MyButton extends Button {
    public MyButton(Context context) {
        super(context);
        setAllCaps(false);
    }

    public MyButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        setAllCaps(false);
    }

    public MyButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setAllCaps(false);
    }
}

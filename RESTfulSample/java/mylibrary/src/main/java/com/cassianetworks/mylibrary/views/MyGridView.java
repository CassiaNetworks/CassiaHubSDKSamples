package com.cassianetworks.mylibrary.views;

import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;
import android.widget.GridView;

/**
 * Created by ZhangMin on 2015/3/24.。
 */
public class MyGridView extends GridView {

    public boolean hasScrollBar = true;

    public MyGridView(Context context) {
        this(context, null);

    }


    public MyGridView(Context context, AttributeSet attrs) {
        super(context, attrs, 0);

    }

    public MyGridView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        delEdge();
    }

    private void delEdge() {
        if (Build.VERSION.SDK_INT >= 9) {
            this.setOverScrollMode(View.OVER_SCROLL_NEVER);
//            this.setFadingEdgeLength(0);
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int expandSpec;
        if (hasScrollBar) {
            expandSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2,
                    MeasureSpec.AT_MOST);
            super.onMeasure(widthMeasureSpec, expandSpec);// 注意这里,这里的意思是直接测量出GridView的高度
        } else {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        }
    }
}

package com.cassianetworks.mylibrary.views;

import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ListView;

/**
 * Created by ZhangMin on 2015/3/24./
 */
public class MyListView extends ListView {


    public MyListView(Context context) {
        super(context);
        delEdge();
    }

    public MyListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        delEdge();
    }

    public MyListView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        delEdge();
    }

    private void delEdge() {
        if (Build.VERSION.SDK_INT >= 9) {
            this.setOverScrollMode(View.OVER_SCROLL_NEVER);
        }
    }
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int expandSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2,
                MeasureSpec.AT_MOST);
        super.onMeasure(widthMeasureSpec, expandSpec);
    }


}

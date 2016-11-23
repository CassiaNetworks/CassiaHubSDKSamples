package com.cassianetworks.mylibrary.views;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Rect;
import android.os.Build;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.TranslateAnimation;
import android.widget.ScrollView;

/**
 * Created by on 2015/7/24./
 */
public class DampScrollView extends ScrollView {
    //scrollView下的控件
    private View inner;

    private float y;

    //保存状态的矩形框
    private Rect normal = new Rect();

    //是否要计算
    private boolean isCount = false;

    @SuppressLint("NewApi")
    public DampScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
        delEdge();
    }

    //加载完布局文件之后调用
    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        if (getChildCount() > 0) {
            //获取控件
            inner = getChildAt(0);
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        if (inner != null) {
            commOnTouchEvent(ev);
        }
        return super.onTouchEvent(ev);
    }

    //处理触摸事件
    public void commOnTouchEvent(MotionEvent ev) {
        int action = ev.getAction();
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                break;
            case MotionEvent.ACTION_UP:
                //是不是需要动画
                if (isNeedAnimation()) {
                    animation();
                    isCount = false;
                }
                break;
            case MotionEvent.ACTION_MOVE:
                final float preY = y;
                float nowY = ev.getY();
                int deltaY = (int) (preY - nowY);
                if (!isCount) {
                    deltaY = 0;
                }

                y = nowY;
                if (isNeedMove()) {
                    if (normal.isEmpty()) {
                        normal.set(inner.getLeft(), inner.getTop(), inner.getRight(), inner.getBottom());
                    }
                    inner.layout(inner.getLeft(), inner.getTop() - deltaY / 2, inner.getRight(), inner.getBottom() - deltaY / 2);
                }
                isCount = true;
                break;

            default:
                break;
        }
    }

    public void animation() {
        TranslateAnimation ta = new TranslateAnimation(0, 0, inner.getTop(), normal.top);
        ta.setDuration(200);
        inner.startAnimation(ta);
        inner.layout(normal.left, normal.top, normal.right, normal.bottom);
        normal.setEmpty();

    }

    // return left >= right || top >= bottom;
    public boolean isNeedAnimation() {
        return !normal.isEmpty();
    }

    public boolean isNeedMove() {
        int offset = inner.getMeasuredHeight() - getHeight();
        int scrollY = getScrollY();
        return scrollY == 0 || scrollY == offset;
    }

    @Override
    protected int computeScrollDeltaToGetChildRectOnScreen(Rect rect) {
        return 0;
    }

    private void delEdge() {
        if (Build.VERSION.SDK_INT >= 9) {
            this.setOverScrollMode(View.OVER_SCROLL_NEVER);
        }
    }
}


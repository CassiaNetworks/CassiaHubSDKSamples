package com.cassianetworks.mylibrary.views;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.CountDownTimer;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Interpolator;

import com.cassianetworks.mylibrary.R;
import com.cassianetworks.mylibrary.utils.DisplayUtils;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by ZhangMin on 2016/4/12.、
 */
public class CircleDotLoadingView extends View {
    private static final int STATE_INIT = 1; //初始化
    private static final int STATE_ING = 2; //加载中
    /*下面是内部变量*/
    Paint circlePaint = new Paint(); //画笔
    Paint dotPaint = new Paint(); //画笔
    //定时器
    Timer mTimer;
    private int state = STATE_INIT; //开始,加载中,加载成功,加载失败
    private int MIN_SIZE;     //默认的宽高
    private int mCircleRadius; //圆圈的半径
    private float mDotRadius;    //圆点的半径
    private Interpolator mInterpolator; //圆点旋转的插值器
    private int cx; //圆心坐标
    private int cy;
    private float dotX; //小圆点的x坐标
    private float dotY; //小圆点的y坐标
    private int linearAngle = 0; //线性角度变化值:0-360
//    private TimeCount time;

    public CircleDotLoadingView(Context context) {
        super(context);
        init();
    }

    public CircleDotLoadingView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public CircleDotLoadingView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        /*下面的值可以通过自定义属性获得*/
        float mCircleStrokeWidth = getContext().getResources().getDimension(R.dimen.circle_stroke_width);
        int mColor = getContext().getResources().getColor(R.color.loading_dot);
        MIN_SIZE = DisplayUtils.dip2px(getContext(), 83); //这里的dip2px方法就是简单的将72dp转换为本机对应的px,可以去网上随便搜一个
        mDotRadius = getContext().getResources().getDimension(R.dimen.loading_dot_size);
        mInterpolator = new AccelerateDecelerateInterpolator();
        circlePaint.setColor(mColor);
        circlePaint.setAntiAlias(true);
        circlePaint.setStrokeWidth(mCircleStrokeWidth);
        circlePaint.setStyle(Paint.Style.STROKE);
        dotPaint.setAntiAlias(true);
        dotPaint.setColor(Color.parseColor("#ffad00"));
        dotPaint.setStyle(Paint.Style.FILL);
//        time = new TimeCount(30000, 3);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);
        if (widthMode == MeasureSpec.AT_MOST && heightMode == MeasureSpec.AT_MOST) {
            setMeasuredDimension(MIN_SIZE, MIN_SIZE);
        } else if (widthMode == MeasureSpec.AT_MOST) {
            setMeasuredDimension(MIN_SIZE, heightSize);
        } else if (heightMode == MeasureSpec.AT_MOST) {
            setMeasuredDimension(widthSize, MIN_SIZE);
        }
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        /*注意,绘制的坐标是以当前View的左上角为圆点的,而不是当前View的坐标*/
        //圆心坐标计算
        cx = (getWidth() + getPaddingLeft() - getPaddingRight()) / 2;
        cy = (getHeight() + getPaddingTop() + -getPaddingBottom()) / 2;
        //圆圈的半径计算
        int radiusH = (getWidth() - getPaddingRight() - getPaddingLeft()) / 2 - (int) mDotRadius;
        int radiusV = (getHeight() - getPaddingBottom() - getPaddingTop()) / 2 - (int) mDotRadius;
        mCircleRadius = Math.min(radiusV, radiusH);
        //初始化小圆点位置坐标
        dotX = cx;
        dotY = getPaddingTop() + mDotRadius * 2;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        //画圆圈
        canvas.drawCircle(cx, cy, mCircleRadius, circlePaint);
        //画小圆
        if (state == STATE_ING) {
            canvas.drawCircle(dotX, dotY, mDotRadius, dotPaint);
        }
    }

    public void startLoading() {
        state = STATE_ING;
        if (mTimer == null) {
            mTimer = new Timer();
        }


        TimerTask mTimerTask = new TimerTask() {
            @Override
            public void run() {
                refreshDotPosition();
            }
        };


        if (mTimer != null)
            mTimer.schedule(mTimerTask, 10, 3);
//        if(time != null)
//            time.start();
//
    }

    public void stopLoading() {
//        if(time != null)
//            time.cancel();
        if (mTimer != null) {
            mTimer.cancel();
            mTimer.purge();
            mTimer = null;
        }

//        if (mTimerTask != null) {
//            mTimerTask.cancel();
//            mTimerTask = null;
//        }
    }


    public void setDotPaintColor(int color) {
        dotPaint.setColor(color);
        postInvalidate();
    }

    public void setDotPaintColor(String color) {
        dotPaint.setColor(Color.parseColor(color));
        postInvalidate();
    }

    /**
     * 刷新dot位置
     */
    private void refreshDotPosition() {
        final float input = (linearAngle % 360.0F);
        float f = mInterpolator.getInterpolation(input / 360.0F);
        double realAngle = f * 2 * Math.PI; //真实的角度
        dotX = cx + (float) (mCircleRadius * Math.sin(realAngle));
        dotY = cy - (float) (mCircleRadius * Math.cos(realAngle));
        postInvalidate();
        linearAngle = linearAngle + 1;
    }

    @SuppressWarnings("unused")
    class TimeCount extends CountDownTimer {

        public TimeCount(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }

        @Override
        public void onTick(long millisUntilFinished) {
            refreshDotPosition();

        }

        @Override
        public void onFinish() {
        }
    }

}

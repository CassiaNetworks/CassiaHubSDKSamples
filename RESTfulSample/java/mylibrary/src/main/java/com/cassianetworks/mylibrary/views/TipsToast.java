package com.cassianetworks.mylibrary.views;

import android.content.Context;
import android.content.res.Resources;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.cassianetworks.mylibrary.R;


/**
 * 自定义提示Toast
 *
 * @author kangwei
 */
public class TipsToast extends Toast {

    public TipsToast(Context context) {
        super(context);
    }

    public static TipsToast makeText(Context context, CharSequence text, int duration) {
        TipsToast result = new TipsToast(context);

        LayoutInflater inflate = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View v = inflate.inflate(R.layout.view_tips, null);
        TextView tv = (TextView) v.findViewById(android.R.id.message);
        tv.setText(text);

        result.setView(v);
        // setGravity方法用于设置位置，此处为垂直居中
        result.setGravity(Gravity.BOTTOM, 0, 100);
        result.setDuration(duration);

        return result;
    }

    public static TipsToast makeText(Context context, int resId, int duration) throws Resources.NotFoundException {
        return makeText(context, context.getString(resId), duration);
    }

    public void setIcon(int iconResId) {
        if (getView() == null) {
            throw new RuntimeException("This Toast was not created with Toast.makeText()");
        }
        ImageView iv = (ImageView) getView().findViewById(R.id.tips_icon);
        if (iv == null) {
            throw new RuntimeException("This Toast was not created with Toast.makeText()");
        }
        iv.setImageResource(iconResId);
    }

    @Override
    public void setText(CharSequence s) {
        if (getView() == null) {
            throw new RuntimeException("This Toast was not created with Toast.makeText()");
        }
        TextView tv = (TextView) getView().findViewById(android.R.id.message);
        if (tv == null) {
            throw new RuntimeException("This Toast was not created with Toast.makeText()");
        }
        tv.setText(s);
    }
}

package com.cassianetworks.fall.utils;

import android.content.Context;
import android.content.DialogInterface;
import android.text.TextUtils;

import com.cassianetworks.fall.R;
import com.cassianetworks.fall.views.CustomDialog;


/**
 * Created by ZhangMin on 2015/10/20./
 */
public class DialogUtils {
    /**
     * 创建一个提示框
     *
     * @param context    context
     * @param msg        内容提示
     * @param no         取消按钮文字
     * @param ok         确定按钮的文字
     * @param noCallback 取消按钮的执行动作
     * @param okCallback 确定按钮的执行动作
     */
    public static void showDiyYNTipDialog(Context context, String msg, String no,
                                          final Runnable noCallback, String ok, final Runnable okCallback) {
        showDialog(context, context.getString(R.string.tip), msg, no, noCallback, ok, okCallback);
    }

    /**
     * 创建一个[取消],[确定]的提示框
     *
     * @param context    context
     * @param msg        dialog的内容
     * @param okCallback 点击确定按钮的事件
     */
    public static void showDefaultYNTipDialog(final Context context, String msg, final Runnable okCallback) {
        showDefaultYNTipDialog(context, msg, okCallback, null);
    }

    /**
     * 创建一个[取消],[确定]的提示框
     *
     * @param context    context
     * @param msg        dialog的内容
     * @param okCallback 点击确定按钮的事件
     * @param noCalback  点击取消按钮的事件
     */

    public static void showDefaultYNTipDialog(final Context context, String msg, final Runnable okCallback, final Runnable noCalback) {
        showDiyYNTipDialog(context, msg, context.getString(R.string.dialog_no), noCalback, context.getString(R.string.dialog_yes), okCallback);
    }

    /**
     * 创建一个只有一个确定按钮的对话框
     *
     * @param context    context
     * @param title      标题
     * @param msg        提示内容
     * @param okCallback 确定的回调事件
     */
    public static void showDiyYDialog(Context context, String title, String msg, final Runnable okCallback) {
        showDialog(context, title, msg, null, null, context.getString(R.string.dialog_yes), okCallback);
    }

    /**
     * 创建一个只有一个确定按钮的提示框
     *
     * @param context context
     * @param msg     对话框的提示内容
     */
    public static void showDefaultYTipDialog(Context context, String msg, final Runnable onOk) {
        showDiyYDialog(context, context.getString(R.string.tip), msg, onOk);
    }

    /**
     * 创建对话框
     *
     * @param context
     * @param title      标题
     * @param msg        内容
     * @param no         取消按钮文案 如果为"" 或者是null,不显示
     * @param noCallback 取消按钮回调事件
     * @param ok         确认按钮文案
     * @param okCallback 确认按钮回调事件
     */

    public static void showDialog(Context context, String title, String msg,
                                  String no, final Runnable noCallback,
                                  String ok, final Runnable okCallback) {

        CustomDialog.Builder builder = new CustomDialog.Builder(context);
        if (!TextUtils.isEmpty(title))
            builder.setTitle(title);
        builder.setMessage(msg);
        if (!TextUtils.isEmpty(no))
            builder.setNegativeButton(no, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    if (noCallback != null)
                        noCallback.run();
                    dialog.dismiss();
                }
            });

        builder.setPositiveButton(ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (okCallback != null)
                    okCallback.run();
                dialog.dismiss();
            }
        });

        builder.create().show();
    }

}

package com.cassianetworks.mylibrary.views;


import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;

import com.cassianetworks.mylibrary.R;


/**
 * 加载中Dialog
 *
 * @author kangwei
 */
public class LoadingDialog extends AlertDialog {

//    private TextView tips_loading_msg;

//    private String message = null;

    private int contentView;

    public LoadingDialog(Context context) {
        super(context, R.style.Dialog_bocop);
    }

    public LoadingDialog(Context context, String message) {
        super(context);
        this.setCancelable(false);
    }

    public LoadingDialog(Context context, String message, int contentView) {
        super(context, R.style.Dialog_bocop);
        this.contentView = contentView;
    }

    public LoadingDialog(Context context, int theme, String message) {
        super(context, theme);
        this.setCancelable(false);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (contentView == 0)
            this.setContentView(R.layout.view_tips_loading);
        else
            setContentView(contentView);
    }

}

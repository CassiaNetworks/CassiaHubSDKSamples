package com.cassianetworks.fall.views;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;

import com.cassianetworks.fall.R;

public class LoadingDialog extends AlertDialog {

    private int contentView;

    public LoadingDialog(Context context) {
        super(context, R.style.Dialog_bocop);
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

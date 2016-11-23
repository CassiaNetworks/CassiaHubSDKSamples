package com.cassianetworks.fall;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;
import android.view.Window;

import com.cassianetworks.fall.utils.SysUtils;

import org.xutils.common.util.LogUtil;
import org.xutils.x;

public abstract class BaseActivity extends FragmentActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        x.view().inject(this);
        init();
    }

    /**
     * 初始化数据
     */
    protected abstract void init();

    /**
     * Debug输出Log信息
     *
     * @param msg 输出信息
     */
    protected void debugLog(String msg) {
        LogUtil.d(msg);
    }


    /**
     * 通过Class跳转界面
     */
    public void startActivity(Class<?> cls) {
        startActivity(cls, null);
    }

    /**
     * 含有Bundle通过Class跳转界面
     */
    public void startActivity(Class<?> cls, Bundle bundle) {
        Intent intent = new Intent();
        intent.setClass(this, cls);
        if (bundle != null) {
            intent.putExtras(bundle);
        }
        startActivity(intent);
    }

    /**
     * 通过Action跳转界面
     */
    public void startActivity(String action) {
        startActivity(action, null);

    }

    /**
     * 含有Bundle通过Action跳转界面
     */
    public void startActivity(String action, Bundle bundle) {
        Intent intent = new Intent();
        intent.setAction(action);
        if (bundle != null) {
            intent.putExtras(bundle);
        }
        startActivity(intent);

    }

    public void startActivityForResult(Class<?> cls, int requestCode) {
        startActivityForResult(cls, null, requestCode);
    }

    /**
     * 含有Bundle通过Class打开编辑界面
     */
    public void startActivityForResult(Class<?> cls, Bundle bundle, int requestCode) {
        Intent intent = new Intent();
        intent.setClass(this, cls);
        if (bundle != null) {
            intent.putExtras(bundle);
        }
        startActivityForResult(intent, requestCode);
    }


    public void showTips(final int resId) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                SysUtils.showShortTips(resId);
            }
        });
    }

    public void showTips(final String msg) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (TextUtils.isEmpty(msg)) return;
                SysUtils.showShortTips(msg);
            }
        });
    }


}
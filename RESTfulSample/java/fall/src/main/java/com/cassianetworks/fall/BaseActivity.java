package com.cassianetworks.fall;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;
import android.view.Window;

import com.cassianetworks.fall.utils.SysUtils;

import org.xutils.common.util.LogUtil;

import static android.R.attr.x;


/**
 * Created by Cassia on 2015/5/8.。
 */

/**
 * 功能描述：对Activity类进行扩展
 *
 * @author android_ls
 */
public abstract class BaseActivity extends FragmentActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);

//        int layoutId = getLayoutId();
//        if (layoutId != 0) {
//            setContentView(layoutId);
//        }



        // 向用户展示信息前的准备工作在这个方法里处理
        preliminary();
    }


    /**
     * 向用户展示信息前的准备工作在这个方法里处理
     */
    protected void preliminary() {
        // 初始化组件
        setupViews();

        // 初始化数据
        initialized();
    }


    /**
     * @return 布局文件ID
     */
//    protected abstract int getLayoutId();

    /**
     * 初始化组件
     */
    protected abstract void setupViews();

    /**
     * 初始化数据
     */
    protected abstract void initialized();

    /**
     * Debug输出Log信息
     *
     * @param msg 输出信息
     */
    protected void debugLog(String msg) {
        LogUtil.d(msg);
    }

    /**
     * Error输出Log信息
     *
     * @param msg 输出信息
     */
    protected void errorLog(String msg) {
        LogUtil.e(msg);
    }

    /**
     * Info输出Log信息
     *
     * @param msg 输出信息
     */
    protected void infoLog(String msg) {
        LogUtil.i(msg);
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
//        overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
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
//        overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);

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
//        overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
    }


    /**
     * 默认退出
     */
    public void defaultFinish() {
        super.finish();
    }




    @Override
    protected void onResume() {
        super.onResume();
        BaseApplication.currentActivity = this;
    }


}
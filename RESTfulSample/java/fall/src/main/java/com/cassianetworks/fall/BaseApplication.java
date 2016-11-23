package com.cassianetworks.fall;

import android.app.Application;
import android.os.Handler;
import android.os.Looper;

import org.xutils.common.util.LogUtil;
import org.xutils.x;

import java.util.ArrayList;

import static android.R.attr.x;

public class BaseApplication extends Application {
    private static BaseApplication application;
    public static boolean startFromSplash = false;
    private static ArrayList<BaseActivity> activities = new ArrayList<>();
    public static BaseActivity currentActivity;
    public static DeviceManager deviceManager;
    public static String HUB_MAC = "";//CC:1B:E0:E0:18:FC

    public static BaseApplication getApplication() {
        return application;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        x.Ext.init(this);
        x.Ext.setDebug(BuildConfig.DEBUG);
        LogUtil.e("enter application on create");

        application = this;
        deviceManager = DeviceManager.getInstance(this);
        HUB_MAC = deviceManager.loadPref("hub_mac");
        LogUtil.d("hub mac : " + HUB_MAC);
//        HUB_MAC = "CC:1B:E0:E0:18:FC";//
    }


    /**
     * 添加Activity到ArrayList<Activity>管理集合
     *
     * @param activity activity
     */
    public void addActivity(BaseActivity activity) {
        String className = activity.getClass().getName();
        for (BaseActivity at : activities) {
            if (className.equals(at.getClass().getName())) {
                activities.remove(at);
                break;
            }
        }
        currentActivity = activity;
        activities.add(activity);
    }


    /**
     * 退出应用程序的时候，手动调用
     */
    @Override
    public void onTerminate() {
        super.onTerminate();
        for (BaseActivity activity : activities) {
            activity.defaultFinish();
        }
    }


    public static void runOnMain(Runnable r) {
        Looper looper = Looper.getMainLooper();
        Handler handler = new Handler(looper);
        handler.post(r);
    }


    public BaseActivity getCurrentActivity() {
        LogUtil.d("currentActivity" + currentActivity);
        return currentActivity;
    }


}
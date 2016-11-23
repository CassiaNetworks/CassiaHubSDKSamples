package com.cassianetworks.fall;

import android.app.Application;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;

import com.cassianetworks.sdklibrary.Indicator;

import org.xutils.x;

public class BaseApplication extends Application {
    private static BaseApplication application;
    public static DeviceManager deviceManager;
    public static String HUB_MAC = "CC:1B:E0:E0:05:B8";
    public Intent scanIntent;
    public static Indicator indicator;

    public static BaseApplication getApplication() {
        return application;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        x.Ext.init(this);
        x.Ext.setDebug(BuildConfig.DEBUG);

        application = this;
        deviceManager = DeviceManager.getInstance(this);
        indicator = new Indicator(this, HUB_MAC);
        indicator.setDebug(true);
        indicator.startService();

//        scanIntent = new Intent(this, ScanService.class);
//        startService(scanIntent);

    }

    public static void runOnMain(Runnable r) {
        Looper looper = Looper.getMainLooper();
        Handler handler = new Handler(looper);
        handler.post(r);
    }


}
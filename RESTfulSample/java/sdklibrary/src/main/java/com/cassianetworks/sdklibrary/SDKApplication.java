package com.cassianetworks.sdklibrary;

import android.app.Application;

import org.xutils.BuildConfig;
import org.xutils.x;

public class SDKApplication extends Application {


    @Override
    public void onCreate() {
        super.onCreate();
        x.Ext.init(this);
        x.Ext.setDebug(BuildConfig.DEBUG);
    }

}
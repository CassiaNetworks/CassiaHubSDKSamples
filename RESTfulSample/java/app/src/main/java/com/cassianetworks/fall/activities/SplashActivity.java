package com.cassianetworks.fall.activities;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;

import com.cassianetworks.fall.BaseActivity;
import com.cassianetworks.fall.BaseApplication;
import com.cassianetworks.fall.R;
import com.cassianetworks.fall.domain.Device;
import com.cassianetworks.sdklibrary.Callback;
import com.cassianetworks.sdklibrary.HttpUtils;
import com.cassianetworks.sdklibrary.SDKService;
import com.google.gson.Gson;
import com.google.gson.internal.LinkedTreeMap;

import org.xutils.common.util.LogUtil;
import org.xutils.view.annotation.ContentView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static com.cassianetworks.fall.BaseApplication.deviceManager;
import static com.cassianetworks.fall.BaseApplication.indicator;

@ContentView(R.layout.activity_splash)
public class SplashActivity extends BaseActivity {
    final Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            openMain(msg.what);
            return false;
        }
    });

    @Override
    protected void init() {
        LogUtil.d("Splash init");
    }


    @Override
    protected void onResume() {
        super.onResume();
        getConnectList();
        deviceManager.clearDevList();
        indicator.oauth("tester", "10b83f9a2e823c47", new Callback<Integer>() {
            @Override
            public void run(Integer value) {
                LogUtil.d(" oauth " + value);
                if (value == 1) {
                    LogUtil.d("oauth success");
                    List<Device> devList = deviceManager.getDevList();
                    devList.clear();

                    List<Device> devices = deviceManager.loadDeviceListPref();
                    if (devices != null)
                        devList.addAll(devices);
                }
                handler.sendEmptyMessageDelayed(deviceManager.getDevList().size(), 3 * 1000);
            }
        });
//        HttpUtils.oauth("tester", "10b83f9a2e823c47");






    }

    private void getConnectList() {
        indicator.connectList( new Callback<String>() {
            @Override
            public void run(String value) {
                if (TextUtils.isEmpty(value)) {
                    LogUtil.d("connectList fail");
                } else {
                    LogUtil.d("connectList success value = " + value);
                    HashMap ret = new Gson().fromJson(value, HashMap.class);
                    ArrayList nodes = (ArrayList) ret.get("nodes");
                    for (int i = 0; i < nodes.size(); i++) {
                        LinkedTreeMap<String, Object> map = (LinkedTreeMap<String, Object>) nodes.get(i);
                        String handle = (String) map.get("handle");
                        String id = (String) map.get("id");
                        String connectionState = (String) map.get("connectionState");
                        Device dev = new Device();
                        dev.setBdaddr(id);
                        dev.setName(id);
                        LogUtil.d("deviceManager.device size" + dev.toString());

                    }


                }
            }
        });
    }


    private void openMain(int type) {
        Bundle bundle = new Bundle();
        bundle.putInt("type", type);
        startActivity(MainActivity.class, bundle);
        finish();
    }
}

package com.cassianetworks.fall.activities;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;

import com.cassianetworks.fall.BaseActivity;
import com.cassianetworks.fall.BaseApplication;
import com.cassianetworks.fall.R;
import com.cassianetworks.fall.domain.Device;
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
        deviceManager.clearDevList();
        indicator.oauth("tester", "10b83f9a2e823c47", new SDKService.Callback<Integer>() {
            @Override
            public void run(Integer value) {
                LogUtil.d("splashLog " + value);
                if (value == 1) {
                    List<Device> devList = deviceManager.getDevList();
                    devList.clear();

                    List<Device> devices = deviceManager.loadDeviceListPref();
                    if (devices != null)
                        devList.addAll(devices);
//                    getConnectList();
//                    indicator.scan("CC:1B:E0:E0:05:B8", 1000, "CassiaFD_1.2");
                }
                handler.sendEmptyMessageDelayed(deviceManager.getDevList().size(), 3 * 1000);
            }
        });






    }

    private void getConnectList() {
        indicator.connectList( new SDKService.Callback<String>() {
            @Override
            public void run(String value) {
                if (TextUtils.isEmpty(value)) {
                    LogUtil.d("connectList fail");
                    handler.sendEmptyMessageDelayed(deviceManager.getDevList().size(), 3 * 1000);
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

                        deviceManager.addDevice(dev);
                        LogUtil.d("deviceManager.device size" + deviceManager.getDevList().size());
//                        LogUtil.d(" i =" + i + " handle=" + handle + " id=" + id + " connectionState=" + connectionState);

                    }
                    handler.sendEmptyMessageDelayed(deviceManager.getDevList().size(), 3 * 1000);


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

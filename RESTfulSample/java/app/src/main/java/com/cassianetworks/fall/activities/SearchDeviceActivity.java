package com.cassianetworks.fall.activities;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.cassianetworks.fall.BaseActivity;
import com.cassianetworks.fall.R;
import com.cassianetworks.fall.domain.Callback;
import com.cassianetworks.fall.domain.Device;
import com.cassianetworks.fall.domain.DeviceHandle;
import com.cassianetworks.fall.views.MyListView;
import com.cassianetworks.sdklibrary.SDKService;
import com.google.gson.Gson;
import com.google.gson.internal.LinkedTreeMap;

import org.xutils.common.util.LogUtil;
import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.ViewInject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Observable;
import java.util.Observer;

import static com.cassianetworks.fall.BaseApplication.deviceManager;
import static com.cassianetworks.fall.BaseApplication.indicator;

@ContentView(R.layout.activity_search_device)
public class SearchDeviceActivity extends BaseActivity {
    @ViewInject(R.id.tv_page_name)
    TextView tvPageName;
    private Observer observer;
    Device device;
    @ViewInject(R.id.lv_scan_result)
    MyListView lvScanResult;

    @Override
    protected void init() {

        tvPageName.setText(R.string.search_device_result);
        final ScanResultAdapter adapter = new ScanResultAdapter();
        lvScanResult.setAdapter(adapter);
        deviceManager.clearScanDevList();
        observer = new Observer() {
            @Override
            public void update(Observable observable, final Object data) {
                Intent intent = (Intent) data;
                final String action = intent.getAction();
                switch (action) {
                    case SDKService.ACTION_DEVICE_FOUND:
                        String name = intent.getStringExtra("name");
                        String addr = intent.getStringExtra("addr");
                        double rssi = intent.getDoubleExtra("rssi", 0);
                        String scanData = intent.getStringExtra("scanData");
                        device = new Device(name, addr, rssi, scanData);
                        deviceManager.addScanDevice(device);
                        adapter.notifyDataSetChanged();
                        break;
                    default:
                        break;

                }
            }

        };
        indicator.scan(10000, "CassiaFD_1.2");
    }

    @Override
    protected void onResume() {
        super.onResume();
        addObserver();
    }

    @Override
    protected void onPause() {
        super.onPause();
        removeObserver();
    }


    public void removeObserver() {
        LogUtil.d("removeObserver");
        if (observer != null)
            indicator.removeObserver(observer);
    }

    public void addObserver() {
        LogUtil.d("addObserver");
        if (observer != null)
            indicator.addObserver(observer);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        removeObserver();
        indicator.stopScan();
    }

    private class ScanResultAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return deviceManager.getScanDevList().size();
        }

        @Override
        public Device getItem(int position) {
            return deviceManager.getScanDevList().get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }


        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {

            LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            if (convertView == null)
                convertView = inflater.inflate(R.layout.item_lv_search_device_list, parent, false);
            final TextView tvDeviceName = (TextView) convertView.findViewById(R.id.tv_name);
            final TextView tvDeviceMac = (TextView) convertView.findViewById(R.id.tv_mac);
            final TextView tvDeviceRssi = (TextView) convertView.findViewById(R.id.tv_rssi);
            final View ivAdd = convertView.findViewById(R.id.iv_add);
            final Device device = getItem(position);
            final String mac = device.getBdaddr();
            tvDeviceName.setText(device.getName());
            tvDeviceMac.setText(mac);
            tvDeviceRssi.setText(device.getRssi() + "");
            ivAdd.setSelected(false);

            ivAdd.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    indicator.connect(mac, new SDKService.Callback<Integer>() {
                        @Override
                        public void run(Integer value) {
                            LogUtil.d("test search connect value" + value);
                            if (value == 1) {

                                indicator.discoverServices(mac, new SDKService.Callback<String>() {
                                    @Override
                                    public void run(String value) {
                                        if (!TextUtils.isEmpty(value)) {


                                            LogUtil.d("test discover service data " + value);
                                            HashMap ret = new Gson().fromJson(value, HashMap.class);
                                            ArrayList services = (ArrayList) ret.get("services");
                                            for (int i = 0; i < services.size(); i++) {
                                                LinkedTreeMap<String, Object> map = (LinkedTreeMap<String, Object>) services.get(i);
                                                int handle = ((Double) map.get("handle")).intValue();
                                                String uuid = (String) map.get("uuid");
                                                LogUtil.d(" i =" + i + " handle=" + handle + " uuid=" + uuid);
                                                device.getHandleList().add(new DeviceHandle(handle, uuid));
                                            }
                                            runOnUiThread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    deviceManager.refreshDevicesList(true, device, new Callback<Integer>() {
                                                        @Override
                                                        public void run(Integer value) {
                                                        }
                                                    });
                                                }
                                            });

                                        } else {
                                            ivAdd.setSelected(!ivAdd.isSelected());
                                        }

                                    }
                                });
                            } else {
                                LogUtil.d("test connect device fail value" + value);
                            }
                        }
                    });

                    ivAdd.setSelected(!ivAdd.isSelected());
                }
            });
            return convertView;
        }


    }

}

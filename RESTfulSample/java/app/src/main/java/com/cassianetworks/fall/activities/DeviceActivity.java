package com.cassianetworks.fall.activities;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.cassianetworks.fall.BaseActivity;
import com.cassianetworks.fall.R;
import com.cassianetworks.fall.domain.Device;
import com.cassianetworks.fall.domain.DeviceHandle;
import com.cassianetworks.fall.utils.DialogUtils;
import com.cassianetworks.fall.views.MyListView;
import com.cassianetworks.sdklibrary.Callback;

import org.xutils.common.util.LogUtil;
import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import static com.cassianetworks.fall.BaseApplication.deviceManager;
import static com.cassianetworks.fall.BaseApplication.indicator;

@ContentView(R.layout.activity_device)
public class DeviceActivity extends BaseActivity {

    @ViewInject(R.id.tv_mac)
    TextView tvMac;
    @ViewInject(R.id.tv_battery)
    TextView tvBattery;
    @ViewInject(R.id.iv_icon)
    ImageView ivIcon;
    @ViewInject(R.id.lv_device_handle)
    MyListView lv_device_handle;

    private Device device;
    private String deviceBdaddr;
    private HandleAdpter adapter;

    @Event(value = {R.id.iv_left, R.id.iv_right, R.id.tv_del, R.id.tv_record})
    private void getEvent(View v) {
        switch (v.getId()) {
            case R.id.iv_left:
                finish();
                break;
            case R.id.iv_right:
                break;
            case R.id.tv_del:
                DialogUtils.showDefaultYNTipDialog(this, getString(R.string.del_device_dialog_msg), new Runnable() {
                    @Override
                    public void run() {
                        indicator.disconnect(deviceBdaddr, new Callback<Integer>() {
                            @Override
                            public void run(Integer value) {
                                if (value == 1) {
                                    LogUtil.d("disconnect device success");
                                    deviceManager.delDevice(device, new Callback<Integer>() {
                                        @Override
                                        public void run(Integer value) {
                                            if (value == -1) {
                                                showTips(R.string.del_device_fail);
                                            } else {
                                                if (MainActivity.instance != null)
                                                    MainActivity.instance.finish();
                                                startActivity(MainActivity.class);
                                                finish();

                                            }
                                        }
                                    });
                                } else {
                                    LogUtil.d("disconnect device fail");

                                }
                            }
                        });


                    }
                });
                break;
            case R.id.tv_record:
                break;
            default:
                break;
        }
    }

    private void initDeviceInfoView() {
        tvMac.setText(deviceBdaddr + "");
        ivIcon.setImageResource(R.mipmap.device_icon);
        tvBattery.setText("电量高");



    }

    @Override
    protected void init() {
        deviceBdaddr = getIntent().getStringExtra("device_bdaddr");
        device = deviceManager.getDevice(deviceBdaddr);
        if (device == null) {
            startActivity(SearchDeviceActivity.class);
            finish();
            return;
        }
        initDeviceInfoView();
        adapter = new HandleAdpter();
        lv_device_handle.setAdapter(adapter);
        lv_device_handle.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                indicator.writeHandle(device.getBdaddr(), adapter.getItem(position).handle, "0101", new Callback<String>() {
                    @Override
                    public void run(String value) {
                        LogUtil.d("writeHandle value"+value);
                    }
                });

            }
        });
    }


    private class HandleAdpter extends BaseAdapter {

        @Override
        public int getCount() {
            return device.getHandleList() == null ? 0 : device.getHandleList().size();

        }

        @Override
        public DeviceHandle getItem(int position) {
            return device.getHandleList().get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }


        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            HandleViewHolder holder;

            LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            if (convertView == null) {
                convertView = inflater.inflate(R.layout.item_lv_device_handle_list, parent, false);
                holder = new HandleViewHolder();
                x.view().inject(holder, convertView);
                convertView.setTag(holder);
            } else {
                holder = (HandleViewHolder) convertView.getTag();
            }
            DeviceHandle device = getItem(position);
            holder.tv_uuid.setText(String.format(getString(R.string.uuid), device.uuid));
            holder.tv_handle.setText(String.format(getString(R.string.handle), device.handle));
            holder.tv_properties.setText(String.format(getString(R.string.properties), device.properties));
            holder.tv_valueHandle.setText(String.format(getString(R.string.valuehandle), device.valueHandle));
            return convertView;
        }


    }

    class HandleViewHolder {
        @ViewInject(R.id.tv_handle)
        private TextView tv_handle;
        @ViewInject(R.id.tv_uuid)
        private TextView tv_uuid;
        @ViewInject(R.id.tv_properties)
        private TextView tv_properties;
        @ViewInject(R.id.tv_valueHandle)
        private TextView tv_valueHandle;
    }
}

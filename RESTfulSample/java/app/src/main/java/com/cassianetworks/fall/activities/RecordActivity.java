package com.cassianetworks.fall.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.cassianetworks.fall.BaseActivity;
import com.cassianetworks.fall.IndicatorService;
import com.cassianetworks.fall.R;
import com.cassianetworks.fall.RecordAdapter;
import com.cassianetworks.fall.domain.Record;
import com.cassianetworks.fall.utils.DialogUtils;
import com.cassianetworks.fall.utils.SysUtils;

import org.xutils.common.util.LogUtil;
import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

import static com.cassianetworks.fall.BaseApplication.deviceManager;

@ContentView(R.layout.activity_record)
public class RecordActivity extends BaseActivity {
    Comparator<Record> comparator;
    private List<Record> recordsData;
    private String deviceMac;
    private RecordAdapter adapter;
    @ViewInject(R.id.tv_page_name)
    TextView tv_page_name;
    @ViewInject(R.id.lv_record)
    ListView lvRecord;
    private Handler handler;
    private Observer observer;


    @Override
    protected void init() {
        handler = new Handler();
        comparator = new RecordTimeLowToHighComparator();
        Bundle bundle = getIntent().getExtras();
        deviceMac = bundle.getString("device_mac");
        tv_page_name.setText(R.string.show_record);
        adapter = new RecordAdapter(this);
        lvRecord.setAdapter(adapter);
        observer = new Observer() {
            @Override
            public void update(Observable observable, final Object data) {
                Intent intent = (Intent) data;
                final String action = intent.getAction();
                LogUtil.d("RecordActivity receive broadcast " + action);
                switch (action) {
                    case IndicatorService.ACTION_NOTIFICATION_RECEIVE:
                        getData();
                        adapter.refreshData(recordsData);
                        adapter.notifyDataSetChanged();
                        break;
                    default:
                        break;
                }
            }
        };


    }

    @Override
    public void onResume() {
        super.onResume();
        IndicatorService.messenger.addObserver(observer);
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onPause() {
        super.onPause();
        IndicatorService.messenger.deleteObserver(observer);

    }


    @Event(value = {R.id.iv_left, R.id.tv_clear})
    private void getEvent(View v) {
        switch (v.getId()) {
            case R.id.iv_left:
                finish();
                break;
            case R.id.tv_clear:
                DialogUtils.showDefaultYNTipDialog(this, getString(R.string.clear_records_tip), new Runnable() {
                    @Override
                    public void run() {
                        clearRecord();
                    }
                });
                break;
            default:
                break;
        }
    }

    private void clearRecord() {
        deviceManager.clearRecordListPref(deviceMac);
        adapter.refreshData(null);
        adapter.notifyDataSetChanged();

    }

    private void getData() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                recordsData = deviceManager.loadRecordListPref(deviceMac);
                if (recordsData == null) recordsData = new ArrayList<>();
                Collections.sort(recordsData, comparator);
            }
        }).start();

    }


    private class RecordTimeLowToHighComparator implements Comparator<Record> {
        @Override
        public int compare(Record record1, Record record2) {
            long time1L = 0;
            long time2L = 0;
            String time1S = record1.getTime();
            String time2S = record2.getTime();
            if (!TextUtils.isEmpty(time1S)) {
                time1L = SysUtils.getLongDate(time1S);
            }
            if (!TextUtils.isEmpty(time2S)) {
                time2L = SysUtils.getLongDate(time2S);
                SysUtils.getLongDate(time2S);
            }


            if (time1L > time2L) {
                return -1;
            } else if (time1L < time2L) {
                return 1;
            } else {
                return 0;
            }
        }
    }
}

package com.cassianetworks.fall;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;

import com.cassianetworks.fall.domain.Device;
import com.cassianetworks.fall.domain.Record;
import com.cassianetworks.fall.utils.SysUtils;
import com.cassianetworks.sdklibrary.Indicator;
import com.google.gson.Gson;

import org.xutils.common.util.LogUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Observable;

import static com.cassianetworks.fall.BaseApplication.deviceManager;
import static com.cassianetworks.fall.BaseApplication.indicator;

public class IndicatorService extends Service {
    public final static String ACTION_NOTIFICATION_RECEIVE = "com.cassianetworks.fall.ACTION_NOTIFICATION_RECEIVE";
    private MyBinder binder = new MyBinder();
    public static Messenger messenger = new Messenger();

    public IndicatorService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
        getNotification();

    }

    @Override
    public void onRebind(Intent intent) {
        super.onRebind(intent);
        getNotification();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }


    public void broadcastUpdate(Intent intent) {
        messenger.broadcast(intent);
    }

    public class MyBinder extends Binder {


    }

    public static class Messenger extends Observable {
        public void broadcast(final Object arg) {
            Handler handler = new Handler(Looper.getMainLooper());
            handler.post(new Runnable() {
                @Override
                public void run() {
                    setChanged();
                    notifyObservers(arg);
                }
            });
        }
    }

    public void getNotification() {
        indicator.getNotification(new Indicator.Callback<String>() {
            @Override
            public void run(boolean success, String msg) {
                if (success) {
                    LogUtil.e("get Notification success" + msg);
                    if ("".equals(msg) || msg.contains("keep-alive") || msg.contains("retry"))
                        return;
                    HashMap result = new Gson().fromJson(msg.split("data:")[1], HashMap.class);
                    final String value = (String) result.get("value");
                    final String name = (String) result.get("name");
                    final String dataType = (String) result.get("dataType");
                    final String id = (String) result.get("id");
                    Device tem = new Device(name, id);
                    if (!deviceManager.getDevList().contains(tem))
                        return;
                    int handle = ((Double) result.get("handle")).intValue();

                    List<Record> records = deviceManager.loadRecordListPref(id);
                    String time = SysUtils.getCurData();

                    if (records == null) {
                        records = new ArrayList<>();
                        records.add(new Record(0, value, name, dataType, id, handle, time.split(" ")[0] + " 23:59:59"));
                        broadcast(id, records);
                    }
                    if (records.size() > 1) {
                        if (!records.get(records.size() - 1).getTime().split(" ")[0].equals(time.split(" ")[0])) {
                            records.add(new Record(0, value, name, dataType, id, handle, time.split(" ")[0] + " 23:59:59"));
                            broadcast(id, records);
                        }

                    }

                    if (!records.get(records.size() - 1).getValue().equals(value)) {
                        records.add(new Record(1, value, name, dataType, id, handle, time));
                        broadcast(id, records);
                    }

                } else {
                    LogUtil.e("get Notification fail" + msg);
                }
            }
        });
//        indicator.getNotification(new HttpUtils.OkHttpCallback() {
//            @Override
//            protected void onSuccess(Response response) {
//                LogUtil.e("get Notification success");
//                Reader charStream = response.body().charStream();
//                BufferedReader in = new BufferedReader(charStream);
//                String line;
//
//                try {
//                    while ((line = in.readLine()) != null) {
//                        if ("".equals(line) || line.contains("keep-alive") || line.contains("retry"))
//                            continue;
//                        HashMap result = new Gson().fromJson(line.split("data:")[1], HashMap.class);
//                        final String value = (String) result.get("value");
//                        final String name = (String) result.get("name");
//                        final String dataType = (String) result.get("dataType");
//                        final String id = (String) result.get("id");
//                        Device tem = new Device(name, id);
//                        if (!deviceManager.getDevList().contains(tem))
//                            continue;
//                        int handle = ((Double) result.get("handle")).intValue();
//
//                        List<Record> records = BaseApplication.deviceManager.loadRecordListPref(id);
//                        String time = SysUtils.getCurData();
//
//                        if (records == null) {
//                            records = new ArrayList<>();
//                            records.add(new Record(0, value, name, dataType, id, handle, time.split(" ")[0] + " 23:59:59"));
//                            broadcast(id, records);
//                        }
//                        if (records.size() > 1) {
//                            if (!records.get(records.size() - 1).getTime().split(" ")[0].equals(time.split(" ")[0])) {
//                                records.add(new Record(0, value, name, dataType, id, handle, time.split(" ")[0] + " 23:59:59"));
//                                broadcast(id, records);
//                            }
//
//                        }
//
//                        if (!records.get(records.size() - 1).getValue().equals(value)) {
//                            records.add(new Record(1, value, name, dataType, id, handle, time));
//                            broadcast(id, records);
//                        }
//
//
//                    }
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//
//            }
//
//            @Override
//            protected void onFailure(String msg) {
//                LogUtil.e("get Notification fail" + msg);
//            }
//        });
    }

    private void broadcast(String id, List<Record> records) {
        deviceManager.saveRecordListPref(id, records);
        broadcastUpdate(new Intent(ACTION_NOTIFICATION_RECEIVE));
    }

}

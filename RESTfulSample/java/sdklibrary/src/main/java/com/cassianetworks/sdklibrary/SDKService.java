package com.cassianetworks.sdklibrary;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.text.TextUtils;

import com.cassianetworks.sdklibrary.HttpUtils.OkHttpCallback;
import com.google.gson.Gson;
import com.google.gson.internal.LinkedTreeMap;

import org.xutils.common.util.LogUtil;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Observable;

import okhttp3.Response;

public class SDKService extends Service {
    public final static String ACTION_DEVICE_FOUND = "com.cassianetworks.ble.ACTION_DEVICE_FOUND";
    private MyBinder binder = new MyBinder();
    public static Messenger messenger = new Messenger();
    public boolean scanning = false;


    public void connect(String mac, String chip, final Callback<Integer> callback) {
        HttpUtils.connect(mac, chip, new OkHttpCallback() {
            @Override
            void onSuccess(Response response) {
                LogUtil.d("--connect success ");
                callback.run(1);
            }

            @Override
            void onFailure(String msg) {
                LogUtil.d("--connect fail " + msg);
                callback.run(0);
            }
        });
    }

    public void writeHandle(String mac, int handle, String value, final Callback<String> callback) {
        HttpUtils.writeHandle(mac, handle, value, new OkHttpCallback() {
            @Override
            void onSuccess(Response response) {
                LogUtil.d("--writeHandle success ");
                callback.run("1");
            }

            @Override
            void onFailure(String msg) {
                LogUtil.d("--writeHandle fail " + msg);
                callback.run("0");
            }
        });
    }

    public void connectList(String connection_state, final Callback<String> callback) {
        HttpUtils.connectList(connection_state, new OkHttpCallback() {
            @Override
            void onSuccess(final Response response) {
                LogUtil.d("--connectList success ");
                formatResponse(response, callback);
            }

            @Override
            void onFailure(String msg) {
                LogUtil.d("--connectList fail " + msg);
                callback.run("");
            }
        });
    }


    public void disconnect(String mac, final Callback<Integer> callback) {
        HttpUtils.disconnect(mac, new OkHttpCallback() {
            @Override
            void onSuccess(Response response) {
                LogUtil.d("--disconnect device success ");
                callback.run(1);
            }

            @Override
            void onFailure(String msg) {
                LogUtil.d("--disconnect device fail " + msg);
                callback.run(0);
            }
        });
    }

    public void discoverServices(String mac, final Callback<String> callback) {
        HttpUtils.discoverServices(mac, new OkHttpCallback() {
            @Override
            void onSuccess(final Response response) {
                LogUtil.d("--discoverServices success ");
                formatResponse(response, callback);
            }

            @Override
            void onFailure(String msg) {
                LogUtil.d("--discoverServices fail " + msg);
                callback.run("");
            }
        });
    }

    public void oauth(final Callback<Integer> callback, String developer, String pwd) {
        HttpUtils.oauth(new HttpCallBack<String>() {
            @Override
            public void onFailed(String result) {
                LogUtil.d("err:" + result);
                callback.run(0);

            }

            @Override
            public void onSuccess(HashMap result) {
                HttpUtils.access_token = (String) result.get("access_token");
                callback.run(1);

            }
        }, developer, pwd);

    }


    public void scan(int milliseconds, final String key) {

        LogUtil.d("service start scan...");
        if (milliseconds != -1) {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    LogUtil.d("postDelayed stop scan...");
                    stopScan();
                }
            }, milliseconds);
        }

        HttpUtils.scan(new OkHttpCallback() {
                           @Override
                           public void onSuccess(final Response response) {
                               new Thread(new Runnable() {
                                   @Override
                                   public void run() {

                                       Reader charStream = response.body().charStream();
                                       BufferedReader in = new BufferedReader(charStream);
                                       String line;

                                       try {
                                           while ((line = in.readLine()) != null) {
                                               scanning = true;
                                               if (line.contains(key) || TextUtils.isEmpty(key)) {
                                                   HashMap result = new Gson().fromJson(line.split("data:")[1], HashMap.class);
                                                   Intent intent = new Intent(ACTION_DEVICE_FOUND);
                                                   LinkedTreeMap bdaddrs = (LinkedTreeMap) ((ArrayList) result.get("bdaddrs")).get(0);
                                                   String bdaddr = (String) bdaddrs.get("bdaddr");
                                                   String scanData = (String) result.get("scanData");
                                                   String name = (String) result.get("name");
                                                   double rssi = (double) result.get("rssi");

                                                   intent.putExtra("name", name);
                                                   intent.putExtra("addr", bdaddr);
                                                   intent.putExtra("rssi", rssi);
                                                   intent.putExtra("scanData", scanData);
                                                   broadcastUpdate(intent);
                                               }
                                           }
                                       } catch (IOException e) {
                                           scanning = false;
                                           e.printStackTrace();
                                       }
                                   }
                               }).start();


                           }

                           @Override
                           public void onFailure(String msg) {
                               scanning = false;

                           }
                       }

        );

    }

    public void stopScan() {
        LogUtil.d("stop scan");
        if (scanning) {
            scanning = false;
            HttpUtils.removeRequest();
        }

    }


    private static class SingleTonHolder {
        private static final SDKService INSTANCE = new SDKService();
    }

    public static SDKService getInstance() {
        return SingleTonHolder.INSTANCE;
    }

    public SDKService() {

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
        void broadcast(final Object arg) {
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


    public void close() {
    }

    public interface Callback<T> {
        void run(T value);
    }

    private void formatResponse(final Response response, final Callback<String> callback) {
        new Thread(new Runnable() {
            @Override
            public void run() {

                Reader charStream = response.body().charStream();
                BufferedReader in = new BufferedReader(charStream);
                String line;
                try {
                    while ((line = in.readLine()) != null) {
                        callback.run(line);
                    }
                } catch (IOException e) {
                    scanning = false;
                    e.printStackTrace();
                } finally {
                    response.close();
                    try {
                        in.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    try {
                        charStream.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }
}

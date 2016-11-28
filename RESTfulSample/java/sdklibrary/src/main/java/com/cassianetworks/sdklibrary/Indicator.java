package com.cassianetworks.sdklibrary;

import android.util.Log;

import com.cassianetworks.sdklibrary.HttpUtils.OkHttpCallback;
import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.Reader;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

import okhttp3.Call;
import okhttp3.Response;

public class Indicator {
    public static final String CONNECT_TYPE_PUBLIC = "public";
    public static final String CONNECT_TYPE_RANDOM = "random";
    private static boolean debug = true;
    private final static String TAG = "CassiaIndicator";
    private Call scanCall = null;
    private Call notificationCall = null;
    private String hubMac = "";

    public Indicator(String hubMac) throws Exception {
        if (hubMac == null || "".equals(hubMac)) {
            throw new Exception("hub mac is invalid");
        }
        this.hubMac = hubMac;

    }

    /**
     * 认证开发者信息
     *
     * @param developer 开发者帐号
     * @param pwd       开发者密码
     * @param callback
     */
    public void oauth(String developer, String pwd, final Callback<String> callback) {
        HttpUtils.getInstance().oauth(developer, pwd, new OkHttpCallback() {
            @Override
            protected void onSuccess(Call call, Response response) {
                formatResponse(response, callback);
                Reader charStream = response.body().charStream();
                BufferedReader in = new BufferedReader(charStream);
                String line;
                try {
                    while ((line = in.readLine()) != null) {
                        HashMap result = new Gson().fromJson(line, HashMap.class);
                        HttpUtils.getInstance().setAccess_token((String) result.get("access_token"));
                        callback.run(true, "ok");
                    }
                } catch (Exception e) {
                    callback.run(false, e.getMessage());
                    e.printStackTrace();
                }
            }

            @Override
            protected void onFailure(String msg) {
                callback.run(false, msg);
            }
        });
    }

    /**
     * 连接设备
     *
     * @param type     连接的类型 CONNECT_TYPE_PUBLIC/CONNECT_TYPE_RANDOM
     * @param mac      设备的MAC地址
     * @param chip     HUB的芯片 {0,1}
     * @param callback
     */
    public void connect(String type, String mac, String chip, final Callback<String> callback) throws Exception {
        if (!CONNECT_TYPE_PUBLIC.equals(type) || !CONNECT_TYPE_RANDOM.equals(type)) {
            throw new Exception("type is invalid");
        }
        HttpUtils.getInstance().connect(hubMac,type, mac, chip, new OkHttpCallback() {
            @Override
            protected void onSuccess(Call call, Response response) {
                log("--connect success ");
                callback.run(true, "ok");
            }

            @Override
            public void onFailure(String msg) {
                log("--connect fail " + msg);
                callback.run(false, msg);
            }
        });
    }

    /**
     * 在芯片1上连接设备
     *
     * @param type     连接的类型 CONNECT_TYPE_PUBLIC/CONNECT_TYPE_RANDOM
     * @param mac      设备的MAC地址
     * @param callback
     */
    public void connect(String type, String mac, final Callback<String> callback) throws Exception {
        connect(type, mac, "1", callback);
    }

    /**
     * 写入Handle
     *
     * @param mac      设备的MAC地址
     * @param handle   handle的id
     * @param value    handle的值
     * @param callback
     */
    public void writeHandle(String mac, int handle, String value, final Callback<String> callback) {
        HttpUtils.getInstance().writeHandle(hubMac,mac, handle, value, new OkHttpCallback() {
            @Override
            protected void onSuccess(Call call, Response response) {
                log("--writeHandle success ");
                callback.run(true, "ok");
            }

            @Override
            public void onFailure(String msg) {
                log("--writeHandle fail " + msg);
                callback.run(false, msg);
            }
        });
    }

    /**
     * 获取HUB指定连接状态的设备集合
     *
     * @param connection_state 设备的连接状态 {connected:已连接,disconnected:未连接}
     * @param callback
     */
    public void connectList(String connection_state, final Callback<String> callback) {
        HttpUtils.getInstance().connectList(hubMac,connection_state, new OkHttpCallback() {
            @Override
            protected void onSuccess(Call call, final Response response) {
                log("--connectList success ");
                formatResponse(response, callback);
            }

            @Override
            public void onFailure(String msg) {
                log("--connectList fail " + msg);
                callback.run(true, msg);
            }
        });
    }

    /**
     * 获取hub已连接设备列表
     *
     * @param callback
     */
    public void connectList(final Callback<String> callback) {
        connectList("connected", callback);
    }

    /**
     * 解绑设备
     *
     * @param mac      设备地址
     * @param callback
     */

    public void disconnect(String mac, final Callback<String> callback) {
        HttpUtils.getInstance().disconnect(hubMac,mac, new OkHttpCallback() {
            @Override
            protected void onSuccess(Call call, Response response) {
                log("--disconnect device success ");
                callback.run(true, "ok");
            }

            @Override
            public void onFailure(String msg) {
                log("--disconnect device fail " + msg);
                callback.run(false, msg);
            }
        });
    }

    /**
     * 发现设备的服务
     *
     * @param mac      设备的mac地址
     * @param callback
     */
    public void discoverServices(String mac, final Callback<String> callback) {
        HttpUtils.getInstance().discoverServices(hubMac,mac, new OkHttpCallback() {
            @Override
            protected void onSuccess(Call call, final Response response) {
                log("--discoverServices success ");
                formatResponse(response, callback);
            }

            @Override
            public void onFailure(String msg) {
                log("--discoverServices fail " + msg);
                callback.run(false, msg);
            }
        });
    }

    /**
     * 在芯片1上开始扫描
     *
     * @param milliseconds 扫描时间 如果milliseconds == -1 表示不停止扫描
     * @param callback
     */
    public void scan(final int milliseconds, final Callback<String> callback) {
        scan(milliseconds, "1", callback);


    }

    /**
     * 在指定芯片上开始扫描
     *
     * @param milliseconds 扫描时间 如果milliseconds == -1 表示不停止扫描
     * @param chip         芯片id
     * @param callback
     */
    public void scan(final int milliseconds, String chip, final Callback<String> callback) {
        if (milliseconds != -1) {
            new Timer().schedule(new TimerTask() {
                @Override
                public void run() {
                    log("postDelayed stop scan...");
                    stopScan();
                }
            }, milliseconds);
        }

        HttpUtils.getInstance().scan(hubMac,chip, new OkHttpCallback() {
            @Override
            protected void onSuccess(Call call, Response response) {
                scanCall = call;
                formatResponse(response, callback);
                log("service start scan...");
            }

            @Override
            protected void onFailure(String msg) {
                callback.run(false, msg);
            }
        });


    }

    /**
     * 停止扫描
     */
    public void stopScan() {
        HttpUtils.getInstance().removeRequest(scanCall);
    }

    /**
     * 获取通知
     *
     * @param callback
     */
    public void getNotification(final Callback<String> callback) {
        HttpUtils.getInstance().getNotification(hubMac,new OkHttpCallback() {
            @Override
            protected void onSuccess(Call call, Response response) {
                notificationCall = call;
                formatResponse(response, callback);
            }

            @Override
            protected void onFailure(String msg) {
                callback.run(false, msg);
            }
        });

    }

    /**
     * 关闭接收通知
     */
    public void closeNotification() {
        HttpUtils.getInstance().removeRequest(notificationCall);

    }


    /**
     * 重启HUB
     *
     * @param callback
     */
    public void rebootHub(final Callback<String> callback) {
        HttpUtils.getInstance().rebootHub(new OkHttpCallback() {
            @Override
            protected void onSuccess(Call call, Response response) {
                callback.run(true, "ok");
            }

            @Override
            protected void onFailure(String msg) {
                callback.run(false, msg);
            }
        });
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
                        callback.run(true, line);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    callback.run(false, e.getMessage());
                }
            }
        }).start();
    }

    public interface Callback<T> {
        /**
         * @param success true:success;false:fail
         * @param msg     返回的消息
         */
        void run(boolean success, T msg);
    }

    /**
     * 调试模式,默认开启
     *
     * @param d 是否调试模式
     */
    public void setDebug(boolean d) {
        debug = d;
    }

    public static void log(String message) {
        if (debug) {
            Log.d(TAG, message);
        }
    }
}

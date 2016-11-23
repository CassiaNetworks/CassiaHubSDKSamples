package com.cassianetworks.sdklibrary;


import android.util.Log;

import static com.cassianetworks.sdklibrary.HttpUtils.hubMac;


public class Indicator {
    private static boolean debug = true;
    private final static String TAG = "CassiaIndicator";

    /**
     * 初始化Indicator
     *
     * @param mac HUB的mac地址
     */
    public Indicator(String mac) {
        hubMac = mac;
    }

    /**
     * 验证开发者信息
     *
     * @param developer 开发者帐号
     * @param pwd       开发者密码
     * @param callback  {1:success,0:fail}
     */
    public void oauth(String developer, String pwd, final Callback<Integer> callback) {
        HttpUtils.oauth(callback, developer, pwd);
    }

    /**
     * 开始扫描
     *
     * @param milliseconds 扫描时间 如果milliseconds == -1 表示不停止扫描
     */
    public void scan(final int milliseconds, HttpUtils.OkHttpCallback callback) {
        SDKService.getInstance().scan(milliseconds, callback);
    }

    /**
     * 停止扫描
     */
    public void stopScan() {
        SDKService.getInstance().stopScan();
    }


    /**
     * 连接设备
     *
     * @param mac      设备的MAC地址
     * @param chip     HUB的芯片 {0,1}
     * @param callback {1:success,0:fail}
     */
    public void connect(String mac, String chip, final Callback<Integer> callback) {
        SDKService.getInstance().connect(mac, chip, callback);
    }


    /**
     * 在芯片1上连接设备
     *
     * @param mac      设备的MAC地址
     * @param callback {1:success,0:fail}
     */
    public void connect(String mac, final Callback<Integer> callback) {
        connect(mac, "1", callback);
    }

    /**
     * 解绑设备
     *
     * @param mac 设备地址
     */
    public void disconnect(String mac, final Callback<Integer> callback) {
        SDKService.getInstance().disconnect(mac, callback);
    }

    /**
     * 获取HUB指定连接状态的设备集合
     *
     * @param connection_state 设备的连接状态 {connected:已连接,disconnected:未连接}
     * @param callback         返回连接设备的字符串,空串表示没有设备或者是获取失败
     */
    public void connectList(String connection_state, final Callback<String> callback) {
        SDKService.getInstance().connectList(connection_state, callback);
    }

    /**
     * 获取hub已连接设备列表
     *
     * @param callback 返回连接设备的json字符串,空串表示没有设备或者是获取失败
     */
    public void connectList(final Callback<String> callback) {
        connectList("connected", callback);
    }

    /**
     * 发现设备的服务
     *
     * @param mac      设备的mac地址
     * @param callback 设备服务集合的json字符串
     */
    public void discoverServices(String mac, final Callback<String> callback) {
        SDKService.getInstance().discoverServices(mac, callback);
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
        SDKService.getInstance().writeHandle(mac, handle, value, callback);

    }

    public void getNotification(String mac, int handle, String value, final Callback<String> callback) {
        SDKService.getInstance().writeHandle(mac, handle, value, callback);
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

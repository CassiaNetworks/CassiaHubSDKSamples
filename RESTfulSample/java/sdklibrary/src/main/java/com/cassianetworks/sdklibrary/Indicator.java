package com.cassianetworks.sdklibrary;


import android.util.Log;


public class Indicator {
    private static boolean debug = true;
    private final static String TAG = "CassiaIndicator";

    /**
     * 初始化Indicator
     *
     * @param mac HUB的mac地址
     */
    public Indicator(String mac) {
        HttpUtils.getInstance().setHubMac(mac);
    }

    /**
     * 认证开发者信息
     *
     * @param developer 开发者帐号
     * @param pwd       开发者密码
     * @param callback  {"ok":认证成功;"err:错误信息":认证失败}
     */
    public void oauth(String developer, String pwd, final Callback<String> callback) {
        HttpUtils.getInstance().oauth(callback, developer, pwd);
    }

    /**
     * 开始扫描
     *
     * @param milliseconds 扫描时间 如果milliseconds == -1 表示不停止扫描
     * @param callback     扫描的返回结果
     */
    public void scan(final int milliseconds, HttpUtils.OkHttpCallback callback) {
        SDKService.getInstance().scan(milliseconds, callback);
    }

    /**
     * 停止扫描
     */
    public void stopScan() {
        HttpUtils.getInstance().removeRequest();
    }


    /**
     * 连接设备
     *
     * @param mac      设备的MAC地址
     * @param chip     HUB的芯片 {0,1}
     * @param callback {"ok":连接成功;"err:错误信息":连接失败}
     */
    public void connect(String mac, String chip, final Callback<String> callback) {
        SDKService.getInstance().connect(mac, chip, callback);
    }


    /**
     * 在芯片1上连接设备
     *
     * @param mac      设备的MAC地址
     * @param callback {"ok":连接成功;"err:错误信息":连接失败}
     */
    public void connect(String mac, final Callback<String> callback) {
        connect(mac, "1", callback);
    }

    /**
     * 解绑设备
     *
     * @param mac      设备地址
     * @param callback {"ok":解绑成功;"err:错误信息":解绑失败}
     */
    public void disconnect(String mac, final Callback<String> callback) {
        SDKService.getInstance().disconnect(mac, callback);
    }

    /**
     * 获取HUB指定连接状态的设备集合
     *
     * @param connection_state 设备的连接状态 {connected:已连接,disconnected:未连接}
     * @param callback         {"设备列表":获取列表的json串;"err:错误信息":获取列表失败}
     */
    public void connectList(String connection_state, final Callback<String> callback) {
        SDKService.getInstance().connectList(connection_state, callback);
    }

    /**
     * 获取hub已连接设备列表
     *
     * @param callback {"设备列表":获取列表的json串;"err:错误信息":获取列表失败}
     */
    public void connectList(final Callback<String> callback) {
        connectList("connected", callback);
    }

    /**
     * 发现设备的服务
     *
     * @param mac      设备的mac地址
     * @param callback {"服务列表":设备服务集合的json字符串;"err:错误信息":获取服务失败}
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
     * @param callback {"ok":写入成功;"err:错误信息":写入失败}
     */
    public void writeHandle(String mac, int handle, String value, final Callback<String> callback) {
        SDKService.getInstance().writeHandle(mac, handle, value, callback);

    }

    public void getNotification(HttpUtils.OkHttpCallback callback) {
        HttpUtils.getInstance().getNotification(callback);

    }

    /**
     * 关闭接收通知
     */
    public void closeNotification() {
        HttpUtils.getInstance().removeRequest();

    }

    /**
     * 重启HUB
     *
     * @param callback {"ok":重启成功;"err:错误信息":重启失败}
     */
    public void rebootHub(Callback<String> callback) {
        SDKService.getInstance().rebootHub(callback);
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

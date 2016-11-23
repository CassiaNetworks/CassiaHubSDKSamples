package com.cassianetworks.sdklibrary;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;

import org.xutils.x;

import java.util.Observer;

import static com.cassianetworks.sdklibrary.HttpUtils.hubMac;


public class Indicator {
    private Context context;

    /**
     * 初始化Indicator
     *
     * @param context 上下文
     * @param mac     HUB的mac地址
     */
    public Indicator(Context context, @NonNull String mac) {
        this.context = context;
        hubMac = mac;
    }

    /**
     * 设置debug模式
     *
     * @param debug
     */
    public void setDebug(boolean debug) {
        x.Ext.setDebug(debug);
    }

    /**
     * 验证开发者信息
     *
     * @param developer 开发者帐号
     * @param pwd       开发者密码
     * @param callback  {1:success,0:fail}
     */
    public void oauth(@NonNull String developer, @NonNull String pwd, final SDKService.Callback<Integer> callback) {
        SDKService.getInstance().oauth(callback, developer, pwd);
    }


    /**
     * 获取hub当前扫描的状态
     *
     * @return {true:正在扫描 false:没有扫描}
     */
    public boolean getScanFlag() {
        return SDKService.getInstance().scanning;
    }

    /**
     * 开始扫描
     *
     * @param milliseconds 扫描时间 如果milliseconds == -1 表示不停止扫描
     * @param key          筛选数据的条件
     */
    public void scan(final int milliseconds, final String key) {
        SDKService.getInstance().scan(milliseconds, key);
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
    public void connect(String mac, String chip, final SDKService.Callback<Integer> callback) {
        SDKService.getInstance().connect(mac, chip, callback);
    }


    /**
     * 在芯片1上连接设备
     *
     * @param mac      设备的MAC地址
     * @param callback {1:success,0:fail}
     */
    public void connect(String mac, final SDKService.Callback<Integer> callback) {
        connect(mac, "1", callback);
    }

    /**
     * 解绑设备
     *
     * @param mac 设备地址
     */
    public void disconnect(String mac, final SDKService.Callback<Integer> callback) {
        SDKService.getInstance().disconnect(mac, callback);
    }

    /**
     * 获取HUB指定连接状态的设备集合
     *
     * @param connection_state 设备的连接状态 {connected:已连接,disconnected:未连接}
     * @param callback         返回连接设备的字符串,空串表示没有设备或者是获取失败
     */
    public void connectList(String connection_state, final SDKService.Callback<String> callback) {
        SDKService.getInstance().connectList(connection_state, callback);
    }

    /**
     * 获取hub已连接设备列表
     *
     * @param callback 返回连接设备的json字符串,空串表示没有设备或者是获取失败
     */
    public void connectList(final SDKService.Callback<String> callback) {
        connectList("connected", callback);
    }

    /**
     * 发现设备的服务
     *
     * @param mac      设备的mac地址
     * @param callback 设备服务集合的json字符串
     */
    public void discoverServices(String mac, final SDKService.Callback<String> callback) {
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
    public void writeHandle(String mac, int handle, String value, final SDKService.Callback<String> callback) {
        SDKService.getInstance().writeHandle(mac, handle, value, callback);
    }

    /**
     * 启动服务
     */
    public void startService() {
        Intent service = new Intent(context, SDKService.class);
        context.startService(service);
    }

    /**
     * 停止服务
     */
    public void stopService() {
        context.stopService(new Intent(context, SDKService.class));
    }

    /**
     * 添加观察者接收消息提醒
     *
     * @param observer 观察者
     */
    public void addObserver(Observer observer) {
        SDKService.messenger.addObserver(observer);
    }

    /**
     * 删除观察者
     *
     * @param observer 观察者
     */
    public void removeObserver(Observer observer) {
        SDKService.messenger.deleteObserver(observer);
    }


}

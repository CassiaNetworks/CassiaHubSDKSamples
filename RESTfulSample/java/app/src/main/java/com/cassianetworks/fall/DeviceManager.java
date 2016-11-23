package com.cassianetworks.fall;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import com.cassianetworks.fall.domain.Device;
import com.cassianetworks.fall.domain.Record;
import com.cassianetworks.fall.utils.SysUtils;
import com.cassianetworks.mylibrary.domain.Callback;
import com.google.gson.Gson;
import com.google.gson.internal.LinkedTreeMap;
import com.google.gson.reflect.TypeToken;

import org.xutils.common.util.LogUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class DeviceManager {
    private static DeviceManager instance;
    private Context context;
    private List<Device> devList = new ArrayList<>();
    private List<Device> scanDevList = new ArrayList<>();

    private DeviceManager(Context context) {
        this.context = context;
    }

    static DeviceManager getInstance(Context context) {
        if (instance == null) instance = new DeviceManager(context);
        return instance;
    }

    public List<Device> getDevList() {
        return devList;
    }

    public List<Device> getScanDevList() {
        return scanDevList;
    }

    public void addDevice(Device device) {
        add(devList, device);
    }

    public void addScanDevice(Device device) {
        add(scanDevList, device);
    }

    private void add(List<Device> tarList, Device device) {
        if (!tarList.contains(device)) {
            tarList.add(device);
        }
    }
    public boolean isNewDevice(Device device) {
        for (int i = 0; i < devList.size(); i++) {
            if (devList.get(i).getBdaddr().equals(device.getBdaddr())) {
                return false;
            }
        }
        return true;
    }

    public boolean isNewScanDevice(Device device) {
        for (int i = 0; i < scanDevList.size(); i++) {
            if (scanDevList.get(i).getBdaddr().equals(device.getBdaddr())) {
                return false;
            }
        }
        return true;
    }


    public void addDevice(final Device device, final Callback<Integer> callback) {
        if (devList.contains(device)) {
            SysUtils.showShortTips(R.string.has_exist);
            if (callback != null) callback.run(-1);
        } else {
            LogUtil.d("add a device start:" + device.toString());
            devList.add(device);
            BaseApplication.deviceManager.saveDeviceListPref(devList);
            if (callback != null) callback.run(0);

        }

    }


    public List<Record> loadRecordListPref(String device_mac) {
        String records_list = loadPref("record_" + device_mac);
        return new Gson().fromJson(records_list, new TypeToken<List<Record>>() {
        }.getType());
    }

    public void refreshDevicesList(boolean add, Device device, Callback<Integer> callback) {
        if (add) addDevice(device, callback);
        else delDevice(device, callback);
    }


    public void delDevice(final Device device, final Callback<Integer> callback) {
        if (!devList.contains(device)) {
            SysUtils.showShortTips(R.string.no_exist);
            if (callback != null) callback.run(-1);
        } else {
            LogUtil.d("del a device :" + device.toString());
            devList.remove(device);
            BaseApplication.deviceManager.clearRecordListPref(device.getBdaddr());
            BaseApplication.deviceManager.saveDeviceListPref(devList);
            if (callback != null) callback.run(0);

        }


    }

    public void updateDeviceList(Device orgDevice, Device newDevice) {
        for (int i = 0; i < devList.size(); i++) {
            if (devList.get(i).getBdaddr().equals(orgDevice.getBdaddr())) {
                devList.set(i, newDevice);
                saveDeviceListPref(devList);
                return;
            }
        }


    }

    public Device getDevice(String bdaddr) {
        for (Device dev : devList) {
            if (dev.getBdaddr().equals(bdaddr))
                return dev;
        }
        return null;
    }

    public Device getScanDevice(String bdaddr) {
        for (Device dev : scanDevList) {
            if (dev.getBdaddr().equals(bdaddr))
                return dev;
        }
        return null;
    }


    public boolean isConnect(String connectionState) {
        return connectionState.equals("connected");

    }


    //获取有用的数据
    public String getBinaryString(String adData) {
        if (adData.equals("")) return "";
        String binary = Integer.toBinaryString(Integer.parseInt(adData, 16));
        int len = binary.length();
        if (len == 16) return binary;
        int dex = 16 - len;
        String zero = "";
        while (dex > 0) {
            zero += "0";
            dex--;
        }
        return zero + binary;
    }

    public String loadPref(String key) {
        SharedPreferences sp = context.getSharedPreferences("security_config", Context.MODE_PRIVATE);
        return sp.contains(key) ? sp.getString(key, "") : "";
    }


    public void saveDeviceListPref(List<Device> device_list) {
        SharedPreferences sp = context.getSharedPreferences("security_config", Context.MODE_PRIVATE);
        String deviceStr = new Gson().toJson(device_list);
        LogUtil.d("save device " + deviceStr);
        Editor editor = sp.edit();
        editor.putString("device_list", deviceStr);
        editor.apply();
    }
    public void clearDevList(){
        devList.clear();
    }
    public void clearScanDevList(){
        scanDevList.clear();
    }


    public void clearDeviceListPref() {
        clearPref("device_list");
    }

    public void clearRecordListPref(String device_mac) {
        clearPref("record_" + device_mac);
    }

    public void clearPref(String key) {
        SharedPreferences sp = context.getSharedPreferences("security_config", Context.MODE_PRIVATE);
        if (sp.contains(key)) {
            Editor editor = sp.edit();
            editor.remove(key);//删除SharedPreferences里指定key对应的数据项
//        editor.putString(key, "");
//        editor.clear();//清空SharedPreferences里所有的数据
            editor.apply();
        }

    }

    public Device formatHashMap2Device(HashMap result) {
        Device device = new Device();
        LinkedTreeMap bdaddrs = (LinkedTreeMap) ((ArrayList) result.get("bdaddrs")).get(0);
        device.setName("CassiaFD_1.2");
        device.setBdaddr((String) bdaddrs.get("bdaddr"));
        device.setScanData((String) result.get("scanData"));
        device.setRssi((double)result.get("rssi"));
        return device;

    }

    public List<Device> loadDeviceListPref() {
        String device_list = loadPref("device_list");
        return new Gson().fromJson(device_list, new TypeToken<List<Device>>() {
        }.getType());

    }


}

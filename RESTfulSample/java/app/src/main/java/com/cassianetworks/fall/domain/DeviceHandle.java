package com.cassianetworks.fall.domain;

import static android.R.attr.handle;

/**
 * Created by ZhangMin on 2016/11/22.
 */

public class DeviceHandle {
    public int handle;
    public String uuid;
    public int properties;
    public int valueHandle;

    public DeviceHandle() {
    }

    public DeviceHandle(int handle, String uuid,int properties, int valueHandle) {

        this.handle = handle;
        this.uuid = uuid;
        this.properties = properties;
        this.valueHandle = valueHandle;
    }
}

package com.cassianetworks.fall.domain;

import static android.R.attr.handle;

/**
 * Created by ZhangMin on 2016/11/22.
 */

public class DeviceHandle {
    public int handle;
    public String uuid;

    public DeviceHandle() {
    }

    public DeviceHandle(int handle, String uuid) {

        this.handle = handle;
        this.uuid = uuid;
    }
}

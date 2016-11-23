package com.cassianetworks.fall.domain;

import com.google.gson.Gson;

import java.io.Serializable;

/**
 * Created by ZhangMin on 2016/8/16.
 */
public class Record implements Serializable {

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    private String status;//0 normal 1 alert
    private String time;

    public String toString() {
        return new Gson().toJson(this);
    }

    public Record() {
    }

    public Record(String status, String time) {
        this.status = status;
        this.time = time;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}

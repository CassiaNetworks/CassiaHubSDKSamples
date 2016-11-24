package com.cassianetworks.fall.domain;

import java.io.Serializable;

/**
 * Created by ZhangMin on 2016/8/16.
 */
public class Record implements Serializable {
    /**
     * 用于区分记录类型
     * 0:header
     * 1:body
     */
    private int type;
    private String value;
    private String name;
    private String dataType;// indication/notification
    private String id;//mac
    private int handle;

    /**
     * @param type
     * @param value
     * @param name
     * @param dataType
     * @param id
     * @param handle
     * @param time
     */
    public Record(int type, String value, String name, String dataType, String id, int handle, String time) {
        this.type = type;
        this.value = value;
        this.name = name;
        this.dataType = dataType;
        this.id = id;
        this.handle = handle;
        this.time = time;
    }

    public Record(int type, String time) {
        this.type = type;
        this.time = time;
    }

    @Override
    public String toString() {
        return "Record{" +
                "value='" + value + '\'' +
                ", name='" + name + '\'' +
                ", dataType='" + dataType + '\'' +
                ", id='" + id + '\'' +
                ", handle=" + handle +
                ", time='" + time + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Record)) return false;

        Record record = (Record) o;

        if (getValue() != null ? !getValue().equals(record.getValue()) : record.getValue() != null)
            return false;
        return getId() != null ? getId().equals(record.getId()) : record.getId() == null;

    }

    @Override
    public int hashCode() {
        int result = getValue() != null ? getValue().hashCode() : 0;
        result = 31 * result + (getId() != null ? getId().hashCode() : 0);
        return result;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDataType() {
        return dataType;
    }

    public void setDataType(String dataType) {
        this.dataType = dataType;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getHandle() {
        return handle;
    }

    public void setHandle(int handle) {
        this.handle = handle;
    }

    public String getTime() {
        return time;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public void setTime(String time) {
        this.time = time;
    }

    private String time;
}

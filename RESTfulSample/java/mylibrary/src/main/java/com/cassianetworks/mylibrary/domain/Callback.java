package com.cassianetworks.mylibrary.domain;

public interface Callback<T> {
    void run(T value);
}

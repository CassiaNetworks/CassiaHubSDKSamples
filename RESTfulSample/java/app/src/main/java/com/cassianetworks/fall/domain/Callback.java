package com.cassianetworks.fall.domain;

public interface Callback<T> {
    void run(T value);
}

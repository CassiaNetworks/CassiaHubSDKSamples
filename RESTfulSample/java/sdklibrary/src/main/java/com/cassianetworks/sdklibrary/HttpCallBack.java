package com.cassianetworks.sdklibrary;


import com.google.gson.Gson;

import org.xutils.common.Callback.CommonCallback;
import org.xutils.common.util.LogUtil;
import org.xutils.ex.HttpException;

import java.util.HashMap;

abstract class HttpCallBack<ResultType> implements CommonCallback<ResultType> {

    @Override
    public void onSuccess(ResultType result) {
        LogUtil.i("request success:\n" + result);
        onSuccess(new Gson().fromJson((String) result, HashMap.class));
    }

    @Override
    public void onError(Throwable ex, boolean isOnCallback) {

        if (ex instanceof HttpException) { // 网络错误
            HttpException httpEx = (HttpException) ex;
            int responseCode = httpEx.getCode();
            String responseMsg = httpEx.getMessage();
            String errorResult = httpEx.getResult();
            LogUtil.e("request fail: " + "\ncode :" + responseCode + " ,\nmsg: " + responseMsg + ",\nresult:" + errorResult);
            onFailed(errorResult);
        }
    }

    @Override
    public void onCancelled(CancelledException cex) {
        LogUtil.d("request fail: " + cex.getMessage());

    }

    @Override
    public void onFinished() {
        LogUtil.d("request finish:");

    }

    public abstract void onFailed(String result);

    public abstract void onSuccess(HashMap result);


}

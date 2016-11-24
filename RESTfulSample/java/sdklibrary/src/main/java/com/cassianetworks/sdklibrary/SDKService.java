package com.cassianetworks.sdklibrary;

import com.cassianetworks.sdklibrary.HttpUtils.OkHttpCallback;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.util.Timer;
import java.util.TimerTask;

import okhttp3.Response;

import static com.cassianetworks.sdklibrary.Indicator.log;

public class SDKService {


    public void connect(String mac, String chip, final Callback<String> callback) {
        HttpUtils.getInstance().connect(mac, chip, new OkHttpCallback() {
            @Override
            protected void onSuccess(Response response) {
                log("--connect success ");
                callback.run("ok");
            }

            @Override
            public void onFailure(String msg) {
                log("--connect fail " + msg);
                callback.run("err:" + msg);
            }
        });
    }

    public void writeHandle(String mac, int handle, String value, final Callback<String> callback) {
        HttpUtils.getInstance().writeHandle(mac, handle, value, new OkHttpCallback() {
            @Override
            protected void onSuccess(Response response) {
                log("--writeHandle success ");
                callback.run("ok");
            }

            @Override
            public void onFailure(String msg) {
                log("--writeHandle fail " + msg);
                callback.run("err:" + msg);
            }
        });
    }

    public void connectList(String connection_state, final Callback<String> callback) {
        HttpUtils.getInstance().connectList(connection_state, new OkHttpCallback() {
            @Override
            protected void onSuccess(final Response response) {
                log("--connectList success ");
                formatResponse(response, callback);
            }

            @Override
            public void onFailure(String msg) {
                log("--connectList fail " + msg);
                callback.run("err:" + msg);
            }
        });
    }


    public void disconnect(String mac, final Callback<String> callback) {
        HttpUtils.getInstance().disconnect(mac, new OkHttpCallback() {
            @Override
            protected void onSuccess(Response response) {
                log("--disconnect device success ");
                callback.run("ok");
            }

            @Override
            public void onFailure(String msg) {
                log("--disconnect device fail " + msg);
                callback.run("err:" + msg);
            }
        });
    }

    public void discoverServices(String mac, final Callback<String> callback) {
        HttpUtils.getInstance().discoverServices(mac, new OkHttpCallback() {
            @Override
            protected void onSuccess(final Response response) {
                log("--discoverServices success ");
                formatResponse(response, callback);
            }

            @Override
            public void onFailure(String msg) {
                log("--discoverServices fail " + msg);
                callback.run("err:"+msg);
            }
        });
    }


    public void scan(int milliseconds, OkHttpCallback callback) {

        log("service start scan...");
        if (milliseconds != -1) {
            new Timer().schedule(new TimerTask() {
                @Override
                public void run() {
                    log("postDelayed stop scan...");
                    stopScan();
                }
            }, milliseconds);
        }
        HttpUtils.getInstance().scan(callback);

    }

    public void stopScan() {
        log("stop scan");
        HttpUtils.getInstance().removeRequest();
    }

    public void getNotification(OkHttpCallback callback) {
        HttpUtils.getInstance().getNotification(callback);
    }

    public void rebootHub(final Callback<String> callback) {
        HttpUtils.getInstance().rebootHub(new OkHttpCallback() {
            @Override
            protected void onSuccess(Response response) {
                callback.run("ok");
            }

            @Override
            protected void onFailure(String msg) {
                callback.run("err:" + msg);
            }
        });
    }


    private static class SingleTonHolder {
        private static final SDKService INSTANCE = new SDKService();
    }

    public static SDKService getInstance() {
        return SingleTonHolder.INSTANCE;
    }

    private SDKService() {

    }


    public void close() {
    }

    private void formatResponse(final Response response, final Callback<String> callback) {
        new Thread(new Runnable() {
            @Override
            public void run() {

                Reader charStream = response.body().charStream();
                BufferedReader in = new BufferedReader(charStream);
                String line;
                try {
                    while ((line = in.readLine()) != null) {
                        callback.run(line);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    callback.run("err:" + e.getMessage());
                } finally {
                    response.close();
                    try {
                        in.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    try {
                        charStream.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }
}

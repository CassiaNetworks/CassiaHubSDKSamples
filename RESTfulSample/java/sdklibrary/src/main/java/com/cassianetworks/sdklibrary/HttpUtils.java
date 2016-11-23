package com.cassianetworks.sdklibrary;

import android.os.Handler;
import android.os.Looper;
import android.util.Base64;

import com.google.gson.Gson;

import org.xutils.common.util.LogUtil;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.Headers;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

class HttpUtils {
    private static Handler handler = new Handler(Looper.getMainLooper());
    private static String root = "http://api.cassianetworks.com/";
    private static final int TIMEOUT = 30;
    static String access_token;
    static String hubMac = "";

    private static OkHttpClient client = new OkHttpClient().newBuilder()
            .connectTimeout(TIMEOUT, TimeUnit.SECONDS)
            .readTimeout(TIMEOUT, TimeUnit.SECONDS)
            .writeTimeout(TIMEOUT, TimeUnit.SECONDS)
            .build();
    private static Call call;

    public static void oauth(HttpCallBack<String> callback, final String developer, final String pwd) {
        String strBase64 = new String(Base64.encode((developer + ":" + pwd).getBytes(), Base64.DEFAULT));
        RequestParams params = new RequestParams(root + "oauth2/token");
        params.setConnectTimeout(30 * 1000);
        params.addHeader("Authorization", "Basic " + strBase64);
        Map<String, String> map = new HashMap<>();
        map.put("grant_type", "client_credentials");
        for (Map.Entry<String, String> entry : map.entrySet()) {
            params.addParameter(entry.getKey(), entry.getValue());

        }
        x.http().post(params, callback);

    }


    public static void scan(OkHttpCallback callback) {
        Map<String, String> map = new HashMap<>();
        map.put("mac", hubMac);
        map.put("event", "1");
        map.put("chip", "1");
        get(root + "gap/nodes/", map, callback);
    }

    public static void connect(String mac, String chip, OkHttpCallback callback) {
        Map<String, Object> map = new HashMap<>();
        map.put("type", "public");
        jsonPost(root + "gap/nodes/" + mac + "/connection?mac=" + hubMac + "&chip=" + chip, map, callback);

    }

    public static void connectList(String connection_state, OkHttpCallback callback) {
        Map<String, String> map = new HashMap<>();
        map.put("mac", hubMac);
        map.put("connection_state", connection_state);
        get(root + "gap/nodes/", map, callback);
    }

    public static void disconnect(String mac, OkHttpCallback callback) {
        delete(root + "gap/nodes/" + mac + "/connection?mac=" + hubMac, null, callback);

    }

    public static void discoverServices(String mac, OkHttpCallback callback) {

        get(root + "gatt/nodes/" + mac + "/services/characteristics/descriptors?mac=" + hubMac + "&all=1", null, callback);

    }

    public static void writeHandle(String mac, int handle, String value, OkHttpCallback callback) {
        get(root + "gatt/nodes/" + mac + "/handle/" + handle + "/value/" + value + "/?mac=" + hubMac, null, callback);
    }

    public static void get(String url, final OkHttpCallback callback) {
        get(url, null, callback);
    }

    public static void get(String url, Map<String, String> params, final OkHttpCallback callback) {
        url = buildGetUrl(url, params);
        LogUtil.d("url=" + url);
        Headers.Builder headersBuilder = new Headers.Builder()
                .add("Authorization", "Bearer " + access_token);
        headersBuilder.set("Content-Type", "application/json; charset=utf-8");
        Headers requestHeaders = headersBuilder.build();
        Request request = new Request.Builder().url(url).headers(requestHeaders).build();
        call = client.newCall(request);
        onStart(callback);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                onError(callback, e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    onSuccess(callback, response);

                } else {
                    onError(callback, response.message());
                    response.body().close();
                }
            }
        });

    }


    public static void removeRequest() {
        if (call != null) {
            call.cancel();
            LogUtil.d("--removeRequest cancel");
        }

    }

    public static void delete(String url, Map<String, Object> map, final OkHttpCallback callback) {
        Headers.Builder headersBuilder = new Headers.Builder()
                .add("Authorization", "Bearer " + access_token);
        Headers requestHeaders = headersBuilder.build();
        Request request = new Request.Builder().url(url).headers(requestHeaders).delete().build();
        onStart(callback);

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                onError(callback, e == null ? "err" : e.getMessage().toString());

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    onSuccess(callback, response);

                } else {
                    onError(callback, response.message());
                }
                response.body().close();

            }
        });
    }

    public static void post(String url, Map<String, Object> map, final OkHttpCallback callback) {
        Headers.Builder headersBuilder = new Headers.Builder()
                .add("Authorization", "Bearer " + access_token)
                .add("type", "public")
                .add("Content-Type  ", "application/json");
        Headers requestHeaders = headersBuilder.build();

        FormBody.Builder builder = new FormBody.Builder();
        if (null != map) {
            for (Map.Entry<String, Object> entry : map.entrySet()) {
                builder.add(entry.getKey(), entry.getValue().toString());
            }
        }

        RequestBody body = builder.build();

        Request request = new Request.Builder().url(url).headers(requestHeaders)
                .post(body).build();

        onStart(callback);

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                onError(callback, e.getMessage().toString());

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    onSuccess(callback, response);

                } else {
                    onError(callback, response.message());
                }
                response.body().close();

            }
        });
    }

    public static void jsonPost(String url, Map<String, Object> map, final OkHttpCallback callback) {
        Headers.Builder headersBuilder = new Headers.Builder()
                .add("Authorization", "Bearer " + access_token);
        Headers requestHeaders = headersBuilder.build();
        MediaType JSON = MediaType.parse("application/json; charset=utf-8");
        RequestBody body = RequestBody.create(JSON, new Gson().toJson(map));

        Request request = new Request.Builder().url(url).headers(requestHeaders)
                .post(body).build();

        onStart(callback);

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                onError(callback, e.getMessage().toString());

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    onSuccess(callback, response);

                } else {
                    onError(callback, response.message());
                }
                response.body().close();

            }
        });
    }

    private static void onStart(OkHttpCallback callback) {
        LogUtil.i("onStart....");
        if (null != callback) {
            callback.onStart();
        }
    }

    private static void onSuccess(final OkHttpCallback callback, final Response response) {
        LogUtil.i("onSuccess....");
        if (null != callback) {
            handler.post(new Runnable() {
                public void run() {
                    callback.onSuccess(response);
                }
            });
        }
    }

    private static void onError(final OkHttpCallback callback, final String msg) {
        LogUtil.e("onError..." + msg);
        if (null != callback) {
            handler.post(new Runnable() {
                public void run() {
                    callback.onFailure(msg);
                }
            });
        }
    }

    public static String buildGetUrl(String url, Map<String, String> map) {
        if (map == null) return url;
        try {
            StringBuilder sb = new StringBuilder();
            for (String name : map.keySet()) {
                sb.append(name).append("=").append(
                        java.net.URLEncoder.encode(map.get(name), "UTF-8")).append("&");
            }
            return url + "?" + sb.toString().substring(0, sb.toString().length() - 1);

        } catch (Exception e) {
            e.printStackTrace();
        }

        return url;
    }

    public static abstract class OkHttpCallback {

        abstract void onSuccess(Response response);

        abstract void onFailure(String msg);

        void onStart() {
        }
    }

}

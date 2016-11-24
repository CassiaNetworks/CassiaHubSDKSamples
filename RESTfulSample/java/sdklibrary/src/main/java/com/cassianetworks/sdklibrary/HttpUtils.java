package com.cassianetworks.sdklibrary;


import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Credentials;
import okhttp3.FormBody;
import okhttp3.Headers;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static com.cassianetworks.sdklibrary.Indicator.log;

public class HttpUtils {
    private String root = "http://api.cassianetworks.com/";
    private final int TIMEOUT = 30;
    private String access_token;
    static String hubMac = "";

    public void setHubMac(String hubMac) {
        HttpUtils.hubMac = hubMac;
    }

    private static class SingleTonHolder {
        private static final HttpUtils INSTANCE = new HttpUtils();
    }

    public static HttpUtils getInstance() {
        return SingleTonHolder.INSTANCE;
    }

    private HttpUtils() {

    }

    private OkHttpClient client = new OkHttpClient().newBuilder()
            .connectTimeout(TIMEOUT, TimeUnit.SECONDS)
            .readTimeout(TIMEOUT, TimeUnit.SECONDS)
            .writeTimeout(TIMEOUT, TimeUnit.SECONDS)
            .build();
    private Call mCall;

    public void oauth(final Callback<Integer> callback, final String developer, final String pwd) {
        String credential = Credentials.basic(developer, pwd);
        Headers.Builder headersBuilder = new Headers.Builder()
                .add("Authorization", credential);
        Headers requestHeaders = headersBuilder.build();

        FormBody.Builder builder = new FormBody.Builder();
        builder.add("grant_type", "client_credentials");
        FormBody body = builder.build();
        final Request request = new Request.Builder().url(root + "oauth2/token").headers(requestHeaders)
                .post(body).build();

        client.newCall(request).enqueue(new okhttp3.Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                log("request fail");
                mCall = call;
                callback.run(0);
            }

            @Override
            public void onResponse(Call call, final Response response) throws IOException {
                mCall = call;
                if (response.isSuccessful()) {

                    Reader charStream = response.body().charStream();
                    BufferedReader in = new BufferedReader(charStream);
                    String line;
                    try {
                        while ((line = in.readLine()) != null) {
                            HashMap result = new Gson().fromJson(line, HashMap.class);
                            access_token = (String) result.get("access_token");
                            log("success access_token=" + access_token);
                            callback.run(1);
                        }
                    } catch (IOException e) {
                        callback.run(0);
                        e.printStackTrace();
                    }
                } else {
                    callback.run(0);
                }
                response.body().close();
            }
        });

    }

    public void scan(OkHttpCallback callback) {
        Map<String, String> map = new HashMap<>();
        map.put("mac", hubMac);
        map.put("event", "1");
        map.put("chip", "1");
        get(root + "gap/nodes/", map, callback);
    }

    public void connect(String mac, String chip, OkHttpCallback callback) {
        Map<String, Object> map = new HashMap<>();
        map.put("type", "public");
        jsonPost(root + "gap/nodes/" + mac + "/connection?mac=" + hubMac + "&chip=" + chip, map, callback);
    }

    public void connectList(String connection_state, OkHttpCallback callback) {
        Map<String, String> map = new HashMap<>();
        map.put("mac", hubMac);
        map.put("connection_state", connection_state);
        get(root + "gap/nodes/", map, callback);
    }

    public void disconnect(String mac, OkHttpCallback callback) {
        delete(root + "gap/nodes/" + mac + "/connection?mac=" + hubMac, null, callback);
    }

    public void discoverServices(String mac, OkHttpCallback callback) {
        get(root + "gatt/nodes/" + mac + "/services/characteristics/descriptors?mac=" + hubMac + "&all=1", null, callback);
    }

    public void writeHandle(String mac, int handle, String value, OkHttpCallback callback) {
        get(root + "gatt/nodes/" + mac + "/handle/" + handle + "/value/" + value + "/?mac=" + hubMac, null, callback);
    }

    public void getNotification(OkHttpCallback callback) {
        Map<String, String> map = new HashMap<>();
        map.put("mac", hubMac);
        map.put("event", "1");
        get(root + "gap/nodes/", map, callback);
    }

    public void get(String url, Map<String, String> params, final OkHttpCallback callback) {
        url = buildGetUrl(url, params);
        log("url=" + url);
        Headers.Builder headersBuilder = new Headers.Builder()
                .add("Authorization", "Bearer " + access_token);
        headersBuilder.set("Content-Type", "application/json; charset=utf-8");
        Headers requestHeaders = headersBuilder.build();
        Request request = new Request.Builder().url(url).headers(requestHeaders).build();
        client.newCall(request).enqueue(new okhttp3.Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                mCall = call;
                callback.onFailure(e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                mCall = call;
                if (response.isSuccessful()) {
                    callback.onSuccess(response);
                } else {
                    callback.onFailure(response.message());
                    response.body().close();
                }
            }
        });


    }


    public void removeRequest() {
        if (mCall != null) {
            mCall.cancel();
            log("--removeRequest cancel");
        }

    }

    public void delete(String url, Map<String, Object> map, final OkHttpCallback callback) {
        Headers.Builder headersBuilder = new Headers.Builder()
                .add("Authorization", "Bearer " + access_token);
        Headers requestHeaders = headersBuilder.build();
        Request request = new Request.Builder().url(url).headers(requestHeaders).delete().build();
        callback.onStart();
        client.newCall(request).enqueue(new okhttp3.Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                mCall = call;
                callback.onFailure(e == null ? "err" : e.getMessage().toString());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                mCall = call;
                if (response.isSuccessful()) {
                    callback.onSuccess(response);
                } else {
                    callback.onFailure(response.message());
                }
                response.body().close();

            }
        });
    }

    public void post(String url, Map<String, Object> map, final OkHttpCallback callback) {
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
        callback.onStart();

        client.newCall(request).enqueue(new okhttp3.Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                mCall = call;
                callback.onFailure(e.getMessage().toString());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                mCall = call;
                if (response.isSuccessful()) {
                    callback.onSuccess(response);
                } else {
                    callback.onFailure(response.message());
                }
                response.body().close();

            }
        });
    }

    public void jsonPost(String url, Map<String, Object> map, final OkHttpCallback callback) {
        Headers.Builder headersBuilder = new Headers.Builder()
                .add("Authorization", "Bearer " + access_token);
        Headers requestHeaders = headersBuilder.build();
        MediaType JSON = MediaType.parse("application/json; charset=utf-8");
        RequestBody body = RequestBody.create(JSON, new Gson().toJson(map));

        Request request = new Request.Builder().url(url).headers(requestHeaders)
                .post(body).build();
        callback.onStart();

        client.newCall(request).enqueue(new okhttp3.Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                mCall = call;
                callback.onFailure(e.getMessage().toString());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                mCall = call;
                if (response.isSuccessful()) {
                    callback.onSuccess(response);
                } else {
                    callback.onFailure(response.message());
                }
                response.body().close();

            }
        });
    }

    public String buildGetUrl(String url, Map<String, String> map) {
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

        protected abstract void onSuccess(Response response);

        protected abstract void onFailure(String msg);

        protected void onStart() {
        }
    }

}

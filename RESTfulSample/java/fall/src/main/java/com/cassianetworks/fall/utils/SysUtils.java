package com.cassianetworks.fall.utils;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.cassianetworks.fall.BaseApplication;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by ZhangMin on 2015/10/9.、
 */
public class SysUtils {
    private static long lastClickTime;
    private static long startTime = 0;

    public static InputMethodManager getImm(Context context) {
        return (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
    }

    public static void hindInputWindow(Context context, View view) {
        InputMethodManager imm = getImm(context);
        if (imm.isActive())
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    public static void showInputWindow(Context context, View view) {
        getImm(context).showSoftInput(view, InputMethodManager.SHOW_FORCED);

    }

    /**
     * edittext以外的区域隐藏键盘
     *
     * @param v     当前获取焦点的view
     * @param event
     * @return
     */
    public static boolean isShouldHideInput(View v, MotionEvent event) {
        if (v != null && (v instanceof EditText)) {
            int[] leftTop = {0, 0};
            //获取输入框当前的location位置
            v.getLocationInWindow(leftTop);
            int left = leftTop[0];
            int top = leftTop[1];
            int bottom = top + v.getHeight();
            int right = left + v.getWidth();
            return !(event.getX() > left && event.getX() < right
                    && event.getY() > top && event.getY() < bottom);
        }
        return false;
    }

    public static boolean isFastClick() {
        long currentTime = System.currentTimeMillis();
        if (currentTime - lastClickTime < 500) {
            return true;
        }
        lastClickTime = currentTime;
        return false;
    }


    private static DisplayMetrics getdm(Context context) {
        DisplayMetrics dm = new DisplayMetrics();
        ((Activity) context).getWindowManager().getDefaultDisplay().getMetrics(dm);
        return dm;
    }

    public static int getScreenHeight(Context context) {
        return getdm(context).heightPixels;
    }

    public static int getScreenWidth(Context context) {
        return getdm(context).widthPixels;
    }

    public static void initGridView(final BaseAdapter adapter, GridView gridview, int itemWidth) {
        int size = adapter.getCount();
        int allWidth = itemWidth * size;
        gridview.setSelector(new ColorDrawable(Color.TRANSPARENT));
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(allWidth,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        gridview.setLayoutParams(params);
        gridview.setColumnWidth(itemWidth);
        gridview.setStretchMode(GridView.NO_STRETCH);
        gridview.setNumColumns(size);
        gridview.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }

    /**
     * 格式化版本信息详情
     *
     * @param comment
     * @return
     */
    public static String getComment(String comment) {
        if (TextUtils.isEmpty(comment)) return "";
        String[] comArr = comment.split(";");
        String result = "";
        try {
            int j = 0;
            for (String aComArr : comArr) {
                result = result + ++j + "." + aComArr + ";\n";
            }
            return result;
        } catch (Exception e) {
            return result;
        }
    }

    /**
     * 将时间戳转换为指定格式的日期字符串
     *
     * @param time
     * @return
     */
    public static String timeStamp2Data(long time) {
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = new Date(time);
        return df.format(date);//定位时间
    }

    /**
     * 日期格式字符串转换成时间戳
     *
     * @param date 字符串日期
     * @return
     */
    public static String date2TimeStamp(String date) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            return String.valueOf(sdf.parse(date).getTime());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    public static long getLongDate(String date) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            return sdf.parse(date).getTime();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    public static boolean isPermission(Context context, String permissionName, String packageName) {
        PackageManager pm = context.getPackageManager();
        return (PackageManager.PERMISSION_GRANTED ==
                pm.checkPermission(permissionName, packageName));
    }

    public static boolean isPermissionReadContact(Context context, String packageName) {
        return isPermission(context, "android.permission.READ_CONTACTS", packageName);
    }

    public static String formatPhone(String phone) {
        String tmpStr = "";
        if (phone.length() > 0) {
            for (int i = 0; i < phone.length(); i++) {
                String tmp = "" + phone.charAt(i);
                if ((tmp).matches("[0-9]")) {
                    tmpStr += tmp;
                }
            }
        }
        return tmpStr;
    }

    public static boolean isContainChinese(String str) {
        Pattern p = Pattern.compile("[u4e00-u9fa5]");
        Matcher m = p.matcher(str);
        return !m.find();
    }

    public static SharedPreferences getSp(Context context) {
        return context.getSharedPreferences("config", Context.MODE_PRIVATE);
    }

    public static SharedPreferences.Editor getEditor(Context context) {
        return getSp(context).edit();
    }

    public static String getSysLanguage() {
        return Locale.getDefault().toString();
//        return context.getResources().getConfiguration().locale.getLanguage();
    }


    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (cm == null) {
            return false;
        } else {
            NetworkInfo[] info = cm.getAllNetworkInfo();
            if (info != null) {
                for (NetworkInfo anInfo : info) {
                    if (anInfo.getState() == NetworkInfo.State.CONNECTED) {
                        if (anInfo.getType() == ConnectivityManager.TYPE_WIFI) {
                            return true;
                        } else if (anInfo.getType() == ConnectivityManager.TYPE_MOBILE) {
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }

    public static int getNetWorkStatus(Context context) {
        int netType = -1;
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (cm == null) {
            return netType;
        } else {
            NetworkInfo[] info = cm.getAllNetworkInfo();
            if (info != null) {
                for (NetworkInfo anInfo : info) {
                    if (anInfo.getState() == NetworkInfo.State.CONNECTED) {
                        if (anInfo.getType() == ConnectivityManager.TYPE_WIFI) {
                            netType = ConnectivityManager.TYPE_WIFI;
                        } else if (anInfo.getType() == ConnectivityManager.TYPE_MOBILE) {
                            netType = ConnectivityManager.TYPE_MOBILE;
                        }
                    }
                }
            }
        }
        return netType;

    }

    public static RelativeLayout.LayoutParams getRelativeLayoutParams() {
        return new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT);
    }


    /**
     * 设置是否打开用户的数据流量
     *
     * @param context
     * @param open
     * @return true 流量打开成功 false 打开失败
     */
    public static void openMobileData(Context context, boolean open) {
        try {
            ConnectivityManager connectivityManager =
                    (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            Class<?> connectivityManagerClass = Class.forName(connectivityManager.getClass().getName());
            Field connectivityManagerField = connectivityManagerClass.getDeclaredField("mService");
            connectivityManagerField.setAccessible(true);
            Object iConnectivityManagerObject = connectivityManagerField.get(connectivityManager);
            Class<?> iConnectivityManagerClass = Class.forName(iConnectivityManagerObject.getClass().getName());
            Class[] cArg = new Class[2];
            cArg[0] = String.class;
            cArg[1] = Boolean.TYPE;
            Method setMobileDataEnabledMethod =
                    iConnectivityManagerClass.getDeclaredMethod("setMobileDataEnabled", cArg);
            Object[] pArg = new Object[2];
            pArg[0] = context.getPackageName();
            pArg[1] = open;
            setMobileDataEnabledMethod.setAccessible(true);
            setMobileDataEnabledMethod.invoke(iConnectivityManagerObject, pArg);
//            setMobileDataEnabledMethod.setAccessible(true);
//            setMobileDataEnabledMethod.invoke(iConnectivityManagerObject, open);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public static void openSettingBlue() {
        Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
        // 设置蓝牙可见性，最多300秒
        intent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 60);
        BaseApplication.currentActivity.startActivity(intent);
    }


    public static String getSdkVersion() {
        return Build.VERSION.RELEASE;
    }

    public static String getAppVersion(Context context) {
        try {
            PackageManager pm = context.getPackageManager();
            PackageInfo packInfo = pm.getPackageInfo(context.getPackageName(), 0);
            return packInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return "0.0.0";
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

    public static String getCurData() {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date curDate = new Date(System.currentTimeMillis());
        return formatter.format(curDate);
    }


}

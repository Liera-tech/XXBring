package com.liera.lib_xxbring.util;

import android.text.TextUtils;
import android.util.Log;

import com.liera.lib_xxbring.BuildConfig;

public class XXBringLog {

    public static boolean Debug = BuildConfig.DEBUG;

    public static void isDebug(boolean isDebug) {
        Debug = isDebug;
    }

    public static void i(String TAG, Object content) {
        if (!Debug || TextUtils.isEmpty(TAG) || content == null) return;
        Log.i(TAG, content.toString());
    }

    public static void i(String TAG, String text, Object... content) {
        if (!Debug || TextUtils.isEmpty(TAG) || TextUtils.isEmpty(text) || content == null || content.length == 0) return;
        Log.i(TAG, String.format(text, content));
    }

    public static void w(String TAG, Object content) {
        if (!Debug || TextUtils.isEmpty(TAG) || content == null) return;
        Log.w(TAG, content.toString());
    }

    public static void w(String TAG, String text, Object... content) {
        if (!Debug || TextUtils.isEmpty(TAG) || TextUtils.isEmpty(text) || content == null || content.length == 0) return;
        Log.w(TAG, String.format(text, content));
    }

    public static void e(String TAG, Object content) {
        if (!Debug || TextUtils.isEmpty(TAG) || content == null) return;
        Log.e(TAG, content.toString());
    }

    public static void e(String TAG, String text, Object... content) {
        if (!Debug || TextUtils.isEmpty(TAG) || TextUtils.isEmpty(text) || content == null || content.length == 0) return;
        Log.e(TAG, String.format(text, content));
    }
}

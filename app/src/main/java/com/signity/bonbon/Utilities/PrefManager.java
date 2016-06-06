package com.signity.bonbon.Utilities;

import android.content.Context;
import android.content.SharedPreferences;

import com.signity.bonbon.BuildConfig;

/**
 * Created by Rajinder on 7/10/15.
 */

public class PrefManager {
    public static final String MyPREFERENCES = "com.android.bonbon.PREF";
    public static final String PREF_PROCESSING = "order_to_processing";
    public static final String PREF_SHIPING = "order_to_shipping";
    public static final String PREF_PRODUCT = "com.android.bonbon.PRODUCT";
    public static final String PREF_CART_OBJECT = "com.android.bonbon.PREF_CART_OBJECT";
    public static final String PREF_ORDER_OBJECT = "com.android.bonbon.PREF_ORDER_OBJECT";
    public static final String PREF_CART_SIZE = "com.android.bonbon.CART_SIZE";
    public static final String PREF_SEARCH_PRODUCT = "com.android.bonbon.PREF_SEARCH_PRODUCT";
    public static final String PROJECT_THEME = BuildConfig.APPLICATION_ID + ".PROJECT_THEME";
    public static final String PROJECT_TYPE = BuildConfig.APPLICATION_ID + ".PROJECT_TYPE";
    public static final String OTP_SKIP = BuildConfig.APPLICATION_ID + ".OTP_SKIP";
    public static final String USER_RECORD_KEY = BuildConfig.APPLICATION_ID + "._KEY_USER";
    public static final String PICKUP_STATUS = BuildConfig.APPLICATION_ID + ".PICKUP_STATUS";
    public static final String GEOFENCE_ENABLE = BuildConfig.APPLICATION_ID + ".GEOFENCE_ENABLE";
    public static final String APP_VERSION = BuildConfig.APPLICATION_ID + ".APP_VERSION";
    public static final String IS_REFER_FN = BuildConfig.APPLICATION_ID + ".is_refer_fn";
    public static final String IS_REFER_FN_ENABLE_FOR_DEVICE = BuildConfig.APPLICATION_ID + ".is_refer_fn_enable_device";
    public static final String REFER_OBJ = BuildConfig.APPLICATION_ID + ".refer_obj";
    public static final String REFER_OBJ_MSG = BuildConfig.APPLICATION_ID + ".refer_obj_msg";


    public static class SharedPrefs {
        public static String Geofences = "SHARED_PREFS_GEOFENCES";
    }

    public Context _ctx;
    SharedPreferences sharedpreferences;

    public PrefManager(Context _ctx) {
        this._ctx = _ctx;
        sharedpreferences = _ctx.getSharedPreferences(
                MyPREFERENCES, Context.MODE_PRIVATE);
    }

    public void storeSharedValue(String key, String value) {
        SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.putString(key, value);
        editor.commit();
    }

    public String getSharedValue(String key) {
        return sharedpreferences.getString(key, "");
    }

    public void clearSharedValue(String key) {

        SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.remove(key);
        editor.commit();

    }

    public static final String NOTIFICATION_CART_LOCAL = BuildConfig.APPLICATION_ID + ".NOTIFICATION_CART_LOCAL";

    public boolean isCartSetLocalNotification() {
        return sharedpreferences.getBoolean(NOTIFICATION_CART_LOCAL, false);
    }

    public void setCartLocalNotification(boolean notify) {
        SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.putBoolean(NOTIFICATION_CART_LOCAL, notify);
        editor.commit();
    }

    public void setProjectTheme(String theme) {
        SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.putString(PROJECT_THEME, theme);
        editor.commit();
    }

    public String getProjectTheme() {
        return sharedpreferences.getString(PROJECT_THEME, "");
    }

    public void setProjectType(String theme) {
        SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.putString(PROJECT_TYPE, theme);
        editor.commit();
    }

    public String getProjectType() {
        return sharedpreferences.getString(PROJECT_TYPE, "");
    }


    public void setOtoSkip(String otpSkip) {
        SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.putString(OTP_SKIP, otpSkip);
        editor.commit();
    }

    public void setPickupFacilityStatus(String pickUp) {
        SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.putString(PICKUP_STATUS, pickUp);
        editor.commit();
    }

    public String getPickupFacilityStatus() {
        return sharedpreferences.getString(PICKUP_STATUS, "0");
//        return "1";
    }

    public void setGeoFenceEnableFeature(String geofenceStatus) {
        SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.putString(GEOFENCE_ENABLE, geofenceStatus);
        editor.commit();
    }

    public String getGeoFenceEnableStatus() {
        return sharedpreferences.getString(GEOFENCE_ENABLE, "0");
    }

    public String getOtoSkip() {
        return sharedpreferences.getString(OTP_SKIP, "");
    }

    public void setUserRecord(String userRecord) {
        SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.putString(USER_RECORD_KEY, userRecord);
        editor.commit();
    }

    public String getUserRecord() {
        return sharedpreferences.getString(USER_RECORD_KEY, "");
    }


    public void storeBoolean(String key, boolean value) {
        SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.putBoolean(key, value);
        editor.commit();
    }

    public boolean getBoolean(String key) {
        return sharedpreferences.getBoolean(key, false);
    }

    public void setAppVersion(String appVersion) {
        SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.putString(APP_VERSION, appVersion);
        editor.commit();
    }

    public String getAppVersion() {
        return sharedpreferences.getString(APP_VERSION, "");
//        return "1";
    }


    public boolean isReferEarnFn() {
        return sharedpreferences.getBoolean(IS_REFER_FN, false);
    }

    public void setReferEarnFn(boolean refer) {
        SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.putBoolean(IS_REFER_FN, refer);
        editor.commit();
    }

    public boolean isReferEarnFnEnableForDevice() {
        return sharedpreferences.getBoolean(IS_REFER_FN_ENABLE_FOR_DEVICE, false);
    }

    public void setReferEarnFnEnableForDevice(boolean refer) {
        SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.putBoolean(IS_REFER_FN_ENABLE_FOR_DEVICE, refer);
        editor.commit();
    }

}

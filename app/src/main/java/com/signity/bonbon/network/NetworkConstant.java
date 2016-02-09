package com.signity.bonbon.network;

import com.signity.bonbon.BuildConfig;

/**
 * Created by Rajinder on 5/10/15.
 */
public class NetworkConstant {
    public static String VERSION = "/api_v1";
//    public static final String BASE = "http://dev.grocersapp.com/17" + VERSION;
    public static final String BASE = BuildConfig.NETWORK_URL;
}

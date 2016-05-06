package com.signity.bonbon.ga;

import com.signity.bonbon.BuildConfig;

/**
 * Created by rajesh on 21/4/16.
 */
public class GAConstant {


    public static final String GA_TRACK_ID_DEBUG = "UA-76344996-2";  // for tangerine
    public static final String GA_TRACK_ID_LIVE = "UA-77008261-1";
    public static final String SPLASH_SCREEN = "Splash screen";


    /*Category*/
    public static final String OFFER = "_offers";
    public static final String ORDER = "_order";
    public static final String PRODUCT = "_product";
    public static final String CONTACT = "_contact_us";
    public static final String CAT = "_cat";
    public static final String SUB_CAT = "_sub_cat";
    public static final String ABOUT = "_about_us";
    public static final String SEARCH = "_search";
    public static final String SHARE = "_share_app";


    /*Platform*/
    public static final String PLATFORM = "_ANDROID";


    /*ACTIONS*/
    public static final String VIEW = "_VIEW";
    public static final String CLICKED = "_CLICKED";
    public static final String PLACED = "_PLACED";
    public static final String SHARED = "_SHARED";

    /*GA EVENT CATEGORY*/
    public static final String EVENT_OFFER = BuildConfig.STORE_ID + PLATFORM + OFFER + CLICKED;
    public static final String EVENT_ORDER = BuildConfig.STORE_ID + PLATFORM + ORDER + PLACED;
    public static final String EVENT_PRODUCT = BuildConfig.STORE_ID + PLATFORM + PRODUCT + VIEW;
    public static final String EVENT_CONTACT = BuildConfig.STORE_ID + PLATFORM + CONTACT + VIEW;
    public static final String EVENT_CAT = BuildConfig.STORE_ID + PLATFORM + CAT + VIEW;
    public static final String EVENT_SUB_CAT = BuildConfig.STORE_ID + PLATFORM + SUB_CAT + VIEW;
    public static final String EVENT_ABOUT_US = BuildConfig.STORE_ID + PLATFORM + ABOUT + VIEW;
    public static final String EVENT_SEARCH = BuildConfig.STORE_ID + PLATFORM + SEARCH + CLICKED;
    public static final String EVENT_SHARE = BuildConfig.STORE_ID + PLATFORM + SHARE;


}

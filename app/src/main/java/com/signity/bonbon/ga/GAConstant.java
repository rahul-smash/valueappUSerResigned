package com.signity.bonbon.ga;

import com.signity.bonbon.BuildConfig;
import com.signity.bonbon.R;

/**
 * Created by rajesh on 21/4/16.
 */
public class GAConstant {

    public static final String GA_TRACK_ID_DEBUG = "UA-76344996-2";  // for tangerine
    public static final String GA_TRACK_ID_LIVE = "UA-77008261-1";
    /*All Screen Tracker*/
    public static final String SPLASH_SCREEN = "SPLASH SCREEN";
    public static final String HOME_SCREEN = "HOME_SCREEN";
    public static final String ABOUT_US_SCREEN = "ABOUT_US_SCREEN";
    public static final String OFFER_SCREEN = "OFFER_SCREEN";
    public static final String SEARCH_SCREEN = "SEARCH_SCREEN";
    public static final String CONTACT_SCREEN = "CONTACT_SCREEN";
    public static final String CHECKOUT_SCREEN = "CHECKOUT_SCREEN";
    public static final String CATEGORY_SCREEN = "CATEGORY_SCREEN";
    public static final String PRODUCT_SCREEN = "PRODUCT_SCREEN";
    /*Platform*/
    public static final String PLATFORM = "_ANDROID";
    /*Category*/
    public static final String GAC_OFFER = PLATFORM + "_OFFERS";
    public static final String ORDER = PLATFORM + "_ORDER";
    public static final String PRODUCT = PLATFORM + "_PRODUCT";
    public static final String GAC_CONTACT = PLATFORM + "_CONTACT_US";
    public static final String GAC_CAT = PLATFORM + "_CATEGORY";
    public static final String SUB_CAT = PLATFORM + "_SUB_CATEGORY";
    public static final String GAC_ABOUT = PLATFORM + "_ABOUT_US";
    public static final String GAC_SEARCH = PLATFORM + "_SEARCH";
    public static final String GAC_SHARE = PLATFORM + "_SHARE_APP";
    /*ACTIONS*/
    public static final String VIEW = "_VIEW";
    public static final String CLICKED = "_CLICKED";
    public static final String PLACED = "_PLACED";
    public static final String SHARED = "_SHARED";
    /*GA EVENT CATEGORY*/
    public static final String EVENT_SUB_CAT = BuildConfig.STORE_ID + PLATFORM + SUB_CAT + VIEW;

}

package com.signity.bonbon.app;

import android.content.Context;
import android.support.v4.app.Fragment;

import com.signity.bonbon.BuildConfig;
import com.signity.bonbon.R;
import com.signity.bonbon.Utilities.AppConstant;
import com.signity.bonbon.Utilities.PrefManager;
import com.signity.bonbon.ui.fragment.HomeFragment;
import com.signity.bonbon.ui.restaurant.storeamritsarizaika21.fragment.HomeFragAmritsarizaikaTheme5;
import com.signity.bonbon.ui.restaurant.storeburaan14.fragment.HomeFragmentBuraanTheme6;
import com.signity.bonbon.ui.restaurant.storetangerine9.fragment.HomeFragmentTangerineTheme1;
import com.signity.bonbon.ui.restaurant.storetownking15.fragment.HomeFragmentTownkingTheme2;

/**
 * Created by rajesh on 19/1/16.
 */
public class ViewController {

    Context context;

    public ViewController(Context context) {
        this.context = context;
    }


    public String getBookNowlbl(String appType) {

        String lbl = null;
        if (appType.equalsIgnoreCase(AppConstant.APP_TYPE_GROCERY)) {
            lbl = context.getString(R.string.class_constatnt_book_now_grocery).toString();
        } else if (appType.equalsIgnoreCase(AppConstant.APP_TYPE_RESTAURANT)) {
            lbl = context.getString(R.string.class_constatnt_book_now_rest).toString();
        }

        return lbl;
    }

    public Fragment getHomeFragment() {

        Fragment fragment = null;
        PrefManager prefManager = new PrefManager(context);
        String themeId = prefManager.getProjectTheme();

        switch (Integer.parseInt(themeId)) {
            case 1:
                fragment = new HomeFragmentTangerineTheme1();
                break;
            case 2:
                fragment = new HomeFragmentTownkingTheme2();
                break;
            case 5:
                fragment = new HomeFragAmritsarizaikaTheme5();
                break;
            case 6:
                fragment = new HomeFragmentBuraanTheme6();
                break;
            default:
                fragment = new HomeFragment();
                break;
        }
        return fragment;
    }

    public int getHomeResourceLayout() {
        String storeId = BuildConfig.STORE_ID;
        int layoutId;
        switch (Integer.parseInt(storeId)) {
            case 9:
                layoutId = R.layout.home_activity_tangerine;
                break;
            case 10:
                layoutId = R.layout.home_activity_bestrestaurant;
                break;
            case 15:
                layoutId = R.layout.home_activity_townking;
                break;
            case 14:
                layoutId = R.layout.home_activity_buraans;
                break;
            case 18:
                layoutId = R.layout.home_activity_theme_5;
                break;
            case 21:
                layoutId = R.layout.home_activity_amritsarizaika;
                break;
            default:
                layoutId = R.layout.home_activity;
                break;

        }
        return layoutId;
    }

}

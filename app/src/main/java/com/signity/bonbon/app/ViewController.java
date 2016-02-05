package com.signity.bonbon.app;

import android.content.Context;
import android.support.v4.app.Fragment;

import com.signity.bonbon.R;
import com.signity.bonbon.Utilities.PrefManager;
import com.signity.bonbon.ui.fragment.HomeFragment;
import com.signity.bonbon.ui.restaurant.storeamritsarizaika21.fragment.HomeFragAmritsarizaikaTheme5;
import com.signity.bonbon.ui.restaurant.storeburaan14.fragment.HomeFragmentBuraanTheme6;
import com.signity.bonbon.ui.restaurant.storetangerine9.fragment.HomeFragmentTangerineTheme1;
import com.signity.bonbon.ui.restaurant.storetheme3.fragment.HomeFragmentTheme3;
import com.signity.bonbon.ui.restaurant.storetheme4.fragment.HomeFragmentTheme4;
import com.signity.bonbon.ui.restaurant.storetownking15.fragment.HomeFragmentTownkingTheme2;

/**
 * Created by rajesh on 19/1/16.
 */
public class ViewController {

    Context context;

    public ViewController(Context context) {
        this.context = context;
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
            case 3:
                fragment = new HomeFragmentTheme3();
                break;
            case 4:
                fragment = new HomeFragmentTheme4();
                break;
            case 5:
                fragment = new HomeFragAmritsarizaikaTheme5();
                break;
            case 6:
                fragment = new HomeFragmentBuraanTheme6();
                break;
            case 11:
//                fragment = new HomeFragmentTheme11();
                break;
            default:
                fragment = new HomeFragment();
                break;
        }
        return fragment;
    }

    public int getHomeResourceLayout() {
        PrefManager prefManager = new PrefManager(context);
        String themeId = prefManager.getProjectTheme();
        int layoutId=0;
        switch (Integer.parseInt(themeId)) {
            case 1:
                // tangerine
                layoutId = R.layout.home_activity_tangerine;
                break;
            case 2:
                // best and townking using theme 2
                layoutId = R.layout.home_activity_townking;
                break;
            case 3:
                // Desitadka and f eleven using theme3
                layoutId = R.layout.home_activity_theme_3;
                break;
            case 4:
                // Mr Rooster and signh eleven
                layoutId = R.layout.home_activity_theme_4;
                break;
            case 5:
                // Bluechillis and Amritsari zaika using theme5
                layoutId = R.layout.home_activity_amritsarizaika_theme_5;
                break;
            case 6:
                //Buraans
                layoutId = R.layout.home_activity_buraans;
                break;
            case 11:
//                layoutId = R.layout.home_activity_theme_11;
                break;
            default:
                layoutId = R.layout.home_activity;
                break;

        }
        return layoutId;
    }

}

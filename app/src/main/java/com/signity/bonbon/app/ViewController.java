package com.signity.bonbon.app;

import android.content.Context;
import android.support.v4.app.Fragment;

import com.signity.bonbon.R;
import com.signity.bonbon.Utilities.AppConstant;
import com.signity.bonbon.Utilities.PrefManager;
import com.signity.bonbon.ui.category.CategoryDetailActivity;
import com.signity.bonbon.ui.category.CategoryDetailGroceryActivity;
import com.signity.bonbon.ui.category.ProductViewActivity;
import com.signity.bonbon.ui.category.ProductViewGroceryActivity;
import com.signity.bonbon.ui.fragment.BookNowFragment;
import com.signity.bonbon.ui.fragment.HomeFragment;
import com.signity.bonbon.ui.fragment.MyFavourite;
import com.signity.bonbon.ui.fragment.MyFavouriteGroceryFragment;
import com.signity.bonbon.ui.grocery.storetheme1.fragment.HomeFragmentTheme1Grocery;
import com.signity.bonbon.ui.grocery.storetheme10.fragment.HomeFragmentTheme10Grocery;
import com.signity.bonbon.ui.grocery.storetheme19.fragment.HomeFragmentEgrocersTheme19;
import com.signity.bonbon.ui.grocery.storetheme4.fragment.HomeFragmentTheme4Grocery;
import com.signity.bonbon.ui.grocery.storetheme7.fragment.HomeFragmentTheme7Grocery;
import com.signity.bonbon.ui.grocery.storetheme8.fragment.HomeFragmentTheme8Grocery;
import com.signity.bonbon.ui.grocery.storetheme9.fragment.HomeFragmentTheme9Grocery;
import com.signity.bonbon.ui.restaurant.storeamritsarizaika21.fragment.HomeFragAmritsarizaikaTheme5;
import com.signity.bonbon.ui.restaurant.storeburaan14.fragment.HomeFragmentBuraanTheme6;
import com.signity.bonbon.ui.restaurant.storetangerine9.fragment.HomeFragmentTangerineTheme1;
import com.signity.bonbon.ui.restaurant.storetheme11.fragment.HomeFragmentTheme11;
import com.signity.bonbon.ui.restaurant.storetheme13.fragment.HomeFragmentKhaneTheme13;
import com.signity.bonbon.ui.restaurant.storetheme14.fragment.HomeFragmentLazeezTheme14;
import com.signity.bonbon.ui.restaurant.storetheme15.fragment.HomeFragmentNukkarTheme15;
import com.signity.bonbon.ui.restaurant.storetheme16.HomeFragmentTheme16;
import com.signity.bonbon.ui.restaurant.storetheme17.fragment.HomeFragmentFoodTheme17;
import com.signity.bonbon.ui.restaurant.storetheme18.HomeFragmentTheme18;
import com.signity.bonbon.ui.restaurant.storetheme20.HomeFragmentTheme20;
import com.signity.bonbon.ui.restaurant.storetheme3.fragment.HomeFragmentTheme3;
import com.signity.bonbon.ui.restaurant.storetheme4.fragment.HomeFragmentTheme4;
import com.signity.bonbon.ui.restaurant.storetheme7.fragment.HomeFragmentHundredTheme7;
import com.signity.bonbon.ui.restaurant.storetownking15.fragment.HomeFragmentTownkingTheme2;
import com.signity.bonbon.ui.search.SearchActivity;
import com.signity.bonbon.ui.search.SearchForGroceryActivity;
import com.signity.bonbon.ui.shopping.ShoppingList;

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
        String storeType = prefManager.getProjectType();

        switch (Integer.parseInt(themeId)) {
            case 1:
                if (storeType.equalsIgnoreCase(AppConstant.APP_TYPE_GROCERY)) {
                    fragment = new HomeFragmentTheme1Grocery();
                } else {
                    fragment = new HomeFragmentTangerineTheme1();
                }
                break;
            case 2:
                fragment = new HomeFragmentTownkingTheme2();
                break;
            case 3:
                fragment = new HomeFragmentTheme3();
                break;
            case 4:
                if (storeType.equalsIgnoreCase(AppConstant.APP_TYPE_GROCERY)) {
                    fragment = new HomeFragmentTheme4Grocery();
                } else {
                    fragment = new HomeFragmentTheme4();
                }

                break;
            case 5:
                fragment = new HomeFragAmritsarizaikaTheme5();
                break;
            case 6:
                fragment = new HomeFragmentBuraanTheme6();
                break;
            case 7:
                if (storeType.equalsIgnoreCase(AppConstant.APP_TYPE_GROCERY)) {
                    fragment = new HomeFragmentTheme7Grocery();
                } else {
                    fragment = new HomeFragmentHundredTheme7();
                }
                break;
            case 8:
                if (storeType.equalsIgnoreCase(AppConstant.APP_TYPE_GROCERY)) {
                    fragment = new HomeFragmentTheme8Grocery();
                } else {

                }
                break;
            case 9:
                if (storeType.equalsIgnoreCase(AppConstant.APP_TYPE_GROCERY)) {
                    fragment = new HomeFragmentTheme9Grocery();
                } else {

                }
                break;
            case 10:
                if (storeType.equalsIgnoreCase(AppConstant.APP_TYPE_GROCERY)) {
                    fragment = new HomeFragmentTheme10Grocery();
                } else {

                }
                break;
            case 11:
                fragment = new HomeFragmentTheme11();
                break;
            case 13:
                fragment = new HomeFragmentKhaneTheme13();
                break;
            case 14:
                fragment = new HomeFragmentLazeezTheme14();
                break;
            case 15:
                fragment = new HomeFragmentNukkarTheme15();
                break;
            case 16:
                fragment = new HomeFragmentTheme16();
                break;
            case 17:
                fragment = new HomeFragmentFoodTheme17();
                break;
            case 18:
                fragment = new HomeFragmentTheme18();
                break;

            case 19:
                if (storeType.equalsIgnoreCase(AppConstant.APP_TYPE_GROCERY)) {
                    fragment = new HomeFragmentEgrocersTheme19();
                } else {

                }
                break;
            case 20:
                fragment = new HomeFragmentTheme20();
                break;

            default:
                fragment = new HomeFragment();
                break;
        }
        return fragment != null ? fragment : new HomeFragment();
    }

    public int getHomeResourceLayout() {
        PrefManager prefManager = new PrefManager(context);
        String themeId = prefManager.getProjectTheme();
        String storeType = prefManager.getProjectType();

        int layoutId = 0;
        switch (Integer.parseInt(themeId)) {
            case 1:
                // tangerine
                if (storeType.equalsIgnoreCase(AppConstant.APP_TYPE_GROCERY)) {
                    layoutId = R.layout.home_activity_theme_1_grocery;
                } else {
                    layoutId = R.layout.home_activity_tangerine;
                }
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
                if (storeType.equalsIgnoreCase(AppConstant.APP_TYPE_GROCERY)) {
                    //Himachal Traders
                    layoutId = R.layout.home_activity_theme_4_grocery;
                } else {
                    // Mr Rooster and signh eleven
                    layoutId = R.layout.home_activity_theme_4;
                }
                break;
            case 5:
                // Bluechillis and Amritsari zaika using theme5
                layoutId = R.layout.home_activity_amritsarizaika_theme_5;
                break;
            case 6:
                //Buraans
                layoutId = R.layout.home_activity_buraans;
                break;
            case 7:
                //Six ten grocery
                if (storeType.equalsIgnoreCase(AppConstant.APP_TYPE_GROCERY)) {
                    layoutId = R.layout.home_activity_theme_7_gorcery;
                } else {
                    //Hundred SPices
                    layoutId = R.layout.home_activity_theme_7;
                }
                break;
            case 8:
                // Empire
                if (storeType.equalsIgnoreCase(AppConstant.APP_TYPE_GROCERY)) {
                    layoutId = R.layout.home_activity_theme_8_grocery;
                } else {
                }
                break;

            case 9:

                if (storeType.equalsIgnoreCase(AppConstant.APP_TYPE_GROCERY)) {
                    layoutId = R.layout.home_activity_theme_9_grocery;
                } else {
                }
                break;
            case 10:
                // Kumar Mega Mall
                if (storeType.equalsIgnoreCase(AppConstant.APP_TYPE_GROCERY)) {
                    layoutId = R.layout.home_activity_theme_10_grocery;
                } else {
                }
                break;
            case 11:
                //Chhatwal Haveli
                layoutId = R.layout.home_activity_theme_11;
                break;
            case 13:
                //Khane Khaas
                layoutId = R.layout.home_activity_theme_13;
                break;
            case 14:
                //Lazeez Rasoi
                layoutId = R.layout.home_activity_theme_14;
                break;

            case 15:
                //Nukkar Dhaba
                layoutId = R.layout.home_activity_theme_15;
                break;
            case 16:
                //La piano
                layoutId = R.layout.home_activity_theme_16;
                break;
            case 17:
                //Food o Nine
                layoutId = R.layout.home_activity_theme_17;
                break;
            case 18:
                //Cafe sweet
                layoutId = R.layout.home_activity_theme_18;
                break;

            case 19:
                //E Grocers
                layoutId = R.layout.home_activity_theme_19_grocery;
                break;
            case 20:
                //Sam Pizza
                layoutId = R.layout.home_activity_theme_20;
                break;
            default:
                layoutId = R.layout.home_activity;
                break;

        }
        return layoutId != 0 ? layoutId : R.layout.home_activity;
    }

    public Class getCategoryDetailActivity() {
        PrefManager prefManager = new PrefManager(context);
        String themeId = prefManager.getProjectTheme();
        String storeType = prefManager.getProjectType();

        if (storeType.equalsIgnoreCase(AppConstant.APP_TYPE_GROCERY)) {
            return CategoryDetailGroceryActivity.class;
        } else {
            return CategoryDetailActivity.class;
        }
    }

    public Class getSearchActivity() {

        PrefManager prefManager = new PrefManager(context);
        String storeType = prefManager.getProjectType();
        if (storeType.equalsIgnoreCase("grocery")) {
            return SearchForGroceryActivity.class;
        } else {
            return SearchActivity.class;
        }
    }

    public Class getProductViewActivity() {

        PrefManager prefManager = new PrefManager(context);
        String storeType = prefManager.getProjectType();
        if (storeType.equalsIgnoreCase("grocery")) {
            return ProductViewGroceryActivity.class;
        } else {
            return ProductViewActivity.class;
        }
    }

    public Fragment getFavouritesFragment() {
        PrefManager prefManager = new PrefManager(context);
        String storeType = prefManager.getProjectType();
        if (storeType.equalsIgnoreCase("grocery")) {
            return new MyFavouriteGroceryFragment();
        } else {
            return new MyFavourite();
        }
    }

    public Fragment getBookNowOrShoppinFragment() {
        PrefManager prefManager = new PrefManager(context);
        String storeType = prefManager.getProjectType();
        if (storeType.equalsIgnoreCase(AppConstant.APP_TYPE_GROCERY)) {
            return new ShoppingList();
        } else {
            return new BookNowFragment();
        }
    }

    public String getMenuTextBookNow() {
        PrefManager prefManager = new PrefManager(context);
        String storeType = prefManager.getProjectType();
        if (storeType.equalsIgnoreCase(AppConstant.APP_TYPE_RESTAURANT)) {
            return "Book Now";
        } else {
            return "Shopping List";
        }
    }

}

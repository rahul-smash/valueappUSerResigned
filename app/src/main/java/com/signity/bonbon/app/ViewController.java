package com.signity.bonbon.app;

import android.content.Context;
import android.support.v4.app.Fragment;

import com.signity.bonbon.R;
import com.signity.bonbon.Utilities.AppConstant;
import com.signity.bonbon.Utilities.PrefManager;
import com.signity.bonbon.ui.Jewellers.storetheme28.HomeFragmentAppleTheme28;
import com.signity.bonbon.ui.Others.storetheme27.HomeFragmentNanuTheme27;
import com.signity.bonbon.ui.category.CategoryDetailActivity;
import com.signity.bonbon.ui.category.CategoryDetailGroceryActivity;
import com.signity.bonbon.ui.category.CategoryDetailJewellersActivity;
import com.signity.bonbon.ui.category.ProductViewActivity;
import com.signity.bonbon.ui.category.ProductViewGroceryActivity;
import com.signity.bonbon.ui.category.ProductViewJewellersActivity;
import com.signity.bonbon.ui.fragment.BookNowFragment;
import com.signity.bonbon.ui.fragment.HomeFragment;
import com.signity.bonbon.ui.fragment.MyFavourite;
import com.signity.bonbon.ui.fragment.MyFavouriteGroceryFragment;
import com.signity.bonbon.ui.fragment.MyFavouriteJewellers;
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
import com.signity.bonbon.ui.restaurant.storetheme21.HomeFragmentWahTheme21;
import com.signity.bonbon.ui.restaurant.storetheme22.HomeFragmentBeliramTheme22;
import com.signity.bonbon.ui.restaurant.storetheme23.HomeFragmentChawlasTheme23;
import com.signity.bonbon.ui.restaurant.storetheme24.HomeFragmentCrazyTheme24;
import com.signity.bonbon.ui.restaurant.storetheme25.fragment.HomeFragmentBigChefTheme25;
import com.signity.bonbon.ui.restaurant.storetheme3.fragment.HomeFragmentTheme3;
import com.signity.bonbon.ui.restaurant.storetheme30.HomeFragmentManjeetTheme30;
import com.signity.bonbon.ui.restaurant.storetheme31.HomeFragmentSuruchiTheme31;
import com.signity.bonbon.ui.restaurant.storetheme32.HomeFragmentBurgerTheme32;
import com.signity.bonbon.ui.restaurant.storetheme4.fragment.HomeFragmentTheme4;
import com.signity.bonbon.ui.restaurant.storetheme52.HomeFragmentKingShakeTheme52;
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
                if (storeType.equalsIgnoreCase(AppConstant.KEY_STORE_TYPE_GROCERY)) {
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
                if (storeType.equalsIgnoreCase(AppConstant.KEY_STORE_TYPE_GROCERY)) {
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
                if (storeType.equalsIgnoreCase(AppConstant.KEY_STORE_TYPE_GROCERY)) {
                    fragment = new HomeFragmentTheme7Grocery();
                } else {
                    fragment = new HomeFragmentHundredTheme7();
                }
                break;
            case 8:
                if (storeType.equalsIgnoreCase(AppConstant.KEY_STORE_TYPE_GROCERY)) {
                    fragment = new HomeFragmentTheme8Grocery();
                } else {

                }
                break;
            case 9:
                if (storeType.equalsIgnoreCase(AppConstant.KEY_STORE_TYPE_GROCERY)) {
                    fragment = new HomeFragmentTheme9Grocery();
                } else {

                }
                break;
            case 10:
                if (storeType.equalsIgnoreCase(AppConstant.KEY_STORE_TYPE_GROCERY)) {
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
                if (storeType.equalsIgnoreCase(AppConstant.KEY_STORE_TYPE_GROCERY)) {
                    fragment = new HomeFragmentEgrocersTheme19();
                } else {

                }
                break;
            case 20:
                fragment = new HomeFragmentTheme20();
                break;
            case 21:
                fragment = new HomeFragmentWahTheme21();
                break;

            case 22:
                fragment = new HomeFragmentBeliramTheme22();
                break;

            case 23:
                fragment = new HomeFragmentChawlasTheme23();
                break;

            case 24:
                fragment = new HomeFragmentCrazyTheme24();
                break;

            case 25:
                fragment = new HomeFragmentBigChefTheme25();
                break;

            case 27:
                fragment = new HomeFragmentNanuTheme27();
                break;

            case 28:
                if (storeType.equalsIgnoreCase(AppConstant.KEY_STORE_TYPE_JEWELLERY)) {
                    fragment = new HomeFragmentAppleTheme28();
                }
                break;

            case 30:
                if (storeType.equalsIgnoreCase(AppConstant.KEY_STORE_TYPE_RESTAURANT)) {
                    fragment = new HomeFragmentManjeetTheme30();
                }
                break;


            case 31:
                if (storeType.equalsIgnoreCase(AppConstant.KEY_STORE_TYPE_RESTAURANT)) {
                    fragment = new HomeFragmentSuruchiTheme31();
                }
                break;

            case 32:
                if (storeType.equalsIgnoreCase(AppConstant.KEY_STORE_TYPE_RESTAURANT)) {
                    fragment = new HomeFragmentBurgerTheme32();
                }
                break;


            case 34:
                if (storeType.equalsIgnoreCase(AppConstant.KEY_STORE_TYPE_RESTAURANT)) {
                    fragment = new HomeFragmentTheme20();
                }
                break;


            case 36:
                if (storeType.equalsIgnoreCase(AppConstant.KEY_STORE_TYPE_RESTAURANT)) {
                    fragment = new HomeFragmentBeliramTheme22();
                }
                break;
            case 52:
                if (storeType.equalsIgnoreCase(AppConstant.KEY_STORE_TYPE_RESTAURANT)) {
                    fragment = new HomeFragmentKingShakeTheme52();
                }
                break;
            case 53:
                if (storeType.equalsIgnoreCase(AppConstant.KEY_STORE_TYPE_RESTAURANT)) {
                    fragment = new HomeFragmentKingShakeTheme52();
                }
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
                if (storeType.equalsIgnoreCase(AppConstant.KEY_STORE_TYPE_GROCERY)) {
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
                if (storeType.equalsIgnoreCase(AppConstant.KEY_STORE_TYPE_GROCERY)) {
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
                if (storeType.equalsIgnoreCase(AppConstant.KEY_STORE_TYPE_GROCERY)) {
                    layoutId = R.layout.home_activity_theme_7_gorcery;
                } else {
                    //Hundred SPices
                    layoutId = R.layout.home_activity_theme_7;
                }
                break;
            case 8:
                // Empire
                if (storeType.equalsIgnoreCase(AppConstant.KEY_STORE_TYPE_GROCERY)) {
                    layoutId = R.layout.home_activity_theme_8_grocery;
                } else {
                }
                break;

            case 9:

                if (storeType.equalsIgnoreCase(AppConstant.KEY_STORE_TYPE_GROCERY)) {
                    layoutId = R.layout.home_activity_theme_9_grocery;
                } else {
                }
                break;
            case 10:
                // Kumar Mega Mall
                if (storeType.equalsIgnoreCase(AppConstant.KEY_STORE_TYPE_GROCERY)) {
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
//                layoutId = R.layout.home_activity_theme_13;
                //Bikanervala
                layoutId = R.layout.home_activity_theme_13_bikanervala;
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
//                layoutId = R.layout.home_activity_theme_18;

                //Chawlas2 Chandigarh
                layoutId = R.layout.home_activity_theme_18_chawlas2;
                break;

            case 19:
                //E Grocers
                layoutId = R.layout.home_activity_theme_19_grocery;
                break;
            case 20:
                //Sam Pizza
                layoutId = R.layout.home_activity_theme_20;
                break;
            case 21:
                //Wah ji Wah and 56 Street.
//                layoutId = R.layout.home_activity_theme_21;
                layoutId = R.layout.home_activity_theme_21_56street;
                break;
            case 22:
                //Beliran Degchiwala
                layoutId = R.layout.home_activity_theme_22;
                break;

            case 23:
                //Chawla 2
                layoutId = R.layout.home_activity_theme_23;
                break;
            case 24:
                //Crazy Chicken
                layoutId = R.layout.home_activity_theme_24;
                break;
            case 25:
                //Big Chef
                layoutId = R.layout.home_activity_theme_25;
                break;

            case 27:

                //Nanu'z Garments
                layoutId = R.layout.home_activity_theme_27;
                break;

            case 28:
                //Apple Diamond
                if (storeType.equalsIgnoreCase(AppConstant.KEY_STORE_TYPE_JEWELLERY)) {
                    layoutId = R.layout.home_activity_theme_28_apple;
                }
                break;

            case 30:
                //Manjeet
                if (storeType.equalsIgnoreCase(AppConstant.KEY_STORE_TYPE_RESTAURANT)) {
                    layoutId = R.layout.home_activity_theme_30_manjeet;
                }
                break;

            case 31:
                //Suruchi Bhog and big wich
                if (storeType.equalsIgnoreCase(AppConstant.KEY_STORE_TYPE_RESTAURANT)) {
//                    layoutId = R.layout.home_activity_theme_31_suruchi;
                    layoutId = R.layout.home_activity_theme_31_bigwich;
                }
                break;

            case 32:
                //Burger Point chd
                if (storeType.equalsIgnoreCase(AppConstant.KEY_STORE_TYPE_RESTAURANT)) {
                    layoutId = R.layout.home_activity_theme_32_burger;
                }
                break;

            case 34:
                //Dunkin Donuts
                if (storeType.equalsIgnoreCase(AppConstant.KEY_STORE_TYPE_RESTAURANT)) {
                    layoutId = R.layout.home_activity_theme_34_dunkin;
                }
                break;

            case 36:
                //Garden Chef
                if (storeType.equalsIgnoreCase(AppConstant.KEY_STORE_TYPE_RESTAURANT)) {
                    layoutId = R.layout.home_activity_theme_36_garden;
                }
                break;

            case 52:
                //King of Shakes
                if (storeType.equalsIgnoreCase(AppConstant.KEY_STORE_TYPE_RESTAURANT)) {
                    layoutId = R.layout.home_activity_theme_52_king_shake;
                }
                break;

            case 53:
                //Rajpoot
                if (storeType.equalsIgnoreCase(AppConstant.KEY_STORE_TYPE_RESTAURANT)) {
                    layoutId = R.layout.home_activity_theme_52_king_shake;
                }
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

        if (storeType.equalsIgnoreCase(AppConstant.KEY_STORE_TYPE_GROCERY)) {
            return CategoryDetailGroceryActivity.class;
        } else if (storeType.equalsIgnoreCase(AppConstant.KEY_STORE_TYPE_JEWELLERY)) {
            return CategoryDetailJewellersActivity.class;
        } else {
            return CategoryDetailActivity.class;
        }
    }

    public Class getSearchActivity() {

        PrefManager prefManager = new PrefManager(context);
        String storeType = prefManager.getProjectType();
        if (storeType.equalsIgnoreCase(AppConstant.KEY_STORE_TYPE_GROCERY)) {
            return SearchForGroceryActivity.class;
        } else {
            return SearchActivity.class;
        }
    }

    public Class getProductViewActivity() {

        PrefManager prefManager = new PrefManager(context);
        String storeType = prefManager.getProjectType();
        if (storeType.equalsIgnoreCase(AppConstant.KEY_STORE_TYPE_GROCERY)) {
            return ProductViewGroceryActivity.class;
        } else if (storeType.equalsIgnoreCase(AppConstant.KEY_STORE_TYPE_JEWELLERY)) {
            return ProductViewJewellersActivity.class;
        } else {
            return ProductViewActivity.class;
        }
    }

    public Fragment getFavouritesFragment() {
        PrefManager prefManager = new PrefManager(context);
        String storeType = prefManager.getProjectType();
        if (storeType.equalsIgnoreCase(AppConstant.KEY_STORE_TYPE_GROCERY)) {
            return new MyFavouriteGroceryFragment();
        } else if (storeType.equalsIgnoreCase(AppConstant.KEY_STORE_TYPE_JEWELLERY)) {
            return new MyFavouriteJewellers();
        } else {
            return new MyFavourite();
        }
    }

    public Fragment getBookNowOrShoppinFragment() {
        PrefManager prefManager = new PrefManager(context);
        String storeType = prefManager.getProjectType();
        if (storeType.equalsIgnoreCase(AppConstant.KEY_STORE_TYPE_GROCERY)) {
            return new ShoppingList();
        } else {
            return new BookNowFragment();
        }
    }

    public String getMenuTextBookNow() {
        PrefManager prefManager = new PrefManager(context);
        String storeType = prefManager.getProjectType();
        if (storeType.equalsIgnoreCase(AppConstant.KEY_STORE_TYPE_RESTAURANT)) {
            return context.getString(R.string.lbl_title_book_now);
        } else {
            return context.getString(R.string.lbl_title_shop_listing);
        }
    }

}

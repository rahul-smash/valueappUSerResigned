package com.signity.bonbon.ui.home;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.SlidingPaneLayout;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.GooglePlayServicesUtil;
import com.signity.bonbon.BuildConfig;
import com.signity.bonbon.R;
import com.signity.bonbon.Utilities.AnimUtil;
import com.signity.bonbon.Utilities.AppConstant;
import com.signity.bonbon.Utilities.DialogHandler;
import com.signity.bonbon.Utilities.PrefManager;
import com.signity.bonbon.app.AppController;
import com.signity.bonbon.app.DataAdapter;
import com.signity.bonbon.app.DbAdapter;
import com.signity.bonbon.app.ViewController;
import com.signity.bonbon.db.AppDatabase;
import com.signity.bonbon.ga.GATrackers;
import com.signity.bonbon.geofence.GeofenceController;
import com.signity.bonbon.geofence.NamedGeofence;
import com.signity.bonbon.model.ForceDownloadModel;
import com.signity.bonbon.model.GeofenceObjectModel;
import com.signity.bonbon.model.GetStoreModel;
import com.signity.bonbon.model.SliderObject;
import com.signity.bonbon.model.Store;
import com.signity.bonbon.network.NetworkAdaper;
import com.signity.bonbon.ui.AboutUs.AboutUsFragment;
import com.signity.bonbon.ui.Delivery.DeliveryActivity;
import com.signity.bonbon.ui.Location.SelectLocationActivity;
import com.signity.bonbon.ui.Notifications.NotificationsDetailActivity;
import com.signity.bonbon.ui.SharenEarn.ShareNEarnFragment;
import com.signity.bonbon.ui.fragment.LoyalityFragment;
import com.signity.bonbon.ui.fragment.Profile;
import com.signity.bonbon.ui.login.LoginScreenActivity;
import com.signity.bonbon.ui.order.OrderHistory;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private static final int REQUESTCODE_FOR_SUCESS_LOGIN = 328;
    public TextView title, user;
    SlidingPaneLayout mSlidingPanel;
    ListView mMenuList;
    ImageButton search, shopingcart;
    Button menu, citySelect;
    ImageView profilePic;
    String[] labels = {"Home", "My Profile", "Delivery Address", "My Orders",
            "Book Now", "My Favorites", "About Us", "Share", "Loyalty Points", "Log In"};
    Integer[] icons = {R.drawable.ic_home, R.drawable.profil_icon, R.drawable.address_icon,
            R.drawable.order_icon,
            R.drawable.my_shopping_list_icon, R.drawable.my_fav_icon,
            R.drawable.faq, R.drawable.ic_share, R.drawable.loyality, R.drawable.sign_out};
    ArrayList<SliderObject> viewList = new ArrayList<SliderObject>();
    AppDatabase appDb;
    Adapter adapter;
    PrefManager prefManager;
    String userId;
    String storeId, areaName;
    String name;
    String phone;
    Context context;
    Store store;
    // Controller;
    ViewController viewController;
    boolean isActivityLoadsFirstTime = true;
    private String TAG = MainActivity.class.getSimpleName();
    private String loyalityStatus;
    private GeofenceController.GeofenceControllerListener geofenceControllerListener = new GeofenceController.GeofenceControllerListener() {
        @Override
        public void onGeofencesUpdated() {
        }

        @Override
        public void onError() {
            Log.i("Geo Fence", "Error on Fence Created");
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.slider_pane);
        context = this;
        GATrackers.getInstance().
                trackScreenView(getString(R.string.ga_home_screen));
        prefManager = new PrefManager(MainActivity.this);
        viewController = AppController.getInstance().getViewController();

        getPreferencesValues(); // getting values from prefrences


        appDb = DbAdapter.getInstance().getDb();

        mSlidingPanel = (SlidingPaneLayout) findViewById(R.id.SlidingPanel);
        menu = (Button) findViewById(R.id.menu);

        citySelect = (Button) findViewById(R.id.citySelect);
        areaName = prefManager.getSharedValue(AppConstant.AREA_NAME);
        citySelect.setText("" + areaName);

        search = (ImageButton) findViewById(R.id.search);
        search.setVisibility(View.GONE);
        title = (TextView) findViewById(R.id.title);
        user = (TextView) findViewById(R.id.user);

        title.setVisibility(View.VISIBLE);
        citySelect.setVisibility(View.GONE);

        updateUserName();

        mMenuList = (ListView) findViewById(R.id.menulist);
        shopingcart = (ImageButton) findViewById(R.id.shopingcart);
        profilePic = (ImageView) findViewById(R.id.profile_pic);

        labels[4] = AppController.getInstance().getViewController().getMenuTextBookNow();

        for (int i = 0; i < labels.length; i++) {
            SliderObject att = new SliderObject();
            att.labels = labels[i];
            att.icons = icons[i];
            viewList.add(att);
        }

        if (loyalityStatus.equalsIgnoreCase("0")) {
            viewList.remove(8);
        }

        adapter = new Adapter(MainActivity.this);
        mMenuList.setAdapter(adapter);

        mSlidingPanel.setParallaxDistance(300);
        mSlidingPanel.setSliderFadeColor(200);

        menu.setOnClickListener(this);
        search.setOnClickListener(this);
        profilePic.setOnClickListener(this);
        citySelect.setOnClickListener(this);

        store = appDb.getStore(storeId);
        updateStoreInfo(store);

        setUpFenceAroundStore();

        Fragment fragment = viewController.getHomeFragment();
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.container, fragment).commit();


        if (prefManager.getReferEarnPopupCheck()) {
            if (prefManager.isReferEarnFnEnableForDevice() && prefManager.isReferEarnFn()) {
                if (userId.isEmpty()) {
                    prefManager.setReferEarnPopupCheck(false);
                    String msg = "Kindly Login with Referral Code for " + store.getStoreName() + " and Earn Free Coupons.";
                    showReferAndEarnDialog(MainActivity.this, "Enter Referral Code", msg);
                }
            }
        }

        showNotificationPopUp();


    }

    private void showNotificationPopUp() {
        String message = null;
        try {
            message = DataAdapter.getInstance().getNotificationMessage();
        } catch (Exception e) {
            message = null;
            e.printStackTrace();
        }

        if (message != null && !message.isEmpty()) {
            Intent intent = new Intent(MainActivity.this, NotificationsDetailActivity.class);
            startActivity(intent);
        }
    }

    private void getPreferencesValues() {

        userId = prefManager.getSharedValue(AppConstant.ID);
        name = prefManager.getSharedValue(AppConstant.NAME);
        phone = prefManager.getSharedValue(AppConstant.PHONE);
        storeId = prefManager.getSharedValue(AppConstant.STORE_ID);
        loyalityStatus = prefManager.getSharedValue(AppConstant.LOYALITY);
    }

    private void setUpFenceAroundStore() {

        if (prefManager.getGeoFenceEnableStatus().equalsIgnoreCase("1")) {
            List<GeofenceObjectModel> listFences = DataAdapter.getInstance().getListGeoFence();
            if (listFences != null && listFences.size() != 0) {
                for (GeofenceObjectModel geofenceObjectModel : listFences) {
                    if (geofenceObjectModel.getStatus()) {
                        addFence(geofenceObjectModel);
                    } else {
                        removeFence(geofenceObjectModel);
                    }
                }
            }
            DataAdapter.getInstance().setListGeoFence(null);
        }
    }

    private void removeFence(GeofenceObjectModel geofenceObjectModel) {


        SharedPreferences geoPref = getSharedPreferences(PrefManager.SharedPrefs.Geofences, Context.MODE_PRIVATE);
        try {
            NamedGeofence geofence = new NamedGeofence();
            geofence.id = store.getId() + "_" + geofenceObjectModel.getId();
            geofence.name = store.getStoreName();
            geofence.message = geofenceObjectModel.getMessage();
            geofence.storeId = store.getId();
            geofence.latitude = Double.parseDouble(geofenceObjectModel.getLat());
            geofence.longitude = Double.parseDouble(geofenceObjectModel.getLng());
            geofence.radius = Float.parseFloat(geofenceObjectModel.getRadius()) * 1.0f;
//            geofence.radius = Float.parseFloat(context.getString(R.string.fence_radius)) * 1000.0f;
            if (!(geoPref.getString(geofence.id, "").equalsIgnoreCase(""))) {
                GeofenceController.getInstance().removeGeofence(geofence, geofenceControllerListener);
                Log.e(TAG, "GeoFence with " + geofence.id + " " + geofence.latitude + " " + geofence.longitude + " at Location " + geofence.name + " Removed");
            }

        } catch (NumberFormatException e) {
            e.printStackTrace();
        }

    }

    private void addFence(GeofenceObjectModel geofenceObjectModel) {
        try {
            if (dataIsValid(geofenceObjectModel)) {
                NamedGeofence geofence = new NamedGeofence();
                geofence.id = store.getId() + "_" + geofenceObjectModel.getId();
                geofence.name = store.getStoreName();
                geofence.message = geofenceObjectModel.getMessage();
                geofence.storeId = store.getId();
                geofence.latitude = Double.parseDouble(geofenceObjectModel.getLat());
                geofence.longitude = Double.parseDouble(geofenceObjectModel.getLng());
                geofence.radius = Float.parseFloat(geofenceObjectModel.getRadius()) * 1.0f;
//                geofence.radius = Float.parseFloat(context.getString(R.string.fence_radius)) * 1000.0f;
                GeofenceController.getInstance().addGeofence(geofence, geofenceControllerListener);
                Log.e(TAG, "GeoFence with " + geofence.id + " " + geofence.latitude + " " + geofence.longitude + " at Location " + geofence.name + " created");

            } else {
                Log.e(TAG, "No latitude longitude available for geofence");
            }
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }

    }

    private boolean dataIsValid(GeofenceObjectModel geofenceObjectModel) {
        boolean status = false;

        if (geofenceObjectModel.getLat() != null && geofenceObjectModel.getLng() != null && !(geofenceObjectModel.getLat().isEmpty()) && !(geofenceObjectModel.getLng().isEmpty())
                && !(geofenceObjectModel.getLat().equalsIgnoreCase("0")) && !(geofenceObjectModel.getLng().equalsIgnoreCase("0"))) {
            status = true;
        }
        return status;
    }

    @Override
    protected void onResume() {
        super.onResume();

        getPreferencesValues();
        updateUserName();
        adapter.notifyDataSetChanged();

        int googlePlayServicesCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        Log.i(MainActivity.class.getSimpleName(), "googlePlayServicesCode = " + googlePlayServicesCode);
        if (googlePlayServicesCode == 1 || googlePlayServicesCode == 2 || googlePlayServicesCode == 3) {
            GooglePlayServicesUtil.getErrorDialog(googlePlayServicesCode, this, 0).show();
        } else {
            if (!isActivityLoadsFirstTime) {
//                getStoreServicesForUpdate();
            } else {
                isActivityLoadsFirstTime = false;
            }
            getStoreServicesForUpdate();
        }
        checkForAppVersion(DataAdapter.getInstance().getForceDownloadModel());
    }

    private void updateUserName() {

        if (!name.isEmpty()) {
            user.setText(name);
        } else if (!phone.isEmpty()) {
            user.setText(phone);
        } else {
            user.setText("Guest");
        }
    }

    private void updateStoreInfo(Store store) {
        if (store != null) {
            if (store.getStoreName() != null && !store.getStoreName().isEmpty()) {
                title.setText("" + store.getStoreName());
            } else {
                title.setText("");
            }
        } else {
            title.setText("");
        }

    }

    public void toggleSlidingMenu() {
        if (!mSlidingPanel.isOpen()) {
            mSlidingPanel.openPane();
        } else {
            mSlidingPanel.closePane();
        }

        if (getCurrentFocus() != null) {
            InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.menu:
                toggleSlidingMenu();
                break;
            case R.id.search:
                startActivity(new Intent(MainActivity.this, AppController.getInstance().getViewController().getSearchActivity()));
                AnimUtil.slideFromRightAnim(MainActivity.this);
                break;
            case R.id.profile_pic:
//                clearBackStack();
                replaceFragment(0);
                mSlidingPanel.closePane();
                break;
            case R.id.citySelect:
                Intent intent_location = new Intent(MainActivity.this, SelectLocationActivity.class);
                startActivity(intent_location);
                finish();
                break;
        }
    }

    private void clearBackStack() {
        FragmentManager fm = getSupportFragmentManager();
        fm.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
    }

    private void replaceFragment(int position) {
        Fragment fragment;
        switch (position) {

            case 0:
                if (store != null) {
                    if (store.getStoreName() != null && !store.getStoreName().isEmpty()) {
                        title.setVisibility(View.VISIBLE);
                        citySelect.setVisibility(View.GONE);
                        title.setText("" + store.getStoreName());
                    } else {
                        title.setText("");
                    }
                }
                replace(viewController.getHomeFragment());
                break;
            case 1:

                if (userId.isEmpty()) {
                    Intent intent = new Intent(MainActivity.this, LoginScreenActivity.class);
                    intent.putExtra(AppConstant.FROM, "menu");
                    startActivityForResult(intent, REQUESTCODE_FOR_SUCESS_LOGIN);
                    AnimUtil.slideUpAnim(MainActivity.this);
                } else {

                    title.setVisibility(View.VISIBLE);
                    citySelect.setVisibility(View.GONE);
                    title.setText("My Profile");
                    fragment = new Profile();
                    replace(fragment);
                }
                break;

            case 2:
                if (userId.isEmpty()) {
                    Intent intent = new Intent(MainActivity.this, LoginScreenActivity.class);
                    intent.putExtra(AppConstant.FROM, "menu");
                    startActivityForResult(intent, REQUESTCODE_FOR_SUCESS_LOGIN);
                    AnimUtil.slideUpAnim(MainActivity.this);
                } else {
//                    title.setText("Delivery Address");
                    title.setVisibility(View.VISIBLE);
                    citySelect.setVisibility(View.GONE);
                    Intent intentDelivery = new Intent(MainActivity.this, DeliveryActivity.class);
                    intentDelivery.putExtra(AppConstant.FROM, "menu");
                    startActivity(intentDelivery);
                    AnimUtil.slideFromRightAnim(MainActivity.this);
                }
                break;

            case 3:
                if (userId.isEmpty()) {
                    Intent intent = new Intent(MainActivity.this, LoginScreenActivity.class);
                    intent.putExtra(AppConstant.FROM, "menu");
                    startActivityForResult(intent, REQUESTCODE_FOR_SUCESS_LOGIN);
                    AnimUtil.slideUpAnim(MainActivity.this);
                } else {
                    title.setVisibility(View.VISIBLE);
                    citySelect.setVisibility(View.GONE);
                    title.setText("My Orders");
                    fragment = new OrderHistory();
                    replace(fragment);
                }
                break;
            case 4:
                title.setVisibility(View.VISIBLE);
                citySelect.setVisibility(View.GONE);
                title.setText(AppController.getInstance().getViewController().getMenuTextBookNow());
                replace(AppController.getInstance().getViewController().getBookNowOrShoppinFragment());
                break;

            case 5:
                title.setVisibility(View.VISIBLE);
                citySelect.setVisibility(View.GONE);
                title.setText("My Favorites");
                replace(AppController.getInstance().getViewController().getFavouritesFragment());
                break;

            case 6:
                title.setVisibility(View.VISIBLE);
                citySelect.setVisibility(View.GONE);
                title.setText("About Us");
                replace(new AboutUsFragment());
                break;
            case 7:
                if (prefManager.isReferEarnFn() && !userId.isEmpty()) {
                    title.setVisibility(View.VISIBLE);
                    citySelect.setVisibility(View.GONE);
                    title.setText("Refer N Earn");
                    replace(new ShareNEarnFragment());
                    String gaCategory = getString(R.string.ga_catagory_click);
                    String action = getString(R.string.app_name) + "_" + getString(R.string.ga_refer_earn_button);
                    GATrackers.getInstance().trackEvent(gaCategory, action,
                            "");
                } else {
                    String gaCategory = getString(R.string.ga_catagory_share);
                    String action = getString(R.string.app_name) + "_" + getString(R.string.ga_action_app_share);
                    String lbl = String.format(getString(R.string.ga_lbl_share_app), getString(R.string.app_name), name, userId);
                    GATrackers.getInstance().trackEvent(gaCategory, action,
                            lbl);
                    String shareContent =
                            "Kindly download " + store.getStoreName() + " app from " +
                                    store.getAndroidAppShareLink() + "\nThanks and Regards\n " +
                                    store.getStoreName() + "\n" + store.getCity() + " , " + store.getState();
                    Intent intent = new Intent(Intent.ACTION_SEND);
                    intent.setType("text/plain");
                    intent.putExtra(Intent.EXTRA_TEXT, shareContent);
                    intent.putExtra(Intent.EXTRA_SUBJECT, store.getStoreName());
                    startActivity(Intent.createChooser(intent, "Share with"));
                }
                break;
            case 8:
                if (loyalityStatus.equalsIgnoreCase("0")) {
                    title.setText(store.getStoreName());
                    if (userId.isEmpty()) {
                        Intent intentLogin = new Intent(MainActivity.this, LoginScreenActivity.class);
                        intentLogin.putExtra(AppConstant.FROM, "menu");
                        startActivityForResult(intentLogin, REQUESTCODE_FOR_SUCESS_LOGIN);
                        AnimUtil.slideUpAnim(MainActivity.this);
                    } else {
                        title.setVisibility(View.VISIBLE);
                        citySelect.setVisibility(View.GONE);
                        replace(viewController.getHomeFragment());
                        logOutUser();
                    }
                    adapter.notifyDataSetChanged();
                } else if (loyalityStatus.equalsIgnoreCase("1")) {
                    if (userId.isEmpty()) {
                        Intent intent9 = new Intent(MainActivity.this, LoginScreenActivity.class);
                        intent9.putExtra(AppConstant.FROM, "menu");
                        startActivityForResult(intent9, REQUESTCODE_FOR_SUCESS_LOGIN);
                        AnimUtil.slideUpAnim(MainActivity.this);
                    } else {
                        title.setVisibility(View.VISIBLE);
                        citySelect.setVisibility(View.GONE);
                        title.setText("Loyalty Points");
                        fragment = new LoyalityFragment();
                        replace(fragment);
                    }
                }
                break;

            case 9:
                title.setText(store.getStoreName());
                if (userId.isEmpty()) {
                    Intent intentLogin = new Intent(MainActivity.this, LoginScreenActivity.class);
                    intentLogin.putExtra(AppConstant.FROM, "menu");
                    startActivityForResult(intentLogin, REQUESTCODE_FOR_SUCESS_LOGIN);
                    AnimUtil.slideUpAnim(MainActivity.this);
                } else {
                    title.setVisibility(View.VISIBLE);
                    citySelect.setVisibility(View.GONE);
                    replace(viewController.getHomeFragment());
                    logOutUser();
                }
                adapter.notifyDataSetChanged();
                break;


        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQUESTCODE_FOR_SUCESS_LOGIN:
                if (resultCode == Activity.RESULT_OK) {
                    Toast.makeText(context, "Login Successfully!", Toast.LENGTH_SHORT).show();
                    getPreferencesValues();
                    updateUserName();
                    adapter.notifyDataSetChanged();
                } else if (resultCode == Activity.RESULT_CANCELED) {
                    Toast.makeText(context, "Login Failed or Cancel", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    private void logOutUser() {
        PrefManager prefManager = new PrefManager(MainActivity.this);
        prefManager.clearSharedValue(AppConstant.ID);
        prefManager.clearSharedValue(AppConstant.PHONE);
        prefManager.clearSharedValue(AppConstant.NAME);
        prefManager.clearSharedValue(AppConstant.EMAIL);
        user.setText("Guest");
        userId = prefManager.getSharedValue(AppConstant.ID);
        appDb.deleteCartElement();

    }

    public void replace(Fragment fragment) {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.container, fragment).commit();
    }

    @Override
    public void onBackPressed() {

//        FragmentManager fm = getSupportFragmentManager();
//        fm.getFragments();
//        fm.getBackStackEntryCount();
        mSlidingPanel.closePane();
        if (!title.getText().toString().equalsIgnoreCase(store.getStoreName())) {
            if (store != null) {
                if (store.getStoreName() != null && !store.getStoreName().isEmpty()) {
                    title.setVisibility(View.VISIBLE);
                    citySelect.setVisibility(View.GONE);
                    title.setText("" + store.getStoreName());
                } else {
                    title.setText("");
                }
            }
            replace(viewController.getHomeFragment());
        } else {
            MainActivity.super.onBackPressed();
        }

    }


    private void getStoreServicesForUpdate() {

        String deviceid = Settings.Secure.getString(MainActivity.this.getApplicationContext().getContentResolver(), Settings.Secure.ANDROID_ID);
        String deviceToken = prefManager.getSharedValue(AppConstant.DEVICE_TOKEN);

        Map<String, String> param = new HashMap<String, String>();
        param.put("device_id", deviceid);
        param.put("device_token", deviceToken);
        param.put("platform", AppConstant.PLATFORM);

        NetworkAdaper.getInstance().getNetworkServices().getStore(param, new Callback<GetStoreModel>() {
            @Override
            public void success(GetStoreModel getStroeModel, Response response) {
                if (getStroeModel.getSuccess()) {
                    appDb.addStore(getStroeModel.getStore());
                    store = getStroeModel.getStore();
                    prefManager.storeSharedValue(AppConstant.STORE_ID, store.getId());
                    prefManager.storeSharedValue(AppConstant.APP_VERISON, store.getVersion());
                    prefManager.setProjectTheme(store.getTheme());
                    prefManager.setProjectType(store.getType());
                    prefManager.setOtoSkip(store.getOtpSkip());
                    prefManager.setPickupFacilityStatus(store.getPickUpFacility());
                    prefManager.setDeliveryFacilityStatus(store.getDeliveryFacility());
                    prefManager.setInStoreFacilityStatus(store.getInStore());
                    prefManager.storeSharedValue(AppConstant.is24x7_open, store.getIs24x7_open());
                    prefManager.storeSharedValue(AppConstant.OPEN_TIME, store.getOpenhoursFrom());
                    prefManager.storeSharedValue(AppConstant.CLOSE_TIME, store.getOpenhoursTo());
                    prefManager.storeSharedValue(AppConstant.OPEN_DAYS, store.getStoreOpenDays());
                    prefManager.storeSharedValue(AppConstant.MESSAGE, store.getClosehoursMessage());
                    prefManager.storeSharedValue(AppConstant.istaxenable, store.getIstaxenable());
                    prefManager.storeSharedValue(AppConstant.tax_label_name, store.getTaxLabelName());
                    prefManager.storeSharedValue(AppConstant.tax_rate, store.getTaxRate());
                    prefManager.storeSharedValue(AppConstant.ONLINE_PAYMENT, store.getOnlinePayment());
                    prefManager.storeSharedValue(AppConstant.PRODUCT_IMAGE, store.getProductImage());
                    prefManager.storeSharedValue(AppConstant.RECOMMENDED_ITEMS, store.getRecommendedProducts());
                    prefManager.setReferEarnFn(store.getReferFnEnable());
                    prefManager.setReferEarnFnEnableForDevice(store.getReferForDeviceEnable());

                    if (store.getCurrency().isEmpty()) {
                        prefManager.storeSharedValue(AppConstant.CURRENCY, "\\u20B9");
                    } else {

                        if (store.getCurrency().contains("\\")) {
                            prefManager.storeSharedValue(AppConstant.CURRENCY, store.getCurrency());
                        } else {
                            String ruppee = String.valueOf(Html.fromHtml(store.getCurrency()));
                            prefManager.storeSharedValue(AppConstant.CURRENCY, ruppee);
                        }

//                        prefManager.storeSharedValue(AppConstant.CURRENCY,"\uFF04");
                    }


                    String oldVerision = prefManager.getSharedValue(AppConstant.APP_OLD_VERISON);
                    if (oldVerision.isEmpty()) {
                        prefManager.storeSharedValue(AppConstant.APP_OLD_VERISON, store.getVersion());
                    }
//                    Log.e("Store Version", store.getVersion());
                    oldVerision = prefManager.getSharedValue(AppConstant.APP_OLD_VERISON);
                    String appVersion = prefManager.getSharedValue(AppConstant.APP_VERISON);
                    if (!appVersion.equalsIgnoreCase(oldVerision)) {
//                        appDb.deleteCartAll();
                        appDb.deleteCartElement();
                        appDb.deleteCategoryAll();
                        appDb.deleteSubcategoryAll();
                        appDb.deleteProducts();
//                        prefManager.storeSharedValue(AppConstant.APP_OLD_VERISON, store.getVersion());
                    }

                    if (!(store.getStoreStatus().equalsIgnoreCase("1"))) {
                        String msg = "" + store.getStoreMsg();
                        new DialogHandler(MainActivity.this).setdialogForFinish("Message", msg, true);
                    }


                } else {
                    Log.e("Error", "Error success false");
                }
            }

            @Override
            public void failure(RetrofitError error) {
                DialogHandler dialogHandler = new DialogHandler(MainActivity.this);
                dialogHandler.setdialogForFinish("Message", getResources().getString(R.string.error_code_message), false);
            }
        });

    }

    public void showReferAndEarnDialog(Context context, String title,
                                       String message) {

        final Dialog dialog = new Dialog(MainActivity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.setContentView(R.layout.refer_n_earn_dialog);

        Button skipBtn = (Button) dialog.findViewById(R.id.skipBtn);
        Button okBtn = (Button) dialog.findViewById(R.id.okBtn);
        TextView textMsg = (TextView) dialog.findViewById(R.id.textMsg);
        TextView label = (TextView) dialog.findViewById(R.id.label);
        label.setText("" + title);
        textMsg.setText("" + message);


        skipBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        okBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                Intent intent = new Intent(MainActivity.this, LoginScreenActivity.class);
                intent.putExtra(AppConstant.FROM, "menu");
                startActivity(intent);
                AnimUtil.slideUpAnim(MainActivity.this);

            }
        });


        dialog.setCanceledOnTouchOutside(true);
        dialog.show();
    }


    private boolean openTime(Store store) {

        boolean status = false;
        if (store.getOpenhoursFrom().isEmpty() || store.getClosehoursMessage().isEmpty()) {
            return true;
        } else {

            Calendar calendar = Calendar.getInstance();
            SimpleDateFormat formatter = new SimpleDateFormat("hh:mmaa");
            String currentDate = formatter.format(calendar.getTime());

            String strOpenHrs = store.getOpenhoursFrom();
            String strCloseHrs = store.getOpenhoursTo();

            SimpleDateFormat format = new SimpleDateFormat("hh:mmaa");
            Date openDate = null, closeDate = null, currentTime = null;
            try {
                openDate = format.parse(strOpenHrs);

                closeDate = format.parse(strCloseHrs);

                currentTime = format.parse(currentDate);

                if (currentTime.compareTo(openDate) > 0 && currentTime.compareTo(closeDate) < 0) {
                    status = true;
                } else {
                    status = false;
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }


//            format = new SimpleDateFormat("MMM dd,yyyy hh:mm a");
//            String date = format.format(newDate);
        }
        return status;

    }

    private void checkForAppVersion(ForceDownloadModel forceDownloadModel) {


        PackageInfo pInfo = null;

        try {
            pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        String appVersionName = pInfo.versionName;

        if (forceDownloadModel != null) {
            String version = forceDownloadModel.getAndroidAppVerison() != null ?
                    forceDownloadModel.getAndroidAppVerison() : "";
            if (!version.isEmpty()) {

                try {
                    double appVersion = Double.parseDouble(version);
                    double appVersionSystem = Double.parseDouble(appVersionName);
                    if (appVersion > appVersionSystem) {
                        openDialogForVersion(forceDownloadModel);
                    }
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                }
            }
        }


       /* if (forceDownloadModel != null) {
            PrefManager prefManager = new PrefManager(MainActivity.this);
            String appVersion = prefManager.getAppVersion();
            if (appVersion.isEmpty()) {
                String version = forceDownloadModel.getAndroidAppVerison() != null ?
                        forceDownloadModel.getAndroidAppVerison() : "";
                if (!version.isEmpty()) {
                    prefManager.setAppVersion(version);
                }
            } else {
                String version = forceDownloadModel.getAndroidAppVerison() != null ?
                        forceDownloadModel.getAndroidAppVerison() : "";
                if (!version.isEmpty()) {
                    if (!appVersion.equalsIgnoreCase(version)) {
                        openDialogForVersion(forceDownloadModel);
                    } else {
                        prefManager.setAppVersion(version);
                    }
                }
            }
        }*/
    }

    private void openDialogForVersion(ForceDownloadModel forceDownloadModel) {

        if (forceDownloadModel.getForceDownload() != null) {
            final DialogHandler dialogHandler = new DialogHandler(MainActivity.this);
            dialogHandler.setDialog("Latest Update", forceDownloadModel.getForceDownloadMessage());
            if (forceDownloadModel.getForceDownload().equalsIgnoreCase("1")) {
                dialogHandler.setCancelable(false);
                dialogHandler.setPostiveButton("Update", true).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        openPlayStoreLink();
                        dialogHandler.dismiss();
                    }

                });
            } else {
                dialogHandler.setNegativeButton("Cancel", true).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialogHandler.dismiss();
                    }
                });
                dialogHandler.setPostiveButton("Update", true).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        openPlayStoreLink();
                        dialogHandler.dismiss();
                    }
                });
            }
        }
    }

    private void openPlayStoreLink() {
        final String appPackageName = BuildConfig.APPLICATION_ID; // getPackageName() from Context or Activity object
        try {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
        } catch (android.content.ActivityNotFoundException anfe) {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://play.google.com/store/apps/details?id=" + appPackageName)));
        }
    }

    class Adapter extends BaseAdapter {
        Activity context;
        LayoutInflater l;
        boolean login = false;
        ViewHolder holder;

        public Adapter(Activity context) {
            this.context = context;
            l = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public int getCount() {
            return viewList.size();
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {

            if (convertView == null) {
                convertView = l.inflate(R.layout.slider_child, null);
                holder = new ViewHolder();
                holder.icons = (ImageView) convertView.findViewById(R.id.icon);
                holder.labels = (TextView) convertView.findViewById(R.id.labels);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            holder.icons.setImageResource(viewList.get(position).icons);
            holder.labels.setText(viewList.get(position).labels);

            SliderObject att = new SliderObject();
            if (loyalityStatus.equalsIgnoreCase("0")) {
                if (userId.isEmpty()) {
                    att.labels = "Log In";
                    att.icons = icons[9];
                    viewList.set(8, att);
                    holder.labels.setText(viewList.get(position).labels);
                    login = true;
                } else if (!userId.isEmpty()) {
                    att.labels = "Log out";
                    att.icons = icons[9];
                    viewList.set(8, att);
                    holder.labels.setText(viewList.get(position).labels);
                    login = false;
                }
            } else if (loyalityStatus.equalsIgnoreCase("1")) {
                if (userId.isEmpty()) {
                    att.labels = "Log In";
                    att.icons = icons[9];
                    viewList.set(9, att);
                    holder.labels.setText(viewList.get(position).labels);
                    login = true;
                } else if (!userId.isEmpty()) {
                    att.labels = "Log out";
                    att.icons = icons[9];
                    viewList.set(9, att);
                    holder.labels.setText(viewList.get(position).labels);
                    login = false;
                }
            }
            if (!userId.isEmpty() && prefManager.isReferEarnFn()) {
                SliderObject atts = new SliderObject();
                atts.labels = "Refer And Earn";
                atts.icons = icons[7];
                viewList.set(7, atts);
                holder.labels.setText(viewList.get(position).labels);
            } else {
                SliderObject atts = new SliderObject();
                atts.labels = "Share";
                atts.icons = icons[7];
                viewList.set(7, atts);
                holder.labels.setText(viewList.get(position).labels);
            }


            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    replaceFragment(position);
                    toggleSlidingMenu();
                }
            });

            return convertView;
        }


        class ViewHolder {
            ImageView icons;
            TextView labels;

        }
    }

}

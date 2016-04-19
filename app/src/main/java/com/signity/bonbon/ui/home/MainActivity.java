package com.signity.bonbon.ui.home;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.SlidingPaneLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.gms.common.GooglePlayServicesUtil;
import com.signity.bonbon.BuildConfig;
import com.signity.bonbon.R;
import com.signity.bonbon.Utilities.AnimUtil;
import com.signity.bonbon.Utilities.AppConstant;
import com.signity.bonbon.Utilities.DialogHandler;
import com.signity.bonbon.Utilities.FontUtil;
import com.signity.bonbon.Utilities.PrefManager;
import com.signity.bonbon.app.AppController;
import com.signity.bonbon.app.DataAdapter;
import com.signity.bonbon.app.DbAdapter;
import com.signity.bonbon.app.ViewController;
import com.signity.bonbon.db.AppDatabase;
import com.signity.bonbon.gcm.GCMClientManager;
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


    private String TAG = MainActivity.class.getSimpleName();
    SlidingPaneLayout mSlidingPanel;
    ListView mMenuList;
    public TextView title, user;
    ImageButton search, shopingcart;
    Button menu, citySelect;
    ImageView profilePic;
    public Typeface typeFaceRobotoRegular, typeFaceRobotoBold;
    String[] labels = {"Home", "My Profile", "Delivery Address", "My Order",
            "Book Now", "My Favorites", "About Us", "Share", "Loyality Points","Log In"};

    Integer[] icons = {R.drawable.ic_home, R.drawable.profil_icon, R.drawable.address_icon,
            R.drawable.order_icon,
            R.drawable.my_shopping_list_icon, R.drawable.my_fav_icon,
            R.drawable.faq, R.drawable.ic_share,R.drawable.loyality, R.drawable.sign_out};
    ArrayList<SliderObject> viewList = new ArrayList<SliderObject>();

    AppDatabase appDb;
    Adapter adapter;
    PrefManager prefManager;
    private GCMClientManager pushClientManager;
    String userId;
    String storeId, areaName;

    String name;
    String phone;
    Context context;
    Store store;

    // Controller;
    ViewController viewController;

    boolean isActivityLoadsFirstTime = true;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.slider_pane);
        context = this;
        prefManager = new PrefManager(MainActivity.this);
        viewController = AppController.getInstance().getViewController();
        pushClientManager = new GCMClientManager(this, AppConstant.PROJECT_NUMBER);
        userId = prefManager.getSharedValue(AppConstant.ID);
        name = prefManager.getSharedValue(AppConstant.NAME);
        phone = prefManager.getSharedValue(AppConstant.PHONE);
        storeId = prefManager.getSharedValue(AppConstant.STORE_ID);

        typeFaceRobotoRegular = FontUtil.getTypeface(context, FontUtil.FONT_ROBOTO_REGULAR);
        typeFaceRobotoBold = FontUtil.getTypeface(context, FontUtil.FONT_ROBOTO_BOLD);

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

        title.setTypeface(typeFaceRobotoBold);
        user.setTypeface(typeFaceRobotoRegular);
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
            geofence.radius = Float.parseFloat(context.getString(R.string.fence_radius)) * 1000.0f;
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
                geofence.radius = Float.parseFloat(context.getString(R.string.fence_radius)) * 1000.0f;
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
        return false;
    }

    @Override
    protected void onResume() {
        super.onResume();
        int googlePlayServicesCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        Log.i(MainActivity.class.getSimpleName(), "googlePlayServicesCode = " + googlePlayServicesCode);
        if (googlePlayServicesCode == 1 || googlePlayServicesCode == 2 || googlePlayServicesCode == 3) {
            GooglePlayServicesUtil.getErrorDialog(googlePlayServicesCode, this, 0).show();
        } else {
            if (!isActivityLoadsFirstTime) {
                getStoreServicesForUpdate();
            } else {
                isActivityLoadsFirstTime = false;
            }
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
                    startActivity(intent);
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
                    startActivity(intent);
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
                    startActivity(intent);
                    AnimUtil.slideUpAnim(MainActivity.this);
                } else {
                    title.setVisibility(View.VISIBLE);
                    citySelect.setVisibility(View.GONE);
                    title.setText("Active Order");
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
                String shareContent =
                        "Kindly download " + store.getStoreName() + " app from " +
                                store.getAndroidAppShareLink() + "\nThanks and Regards\n " +
                                store.getStoreName() + "\n" + store.getCity() + " , " + store.getState();
                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setType("text/plain");
                intent.putExtra(Intent.EXTRA_TEXT, shareContent);
                intent.putExtra(Intent.EXTRA_SUBJECT, store.getStoreName());
                startActivity(Intent.createChooser(intent, "Share with"));
                break;

            case 8:

                if (userId.isEmpty()) {
                    Intent intent9 = new Intent(MainActivity.this, LoginScreenActivity.class);
                    intent9.putExtra(AppConstant.FROM, "menu");
                    startActivity(intent9);
                    AnimUtil.slideUpAnim(MainActivity.this);
                } else {
                    title.setVisibility(View.VISIBLE);
                    citySelect.setVisibility(View.GONE);
                    title.setText("Loyality Points");
                    fragment = new LoyalityFragment();
                    replace(fragment);
                }
                break;



            case 9:

                title.setText(store.getStoreName());
                if (userId.isEmpty()) {
                    Intent intentLogin = new Intent(MainActivity.this, LoginScreenActivity.class);
                    intentLogin.putExtra(AppConstant.FROM, "menu");
                    startActivity(intentLogin);
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

    class Adapter extends BaseAdapter {
        Activity context;
        LayoutInflater l;
        boolean login = false;

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

        ViewHolder holder;

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


    @Override
    public void onBackPressed() {

//        FragmentManager fm = getSupportFragmentManager();
//        fm.getFragments();
//        fm.getBackStackEntryCount();
        mSlidingPanel.closePane();

        if(!title.getText().toString().equalsIgnoreCase(store.getStoreName())){
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
        }else {
            MainActivity.super.onBackPressed();
        }

    }


    private void getStoreServicesForUpdate() {

        String deviceid = Settings.Secure.getString(MainActivity.this.getApplicationContext().getContentResolver(), Settings.Secure.ANDROID_ID);
        String deviceToken = pushClientManager.getRegistrationId(MainActivity.this);

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


                    if(store.getCurrency().isEmpty()){
                        prefManager.storeSharedValue(AppConstant.CURRENCY,"$");
                    }
                    else {
                        prefManager.storeSharedValue(AppConstant.CURRENCY,store.getCurrency());
//                        prefManager.storeSharedValue(AppConstant.CURRENCY,"\uFF04");
                    }



                    String oldVerision = prefManager.getSharedValue(AppConstant.APP_OLD_VERISON);
                    if (oldVerision.isEmpty()) {
                        prefManager.storeSharedValue(AppConstant.APP_OLD_VERISON, store.getVersion());
                    }
                    Log.e("Store Version", store.getVersion());


                        if (!(store.getStoreStatus().equalsIgnoreCase("1"))) {
                            String msg = "" + store.getStoreMsg();
                            new DialogHandler(MainActivity.this).setdialogForFinish("Error", msg, true);
                        }



                } else {
                    Log.e("Error", "Error success false");
                }
            }

            @Override
            public void failure(RetrofitError error) {
                Log.e("Error", error.getMessage());
            }
        });

    }



    private boolean openTime(Store store){

        boolean status = false;
        if(store.getOpenhoursFrom().isEmpty() || store.getClosehoursMessage().isEmpty()){
            return true;
        }else {

            Calendar calendar = Calendar.getInstance();
            SimpleDateFormat formatter = new SimpleDateFormat("hh:mmaa");
            String currentDate=formatter.format(calendar.getTime());

            String strOpenHrs = store.getOpenhoursFrom();
            String strCloseHrs=store.getOpenhoursTo();

            SimpleDateFormat format = new SimpleDateFormat("hh:mmaa");
            Date openDate = null,closeDate=null,currentTime=null;
            try {
                openDate = format.parse(strOpenHrs);

                closeDate=format.parse(strCloseHrs);

                currentTime=format.parse(currentDate);

                if(currentTime.compareTo(openDate)>0 && currentTime.compareTo(closeDate)<0){
                    status=true;
                }
                else {
                    status=false;
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

        if (forceDownloadModel != null) {
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
        }
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

    private GeofenceController.GeofenceControllerListener geofenceControllerListener = new GeofenceController.GeofenceControllerListener() {
        @Override
        public void onGeofencesUpdated() {
        }

        @Override
        public void onError() {
            Log.i("Geo Fence", "Error on Fence Created");
        }
    };

}


//            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
//            alertDialogBuilder.setTitle("Latest Update");
//            alertDialogBuilder.setMessage(forceDownloadModel.getForceDownloadMessage());
//            alertDialogBuilder.setCancelable(false);
//
//            if (forceDownloadModel.getForceDownload().equalsIgnoreCase("1")) {
//                alertDialogBuilder.setPositiveButton("Update", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface arg0, int arg1) {
//                        openPlayStoreLink();
//                    }
//                });
//            } else {
//                alertDialogBuilder.setPositiveButton("Update", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface arg0, int arg1) {
//                        openPlayStoreLink();
//                    }
//                });
//                alertDialogBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        dialog.dismiss();
//                    }
//                });
//            }
//
//            AlertDialog alertDialog = alertDialogBuilder.create();
//            alertDialog.show();
package com.signity.bonbon;

import android.app.Activity;
import android.app.Dialog;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.drawable.ColorDrawable;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.support.v4.app.NotificationCompat;
import android.text.Html;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.signity.bonbon.Utilities.AnimUtil;
import com.signity.bonbon.Utilities.AppConstant;
import com.signity.bonbon.Utilities.DialogHandler;
import com.signity.bonbon.Utilities.PrefManager;
import com.signity.bonbon.Utilities.ProgressDialogUtil;
import com.signity.bonbon.Utilities.Util;
import com.signity.bonbon.app.DataAdapter;
import com.signity.bonbon.app.DbAdapter;
import com.signity.bonbon.db.AppDatabase;
import com.signity.bonbon.ga.GAConstant;
import com.signity.bonbon.ga.GATrackers;
import com.signity.bonbon.gcm.GCMClientManager;
import com.signity.bonbon.geofence.GeofenceController;
import com.signity.bonbon.model.GetStoreArea;
import com.signity.bonbon.model.GetStoreModel;
import com.signity.bonbon.model.Store;
import com.signity.bonbon.network.NetworkAdaper;
import com.signity.bonbon.ui.Delivery.DeliveryPickupActivity;
import com.signity.bonbon.ui.Location.SelectLocationActivity;
import com.signity.bonbon.ui.home.MainActivity;
import com.signity.bonbon.ui.login.LoginScreenActivity;

import java.util.HashMap;
import java.util.Map;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class SplashActivity extends Activity {

    AppDatabase appDb;
    PrefManager prefManager;
    String deviceToken;
    ImageView splash_screen;
    GATrackers trackers;
    private int SPLASH_TIME_OUT = 500;
    private GCMClientManager pushClientManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash);
        trackers = GATrackers.getInstance();
        trackers.trackScreenView(GAConstant.SPLASH_SCREEN);
        GeofenceController.getInstance().init(this);
        prefManager = new PrefManager(this);
        pushClientManager = new GCMClientManager(this, AppConstant.PROJECT_NUMBER);
        appDb = DbAdapter.getInstance().getDb();
        deviceToken = pushClientManager.getRegistrationId(SplashActivity.this);
        splash_screen = (ImageView) findViewById(R.id.splash_screen);

//        startAnimationProcess()
    }

    @Override
    protected void onResume() {
        super.onResume();
        startSplashProcess();
    }

    private void startSplashProcess() {
          /*Animation animation = AnimationUtils.loadAnimation(this, R.anim.zoom_with_bounce_anim);
        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                if (Util.checkInternetConnection(SplashActivity.this)) {
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            initSplash();
                        }
                    }, SPLASH_TIME_OUT);
                } else {
                    showAlertDialogForInternetConnection(SplashActivity.this);
                }
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });*/
//        splash_screen.startAnimation(animation);


        if (Util.checkInternetConnection(this)) {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    initSplash();
                }
            }, SPLASH_TIME_OUT);
        } else {
            showAlertDialogForInternetConnection(SplashActivity.this);
        }
    }

    private void initSplash() {
        if (deviceToken != null && !deviceToken.isEmpty()) {
            getStoreServices();
        } else {
            pushClientManager.registerIfNeeded(new GCMClientManager.RegistrationCompletedHandler() {
                @Override
                public void onSuccess(String registrationId, boolean isNewRegistration) {
                    getStoreServices();
                }

                @Override
                public void onFailure(String ex) {
                    super.onFailure(ex);
                    finish();
                }
            });
        }
    }


    private void checkAgainProcess() {
        if (Util.checkInternetConnection(this)) {
            initSplash();
        } else {
            showAlertDialogForInternetConnection(SplashActivity.this);
        }
    }


    private void getMainActivity() {
        Intent intent_home = new Intent(SplashActivity.this, MainActivity.class);
        startActivity(intent_home);
        finish();
    }

    private void getStoreServices() {

        ProgressDialogUtil.showProgressDialog(SplashActivity.this);
        String deviceid = Settings.Secure.getString(SplashActivity.this.getApplicationContext().getContentResolver(), Settings.Secure.ANDROID_ID);
        String deviceToken = pushClientManager.getRegistrationId(SplashActivity.this);

        Map<String, String> param = new HashMap<String, String>();
        param.put("device_id", deviceid);
        param.put("device_token", deviceToken);
        param.put("platform", AppConstant.PLATFORM);

        NetworkAdaper.getInstance().getNetworkServices().getStore(param, new Callback<GetStoreModel>() {
            @Override
            public void success(GetStoreModel getStroeModel, Response response) {
                ProgressDialogUtil.hideProgressDialog();
                if (getStroeModel.getSuccess() != null ? getStroeModel.getSuccess() : false) {
                    appDb.addStore(getStroeModel.getStore());
                    Store store = getStroeModel.getStore();
                    prefManager.storeSharedValue(AppConstant.STORE_ID, store.getId());
                    prefManager.storeSharedValue(AppConstant.APP_VERISON, store.getVersion());
                    prefManager.setProjectTheme(store.getTheme());
                    prefManager.setProjectType(store.getType());
                    prefManager.setOtoSkip(store.getOtpSkip());
                    prefManager.setPickupFacilityStatus(store.getPickUpFacility());
                    prefManager.setDeliveryFacilityStatus(store.getDeliveryFacility());
                    prefManager.setGeoFenceEnableFeature(store.getStoreStatus());
                    prefManager.storeSharedValue(AppConstant.is24x7_open, store.getIs24x7_open());
                    prefManager.storeSharedValue(AppConstant.OPEN_TIME, store.getOpenhoursFrom());
                    prefManager.storeSharedValue(AppConstant.CLOSE_TIME, store.getOpenhoursTo());
                    prefManager.storeSharedValue(AppConstant.MESSAGE, store.getClosehoursMessage());
                    prefManager.storeSharedValue(AppConstant.OPEN_DAYS, store.getStoreOpenDays());
                    prefManager.storeSharedValue(AppConstant.istaxenable, store.getIstaxenable());
                    prefManager.storeSharedValue(AppConstant.tax_label_name, store.getTaxLabelName());
                    prefManager.storeSharedValue(AppConstant.tax_rate, store.getTaxRate());
                    prefManager.setReferEarnFn(store.getReferFnEnable());
                    prefManager.setReferEarnFnEnableForDevice(store.getReferForDeviceEnable());
                    prefManager.storeSharedValue(AppConstant.CATEGORY_LAYOUT_TYPE, store.getCategoryLayoutType());

                    if (store.getLoyality() != null && !store.getLoyality().isEmpty()) {
                        prefManager.storeSharedValue(AppConstant.LOYALITY, store.getLoyality());
                    } else {
                        prefManager.storeSharedValue(AppConstant.LOYALITY, "0");
                    }

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

                    oldVerision = prefManager.getSharedValue(AppConstant.APP_OLD_VERISON);
                    String appVersion = prefManager.getSharedValue(AppConstant.APP_VERISON);
                    if (!appVersion.equalsIgnoreCase(oldVerision)) {
//                        appDb.deleteCartAll();
                        appDb.deleteCartElement();
                        prefManager.storeSharedValue(AppConstant.APP_OLD_VERISON, store.getVersion());
                    }

                    if (store.getBanners() != null && store.getBanners().size() != 0) {
                        DataAdapter.getInstance().setBanners(store.getBanners());
                    }


                    if (store.getForceDownload() != null && store.getForceDownload().size() > 0) {
                        DataAdapter.getInstance().setForceDownloadModel(store.getForceDownload().get(0));
                    }

                    if (!prefManager.getGeoFenceEnableStatus().equalsIgnoreCase("0")) {
                        DataAdapter.getInstance().setListGeoFence(store.getGeofenceObjects());
                    }

                    if (store.getStoreStatus().equalsIgnoreCase("1")) {

                        getMainActivity();

                    } else {
                        String msg = "" + store.getStoreMsg();
                        showAlertDialog(SplashActivity.this, "Message", msg);
                    }

                } else {
                    DialogHandler dialogHandler = new DialogHandler(SplashActivity.this);
                    dialogHandler.setdialogForFinish("Message", getResources().getString(R.string.error_code_message), false);
                }
            }

            @Override
            public void failure(RetrofitError error) {
                ProgressDialogUtil.hideProgressDialog();
                DialogHandler dialogHandler = new DialogHandler(SplashActivity.this);
                dialogHandler.setdialogForFinish("Message", getResources().getString(R.string.error_code_message), true);
            }
        });

    }




    private void moveToCitySelection() {

//        boolean x=prefManager.getBoolean(AppConstant.CITY_SELECTED);

        if (prefManager.getBoolean(AppConstant.AREA_SELECTED)) {
            getMainActivity();
        } else {
            getAllAreaDetail();
        }

    }

    private void getAllAreaDetail() {
        ProgressDialogUtil.showProgressDialog(SplashActivity.this);
        NetworkAdaper.getInstance().getNetworkServices().getStoreAreaList(new Callback<GetStoreArea>() {
            @Override
            public void success(GetStoreArea getStoreArea, Response response) {
                if (getStoreArea.getSuccess()) {

                    if (getStoreArea.getData() != null && getStoreArea.getData().size() != 0) {
                        ProgressDialogUtil.hideProgressDialog();
//                        DataAdapter.getInstance().setStoreArea(getStoreArea);
                        Intent intent_location = new Intent(SplashActivity.this, SelectLocationActivity.class);
                        startActivity(intent_location);
                        finish();

                    } else {
                        ProgressDialogUtil.hideProgressDialog();
                        getMainActivity();
                    }
                } else {
                    ProgressDialogUtil.hideProgressDialog();
                    getMainActivity();
                }

            }

            @Override
            public void failure(RetrofitError error) {
                ProgressDialogUtil.hideProgressDialog();
            }
        });
    }


    public void showAlertDialogForInternetConnection(Context context) {

        final DialogHandler dialogHandler = new DialogHandler(SplashActivity.this);
        dialogHandler.setDialog("Internet Connection", "Internet connection is not available");
        dialogHandler.setPostiveButton("OK", true)
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
//                        checkAgainProcess();
                        dialogHandler.dismiss();
                        finish();
                    }
                });
    }


    public void showAlertDialog(Context context, String title,
                                String message) {
        new DialogHandler(context).setdialogForFinish(title, message, true);
    }


    private void sendNotification(String title, String message) {
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
                PendingIntent.FLAG_ONE_SHOT);


        int icon = R.drawable.baliram;
        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                .setContentTitle(title)
                .setContentText(message)
                .setTicker(title)
                .setSmallIcon(icon)
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher))
                .setStyle(new NotificationCompat.BigTextStyle().bigText(message))
                .setContentIntent(pendingIntent);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(1 /* ID of notification */, notificationBuilder.build());
    }


}

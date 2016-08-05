package com.signity.bonbon.app;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;
import android.util.Log;

import com.signity.bonbon.Utilities.PrefManager;
import com.signity.bonbon.ga.GAConstant;
import com.signity.bonbon.ga.GATrackers;
import com.signity.bonbon.network.NetworkAdaper;
import com.squareup.picasso.OkHttpDownloader;
import com.squareup.picasso.Picasso;

/**
 * Created by Rajinder on 5/10/15.
 */
public class MyApplication extends Application implements Application.ActivityLifecycleCallbacks{

    PrefManager prefManager;
    @Override
    public void onCreate() {
        super.onCreate();
        prefManager = new PrefManager(this);
        initSingletons();
        initPicasso();
        registerActivityLifecycleCallbacks(this);
    }

    protected void initSingletons() {
        NetworkAdaper.initInstance();
        DataAdapter.initInstance();
        AppController.initInstance(this);
        DbAdapter.initInstance(this);
        AnalyticsTrackers.initInstance(this);
        GATrackers.getInstance().setDefaultAppTracker(GAConstant.GA_TRACK_ID_LIVE);
    }

    private void initPicasso() {

        Picasso.Builder builder = new Picasso.Builder(this);
        builder.downloader(new OkHttpDownloader(this, Integer.MAX_VALUE));
        Picasso built = builder.build();
        built.setIndicatorsEnabled(false);
        built.setLoggingEnabled(false);
        Picasso.setSingletonInstance(built);
    }

    @Override
    public void onActivityCreated(Activity arg0, Bundle arg1) {
        Log.e("","onActivityCreated");

    }
    @Override
    public void onActivityDestroyed(Activity activity) {
        Log.e("","onActivityDestroyed ");

    }
    @Override
    public void onActivityPaused(Activity activity) {
        prefManager.storeBoolean("applicationOnPause",true);
        Log.e("","onActivityPaused "+activity.getClass());

    }
    @Override
    public void onActivityResumed(Activity activity) {
        prefManager.storeBoolean("applicationOnPause",false);
        Log.e("","onActivityResumed "+activity.getClass());

    }
    @Override
    public void onActivitySaveInstanceState(Activity activity, Bundle outState) {
        Log.e("","onActivitySaveInstanceState");

    }
    @Override
    public void onActivityStarted(Activity activity) {
        Log.e("","onActivityStarted");

    }
    @Override
    public void onActivityStopped(Activity activity) {
        Log.e("","onActivityStopped");

    }
}

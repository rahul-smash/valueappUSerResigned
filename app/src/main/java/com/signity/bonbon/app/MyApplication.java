package com.signity.bonbon.app;

import android.app.Application;

import com.signity.bonbon.ga.GAConstant;
import com.signity.bonbon.ga.GATrackers;
import com.signity.bonbon.network.NetworkAdaper;
import com.squareup.picasso.OkHttpDownloader;
import com.squareup.picasso.Picasso;

/**
 * Created by Rajinder on 5/10/15.
 */
public class MyApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        initSingletons();
        initPicasso();
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

}

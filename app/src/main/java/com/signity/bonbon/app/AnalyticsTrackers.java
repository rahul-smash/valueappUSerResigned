package com.signity.bonbon.app;

import android.content.Context;

import com.signity.bonbon.ga.GATrackers;

/**
 * Created by rajesh on 21/4/16.
 */
public class AnalyticsTrackers {

    public GATrackers gaTracker = null;
    public static AnalyticsTrackers uInstance;

    public AnalyticsTrackers(Context context) {
        GATrackers.initialize(context);
    }

    public static AnalyticsTrackers getInstance() {
        return uInstance;
    }

    public static void initInstance(Context context) {
        if (uInstance == null) {
            uInstance = new AnalyticsTrackers(context);
        }
    }

    public GATrackers getGaTracker() {
        return gaTracker;
    }
}

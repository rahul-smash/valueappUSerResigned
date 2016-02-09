package com.signity.bonbon.app;

import android.content.Context;

import com.signity.bonbon.db.AppDatabase;

/**
 * Created by rajesh on 19/10/15.
 */
public class DbAdapter {
    public AppDatabase appdb = null;

    public static DbAdapter uInstance;

    public DbAdapter(Context context) {
        appdb = new AppDatabase(context);
    }

    public static DbAdapter getInstance() {
        return uInstance;
    }

    public static void initInstance(Context context) {
        if (uInstance == null) {
            uInstance = new DbAdapter(context);
        }
    }

    public AppDatabase getDb() {
        return appdb;
    }
}

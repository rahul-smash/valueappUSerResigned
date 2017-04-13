package com.signity.bonbon.service;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.signity.bonbon.R;
import com.signity.bonbon.Utilities.PrefManager;
import com.signity.bonbon.app.DbAdapter;
import com.signity.bonbon.db.AppDatabase;
import com.signity.bonbon.ui.home.MainActivity;


/**
 * This service is started when an Alarm has been raised
 * <p/>
 * We pop a notification into the status bar for the user to click on
 * When the user clicks the notification a new activity is opened
 *
 * @author paul.blundell
 */
public class NotifyService extends Service {

    /**
     * Class for clients to access
     */

    // Unique id to identify the notification.
    private static final int NOTIFICATION = 123;
    // Name of an intent extra we can use to identify if this service was started to create a notification
    public static final String INTENT_NOTIFY = "com.signity.bonbon.service.INTENT_NOTIFY";
    // The system notification manager
    private NotificationManager mNM;

    AppDatabase appDb;
    PrefManager prefManager;

    @Override
    public void onCreate() {
        mNM = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        prefManager = new PrefManager(this);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i("LocalService", "Received start id " + startId + ": " + intent);

        // If this service was started by out AlarmTask intent then we want to show our notification
        appDb = DbAdapter.getInstance().getDb();
        int cartSize = appDb.getCartSize();
        if (cartSize != 0) {
            String title = getString(R.string.app_name);
            String message = getString(R.string.str_procedd_to_checkout)+" " + cartSize + " "+getString(R.string.str_items_in_cart);
            sendNotification(title, message);
            prefManager.setCartLocalNotification(false);
        }

        // We don't care if this service is stopped as we have already delivered our notification
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    /**
     * Creates a notification and shows it in the OS drag-down status bar
     */

    private void sendNotification(String title, String message) {
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
                PendingIntent.FLAG_ONE_SHOT);

        int icon = com.signity.bonbon.R.mipmap.ic_launcher;
        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(icon)
                .setContentTitle(title)
                .setContentText(message)
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(NOTIFICATION /* ID of notification */, notificationBuilder.build());
        Log.d("Notification", "Notification sent successfully.");
        stopSelf();
    }
}
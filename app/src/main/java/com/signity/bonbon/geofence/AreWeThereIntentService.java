package com.signity.bonbon.geofence;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingEvent;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.signity.bonbon.R;
import com.signity.bonbon.Utilities.PrefManager;
import com.signity.bonbon.Utilities.Util;
import com.signity.bonbon.model.GeoFenceModel;
import com.signity.bonbon.network.NetworkAdaper;
import com.signity.bonbon.ui.home.MainActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class AreWeThereIntentService extends IntentService {

    // region Properties

    private final String TAG = AreWeThereIntentService.class.getName();

    private SharedPreferences prefs;
    private Gson gson;
    String geofenceName = "";
    private String contextText = "";

    // endregion

    // region Constructors

    public AreWeThereIntentService() {
        super("AreWeThereIntentService");
    }

    // endregion

    // region Overrides

    @Override
    protected void onHandleIntent(Intent intent) {
        prefs = getApplicationContext().getSharedPreferences(PrefManager.SharedPrefs.Geofences, Context.MODE_PRIVATE);
        gson = new Gson();

        GeofencingEvent event = GeofencingEvent.fromIntent(intent);
        if (event != null) {
            if (event.hasError()) {
                onError(event.getErrorCode());
            } else {
                int transition = event.getGeofenceTransition();
                if (transition == Geofence.GEOFENCE_TRANSITION_ENTER || transition == Geofence.GEOFENCE_TRANSITION_DWELL || transition == Geofence.GEOFENCE_TRANSITION_EXIT) {
                    List<String> geofenceIds = new ArrayList<>();
                    for (Geofence geofence : event.getTriggeringGeofences()) {
                        geofenceIds.add(geofence.getRequestId());
                    }
                    if (transition == Geofence.GEOFENCE_TRANSITION_ENTER || transition == Geofence.GEOFENCE_TRANSITION_DWELL) {
                        onEnteredGeofences(geofenceIds);
                    }
                }
            }
        }
    }

    // endregion

    // region Private

    private void onEnteredGeofences(List<String> geofenceIds) {

        for (String geofenceId : geofenceIds) {


            // Loop over all geofence keys in prefs and retrieve NamedGeofence from SharedPreference
            Map<String, ?> keys = prefs.getAll();
            for (Map.Entry<String, ?> entry : keys.entrySet()) {
                String jsonString = prefs.getString(entry.getKey(), null);
                try {
                    NamedGeofence namedGeofence = gson.fromJson(jsonString, NamedGeofence.class);
                    if (namedGeofence.id.equals(geofenceId)) {
                        geofenceName = namedGeofence.name;
                        break;
                    }
                } catch (JsonSyntaxException e) {
                    e.printStackTrace();
                }
            }

            if (Util.checkInternetConnection(this)) {

                NetworkAdaper.getInstance().getNetworkServices().getGeofencingMessage(new Callback<GeoFenceModel>() {
                    @Override
                    public void success(GeoFenceModel geoFenceModel, Response response) {

                        try {
                            Scanner scanner = new Scanner(response.getBody().in());
                            StringBuilder stringBuilder = new StringBuilder();
                            while (scanner.hasNext()) {
                                stringBuilder.append(scanner.next());
                            }

                            try {
                                JSONObject mJob = new JSONObject(stringBuilder.toString());


                                if (mJob != null) {
                                    boolean success = mJob.getBoolean("success");

                                    if (success) {

                                        Gson mGson = new Gson();
                                        GeoFenceModel mGeoFenceModel = mGson.fromJson(stringBuilder.toString(), GeoFenceModel.class);

                                        if (mGeoFenceModel.getData() != null) {
                                            if (mGeoFenceModel.getData().getMessage() != null && !mGeoFenceModel.getData().getMessage().isEmpty()) {
                                                contextText = mGeoFenceModel.getData().getMessage();
                                            } else {
                                                contextText = String.format(AreWeThereIntentService.this.getResources().getString(R.string.Notification_Text), geofenceName);
                                            }
                                        } else {
                                            contextText = String.format(AreWeThereIntentService.this.getResources().getString(R.string.Notification_Text), geofenceName);
                                        }

                                    } else {
                                        contextText = String.format(AreWeThereIntentService.this.getResources().getString(R.string.Notification_Text), geofenceName);
                                    }

                                    sendNotificationWithMessage(contextText);

                                }

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                    }

                    @Override
                    public void failure(RetrofitError error) {
                        Log.e("Error", error.getMessage());
                    }
                });
//
            } else {
                // Set the notification text and send the notification
                contextText = String.format(this.getResources().getString(R.string.Notification_Text), geofenceName);
                sendNotificationWithMessage(contextText);
            }


        }
    }

    private void sendNotificationWithMessage(String contextText) {
        NotificationManager notificationManager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingNotificationIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        Notification notification = new NotificationCompat.Builder(this)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher))
                .setContentTitle(this.getResources().getString(R.string.app_name))
                .setContentText(contextText)
                .setContentIntent(pendingNotificationIntent)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(contextText))
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true)
                .build();
        notificationManager.notify(0, notification);
    }

    private void onError(int i) {
        Log.e(TAG, "Geofencing Error: " + i);
    }

    // endregion
}


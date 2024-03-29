package com.signity.bonbon.geofence;

import android.support.annotation.NonNull;

import com.google.android.gms.location.Geofence;

public class NamedGeofence implements Comparable {

    // region Properties

    public String id;
    public String name;
    public String storeId;
    public String message;
    public double latitude;
    public double longitude;
    public float radius;

    // end region

    // region Public

    public Geofence geofence() {
        return new Geofence.Builder()
                .setRequestId(id)
                .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER)
                .setCircularRegion(latitude, longitude, radius)
                .setExpirationDuration(Geofence.NEVER_EXPIRE)
                .build();
    }

    // endregion

    // region Comparable

    @Override
    public int compareTo(@NonNull Object another) {
        NamedGeofence other = (NamedGeofence) another;
        return name.compareTo(other.name);
    }

    // endregion
}

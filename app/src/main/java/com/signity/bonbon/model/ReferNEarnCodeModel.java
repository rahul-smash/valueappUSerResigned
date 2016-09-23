package com.signity.bonbon.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by root on 23/6/16.
 */
public class ReferNEarnCodeModel {

    @SerializedName("id")
    private String id;

    @SerializedName("bl_device_id_unique")
    private Boolean blDeviceIdUnique;

    @SerializedName("shared_message")
    private String sharedMessage;

    @SerializedName("notification_message")
    private String notificationMessage;

    public String getNotificationMessage() {
        return notificationMessage;
    }

    public void setNotificationMessage(String notificationMessage) {
        this.notificationMessage = notificationMessage;
    }

    public String getSharedMessage() {
        return sharedMessage;
    }

    public void setSharedMessage(String sharedMessage) {
        this.sharedMessage = sharedMessage;
    }

    public Boolean getBlDeviceIdUnique() {
        return blDeviceIdUnique;
    }

    public void setBlDeviceIdUnique(Boolean blDeviceIdUnique) {
        this.blDeviceIdUnique = blDeviceIdUnique;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

}

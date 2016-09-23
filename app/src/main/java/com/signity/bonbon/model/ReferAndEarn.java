package com.signity.bonbon.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by rajesh on 2/6/16.
 */
public class ReferAndEarn {

    @SerializedName("id")
    private String id;
    @SerializedName("shared_message")
    private String message;
    @SerializedName("notification_message")
    private String notificationMessage;
    @SerializedName("bl_device_id_unique")
    private Boolean isDeviceUnique;


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getNotificationMessage() {
        return notificationMessage;
    }

    public void setNotificationMessage(String notificationMessage) {
        this.notificationMessage = notificationMessage;
    }

    public Boolean getDeviceUnique() {
        return isDeviceUnique;
    }

    public void setDeviceUnique(Boolean deviceUnique) {
        isDeviceUnique = deviceUnique;
    }
}

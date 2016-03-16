package com.signity.bonbon.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by rajesh on 16/3/16.
 */
public class ForceDownloadModel {

    @SerializedName("ios_app_version")
    @Expose
    private String iosAppVersion;
    @SerializedName("android_app_verison")
    @Expose
    private String androidAppVerison;
    @SerializedName("window_app_version")
    @Expose
    private String windowAppVersion;
    @SerializedName("force_download")
    @Expose
    private String forceDownload;
    @SerializedName("force_download_message")
    @Expose
    private String forceDownloadMessage;

    public String getIosAppVersion() {
        return iosAppVersion;
    }

    public void setIosAppVersion(String iosAppVersion) {
        this.iosAppVersion = iosAppVersion;
    }

    public String getAndroidAppVerison() {
        return androidAppVerison;
    }

    public void setAndroidAppVerison(String androidAppVerison) {
        this.androidAppVerison = androidAppVerison;
    }

    public String getWindowAppVersion() {
        return windowAppVersion;
    }

    public void setWindowAppVersion(String windowAppVersion) {
        this.windowAppVersion = windowAppVersion;
    }

    public String getForceDownload() {
        return forceDownload;
    }

    public void setForceDownload(String forceDownload) {
        this.forceDownload = forceDownload;
    }

    public String getForceDownloadMessage() {
        return forceDownloadMessage;
    }

    public void setForceDownloadMessage(String forceDownloadMessage) {
        this.forceDownloadMessage = forceDownloadMessage;
    }
}

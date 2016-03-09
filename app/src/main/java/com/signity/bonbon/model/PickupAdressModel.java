package com.signity.bonbon.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by rajesh on 9/3/16.
 */
public class PickupAdressModel implements Serializable {

    @SerializedName("pickup_add")
    @Expose
    private String pickupAdd;
    @SerializedName("id")
    @Expose
    private String id;
    @SerializedName("store_id")
    @Expose
    private String storeId;
    @SerializedName("pickup_lat")
    @Expose
    private String pickupLat;
    @SerializedName("pickup_lng")
    @Expose
    private String pickupLng;
    @SerializedName("pickup_phone")
    @Expose
    private String pickupPhone;
    @SerializedName("pickup_email")
    @Expose
    private String pickupEmail;

    public String getPickupAdd() {
        return pickupAdd;
    }

    public void setPickupAdd(String pickupAdd) {
        this.pickupAdd = pickupAdd;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getStoreId() {
        return storeId;
    }

    public void setStoreId(String storeId) {
        this.storeId = storeId;
    }

    public String getPickupLat() {
        return pickupLat;
    }

    public void setPickupLat(String pickupLat) {
        this.pickupLat = pickupLat;
    }

    public String getPickupLng() {
        return pickupLng;
    }

    public void setPickupLng(String pickupLng) {
        this.pickupLng = pickupLng;
    }

    public String getPickupPhone() {
        return pickupPhone;
    }

    public void setPickupPhone(String pickupPhone) {
        this.pickupPhone = pickupPhone;
    }

    public String getPickupEmail() {
        return pickupEmail;
    }

    public void setPickupEmail(String pickupEmail) {
        this.pickupEmail = pickupEmail;
    }

    @Override
    public String toString() {
        return "PickupAdressModel{" +
                "pickupAdd='" + pickupAdd + '\'' +
                ", id='" + id + '\'' +
                ", storeId='" + storeId + '\'' +
                ", pickupLat='" + pickupLat + '\'' +
                ", pickupLng='" + pickupLng + '\'' +
                ", pickupPhone='" + pickupPhone + '\'' +
                ", pickupEmail='" + pickupEmail + '\'' +
                '}';
    }
}

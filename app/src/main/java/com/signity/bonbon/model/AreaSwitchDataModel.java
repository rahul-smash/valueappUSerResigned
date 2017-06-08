package com.signity.bonbon.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by root on 13/9/16.
 */
public class AreaSwitchDataModel {

    @SerializedName("delivery_area")
    @Expose
    private String deliveryArea;
    @SerializedName("radius_in")
    @Expose
    private String radiusIn;
    @SerializedName("city")
    @Expose
    private String city;
    @SerializedName("state")
    @Expose
    private String state;
    @SerializedName("country")
    @Expose
    private String country;

    /**
     *
     * @return
     * The deliveryArea
     */
    public String getDeliveryArea() {
        return deliveryArea;
    }

    /**
     *
     * @param deliveryArea
     * The delivery_area
     */
    public void setDeliveryArea(String deliveryArea) {
        this.deliveryArea = deliveryArea;
    }

    /**
     *
     * @return
     * The radiusIn
     */
    public String getRadiusIn() {
        return radiusIn;
    }

    /**
     *
     * @param radiusIn
     * The radius_in
     */
    public void setRadiusIn(String radiusIn) {
        this.radiusIn = radiusIn;
    }

    /**
     *
     * @return
     * The city
     */
    public String getCity() {
        return city;
    }

    /**
     *
     * @param city
     * The city
     */
    public void setCity(String city) {
        this.city = city;
    }

    /**
     *
     * @return
     * The state
     */
    public String getState() {
        return state;
    }

    /**
     *
     * @param state
     * The state
     */
    public void setState(String state) {
        this.state = state;
    }

    /**
     *
     * @return
     * The country
     */
    public String getCountry() {
        return country;
    }

    /**
     *
     * @param country
     * The country
     */
    public void setCountry(String country) {
        this.country = country;
    }
}

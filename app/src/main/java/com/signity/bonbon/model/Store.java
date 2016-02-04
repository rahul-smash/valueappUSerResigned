package com.signity.bonbon.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by root on 20/10/15.
 */
public class Store {
    @SerializedName("id")
    @Expose
    private String id;
    @SerializedName("store_name")
    @Expose
    private String storeName;
    @SerializedName("location")
    @Expose
    private String location;
    @SerializedName("city")
    @Expose
    private String city;
    @SerializedName("state")
    @Expose
    private String state;
    @SerializedName("country")
    @Expose
    private String country;
    @SerializedName("zipcode")
    @Expose
    private String zipcode;
    @SerializedName("contact_person")
    @Expose
    private String contactPerson;
    @SerializedName("contact_number")
    @Expose
    private String contactNumber;
    @SerializedName("banner")
    @Expose
    private String banner;
    @SerializedName("about_us")
    @Expose
    private String aboutUs;
    @SerializedName("lat")
    @Expose
    private String lat = "";
    @SerializedName("lng")
    @Expose
    private String lng = "";

    @SerializedName("store_status")
    @Expose
    private String storeStatus;

    @SerializedName("version")
    @Expose
    private String version;

    @SerializedName("android_app_share")
    private String androidAppShareLink;

    @SerializedName("type")
    private String type;

    @SerializedName("theme")
    private String theme;

    @SerializedName("current_gold_rate")
    private String currentGoldRate;

    @SerializedName("display_gold_rate")
    private String displayGoldRate;
    @SerializedName("otp_skip")
    private String otpSkip;

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    /**
     * @return The id
     */
    public String getId() {
        return id;
    }

    /**
     * @param id The id
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * @return The storeName
     */
    public String getStoreName() {
        return storeName;
    }

    /**
     * @param storeName The store_name
     */
    public void setStoreName(String storeName) {
        this.storeName = storeName;
    }

    /**
     * @return The location
     */
    public String getLocation() {
        return location;
    }

    /**
     * @param location The location
     */
    public void setLocation(String location) {
        this.location = location;
    }

    /**
     * @return The city
     */
    public String getCity() {
        return city;
    }

    /**
     * @param city The city
     */
    public void setCity(String city) {
        this.city = city;
    }

    /**
     * @return The state
     */
    public String getState() {
        return state;
    }

    /**
     * @param state The state
     */
    public void setState(String state) {
        this.state = state;
    }

    /**
     * @return The country
     */
    public String getCountry() {
        return country;
    }

    /**
     * @param country The country
     */
    public void setCountry(String country) {
        this.country = country;
    }

    /**
     * @return The zipcode
     */
    public String getZipcode() {
        return zipcode;
    }

    /**
     * @param zipcode The zipcode
     */
    public void setZipcode(String zipcode) {
        this.zipcode = zipcode;
    }

    /**
     * @return The contactPerson
     */
    public String getContactPerson() {
        return contactPerson;
    }

    /**
     * @param contactPerson The contact_person
     */
    public void setContactPerson(String contactPerson) {
        this.contactPerson = contactPerson;
    }

    /**
     * @return The contactNumber
     */
    public String getContactNumber() {
        return contactNumber;
    }

    /**
     * @param contactNumber The contact_number
     */
    public void setContactNumber(String contactNumber) {
        this.contactNumber = contactNumber;
    }

    /**
     * @return The banner
     */
    public String getBanner() {
        return banner;
    }

    /**
     * @param banner The banner
     */
    public void setBanner(String banner) {
        this.banner = banner;
    }

    /**
     * @return The aboutUs
     */
    public String getAboutUs() {
        return aboutUs;
    }

    /**
     * @param aboutUs The about_us
     */
    public void setAboutUs(String aboutUs) {
        this.aboutUs = aboutUs;
    }


    public String getLat() {
        return lat;
    }

    public void setLat(String lat) {
        this.lat = lat;
    }

    public String getLng() {
        return lng;
    }

    public void setLng(String lng) {
        this.lng = lng;
    }

    public String getStoreStatus() {
        return storeStatus;
    }

    public void setStoreStatus(String storeStatus) {
        this.storeStatus = storeStatus;
    }


    public String getAndroidAppShareLink() {
        return androidAppShareLink;
    }

    public void setAndroidAppShareLink(String androidAppShareLink) {
        this.androidAppShareLink = androidAppShareLink;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getTheme() {
        return theme;
    }

    public void setTheme(String theme) {
        this.theme = theme;
    }

    public String getCurrentGoldRate() {
        return currentGoldRate;
    }

    public void setCurrentGoldRate(String currentGoldRate) {
        this.currentGoldRate = currentGoldRate;
    }

    public String getDisplayGoldRate() {
        return displayGoldRate;
    }

    public void setDisplayGoldRate(String displayGoldRate) {
        this.displayGoldRate = displayGoldRate;
    }

    public String getOtpSkip() {
        return otpSkip;
    }

    public void setOtpSkip(String otpSkip) {
        this.otpSkip = otpSkip;
    }
}

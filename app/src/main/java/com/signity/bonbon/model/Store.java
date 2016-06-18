package com.signity.bonbon.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

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

    @SerializedName("store_msg")
    @Expose
    private String storeMsg;

    @SerializedName("version")
    @Expose
    private String version;

    @SerializedName("android_app_share")
    private String androidAppShareLink;

    @SerializedName("type")
    private String type;

    @SerializedName("theme")
    private String theme;

    @SerializedName("otp_skip")
    private String otpSkip;
    @SerializedName("category_layout")
    private String categoryLayoutType;   //0 for gird 1 for linear

    @SerializedName("loyality")
    @Expose
    private String loyality;

    @SerializedName("store_open_days")
    @Expose
    private String storeOpenDays;


    @SerializedName("istaxenable")
    @Expose
    private String istaxenable;
    @SerializedName("tax_label_name")
    @Expose
    private String taxLabelName;
    @SerializedName("tax_rate")
    @Expose
    private String taxRate;

    @SerializedName("app_labels")
    @Expose
    private List<AppLabel> appLabels = new ArrayList<AppLabel>();

    @SerializedName("currency")
    @Expose
    private String currency;
    @SerializedName("pickup_facility")
    @Expose
    private String pickUpFacility;


    @SerializedName("delivery_facility")
    @Expose
    private String deliveryFacility;

    @SerializedName("geofencing")
    @Expose
    private String geoFenceStatus;


    @SerializedName("Geofencing")
    @Expose
    private List<GeofenceObjectModel> geofenceObjects = new ArrayList<GeofenceObjectModel>();

    @SerializedName("openhours_to")
    @Expose
    private String openhoursTo;
    @SerializedName("closehours_message")
    @Expose
    private String closehoursMessage;

    @SerializedName("is24x7_open")
    @Expose
    private String is24x7_open;

    @SerializedName("banners")
    @Expose
    private List<Banner> banners = new ArrayList<Banner>();

    @SerializedName("openhours_from")
    @Expose
    private String openhoursFrom;

    @SerializedName("force_download")
    @Expose
    private List<ForceDownloadModel> forceDownload = new ArrayList<ForceDownloadModel>();

    @SerializedName("ga_code")
    @Expose
    private String gaCode;


    @SerializedName("is_referer_fn_enable")
    @Expose
    private Boolean isReferFnEnable;
    @SerializedName("bl_device_id_unique")
    @Expose
    private Boolean isReferForDeviceEnable;


    @SerializedName("tax_detail")
    @Expose
    private List<TaxDetail> taxDetail = new ArrayList<TaxDetail>();

    @SerializedName("fixed_tax_detail")
    @Expose
    private List<FixedTaxDetail> fixedTaxDetail = new ArrayList<FixedTaxDetail>();


    public String getDeliveryFacility() {
        return deliveryFacility;
    }

    public void setDeliveryFacility(String deliveryFacility) {
        this.deliveryFacility = deliveryFacility;
    }

    public String getOpenhoursFrom() {
        return openhoursFrom;
    }

    public void setOpenhoursFrom(String openhoursFrom) {
        this.openhoursFrom = openhoursFrom;
    }


    public String getOpenhoursTo() {
        return openhoursTo;
    }

    public void setOpenhoursTo(String openhoursTo) {
        this.openhoursTo = openhoursTo;
    }

    public String getClosehoursMessage() {
        return closehoursMessage;
    }

    public void setClosehoursMessage(String closehoursMessage) {
        this.closehoursMessage = closehoursMessage;
    }


    public List<Banner> getBanners() {
        return banners;
    }

    public void setBanners(List<Banner> banners) {
        this.banners = banners;
    }


    public List<AppLabel> getAppLabels() {
        return appLabels;
    }

    public void setAppLabels(List<AppLabel> appLabels) {
        this.appLabels = appLabels;
    }


    public List<GeofenceObjectModel> getGeofenceObjects() {
        return geofenceObjects;
    }

    public void setGeofenceObjects(List<GeofenceObjectModel> geofenceObjects) {
        this.geofenceObjects = geofenceObjects;
    }


    public List<FixedTaxDetail> getFixedTaxDetail() {
        return fixedTaxDetail;
    }

    public void setFixedTaxDetail(List<FixedTaxDetail> fixedTaxDetail) {
        this.fixedTaxDetail = fixedTaxDetail;
    }

    public List<TaxDetail> getTaxDetail() {
        return taxDetail;
    }

    public void setTaxDetail(List<TaxDetail> taxDetail) {
        this.taxDetail = taxDetail;
    }
    public String getStoreMsg() {
        return storeMsg;
    }

    public void setStoreMsg(String storeMsg) {
        this.storeMsg = storeMsg;
    }


    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }


    public String getIs24x7_open() {
        return is24x7_open;
    }

    public void setIs24x7_open(String is24x7_open) {
        this.is24x7_open = is24x7_open;
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

    public String getOtpSkip() {
        return otpSkip;
    }

    public void setOtpSkip(String otpSkip) {
        this.otpSkip = otpSkip;
    }


    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getPickUpFacility() {
        return pickUpFacility;
    }

    public void setPickUpFacility(String pickUpFacility) {
        this.pickUpFacility = pickUpFacility;
    }


    public String getGeoFenceStatus() {
        return geoFenceStatus;
    }

    public void setGeoFenceStatus(String geoFenceStatus) {
        this.geoFenceStatus = geoFenceStatus;
    }

    public List<ForceDownloadModel> getForceDownload() {
        return forceDownload;
    }

    public void setForceDownload(List<ForceDownloadModel> forceDownload) {
        this.forceDownload = forceDownload;
    }


    public String getLoyality() {
        return loyality;
    }

    public void setLoyality(String loyality) {
        this.loyality = loyality;
    }

    public String getStoreOpenDays() {
        return storeOpenDays;
    }

    public void setStoreOpenDays(String storeOpenDays) {
        this.storeOpenDays = storeOpenDays;
    }


    public String getIstaxenable() {
        return istaxenable;
    }

    public void setIstaxenable(String istaxenable) {
        this.istaxenable = istaxenable;
    }

    public String getTaxLabelName() {
        return taxLabelName;
    }

    public void setTaxLabelName(String taxLabelName) {
        this.taxLabelName = taxLabelName;
    }

    public String getTaxRate() {
        return taxRate;
    }

    public void setTaxRate(String taxRate) {
        this.taxRate = taxRate;
    }

    public String getGaCode() {
        return gaCode;
    }

    public void setGaCode(String gaCode) {
        this.gaCode = gaCode;
    }

    public String getCategoryLayoutType() {
        return categoryLayoutType;
    }

    public void setCategoryLayoutType(String categoryLayoutType) {
        this.categoryLayoutType = categoryLayoutType;
    }

    public Boolean getReferForDeviceEnable() {
        return isReferForDeviceEnable;
    }

    public void setReferForDeviceEnable(Boolean referForDeviceEnable) {
        isReferForDeviceEnable = referForDeviceEnable;
    }

    public Boolean getReferFnEnable() {
        return isReferFnEnable;
    }

    public void setReferFnEnable(Boolean referFnEnable) {
        isReferFnEnable = referFnEnable;
    }
}

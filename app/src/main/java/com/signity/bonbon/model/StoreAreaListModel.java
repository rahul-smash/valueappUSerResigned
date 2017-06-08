package com.signity.bonbon.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by root on 19/7/16.
 */
public class StoreAreaListModel {

    @SerializedName("area_id")
    @Expose
    private String areaId;
    @SerializedName("city_id")
    @Expose
    private String cityId;
    @SerializedName("store_id")
    @Expose
    private String storeId;
    @SerializedName("area")
    @Expose
    private String area;
    @SerializedName("min_order")
    @Expose
    private String minOrder;
    @SerializedName("charges")
    @Expose
    private String charges;
    @SerializedName("note")
    @Expose
    private String note;
    @SerializedName("not_allow")
    @Expose
    private Boolean notAllow;
    @SerializedName("radius")
    @Expose
    private String radius;

    @SerializedName("radius_circle")
    @Expose
    private String radiusCircle;


    public String getRadiusCircle() {
        return radiusCircle;
    }

    public void setRadiusCircle(String radiusCircle) {
        this.radiusCircle = radiusCircle;
    }

    /**
     *
     * @return
     * The areaId
     */
    public String getAreaId() {
        return areaId;
    }

    /**
     *
     * @param areaId
     * The area_id
     */
    public void setAreaId(String areaId) {
        this.areaId = areaId;
    }

    /**
     *
     * @return
     * The cityId
     */
    public String getCityId() {
        return cityId;
    }

    /**
     *
     * @param cityId
     * The city_id
     */
    public void setCityId(String cityId) {
        this.cityId = cityId;
    }

    /**
     *
     * @return
     * The storeId
     */
    public String getStoreId() {
        return storeId;
    }

    /**
     *
     * @param storeId
     * The store_id
     */
    public void setStoreId(String storeId) {
        this.storeId = storeId;
    }

    /**
     *
     * @return
     * The area
     */
    public String getArea() {
        return area;
    }

    /**
     *
     * @param area
     * The area
     */
    public void setArea(String area) {
        this.area = area;
    }

    /**
     *
     * @return
     * The minOrder
     */
    public String getMinOrder() {
        return minOrder;
    }

    /**
     *
     * @param minOrder
     * The min_order
     */
    public void setMinOrder(String minOrder) {
        this.minOrder = minOrder;
    }

    /**
     *
     * @return
     * The charges
     */
    public String getCharges() {
        return charges;
    }

    /**
     *
     * @param charges
     * The charges
     */
    public void setCharges(String charges) {
        this.charges = charges;
    }

    /**
     *
     * @return
     * The note
     */
    public String getNote() {
        return note;
    }

    /**
     *
     * @param note
     * The note
     */
    public void setNote(String note) {
        this.note = note;
    }

    /**
     *
     * @return
     * The notAllow
     */
    public Boolean getNotAllow() {
        return notAllow;
    }

    /**
     *
     * @param notAllow
     * The not_allow
     */
    public void setNotAllow(Boolean notAllow) {
        this.notAllow = notAllow;
    }

    /**
     *
     * @return
     * The radius
     */
    public String getRadius() {
        return radius;
    }

    /**
     *
     * @param radius
     * The radius
     */
    public void setRadius(String radius) {
        this.radius = radius;
    }
}

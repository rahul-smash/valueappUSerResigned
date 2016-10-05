package com.signity.bonbon.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by root on 13/9/16.
 */
public class AddressSelectData {

    @SerializedName("id")
    @Expose
    private String id;
    @SerializedName("typeName")
    @Expose
    private String typeName;

    @SerializedName("city_id")
    @Expose
    private String cityId;
    @SerializedName("store_id")
    @Expose
    private String storeId;
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
    @SerializedName("area")
    @Expose
    private String area;


    /**
     *
     * @return
     * The id
     */
    public String getId() {
        return id;
    }

    /**
     *
     * @param id
     * The id
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     *
     * @return
     * The typeName
     */
    public String getTypeName() {
        return typeName;
    }

    /**
     *
     * @param typeName
     * The typeName
     */
    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }

    public String getCityId() {
        return cityId;
    }

    public void setCityId(String cityId) {
        this.cityId = cityId;
    }

    public String getStoreId() {
        return storeId;
    }

    public void setStoreId(String storeId) {
        this.storeId = storeId;
    }

    public String getMinOrder() {
        return minOrder;
    }

    public void setMinOrder(String minOrder) {
        this.minOrder = minOrder;
    }

    public String getCharges() {
        return charges;
    }

    public void setCharges(String charges) {
        this.charges = charges;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public Boolean getNotAllow() {
        return notAllow;
    }

    public void setNotAllow(Boolean notAllow) {
        this.notAllow = notAllow;
    }

    public String getRadius() {
        return radius;
    }

    public void setRadius(String radius) {
        this.radius = radius;
    }

    public String getArea() {
        return area;
    }

    public void setArea(String area) {
        this.area = area;
    }

}

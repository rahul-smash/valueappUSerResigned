package com.signity.bonbon.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by root on 16/10/15.
 */
public class GetStoreAreaModel1 {
    @SerializedName("area_id")
    @Expose
    private String areaId;
    @SerializedName("city_id")
    @Expose
    private String cityId;
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
}

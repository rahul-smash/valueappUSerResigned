package com.signity.bonbon.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by root on 29/3/16.
 */
public class LoyalityModel {


    @SerializedName("success")
    @Expose
    private Boolean success;
    @SerializedName("loyality_points")
    @Expose
    private String loyalityPoints;
    @SerializedName("data")
    @Expose
    private List<LoyalityDataModel> data = new ArrayList<LoyalityDataModel>();

    /**
     *
     * @return
     * The success
     */
    public Boolean getSuccess() {
        return success;
    }

    /**
     *
     * @param success
     * The success
     */
    public void setSuccess(Boolean success) {
        this.success = success;
    }

    /**
     *
     * @return
     * The loyalityPoints
     */
    public String getLoyalityPoints() {
        return loyalityPoints;
    }

    /**
     *
     * @param loyalityPoints
     * The loyality_points
     */
    public void setLoyalityPoints(String loyalityPoints) {
        this.loyalityPoints = loyalityPoints;
    }

    /**
     *
     * @return
     * The data
     */
    public List<LoyalityDataModel> getData() {
        return data;
    }

    /**
     *
     * @param data
     * The data
     */
    public void setData(List<LoyalityDataModel> data) {
        this.data = data;
    }

}

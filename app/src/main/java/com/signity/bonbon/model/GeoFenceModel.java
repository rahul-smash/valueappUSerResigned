package com.signity.bonbon.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by rajesh on 10/3/16.
 */
public class GeoFenceModel {

    @SerializedName("success")
    @Expose
    private Boolean success;
    @SerializedName("")
    private GeoFenceMessage data;

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
     * The data
     */
    public GeoFenceMessage getData() {
        return data;
    }

    /**
     *
     * @param data
     * The data
     */
    public void setData(GeoFenceMessage data) {
        this.data = data;
    }
}

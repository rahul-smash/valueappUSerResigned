package com.signity.bonbon.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by root on 13/9/16.
 */
public class AreaSwitchModel {

    @SerializedName("success")
    @Expose
    private Boolean success;
    @SerializedName("data")
    @Expose
    private AreaSwitchDataModel data;

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
    public AreaSwitchDataModel getData() {
        return data;
    }

    /**
     *
     * @param data
     * The data
     */
    public void setData(AreaSwitchDataModel data) {
        this.data = data;
    }

}

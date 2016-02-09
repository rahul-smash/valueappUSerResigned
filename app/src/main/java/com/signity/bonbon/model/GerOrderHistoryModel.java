package com.signity.bonbon.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by rajesh on 15/10/15.
 */
public class GerOrderHistoryModel {

    @SerializedName("success")
    @Expose
    private Boolean success;
    @SerializedName("data")
    @Expose
    private List<OrderHistoryModel> data = new ArrayList<OrderHistoryModel>();

    /**
     * @return The success
     */
    public Boolean getSuccess() {
        return success;
    }

    /**
     * @param success The success
     */
    public void setSuccess(Boolean success) {
        this.success = success;
    }


    public List<OrderHistoryModel> getData() {
        return data;
    }

    public void setData(List<OrderHistoryModel> data) {
        this.data = data;
    }
}

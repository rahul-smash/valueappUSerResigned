package com.signity.bonbon.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by rajesh on 9/3/16.
 */
public class GetPickupApiResponse {


    @SerializedName("success")
    @Expose
    private Boolean success;
    @SerializedName("data")
    @Expose
    private List<PickupAdressModel> data = new ArrayList<PickupAdressModel>();


    public Boolean getSuccess() {
        return success;
    }

    public void setSuccess(Boolean success) {
        this.success = success;
    }

    public List<PickupAdressModel> getData() {
        return data;
    }

    public void setData(List<PickupAdressModel> data) {
        this.data = data;
    }
}

package com.signity.bonbon.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by rajesh on 3/11/15.
 */
public class GetBigComCategory {

    @SerializedName("success")
    @Expose
    private Boolean success;
    @SerializedName("data")
    @Expose
    private List<Category_BigCommerce> data = new ArrayList<Category_BigCommerce>();

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


    public List<Category_BigCommerce> getData() {
        return data;
    }

    public void setData(List<Category_BigCommerce> data) {
        this.data = data;
    }
}

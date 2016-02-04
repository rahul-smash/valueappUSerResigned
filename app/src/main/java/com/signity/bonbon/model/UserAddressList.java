package com.signity.bonbon.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by root on 14/10/15.
 */
public class UserAddressList {

    @SerializedName("success")
    @Expose
    private Boolean success;
    @SerializedName("data")
    @Expose
    private List<UserAddressModel> data = new ArrayList<UserAddressModel>();

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

    /**
     * @return The data
     */
    public List<UserAddressModel> getData() {
        return data;
    }

    /**
     * @param data The data
     */
    public void setData(List<UserAddressModel> data) {
        this.data = data;
    }
}

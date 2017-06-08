package com.signity.bonbon.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by root on 13/9/16.
 */
public class AddressSelectModel {


    @SerializedName("success")
    @Expose
    private Boolean success;

    @SerializedName("message")
    @Expose
    private String message;

    @SerializedName("data")
    @Expose
    private List<AddressSelectData> data = new ArrayList<AddressSelectData>();

    /**
     *
     * @return
     * The success
     */

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }


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
    public List<AddressSelectData> getData() {
        return data;
    }

    /**
     *
     * @param data
     * The data
     */
    public void setData(List<AddressSelectData> data) {
        this.data = data;
    }


}

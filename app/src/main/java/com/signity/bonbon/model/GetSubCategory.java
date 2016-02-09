package com.signity.bonbon.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Rajinder on 5/10/15.
 */
public class GetSubCategory {


    @SerializedName("success")
    @Expose
    private Boolean success;
    @SerializedName("data")
    @Expose
    private List<SubCategory> data = new ArrayList<SubCategory>();

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


    public List<SubCategory> getData() {
        return data;
    }

    public void setData(List<SubCategory> data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "GetSubCategory{" +
                "success=" + success +
                ", data=" + data +
                '}';
    }
}

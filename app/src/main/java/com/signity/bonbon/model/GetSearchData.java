package com.signity.bonbon.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by root on 21/10/15.
 */
public class GetSearchData {

    @SerializedName("success")
    @Expose
    private Boolean success;

    public List<GetSearchProducts> getData() {
        return data;
    }

    public void setData(List<GetSearchProducts> data) {
        this.data = data;
    }

    @SerializedName("data")
    @Expose
    private List<GetSearchProducts> data = new ArrayList<GetSearchProducts>();

    public Boolean getSuccess() {
        return success;
    }

    public void setSuccess(Boolean success) {
        this.success = success;
    }



    @Override
    public String toString() {
        return "ProductListModel{" +
                "success=" + success +
                ", data=" + data +
                '}';
    }
}

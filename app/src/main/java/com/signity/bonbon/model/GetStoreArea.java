package com.signity.bonbon.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by root on 26/10/15.
 */
public class GetStoreArea {
    @SerializedName("success")
    @Expose
    private Boolean success;

    public List<GetStoreAreaModel> getData() {
        return data;
    }

    public void setData(List<GetStoreAreaModel> data) {
        this.data = data;
    }

    @SerializedName("data")
    @Expose
    private List<GetStoreAreaModel> data = new ArrayList<GetStoreAreaModel>();

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

}

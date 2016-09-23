package com.signity.bonbon.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by root on 5/7/16.
 */
public class EnquiryModel
{

    @SerializedName("success")
    @Expose
    private Boolean success;

    @SerializedName("data")
    @Expose
    private EnquiryDataModel enquiryDataModel;


    public Boolean getSuccess() {
        return success;
    }

    public void setSuccess(Boolean success) {
        this.success = success;
    }

    public EnquiryDataModel getEnquiryDataModel() {
        return enquiryDataModel;
    }

    public void setEnquiryDataModel(EnquiryDataModel enquiryDataModel) {
        this.enquiryDataModel = enquiryDataModel;
    }


    @Override
    public String toString()
    {
        return "ClassPojo [data = "+enquiryDataModel+", success = "+success+"]";
    }
}

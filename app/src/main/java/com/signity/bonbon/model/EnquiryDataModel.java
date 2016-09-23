package com.signity.bonbon.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by root on 5/7/16.
 */
public class EnquiryDataModel {

    @SerializedName("message")
    @Expose
    private String message;

    @SerializedName("id")
    @Expose
    private String id;

    @SerializedName("status")
    @Expose
    private Boolean status;

    @SerializedName("store_id")
    @Expose
    private String store_id;


    public String getMessage ()
    {
        return message;
    }

    public void setMessage (String message)
    {
        this.message = message;
    }

    public String getId ()
    {
        return id;
    }

    public void setId (String id)
    {
        this.id = id;
    }

    public Boolean getStatus() {
        return status;
    }

    public void setStatus(Boolean status) {
        this.status = status;
    }

    public String getStore_id ()
    {
        return store_id;
    }

    public void setStore_id (String store_id)
    {
        this.store_id = store_id;
    }

    @Override
    public String toString()
    {
        return "ClassPojo [message = "+message+", id = "+id+", status = "+status+", store_id = "+store_id+"]";
    }


}

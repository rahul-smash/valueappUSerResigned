package com.signity.bonbon.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by root on 23/6/16.
 */
public class ReferNEarnModel {

    @SerializedName("status")
    private String status;

    @SerializedName("message")
    private String message;

    @SerializedName("is_referer_fn_enable")
    private Boolean isRefererFnEnable;

    @SerializedName("ReferEarn")
    private ReferNEarnCodeModel referAndEarn;


    public ReferNEarnCodeModel getReferAndEarn() {
        return referAndEarn;
    }

    public void setReferAndEarn(ReferNEarnCodeModel referAndEarn) {
        this.referAndEarn = referAndEarn;
    }


    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Boolean getIsRefererFnEnable() {
        return isRefererFnEnable;
    }

    public void setIsRefererFnEnable(Boolean isRefererFnEnable) {
        this.isRefererFnEnable = isRefererFnEnable;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

}

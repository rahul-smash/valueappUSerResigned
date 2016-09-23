package com.signity.bonbon.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by root on 27/4/16.
 */
public class OnlinePaymentModel {

    @SerializedName("success")
    @Expose
    private Boolean success;


    @SerializedName("message")
    @Expose
    private String message;

    @SerializedName("payment_url")
    @Expose
    private String paymentUrl;


    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Boolean getSuccess() {
        return success;
    }


    public void setSuccess(Boolean success) {
        this.success = success;
    }


    public String getPaymentUrl() {
        return paymentUrl;
    }

    public void setPaymentUrl(String paymentUrl) {
        this.paymentUrl = paymentUrl;
    }
}

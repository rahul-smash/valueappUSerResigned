package com.signity.bonbon.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by root on 1/9/16.
 */
public class ValidAllCouponsModel {

    @SerializedName("success")
    @Expose
    private Boolean success;
    @SerializedName("message")
    @Expose
    private String message;
    @SerializedName("data")
    @Expose
    private ValidAllCouponData data;
    @SerializedName("DiscountAmount")
    @Expose
    private Double discountAmount;

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

    /**
     *
     * @return
     * The message
     */
    public String getMessage() {
        return message;
    }

    /**
     *
     * @param message
     * The message
     */
    public void setMessage(String message) {
        this.message = message;
    }

    /**
     *
     * @return
     * The data
     */
    public ValidAllCouponData getData() {
        return data;
    }

    /**
     *
     * @param data
     * The data
     */
    public void setData(ValidAllCouponData data) {
        this.data = data;
    }

    /**
     *
     * @return
     * The discountAmount
     */
    public Double getDiscountAmount() {
        return discountAmount;
    }

    /**
     *
     * @param discountAmount
     * The DiscountAmount
     */
    public void setDiscountAmount(Double discountAmount) {
        this.discountAmount = discountAmount;
    }
}

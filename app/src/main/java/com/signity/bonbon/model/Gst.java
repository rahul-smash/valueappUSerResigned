package com.signity.bonbon.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by root on 28/7/17.
 */
public class Gst {

    @SerializedName("type")
    @Expose
    private String type;
    @SerializedName("tax1")
    @Expose
    private Double tax1;
    @SerializedName("tax2")
    @Expose
    private Double tax2;
    @SerializedName("lable1")
    @Expose
    private String lable1;
    @SerializedName("lable2")
    @Expose
    private String lable2;
    @SerializedName("rate")
    @Expose
    private String rate;
    @SerializedName("actual_price")
    @Expose
    private Double actualPrice;
    @SerializedName("total_price")
    @Expose
    private Integer totalPrice;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Double getTax1() {
        return tax1;
    }

    public void setTax1(Double tax1) {
        this.tax1 = tax1;
    }

    public Double getTax2() {
        return tax2;
    }

    public void setTax2(Double tax2) {
        this.tax2 = tax2;
    }

    public String getLable1() {
        return lable1;
    }

    public void setLable1(String lable1) {
        this.lable1 = lable1;
    }

    public String getLable2() {
        return lable2;
    }

    public void setLable2(String lable2) {
        this.lable2 = lable2;
    }

    public String getRate() {
        return rate;
    }

    public void setRate(String rate) {
        this.rate = rate;
    }

    public Double getActualPrice() {
        return actualPrice;
    }

    public void setActualPrice(Double actualPrice) {
        this.actualPrice = actualPrice;
    }

    public Integer getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(Integer totalPrice) {
        this.totalPrice = totalPrice;
    }


}

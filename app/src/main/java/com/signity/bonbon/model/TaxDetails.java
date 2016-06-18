package com.signity.bonbon.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by root on 10/6/16.
 */
public class TaxDetails {


    @SerializedName("label")
    @Expose
    private String label;
    @SerializedName("rate")
    @Expose
    private String rate;
    @SerializedName("tax")
    @Expose
    private String tax;


    public String getTax() {
        return tax;
    }

    public void setTax(String tax) {
        this.tax = tax;
    }

    public String getRate() {
        return rate;
    }

    public void setRate(String rate) {
        this.rate = rate;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }


}

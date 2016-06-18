package com.signity.bonbon.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by root on 10/6/16.
 */
public class FixedTaxDetail {

    @SerializedName("fixed_tax_label")
    @Expose
    private String fixedTaxLabel;

    @SerializedName("fixed_tax_amount")
    @Expose
    private String fixedTaxAmount;

    @SerializedName("is_tax_enable")
    @Expose
    private String isTaxEnable;


    @SerializedName("is_discount_applicable")
    @Expose
    private String isDiscountApplicable;

    public String getIsDiscountApplicable() {
        return isDiscountApplicable;
    }

    public void setIsDiscountApplicable(String isDiscountApplicable) {
        this.isDiscountApplicable = isDiscountApplicable;
    }

    public String getIsTaxEnable() {
        return isTaxEnable;
    }

    public void setIsTaxEnable(String isTaxEnable) {
        this.isTaxEnable = isTaxEnable;
    }

    public String getFixedTaxAmount() {
        return fixedTaxAmount;
    }

    public void setFixedTaxAmount(String fixedTaxAmount) {
        this.fixedTaxAmount = fixedTaxAmount;
    }

    public String getFixedTaxLabel() {
        return fixedTaxLabel;
    }

    public void setFixedTaxLabel(String fixedTaxLabel) {
        this.fixedTaxLabel = fixedTaxLabel;
    }

}

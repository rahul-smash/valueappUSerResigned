package com.signity.bonbon.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by root on 20/5/16.
 */
public class TaxDataModel {

    @SerializedName("total")
    @Expose
    private String total;

    @SerializedName("item_sub_total")
    @Expose
    private String itemSubTotal;

    @SerializedName("tax")
    @Expose
    private String tax;


    @SerializedName("discount")
    @Expose
    private String discount;


    @SerializedName("shipping")
    @Expose
    private String shipping;


    public String getShipping() {
        return shipping;
    }

    public void setShipping(String shipping) {
        this.shipping = shipping;
    }


    public String getDiscount() {
        return discount;
    }

    public void setDiscount(String discount) {
        this.discount = discount;
    }

    public String getTax() {
        return tax;
    }

    public void setTax(String tax) {
        this.tax = tax;
    }

    public String getItemSubTotal() {
        return itemSubTotal;
    }

    public void setItemSubTotal(String itemSubTotal) {
        this.itemSubTotal = itemSubTotal;
    }

    public String getTotal() {
        return total;
    }

    public void setTotal(String total) {
        this.total = total;
    }


}

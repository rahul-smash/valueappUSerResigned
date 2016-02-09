package com.signity.bonbon.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class UpdateCartModel {

    @SerializedName("product_id")
    @Expose
    private String productId;
    @SerializedName("variant_id")
    @Expose
    private String variantId;
    @SerializedName("weight")
    @Expose
    private String weight;
    @SerializedName("mrp_price")
    @Expose
    private String mrpPrice;
    @SerializedName("price")
    @Expose
    private String price;
    @SerializedName("discount")
    @Expose
    private String discount;
    @SerializedName("unit_type")
    @Expose
    private String unitType;
    @SerializedName("quantity")
    @Expose
    private String quantity;

    @SerializedName("product_name")
    @Expose
    private String productName;


    /**
     * @return The productId
     */
    public String getProductId() {
        return productId;
    }

    /**
     * @param productId The product_id
     */
    public void setProductId(String productId) {
        this.productId = productId;
    }

    /**
     * @return The variantId
     */
    public String getVariantId() {
        return variantId;
    }

    /**
     * @param variantId The variant_id
     */
    public void setVariantId(String variantId) {
        this.variantId = variantId;
    }

    /**
     * @return The weight
     */
    public String getWeight() {
        return weight;
    }

    /**
     * @param weight The weight
     */
    public void setWeight(String weight) {
        this.weight = weight;
    }

    /**
     * @return The mrpPrice
     */
    public String getMrpPrice() {
        return mrpPrice;
    }

    /**
     * @param mrpPrice The mrp_price
     */
    public void setMrpPrice(String mrpPrice) {
        this.mrpPrice = mrpPrice;
    }

    /**
     * @return The price
     */
    public String getPrice() {
        return price;
    }

    /**
     * @param price The price
     */
    public void setPrice(String price) {
        this.price = price;
    }

    /**
     * @return The discount
     */
    public String getDiscount() {
        return discount;
    }

    /**
     * @param discount The discount
     */
    public void setDiscount(String discount) {
        this.discount = discount;
    }

    /**
     * @return The unitType
     */
    public String getUnitType() {
        return unitType;
    }

    /**
     * @param unitType The unit_type
     */
    public void setUnitType(String unitType) {
        this.unitType = unitType;
    }

    /**
     * @return The quantity
     */
    public String getQuantity() {
        return quantity;
    }

    /**
     * @param quantity The quantity
     */
    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }


    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }
}


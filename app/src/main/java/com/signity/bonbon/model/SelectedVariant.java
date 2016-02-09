package com.signity.bonbon.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class SelectedVariant {

    @SerializedName("variant_id")
    @Expose
    private String variantId;
    @SerializedName("sku")
    @Expose
    private String sku;
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


    private int varientPosition = 0;

    public int getVarientPosition() {
        return varientPosition;
    }

    public void setVarientPosition(int varientPosition) {
        this.varientPosition = varientPosition;
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

    public String getSku() {
        return sku;
    }

    public void setSku(String sku) {
        this.sku = sku;
    }

    public String getWeight() {
        return weight;
    }

    public void setWeight(String weight) {
        this.weight = weight;
    }

    public String getMrpPrice() {
        return mrpPrice;
    }

    public void setMrpPrice(String mrpPrice) {
        this.mrpPrice = mrpPrice;
    }

    @Override
    public String toString() {
        return "SelectedVariant{" +
                "quantity='" + quantity + '\'' +
                ", discount='" + discount + '\'' +
                ", price='" + price + '\'' +
                ", variantId='" + variantId + '\'' +
                ", unitType='" + unitType + '\'' +
                ", varientPosition=" + varientPosition +
                '}';
    }
}
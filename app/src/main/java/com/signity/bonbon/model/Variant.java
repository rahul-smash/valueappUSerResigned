
package com.signity.bonbon.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Variant {

    @SerializedName("id")
    @Expose
    private String id;
    @SerializedName("product_id")
    @Expose
    private String productId;
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
    @SerializedName("order_by")
    @Expose
    private String orderBy;

    private String quantity = "0";

    /**
     * @return The id
     */
    public String getId() {
        return id;
    }

    /**
     * @param id The id
     */
    public void setId(String id) {
        this.id = id;
    }

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
     * @return The sku
     */
    public String getSku() {
        return sku;
    }

    /**
     * @param sku The sku
     */
    public void setSku(String sku) {
        this.sku = sku;
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
     * @return The orderBy
     */
    public String getOrderBy() {
        return orderBy;
    }

    /**
     * @param orderBy The order_by
     */
    public void setOrderBy(String orderBy) {
        this.orderBy = orderBy;
    }


    public String getQuantity() {
        return quantity;
    }

    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }

    @Override
    public String
    toString() {
        return "Variant{" +
                "id='" + id + '\'' +
                ", productId='" + productId + '\'' +
                ", sku='" + sku + '\'' +
                ", weight='" + weight + '\'' +
                ", mrpPrice='" + mrpPrice + '\'' +
                ", price='" + price + '\'' +
                ", discount='" + discount + '\'' +
                ", unitType='" + unitType + '\'' +
                ", orderBy='" + orderBy + '\'' +
                '}';
    }
}

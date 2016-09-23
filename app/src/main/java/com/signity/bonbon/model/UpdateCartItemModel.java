package com.signity.bonbon.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by root on 20/5/16.
 */
public class UpdateCartItemModel {

    @SerializedName("product_id")
    @Expose
    private String productId;
    @SerializedName("variant_id")
    @Expose
    private String variantId;

    @SerializedName("price")
    @Expose
    private String price;

    @SerializedName("quantity")
    @Expose
    private String quantity;

    @SerializedName("isTaxEnable")
    @Expose
    private String isTaxEnable;



    public String getIsTaxEnable() {
        return isTaxEnable;
    }

    public void setIsTaxEnable(String isTaxEnable) {
        this.isTaxEnable = isTaxEnable;
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

}

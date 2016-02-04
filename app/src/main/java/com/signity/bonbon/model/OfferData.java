package com.signity.bonbon.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by rajesh on 9/12/15.
 */
public class OfferData {

    @SerializedName("id")
    @Expose
    private String id;
    @SerializedName("store_id")
    @Expose
    private String storeId;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("coupon_code")
    @Expose
    private String couponCode;
    @SerializedName("discount")
    @Expose
    private String discount;
    @SerializedName("usage_limit")
    @Expose
    private String usageLimit;
    @SerializedName("minimum_order_amount")
    @Expose
    private String minimumOrderAmount;
    @SerializedName("offer_notification")
    @Expose
    private String offerNotification;
    @SerializedName("valid_from")
    @Expose
    private String validFrom;
    @SerializedName("valid_to")
    @Expose
    private String validTo;
    @SerializedName("image_100_80")
    @Expose
    private String imageSmall;
    @SerializedName("image_300_200")
    @Expose
    private String imageNoraml;
    @SerializedName("image")
    @Expose
    private String image;

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
     * @return The storeId
     */
    public String getStoreId() {
        return storeId;
    }

    /**
     * @param storeId The store_id
     */
    public void setStoreId(String storeId) {
        this.storeId = storeId;
    }

    /**
     * @return The name
     */
    public String getName() {
        return name;
    }

    /**
     * @param name The name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return The couponCode
     */
    public String getCouponCode() {
        return couponCode;
    }

    /**
     * @param couponCode The coupon_code
     */
    public void setCouponCode(String couponCode) {
        this.couponCode = couponCode;
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
     * @return The usageLimit
     */
    public String getUsageLimit() {
        return usageLimit;
    }

    /**
     * @param usageLimit The usage_limit
     */
    public void setUsageLimit(String usageLimit) {
        this.usageLimit = usageLimit;
    }

    /**
     * @return The minimumOrderAmount
     */
    public String getMinimumOrderAmount() {
        return minimumOrderAmount;
    }

    /**
     * @param minimumOrderAmount The minimum_order_amount
     */
    public void setMinimumOrderAmount(String minimumOrderAmount) {
        this.minimumOrderAmount = minimumOrderAmount;
    }

    /**
     * @return The offerNotification
     */
    public String getOfferNotification() {
        return offerNotification;
    }

    /**
     * @param offerNotification The offer_notification
     */
    public void setOfferNotification(String offerNotification) {
        this.offerNotification = offerNotification;
    }

    /**
     * @return The validFrom
     */
    public String getValidFrom() {
        return validFrom;
    }

    /**
     * @param validFrom The valid_from
     */
    public void setValidFrom(String validFrom) {
        this.validFrom = validFrom;
    }

    /**
     * @return The validTo
     */
    public String getValidTo() {
        return validTo;
    }

    /**
     * @param validTo The valid_to
     */
    public void setValidTo(String validTo) {
        this.validTo = validTo;
    }

    public String getImageSmall() {
        return imageSmall;
    }

    public void setImageSmall(String imageSmall) {
        this.imageSmall = imageSmall;
    }

    public String getImageNoraml() {
        return imageNoraml;
    }

    public void setImageNoraml(String imageNoraml) {
        this.imageNoraml = imageNoraml;
    }

    /**
     * @return The image
     */
    public String getImage() {
        return image;
    }

    /**
     * @param image The image
     */
    public void setImage(String image) {
        this.image = image;
    }

}

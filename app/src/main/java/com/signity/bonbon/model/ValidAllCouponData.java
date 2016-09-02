package com.signity.bonbon.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by root on 1/9/16.
 */
public class ValidAllCouponData {

    @SerializedName("id")
    @Expose
    private String id;
    @SerializedName("store_id")
    @Expose
    private String storeId;
    @SerializedName("all_users")
    @Expose
    private String allUsers;
    @SerializedName("all_zones")
    @Expose
    private String allZones;
    @SerializedName("order_facilities")
    @Expose
    private String orderFacilities;
    @SerializedName("all_categories")
    @Expose
    private String allCategories;
    @SerializedName("discount_24x7")
    @Expose
    private String discount24x7;
    @SerializedName("payment_method")
    @Expose
    private String paymentMethod;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("discount_type")
    @Expose
    private String discountType;
    @SerializedName("valid_from")
    @Expose
    private String validFrom;
    @SerializedName("valid_to")
    @Expose
    private String validTo;
    @SerializedName("discount_hours_from")
    @Expose
    private String discountHoursFrom;
    @SerializedName("discount_hours_to")
    @Expose
    private String discountHoursTo;
    @SerializedName("discount_days")
    @Expose
    private String discountDays;
    @SerializedName("coupon_code")
    @Expose
    private String couponCode;
    @SerializedName("discount")
    @Expose
    private String discount;
    @SerializedName("minimum_order_amount")
    @Expose
    private String minimumOrderAmount;
    @SerializedName("offer_notification")
    @Expose
    private String offerNotification;
    @SerializedName("offer_term_condition")
    @Expose
    private String offerTermCondition;
    @SerializedName("usage_limit")
    @Expose
    private String usageLimit;
    @SerializedName("created")
    @Expose
    private String created;
    @SerializedName("modified")
    @Expose
    private String modified;

    /**
     *
     * @return
     * The id
     */
    public String getId() {
        return id;
    }

    /**
     *
     * @param id
     * The id
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     *
     * @return
     * The storeId
     */
    public String getStoreId() {
        return storeId;
    }

    /**
     *
     * @param storeId
     * The store_id
     */
    public void setStoreId(String storeId) {
        this.storeId = storeId;
    }

    /**
     *
     * @return
     * The allUsers
     */
    public String getAllUsers() {
        return allUsers;
    }

    /**
     *
     * @param allUsers
     * The all_users
     */
    public void setAllUsers(String allUsers) {
        this.allUsers = allUsers;
    }

    /**
     *
     * @return
     * The allZones
     */
    public String getAllZones() {
        return allZones;
    }

    /**
     *
     * @param allZones
     * The all_zones
     */
    public void setAllZones(String allZones) {
        this.allZones = allZones;
    }

    /**
     *
     * @return
     * The orderFacilities
     */
    public String getOrderFacilities() {
        return orderFacilities;
    }

    /**
     *
     * @param orderFacilities
     * The order_facilities
     */
    public void setOrderFacilities(String orderFacilities) {
        this.orderFacilities = orderFacilities;
    }

    /**
     *
     * @return
     * The allCategories
     */
    public String getAllCategories() {
        return allCategories;
    }

    /**
     *
     * @param allCategories
     * The all_categories
     */
    public void setAllCategories(String allCategories) {
        this.allCategories = allCategories;
    }

    /**
     *
     * @return
     * The discount24x7
     */
    public String getDiscount24x7() {
        return discount24x7;
    }

    /**
     *
     * @param discount24x7
     * The discount_24x7
     */
    public void setDiscount24x7(String discount24x7) {
        this.discount24x7 = discount24x7;
    }

    /**
     *
     * @return
     * The paymentMethod
     */
    public String getPaymentMethod() {
        return paymentMethod;
    }

    /**
     *
     * @param paymentMethod
     * The payment_method
     */
    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    /**
     *
     * @return
     * The name
     */
    public String getName() {
        return name;
    }

    /**
     *
     * @param name
     * The name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     *
     * @return
     * The discountType
     */
    public String getDiscountType() {
        return discountType;
    }

    /**
     *
     * @param discountType
     * The discount_type
     */
    public void setDiscountType(String discountType) {
        this.discountType = discountType;
    }

    /**
     *
     * @return
     * The validFrom
     */
    public String getValidFrom() {
        return validFrom;
    }

    /**
     *
     * @param validFrom
     * The valid_from
     */
    public void setValidFrom(String validFrom) {
        this.validFrom = validFrom;
    }

    /**
     *
     * @return
     * The validTo
     */
    public String getValidTo() {
        return validTo;
    }

    /**
     *
     * @param validTo
     * The valid_to
     */
    public void setValidTo(String validTo) {
        this.validTo = validTo;
    }

    /**
     *
     * @return
     * The discountHoursFrom
     */
    public String getDiscountHoursFrom() {
        return discountHoursFrom;
    }

    /**
     *
     * @param discountHoursFrom
     * The discount_hours_from
     */
    public void setDiscountHoursFrom(String discountHoursFrom) {
        this.discountHoursFrom = discountHoursFrom;
    }

    /**
     *
     * @return
     * The discountHoursTo
     */
    public String getDiscountHoursTo() {
        return discountHoursTo;
    }

    /**
     *
     * @param discountHoursTo
     * The discount_hours_to
     */
    public void setDiscountHoursTo(String discountHoursTo) {
        this.discountHoursTo = discountHoursTo;
    }

    /**
     *
     * @return
     * The discountDays
     */
    public String getDiscountDays() {
        return discountDays;
    }

    /**
     *
     * @param discountDays
     * The discount_days
     */
    public void setDiscountDays(String discountDays) {
        this.discountDays = discountDays;
    }

    /**
     *
     * @return
     * The couponCode
     */
    public String getCouponCode() {
        return couponCode;
    }

    /**
     *
     * @param couponCode
     * The coupon_code
     */
    public void setCouponCode(String couponCode) {
        this.couponCode = couponCode;
    }

    /**
     *
     * @return
     * The discount
     */
    public String getDiscount() {
        return discount;
    }

    /**
     *
     * @param discount
     * The discount
     */
    public void setDiscount(String discount) {
        this.discount = discount;
    }

    /**
     *
     * @return
     * The minimumOrderAmount
     */
    public String getMinimumOrderAmount() {
        return minimumOrderAmount;
    }

    /**
     *
     * @param minimumOrderAmount
     * The minimum_order_amount
     */
    public void setMinimumOrderAmount(String minimumOrderAmount) {
        this.minimumOrderAmount = minimumOrderAmount;
    }

    /**
     *
     * @return
     * The offerNotification
     */
    public String getOfferNotification() {
        return offerNotification;
    }

    /**
     *
     * @param offerNotification
     * The offer_notification
     */
    public void setOfferNotification(String offerNotification) {
        this.offerNotification = offerNotification;
    }

    /**
     *
     * @return
     * The offerTermCondition
     */
    public String getOfferTermCondition() {
        return offerTermCondition;
    }

    /**
     *
     * @param offerTermCondition
     * The offer_term_condition
     */
    public void setOfferTermCondition(String offerTermCondition) {
        this.offerTermCondition = offerTermCondition;
    }

    /**
     *
     * @return
     * The usageLimit
     */
    public String getUsageLimit() {
        return usageLimit;
    }

    /**
     *
     * @param usageLimit
     * The usage_limit
     */
    public void setUsageLimit(String usageLimit) {
        this.usageLimit = usageLimit;
    }

    /**
     *
     * @return
     * The created
     */
    public String getCreated() {
        return created;
    }

    /**
     *
     * @param created
     * The created
     */
    public void setCreated(String created) {
        this.created = created;
    }

    /**
     *
     * @return
     * The modified
     */
    public String getModified() {
        return modified;
    }

    /**
     *
     * @param modified
     * The modified
     */
    public void setModified(String modified) {
        this.modified = modified;
    }


}

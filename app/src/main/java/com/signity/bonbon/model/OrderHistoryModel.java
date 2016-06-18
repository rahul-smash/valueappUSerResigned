package com.signity.bonbon.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by rajesh on 15/10/15.
 */
public class OrderHistoryModel {

    @SerializedName("order_id")
    @Expose
    private String orderId;
    @SerializedName("paid")
    @Expose
    private Integer paid;
    @SerializedName("payment_method")
    @Expose
    private String paymentMethod;
    @SerializedName("note")
    @Expose
    private String note;
    @SerializedName("order_date")
    @Expose
    private String orderDate;
    @SerializedName("status")
    @Expose
    private String status;

    @SerializedName("total")
    @Expose
    private String totalOrderVal;
    @SerializedName("discount")
    @Expose
    private String discount;
    @SerializedName("checkout")
    @Expose
    private String checkOut;
    @SerializedName("shipping_charges")
    @Expose
    private String shipping;
    @SerializedName("address")
    @Expose
    private String address;


    @SerializedName("tax")
    @Expose
    private String tax;

    @SerializedName("order_items")
    @Expose
    private List<OrderHistoryItemModel> orderItems = new ArrayList<OrderHistoryItemModel>();


    @SerializedName("store_tax_rate_detail")
    @Expose
    private List<TaxDetail> storeTaxRateDetail = new ArrayList<TaxDetail>();

    @SerializedName("store_fixed_tax_detail")
    @Expose
    private List<FixedTaxDetail> storeFixedTaxDetail = new ArrayList<FixedTaxDetail>();


    @SerializedName("calculated_tax_detail")
    @Expose
    private List<TaxDetails> calculatedTaxDetail = new ArrayList<TaxDetails>();

    public List<TaxDetails> getCalculatedTaxDetail() {
        return calculatedTaxDetail;
    }

    public void setCalculatedTaxDetail(List<TaxDetails> calculatedTaxDetail) {
        this.calculatedTaxDetail = calculatedTaxDetail;
    }

    public List<FixedTaxDetail> getStoreFixedTaxDetail() {
        return storeFixedTaxDetail;
    }

    public void setStoreFixedTaxDetail(List<FixedTaxDetail> storeFixedTaxDetail) {
        this.storeFixedTaxDetail = storeFixedTaxDetail;
    }

    public List<TaxDetail> getStoreTaxRateDetail() {
        return storeTaxRateDetail;
    }

    public void setStoreTaxRateDetail(List<TaxDetail> storeTaxRateDetail) {
        this.storeTaxRateDetail = storeTaxRateDetail;
    }

    public String getTax() {
        return tax;
    }

    public void setTax(String tax) {
        this.tax = tax;
    }

    /**
     * @return The orderId
     */
    public String getOrderId() {
        return orderId;
    }

    /**
     * @param orderId The order_id
     */
    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    /**
     * @return The paid
     */
    public Integer getPaid() {
        return paid;
    }

    /**
     * @param paid The paid
     */
    public void setPaid(Integer paid) {
        this.paid = paid;
    }

    /**
     * @return The paymentMethod
     */
    public String getPaymentMethod() {
        return paymentMethod;
    }

    /**
     * @param paymentMethod The payment_method
     */
    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    /**
     * @return The note
     */
    public String getNote() {
        return note;
    }

    /**
     * @param note The note
     */
    public void setNote(String note) {
        this.note = note;
    }

    /**
     * @return The orderDate
     */
    public String getOrderDate() {
        return orderDate;
    }

    /**
     * @param orderDate The order_date
     */
    public void setOrderDate(String orderDate) {
        this.orderDate = orderDate;
    }

    /**
     * @return The status
     */
    public String getStatus() {
        return status;
    }

    /**
     * @param status The status
     */
    public void setStatus(String status) {
        this.status = status;
    }


    public List<OrderHistoryItemModel> getOrderItems() {
        return orderItems;
    }

    public void setOrderItems(List<OrderHistoryItemModel> orderItems) {
        this.orderItems = orderItems;
    }

    public String getTotalOrderVal() {
        return totalOrderVal;
    }

    public void setTotalOrderVal(String totalOrderVal) {
        this.totalOrderVal = totalOrderVal;
    }

    public String getShipping() {
        return shipping;
    }

    public void setShipping(String shipping) {
        this.shipping = shipping;
    }

    public String getCheckOut() {
        return checkOut;
    }

    public void setCheckOut(String checkOut) {
        this.checkOut = checkOut;
    }

    public String getDiscount() {
        return discount;
    }

    public void setDiscount(String discount) {
        this.discount = discount;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
}

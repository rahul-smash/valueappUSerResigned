package com.signity.bonbon.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by root on 19/10/15.
 */
public class AboutUsModel2 {

    @SerializedName("due_orders")
    @Expose
    private Integer dueOrders;
    @SerializedName("active_orders")
    @Expose
    private Integer activeOrders;
    @SerializedName("customers")
    @Expose
    private Integer customers;
    @SerializedName("outstanding")
    @Expose
    private Integer outstanding;

    public AboutUsModel3 getAboutUsModel3() {
        return aboutUsModel3;
    }

    public void setAboutUsModel3(AboutUsModel3 aboutUsModel3) {
        this.aboutUsModel3 = aboutUsModel3;
    }

    @SerializedName("store")
    @Expose
    private AboutUsModel3 aboutUsModel3;

    /**
     *
     * @return
     * The dueOrders
     */
    public Integer getDueOrders() {
        return dueOrders;
    }

    /**
     *
     * @param dueOrders
     * The due_orders
     */
    public void setDueOrders(Integer dueOrders) {
        this.dueOrders = dueOrders;
    }

    /**
     *
     * @return
     * The activeOrders
     */
    public Integer getActiveOrders() {
        return activeOrders;
    }

    /**
     *
     * @param activeOrders
     * The active_orders
     */
    public void setActiveOrders(Integer activeOrders) {
        this.activeOrders = activeOrders;
    }

    /**
     *
     * @return
     * The customers
     */
    public Integer getCustomers() {
        return customers;
    }

    /**
     *
     * @param customers
     * The customers
     */
    public void setCustomers(Integer customers) {
        this.customers = customers;
    }

    /**
     *
     * @return
     * The outstanding
     */
    public Integer getOutstanding() {
        return outstanding;
    }

    /**
     *
     * @param outstanding
     * The outstanding
     */
    public void setOutstanding(Integer outstanding) {
        this.outstanding = outstanding;
    }

}

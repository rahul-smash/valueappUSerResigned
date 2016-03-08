package com.signity.bonbon.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by root on 3/3/16.
 */
public class Banner {

    @SerializedName("id")
    @Expose
    private String id;
    @SerializedName("store_id")
    @Expose
    private String storeId;
    @SerializedName("link")
    @Expose
    private String link;
    @SerializedName("title")
    @Expose
    private String title;
    @SerializedName("bigcommerce_category_id")
    @Expose
    private String bigcommerceCategoryId;
    @SerializedName("bigcommerce_product_id")
    @Expose
    private String bigcommerceProductId;
    @SerializedName("image")
    @Expose
    private String image;
    @SerializedName("status")
    @Expose
    private Boolean status;
    @SerializedName("is_home")
    @Expose
    private Boolean isHome;
    @SerializedName("is_category")
    @Expose
    private String isCategory;
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
     * The link
     */
    public String getLink() {
        return link;
    }

    /**
     *
     * @param link
     * The link
     */
    public void setLink(String link) {
        this.link = link;
    }

    /**
     *
     * @return
     * The title
     */
    public String getTitle() {
        return title;
    }

    /**
     *
     * @param title
     * The title
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     *
     * @return
     * The bigcommerceCategoryId
     */
    public String getBigcommerceCategoryId() {
        return bigcommerceCategoryId;
    }

    /**
     *
     * @param bigcommerceCategoryId
     * The bigcommerce_category_id
     */
    public void setBigcommerceCategoryId(String bigcommerceCategoryId) {
        this.bigcommerceCategoryId = bigcommerceCategoryId;
    }

    /**
     *
     * @return
     * The bigcommerceProductId
     */
    public String getBigcommerceProductId() {
        return bigcommerceProductId;
    }

    /**
     *
     * @param bigcommerceProductId
     * The bigcommerce_product_id
     */
    public void setBigcommerceProductId(String bigcommerceProductId) {
        this.bigcommerceProductId = bigcommerceProductId;
    }

    /**
     *
     * @return
     * The image
     */
    public String getImage() {
        return image;
    }

    /**
     *
     * @param image
     * The image
     */
    public void setImage(String image) {
        this.image = image;
    }

    /**
     *
     * @return
     * The status
     */
    public Boolean getStatus() {
        return status;
    }

    /**
     *
     * @param status
     * The status
     */
    public void setStatus(Boolean status) {
        this.status = status;
    }

    /**
     *
     * @return
     * The isHome
     */
    public Boolean getIsHome() {
        return isHome;
    }

    /**
     *
     * @param isHome
     * The is_home
     */
    public void setIsHome(Boolean isHome) {
        this.isHome = isHome;
    }

    /**
     *
     * @return
     * The isCategory
     */
    public String getIsCategory() {
        return isCategory;
    }

    /**
     *
     * @param isCategory
     * The is_category
     */
    public void setIsCategory(String isCategory) {
        this.isCategory = isCategory;
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

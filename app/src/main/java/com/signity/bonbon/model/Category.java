package com.signity.bonbon.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by rajesh on 5/10/15.
 */
public class Category {

    @SerializedName("id")
    @Expose
    private String id;

    @SerializedName("master_category_id")
    @Expose
    private String masterCategoryId;

    @SerializedName("title")
    @Expose
    private String title;
    @SerializedName("version")
    @Expose
    private String version;
    @SerializedName("image")
    @Expose
    private String image;
    @SerializedName("image_100_80")
    @Expose
    private String imageSmall;
    @SerializedName("image_300_200")
    @Expose
    private String imageMedium;

    @SerializedName("sub_category")
    @Expose
    private List<SubCategory> subCategoryList;

    @SerializedName("status")
    @Expose
    private String isEnable;

    @SerializedName("deleted")
    @Expose
    private boolean isDeleted;


    private String oldVersion = "";

    @SerializedName("sort")
    @Expose
    private String sortOrder;


    @SerializedName("show_product_image")
    @Expose
    private String showProductImage;

    public String getShowProductImage() {
        return showProductImage;
    }

    public void setShowProductImage(String showProductImage) {
        this.showProductImage = showProductImage;
    }

    public String getMasterCategoryId() {
        return masterCategoryId;
    }

    public void setMasterCategoryId(String masterCategoryId) {
        this.masterCategoryId = masterCategoryId;
    }
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
     * @return The title
     */
    public String getTitle() {
        return title;
    }

    /**
     * @param title The title
     */
    public void setTitle(String title) {
        this.title = title;
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

    public List<SubCategory> getSubCategoryList() {
        return subCategoryList;
    }

    public void setSubCategoryList(List<SubCategory> subCategoryList) {
        this.subCategoryList = subCategoryList;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getOldVersion() {
        return oldVersion;
    }

    public void setOldVersion(String oldVersion) {
        this.oldVersion = oldVersion;
    }

    public String getImageSmall() {
        return imageSmall;
    }

    public void setImageSmall(String imageSmall) {
        this.imageSmall = imageSmall;
    }

    public String getImageMedium() {
        return imageMedium;
    }

    public void setImageMedium(String imageMedium) {
        this.imageMedium = imageMedium;
    }

    public String getIsEnable() {
        return isEnable;
    }

    public void setIsEnable(String isEnable) {
        this.isEnable = isEnable;
    }

    public boolean isDeleted() {
        return isDeleted;
    }

    public void setIsDeleted(boolean isDeleted) {
        this.isDeleted = isDeleted;
    }


    public String getSortOrder() {
        return sortOrder;
    }

    public void setSortOrder(String sortOrder) {
        this.sortOrder = sortOrder;
    }
}


package com.signity.bonbon.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class Product {

    @SerializedName("id")
    @Expose
    private String id;
    @SerializedName("store_id")
    @Expose
    private String storeId;
    @SerializedName("category_ids")
    @Expose
    private String categoryIds;
    @SerializedName("title")
    @Expose
    private String title;
    @SerializedName("brand")
    @Expose
    private String brand;
    @SerializedName("nutrient")
    @Expose
    private String nutrient;
    @SerializedName("description")
    @Expose
    private String description;
    @SerializedName("image")
    @Expose
    private String image;
    @SerializedName("image_100_80")
    @Expose
    private String imageSmall;
    @SerializedName("image_300_200")
    @Expose
    private String imageMedium;
    @SerializedName("show_price")
    @Expose
    private String showPrice;

    private String varientId;

    private int slecteVarientPosition = 0;

    @SerializedName("variants")
    @Expose
    private List<Variant> variants = new ArrayList<Variant>();


    @SerializedName("selectedVariant")
    @Expose
    private SelectedVariant selectedVariant;


    @SerializedName("favorites")
    @Expose
    private boolean favorites;

    @SerializedName("deleted")
    @Expose
    private boolean isDeleted;
    @SerializedName("status")
    @Expose
    private String isEnable;
    @SerializedName("sort")
    @Expose
    private String sortOrder;

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
     * @return The selectedVariant
     */
    public SelectedVariant getSelectedVariant() {
        return selectedVariant;
    }

    /**
     * @param selectedVariant The selectedVariant
     */
    public void setSelectedVariant(SelectedVariant selectedVariant) {
        this.selectedVariant = selectedVariant;
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
     * @return The categoryIds
     */
    public String getCategoryIds() {
        return categoryIds;
    }

    /**
     * @param categoryIds The category_ids
     */
    public void setCategoryIds(String categoryIds) {
        this.categoryIds = categoryIds;
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
     * @return The brand
     */
    public String getBrand() {
        return brand;
    }

    /**
     * @param brand The brand
     */
    public void setBrand(String brand) {
        this.brand = brand;
    }

    /**
     * @return The nutrient
     */
    public String getNutrient() {
        return nutrient;
    }

    /**
     * @param nutrient The nutrient
     */
    public void setNutrient(String nutrient) {
        this.nutrient = nutrient;
    }

    /**
     * @return The description
     */
    public String getDescription() {
        return description;
    }

    /**
     * @param description The description
     */
    public void setDescription(String description) {
        this.description = description;
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

    /**
     * @return The showPrice
     */
    public String getShowPrice() {
        return showPrice;
    }

    /**
     * @param showPrice The show_price
     */
    public void setShowPrice(String showPrice) {
        this.showPrice = showPrice;
    }


    public String getVarientId() {
        return varientId;
    }

    public void setVarientId(String varientId) {
        this.varientId = varientId;
    }

    public int getSlecteVarientPosition() {
        return slecteVarientPosition;
    }

    public void setSlecteVarientPosition(int slecteVarientPosition) {
        this.slecteVarientPosition = slecteVarientPosition;
    }

    /**
     * @return The variants
     */
    public List<Variant> getVariants() {
        return variants;
    }

    /**
     * @param variants The variants
     */
    public void setVariants(List<Variant> variants) {
        this.variants = variants;
    }


    public boolean isFavorites() {
        return favorites;
    }

    public void setFavorites(boolean favorites) {
        this.favorites = favorites;
    }


    public boolean isDeleted() {
        return isDeleted;
    }

    public void setIsDeleted(boolean isDeleted) {
        this.isDeleted = isDeleted;
    }

    public String getIsEnable() {
        return isEnable;
    }

    public void setIsEnable(String isEnable) {
        this.isEnable = isEnable;
    }

    public String getSortOrder() {
        return sortOrder;
    }

    public void setSortOrder(String sortOrder) {
        this.sortOrder = sortOrder;
    }

    @Override
    public String toString() {
        return "Product{" +
                "id='" + id + '\'' +
                ", storeId='" + storeId + '\'' +
                ", categoryIds='" + categoryIds + '\'' +
                ", title='" + title + '\'' +
                ", brand='" + brand + '\'' +
                ", nutrient='" + nutrient + '\'' +
                ", description='" + description + '\'' +
                ", image='" + image + '\'' +
                ", imageSmall='" + imageSmall + '\'' +
                ", imageMedium='" + imageMedium + '\'' +
                ", showPrice='" + showPrice + '\'' +
                ", varientId='" + varientId + '\'' +
                ", slecteVarientPosition=" + slecteVarientPosition +
                ", variants=" + variants +
                ", selectedVariant=" + selectedVariant +
                ", favorites=" + favorites +
                '}';
    }
}

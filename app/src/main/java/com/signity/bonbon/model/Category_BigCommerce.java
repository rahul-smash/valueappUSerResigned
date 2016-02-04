package com.signity.bonbon.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Rajinder on 3/11/15.
 */
public class Category_BigCommerce {

    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("parent_id")
    @Expose
    private Integer parentId;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("description")
    @Expose
    private String description;
    @SerializedName("sort_order")
    @Expose
    private Integer sortOrder;
    @SerializedName("page_title")
    @Expose
    private String pageTitle;
    @SerializedName("meta_keywords")
    @Expose
    private String metaKeywords;
    @SerializedName("meta_description")
    @Expose
    private String metaDescription;
    @SerializedName("layout_file")
    @Expose
    private String layoutFile;
    @SerializedName("parent_category_list")
    @Expose
    private List<Integer> parentCategoryList = new ArrayList<Integer>();
    @SerializedName("image_file")
    @Expose
    private String imageFile;
    @SerializedName("is_visible")
    @Expose
    private Boolean isVisible;
    @SerializedName("search_keywords")
    @Expose
    private String searchKeywords;
    @SerializedName("url")
    @Expose
    private String url;

    /**
     * @return The id
     */
    public Integer getId() {
        return id;
    }

    /**
     * @param id The id
     */
    public void setId(Integer id) {
        this.id = id;
    }

    /**
     * @return The parentId
     */
    public Integer getParentId() {
        return parentId;
    }

    /**
     * @param parentId The parent_id
     */
    public void setParentId(Integer parentId) {
        this.parentId = parentId;
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
     * @return The sortOrder
     */
    public Integer getSortOrder() {
        return sortOrder;
    }

    /**
     * @param sortOrder The sort_order
     */
    public void setSortOrder(Integer sortOrder) {
        this.sortOrder = sortOrder;
    }

    /**
     * @return The pageTitle
     */
    public String getPageTitle() {
        return pageTitle;
    }

    /**
     * @param pageTitle The page_title
     */
    public void setPageTitle(String pageTitle) {
        this.pageTitle = pageTitle;
    }

    /**
     * @return The metaKeywords
     */
    public String getMetaKeywords() {
        return metaKeywords;
    }

    /**
     * @param metaKeywords The meta_keywords
     */
    public void setMetaKeywords(String metaKeywords) {
        this.metaKeywords = metaKeywords;
    }

    /**
     * @return The metaDescription
     */
    public String getMetaDescription() {
        return metaDescription;
    }

    /**
     * @param metaDescription The meta_description
     */
    public void setMetaDescription(String metaDescription) {
        this.metaDescription = metaDescription;
    }

    /**
     * @return The layoutFile
     */
    public String getLayoutFile() {
        return layoutFile;
    }

    /**
     * @param layoutFile The layout_file
     */
    public void setLayoutFile(String layoutFile) {
        this.layoutFile = layoutFile;
    }

    /**
     * @return The parentCategoryList
     */
    public List<Integer> getParentCategoryList() {
        return parentCategoryList;
    }

    /**
     * @param parentCategoryList The parent_category_list
     */
    public void setParentCategoryList(List<Integer> parentCategoryList) {
        this.parentCategoryList = parentCategoryList;
    }

    /**
     * @return The imageFile
     */
    public String getImageFile() {
        return imageFile;
    }

    /**
     * @param imageFile The image_file
     */
    public void setImageFile(String imageFile) {
        this.imageFile = imageFile;
    }

    /**
     * @return The isVisible
     */
    public Boolean getIsVisible() {
        return isVisible;
    }

    /**
     * @param isVisible The is_visible
     */
    public void setIsVisible(Boolean isVisible) {
        this.isVisible = isVisible;
    }

    /**
     * @return The searchKeywords
     */
    public String getSearchKeywords() {
        return searchKeywords;
    }

    /**
     * @param searchKeywords The search_keywords
     */
    public void setSearchKeywords(String searchKeywords) {
        this.searchKeywords = searchKeywords;
    }

    /**
     * @return The url
     */
    public String getUrl() {
        return url;
    }

    /**
     * @param url The url
     */
    public void setUrl(String url) {
        this.url = url;
    }
}

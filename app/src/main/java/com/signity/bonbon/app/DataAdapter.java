package com.signity.bonbon.app;

import com.signity.bonbon.model.Banner;
import com.signity.bonbon.model.FixedTaxDetail;
import com.signity.bonbon.model.ForceDownloadModel;
import com.signity.bonbon.model.GeofenceObjectModel;
import com.signity.bonbon.model.GetStoreArea;
import com.signity.bonbon.model.OrderHistoryModel;
import com.signity.bonbon.model.PickupAdressModel;
import com.signity.bonbon.model.Product;
import com.signity.bonbon.model.TaxDetail;

import java.util.HashMap;
import java.util.List;

/**
 * Created by Rajinder on 7/10/15.
 */
public class DataAdapter {


    public static DataAdapter cInstance;

    public HashMap<String, List<Product>> mapCategroy;
    public HashMap<String, String> mapCategroyId;
    public OrderHistoryModel orderHistoryModel;
    public List<GeofenceObjectModel> listGeoFence;

    public boolean isCartUpdated;


    public GetStoreArea getStoreArea() {
        return storeArea;
    }

    public void setStoreArea(GetStoreArea storeArea) {
        this.storeArea = storeArea;
    }

    public GetStoreArea storeArea;


    private List<Banner> banners;

    private List<Product> recommendProductList;

    private List<TaxDetail> taxDetail;

    private List<FixedTaxDetail> fixedTaxDetail;

    private ForceDownloadModel forceDownloadModel;

    public PickupAdressModel pickupAdressModel;

    public PickupAdressModel getPickupAdressModel() {
        return pickupAdressModel;
    }

    public String notificationMessage;

    public void setPickupAdressModel(PickupAdressModel pickupAdressModel) {
        this.pickupAdressModel = pickupAdressModel;
    }




    public List<FixedTaxDetail> getFixedTaxDetail() {
        return fixedTaxDetail;
    }

    public void setFixedTaxDetail(List<FixedTaxDetail> fixedTaxDetail) {
        this.fixedTaxDetail = fixedTaxDetail;
    }

    public List<TaxDetail> getTaxDetail() {
        return taxDetail;
    }

    public void setTaxDetail(List<TaxDetail> taxDetail) {
        this.taxDetail = taxDetail;
    }

    public List<Banner> getBanners() {
        return banners;
    }

    public void setBanners(List<Banner> banners) {
        this.banners = banners;
    }

    public void setForceDownloadModel(ForceDownloadModel forceDownloadModel) {
        this.forceDownloadModel = forceDownloadModel;
    }

    public ForceDownloadModel getForceDownloadModel() {
        return forceDownloadModel;
    }

    public List<GeofenceObjectModel> getListGeoFence() {
        return listGeoFence;
    }

    public void setListGeoFence(List<GeofenceObjectModel> listGeoFence) {
        this.listGeoFence = listGeoFence;
    }

    /* Static 'instance' method */
    public static DataAdapter getInstance() {
        return cInstance;
    }

    public static void initInstance() {

        if (cInstance == null) {
            cInstance = new DataAdapter();
        }
    }


    public HashMap<String, List<Product>> getMapCategroy() {
        return mapCategroy;
    }

    public void setMapCategroy(HashMap<String, List<Product>> mapCategroy) {
        this.mapCategroy = mapCategroy;
    }

    public OrderHistoryModel getOrderHistoryModel() {
        return orderHistoryModel;
    }

    public void setOrderHistoryModel(OrderHistoryModel orderHistoryModel) {
        this.orderHistoryModel = orderHistoryModel;
    }


    public HashMap<String, String> getMapCategroyId() {
        return mapCategroyId;
    }

    public void setMapCategroyId(HashMap<String, String> mapCategroyId) {
        this.mapCategroyId = mapCategroyId;
    }

    public boolean isCartUpdated() {
        return isCartUpdated;
    }

    public void setIsCartUpdated(boolean isCartUpdated) {
        this.isCartUpdated = isCartUpdated;
    }

    public List<Product> getProductList() {
        return recommendProductList;
    }

    public void setProductList(List<Product> productList) {
        this.recommendProductList = productList;
    }

    public String getNotificationMessage() {
        return notificationMessage;
    }

    public void setNotificationMessage(String notificationMessage) {
        this.notificationMessage = notificationMessage;
    }

}

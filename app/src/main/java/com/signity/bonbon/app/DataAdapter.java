package com.signity.bonbon.app;

import com.signity.bonbon.model.Banner;
import com.signity.bonbon.model.GetStoreArea;
import com.signity.bonbon.model.OrderHistoryModel;
import com.signity.bonbon.model.Product;

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

    public boolean isCartUpdated;


    public GetStoreArea getStoreArea() {
        return storeArea;
    }

    public void setStoreArea(GetStoreArea storeArea) {
        this.storeArea = storeArea;
    }

    public GetStoreArea storeArea;



    private List<Banner> banners;


    public List<Banner> getBanners() {
        return banners;
    }

    public void setBanners(List<Banner> banners) {
        this.banners = banners;
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
}

package com.signity.bonbon.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by root on 16/10/15.
 */
public class GetStoreAreaModel {
    @SerializedName("City")
    @Expose
    private GetStoreAreaModel2 City;
    @SerializedName("Area")
    @Expose
    private List<GetStoreAreaModel1> Area = new ArrayList<GetStoreAreaModel1>();

    /**
     *
     * @return
     * The City
     */
    public GetStoreAreaModel2 getCity() {
        return City;
    }

    /**
     *
     * @param City
     * The City
     */
    public void setCity(GetStoreAreaModel2 City) {
        this.City = City;
    }

    /**
     *
     * @return
     * The Area
     */
    public List<GetStoreAreaModel1> getArea() {
        return Area;
    }

    /**
     *
     * @param Area
     * The Area
     */
    public void setArea(List<GetStoreAreaModel1> Area) {
        this.Area = Area;
    }
}

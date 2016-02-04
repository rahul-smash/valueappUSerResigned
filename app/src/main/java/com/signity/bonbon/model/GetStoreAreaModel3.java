package com.signity.bonbon.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by root on 16/10/15.
 */
public class GetStoreAreaModel3 {
    @SerializedName("areas")
    @Expose
    private List<GetStoreAreaModel> areas = new ArrayList<GetStoreAreaModel>();

    /**
     *
     * @return
     * The areas
     */
    public List<GetStoreAreaModel> getAreas() {
        return areas;
    }

    /**
     *
     * @param areas
     * The areas
     */
    public void setAreas(List<GetStoreAreaModel> areas) {
        this.areas = areas;
    }
}

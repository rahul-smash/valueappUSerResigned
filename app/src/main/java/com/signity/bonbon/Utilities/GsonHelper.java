/**/
package com.signity.bonbon.Utilities;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import com.signity.bonbon.model.OfferData;
import com.signity.bonbon.model.Product;
import com.signity.bonbon.model.ReferAndEarn;
import com.signity.bonbon.model.SelectedVariant;
import com.signity.bonbon.model.SubCategory;
import com.signity.bonbon.model.UserRecord;
import com.signity.bonbon.model.ValidAllCouponData;
import com.signity.bonbon.model.Variant;

import java.lang.reflect.Type;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

/**
 * Created by rajesh on 19/10/15.
 */
public class GsonHelper {
    Gson gson = null;

    public GsonHelper() {
        gson = new Gson();
    }

    public String getSubCategory(List<SubCategory> subCategoryList) {
        Type type = new TypeToken<List<SubCategory>>() {
        }.getType();
        return gson.toJson(subCategoryList, type);
    }

    public List<SubCategory> getSubCategory(String subCategoryList) {
        List<SubCategory> list;
        Type type = new TypeToken<List<SubCategory>>() {
        }.getType();
        list = gson.fromJson(subCategoryList, type);
        Collections.sort(list, new Comparator<SubCategory>() {
            @Override
            public int compare(SubCategory subCategory, SubCategory subCategory2) {
                Integer sort1, sort2;
                try {
                    sort1 = Integer.parseInt(subCategory.getSortOrder());
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                    sort1 = 0;
                }
                try {
                    sort2 = Integer.parseInt(subCategory2.getSortOrder());
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                    sort2 = 0;
                }

                return sort1.compareTo(sort2);
            }
        });
        return list;
    }

    public String getProductVarientsArray(List<Variant> variants) {
        Type type = new TypeToken<List<Variant>>() {
        }.getType();
        return gson.toJson(variants, type);
    }

    public List<Variant> getProductVarientsArray(String varient) {
        List<Variant> list;
        Type type = new TypeToken<List<Variant>>() {
        }.getType();
        list = gson.fromJson(varient, type);
        return list;
    }

    public String getSelectedVarient(SelectedVariant selectedVariant) {
        Type type = new TypeToken<SelectedVariant>() {
        }.getType();
        return gson.toJson(selectedVariant, type);
    }

    public SelectedVariant getSelectedVarient(String selectedVariant) {
        SelectedVariant object;
        Type type = new TypeToken<SelectedVariant>() {
        }.getType();
        object = gson.fromJson(selectedVariant, type);
        return object;
    }

    public String getOfferDataString(OfferData offerData) {
        Type type = new TypeToken<OfferData>() {
        }.getType();
        return gson.toJson(offerData, type);
    }

    public String getOfferDataString(ValidAllCouponData data) {
        Type type = new TypeToken<ValidAllCouponData>() {
        }.getType();
        return gson.toJson(data, type);
    }
    public OfferData getOfferDataObject(String offerData) {
        OfferData object;
        Type type = new TypeToken<OfferData>() {
        }.getType();
        object = gson.fromJson(offerData, type);
        return object;
    }

    public String getProduct(Product product) {
        Type type = new TypeToken<Product>() {
        }.getType();
        return gson.toJson(product, type);
    }

    public Product getProduct(String productString) {
        Product object;
        Type type = new TypeToken<Product>() {
        }.getType();
        object = gson.fromJson(productString, type);
        return object;
    }

    public String getUserRecordData(HashMap<String, UserRecord> map) {
        Type type = new TypeToken<HashMap<String, UserRecord>>() {
        }.getType();
        return gson.toJson(map, type);
    }

    public HashMap<String, UserRecord> getUserRecordData(String string) {
        HashMap<String, UserRecord> object = null;
        try {
            Type type = new TypeToken<HashMap<String, UserRecord>>() {
            }.getType();
            object = gson.fromJson(string, type);
        } catch (JsonSyntaxException e) {
            e.printStackTrace();
        }
        return object;
    }

//    public String getReferData(ReferAndEarn referAndEarn) {
//        Type type = new TypeToken<ReferAndEarn>() {
//        }.getType();
//        return gson.toJson(referAndEarn, type);
//    }
//
//    public ReferAndEarn getReferData(String referAndEarnStr) {
//        ReferAndEarn object = null;
//        try {
//            Type type = new TypeToken<ReferAndEarn>() {
//            }.getType();
//            object = gson.fromJson(referAndEarnStr, type);
//        } catch (JsonSyntaxException e) {
//            e.printStackTrace();
//        }
//        return object;
//    }
}

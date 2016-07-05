package com.signity.bonbon.network;

import com.signity.bonbon.model.AboutUsModel1;
import com.signity.bonbon.model.EmailResponse;
import com.signity.bonbon.model.EnquiryModel;
import com.signity.bonbon.model.GeoFenceModel;
import com.signity.bonbon.model.GerOrderHistoryModel;
import com.signity.bonbon.model.GetBigComCategory;
import com.signity.bonbon.model.GetCategory;
import com.signity.bonbon.model.GetOfferResponse;
import com.signity.bonbon.model.GetPickupApiResponse;
import com.signity.bonbon.model.GetStoreArea;
import com.signity.bonbon.model.GetStoreModel;
import com.signity.bonbon.model.GetSubCategory;
import com.signity.bonbon.model.GetValidCouponResponse;
import com.signity.bonbon.model.LoyalityModel;
import com.signity.bonbon.model.MobResponse;
import com.signity.bonbon.model.ProductListModel;
import com.signity.bonbon.model.ReferNEarnModel;
import com.signity.bonbon.model.ResponseData;
import com.signity.bonbon.model.TaxCalculationModel;
import com.signity.bonbon.model.UserAddressList;
import com.signity.bonbon.model.VerifyOtpResponse;

import java.util.Map;

import retrofit.Callback;
import retrofit.http.FieldMap;
import retrofit.http.FormUrlEncoded;
import retrofit.http.GET;
import retrofit.http.POST;
import retrofit.http.Path;

/**
 * Created by rajinder on 5/10/15.
 */
public interface ApiService {
    @GET("/getCategories")
    void getCategoryList(Callback<GetCategory> response);

    @GET("/categories")
    void getBCCategoryList(Callback<GetBigComCategory> response);

    @FormUrlEncoded
    @POST("/getProducts/{id}")
    void getSubCategoryList(@FieldMap Map<String, String> parameters, @Path("id") String id, Callback<GetSubCategory> response);

    @FormUrlEncoded
    @POST("/getSubCategoryProducts/{id}")
    void getSubCategoryProduct(@FieldMap Map<String, String> parameters, @Path("id") String id, Callback<GetSubCategory> response);

    @FormUrlEncoded
    @POST("/searchProducts")
    void getSearchList(@FieldMap Map<String, String> parameters, Callback<GetSubCategory> response);

    @FormUrlEncoded
    @POST("/addToCart")
    void setCartItems(@FieldMap Map<String, String> parameters, retrofit.Callback<ResponseData> response);

    @FormUrlEncoded
    @POST("/cartHistory")
    void getCartHistory(@FieldMap Map<String, String> parameters, Callback<ProductListModel> response);

    @FormUrlEncoded
    @POST("/placeOrder")
    void placeOrder(@FieldMap Map<String, String> parameters, Callback<ResponseData> response);

    @FormUrlEncoded
    @POST("/orderCancel")
    void cancelOrder(@FieldMap Map<String, String> parameters, Callback<EmailResponse> response);

    @FormUrlEncoded
    @POST("/mobileVerification")
    void moblieVerification(@FieldMap Map<String, String> parameters, Callback<MobResponse> response);

    @FormUrlEncoded
    @POST("/favorites")
    void addToFavourite(@FieldMap Map<String, String> parameters, retrofit.Callback<ResponseData> response);

    @FormUrlEncoded
    @POST("/favorites")
    void getFavouriteList(@FieldMap Map<String, String> parameters, Callback<ProductListModel> response);

    @FormUrlEncoded
    @POST("/updateProfile")
    void updateProfile(@FieldMap Map<String, String> parameters, Callback<EmailResponse> response);

    @FormUrlEncoded
    @POST("/deliveryAddress")
    void getAddressList(@FieldMap Map<String, String> parameters, Callback<UserAddressList> response);

    @FormUrlEncoded
    @POST("/deliveryAddress")
    void addNewDeliveryAddress(@FieldMap Map<String, String> parameters, Callback<EmailResponse> response);

    @FormUrlEncoded
    @POST("/deliveryAddress")
    void updateDeliveryAddress(@FieldMap Map<String, String> parameters, Callback<EmailResponse> response);

    @FormUrlEncoded
    @POST("/deliveryAddress")
    void deleteDeliveryAddress(@FieldMap Map<String, String> parameters, Callback<EmailResponse> response);

    @FormUrlEncoded
    @POST("/orderHistory")
    void getOrderHistoryList(@FieldMap Map<String, String> parameters, Callback<GerOrderHistoryModel> response);


    @GET("/storearea")
    void getStoreAreaList(Callback<GetStoreArea> response);

    @FormUrlEncoded
    @POST("/storedashboard")
    void getAboutUs(@FieldMap Map<String, String> parameters, Callback<AboutUsModel1> response);

    @FormUrlEncoded
    @POST("/version")
    void getStore(@FieldMap Map<String, String> parameters, Callback<GetStoreModel> response);

    @FormUrlEncoded
    @POST("/storeOffers")
    void getStoreOffer(@FieldMap Map<String, String> parameters, Callback<GetOfferResponse> response);

    @FormUrlEncoded
    @POST("/validateCoupon")
    void validateCoupon(@FieldMap Map<String, String> parameters, Callback<GetValidCouponResponse> response);

    @FormUrlEncoded
    @POST("/setStoreQuery")
    void query(@FieldMap Map<String, String> parameters, Callback<EmailResponse> response);

    @FormUrlEncoded
    @POST("/verifyOtp")
    void verifyOtp(@FieldMap Map<String, String> parameters, Callback<VerifyOtpResponse> response);

    @FormUrlEncoded
    @POST("/getPickupAddress")
    void getPickupLocation(@FieldMap Map<String, String> parameters, Callback<GetPickupApiResponse> response);

    @GET("/getGeofencingMessage")
    void getGeofencingMessage(Callback<GeoFenceModel> response);

    @FormUrlEncoded
    @POST("/getLoyalityPoints")
    void getLoyalityPoints(@FieldMap Map<String, String> parameters, Callback<LoyalityModel> response);

    @FormUrlEncoded
    @POST("/jewellers/setJewellerQuery")
    void submitQuery(@FieldMap Map<String, String> parameters, Callback<EmailResponse> response);

    @FormUrlEncoded
    @POST("/multiple_tax_calculation")
    void getTaxCalculations(@FieldMap Map<String, String> parameters, Callback<TaxCalculationModel> response);

    @FormUrlEncoded
    @POST("/productDetail")
    void getProductDetails(@FieldMap Map<String, String> parameters, Callback<GetSubCategory> response);

    @FormUrlEncoded
    @POST("/getReferDetails")
    void getUserReferCode(@FieldMap Map<String, String> parameters, Callback<ReferNEarnModel> response);

    @GET("/getEnquiryFormMessage")
    void getEnquiryFormMessage(Callback<EnquiryModel> response);
}

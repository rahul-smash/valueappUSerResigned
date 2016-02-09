package com.signity.bonbon.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by rajesh on 13/10/15.
 */
public class MobData {

    @SerializedName("id")
    @Expose
    private String id;
    @SerializedName("full_name")
    @Expose
    private String fullName;
    @SerializedName("fb_id")
    @Expose
    private String fbId;
    @SerializedName("email")
    @Expose
    private String email;
    @SerializedName("phone")
    @Expose
    private String phone;
    @SerializedName("otp")
    @Expose
    private String otp;
    @SerializedName("status")
    @Expose
    private String status;
    @SerializedName("device_id")
    @Expose
    private String deviceId;
    @SerializedName("device_token")
    @Expose
    private String deviceToken;
    @SerializedName("platform")
    @Expose
    private String platform;
    @SerializedName("deliveryAddress")
    @Expose
    private List<UserAddressModel> deliverAddress;

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
     * @return The fullName
     */
    public String getFullName() {
        return fullName;
    }

    /**
     * @param fullName The full_name
     */
    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    /**
     * @return The fbId
     */
    public String getFbId() {
        return fbId;
    }

    /**
     * @param fbId The fb_id
     */
    public void setFbId(String fbId) {
        this.fbId = fbId;
    }

    /**
     * @return The email
     */
    public String getEmail() {
        return email;
    }

    /**
     * @param email The email
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * @return The phone
     */
    public String getPhone() {
        return phone;
    }

    /**
     * @param phone The phone
     */
    public void setPhone(String phone) {
        this.phone = phone;
    }

    /**
     * @return The otp
     */
    public String getOtp() {
        return otp;
    }

    /**
     * @param otp The otp
     */
    public void setOtp(String otp) {
        this.otp = otp;
    }

    /**
     * @return The status
     */
    public String getStatus() {
        return status;
    }

    /**
     * @param status The status
     */
    public void setStatus(String status) {
        this.status = status;
    }

    /**
     * @return The deviceId
     */
    public String getDeviceId() {
        return deviceId;
    }

    /**
     * @param deviceId The device_id
     */
    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    /**
     * @return The deviceToken
     */
    public String getDeviceToken() {
        return deviceToken;
    }

    /**
     * @param deviceToken The device_token
     */
    public void setDeviceToken(String deviceToken) {
        this.deviceToken = deviceToken;
    }

    /**
     * @return The platform
     */
    public String getPlatform() {
        return platform;
    }

    /**
     * @param platform The platform
     */
    public void setPlatform(String platform) {
        this.platform = platform;
    }

    public List<UserAddressModel> getDeliverAddress() {
        return deliverAddress;
    }

    public void setDeliverAddress(List<UserAddressModel> deliverAddress) {
        this.deliverAddress = deliverAddress;
    }
}

package com.signity.bonbon.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by root on 18/3/16.
 */
public class LoginData {


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
    @SerializedName("otp_verify")
    @Expose
    private String otpVerify;
    @SerializedName("role_id")
    @Expose
    private String roleId;
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

    @SerializedName("is_referer_fn_enable")
    @Expose
    private Boolean isReferFnEnable;

    @SerializedName("bl_device_id_unique")
    @Expose
    private Boolean isReferForDeviceEnable;

    /**
     *
     * @return
     * The id
     */
    public String getId() {
        return id;
    }

    /**
     *
     * @param id
     * The id
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     *
     * @return
     * The fullName
     */
    public String getFullName() {
        return fullName;
    }

    /**
     *
     * @param fullName
     * The full_name
     */
    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    /**
     *
     * @return
     * The fbId
     */
    public String getFbId() {
        return fbId;
    }

    /**
     *
     * @param fbId
     * The fb_id
     */
    public void setFbId(String fbId) {
        this.fbId = fbId;
    }

    /**
     *
     * @return
     * The email
     */
    public String getEmail() {
        return email;
    }

    /**
     *
     * @param email
     * The email
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     *
     * @return
     * The phone
     */
    public String getPhone() {
        return phone;
    }

    /**
     *
     * @param phone
     * The phone
     */
    public void setPhone(String phone) {
        this.phone = phone;
    }

    /**
     *
     * @return
     * The otp
     */
    public String getOtp() {
        return otp;
    }

    /**
     *
     * @param otp
     * The otp
     */
    public void setOtp(String otp) {
        this.otp = otp;
    }

    /**
     *
     * @return
     * The otpVerify
     */
    public String getOtpVerify() {
        return otpVerify;
    }

    /**
     *
     * @param otpVerify
     * The otp_verify
     */
    public void setOtpVerify(String otpVerify) {
        this.otpVerify = otpVerify;
    }

    /**
     *
     * @return
     * The roleId
     */
    public String getRoleId() {
        return roleId;
    }

    /**
     *
     * @param roleId
     * The role_id
     */
    public void setRoleId(String roleId) {
        this.roleId = roleId;
    }

    /**
     *
     * @return
     * The status
     */
    public String getStatus() {
        return status;
    }

    /**
     *
     * @param status
     * The status
     */
    public void setStatus(String status) {
        this.status = status;
    }

    /**
     *
     * @return
     * The deviceId
     */
    public String getDeviceId() {
        return deviceId;
    }

    /**
     *
     * @param deviceId
     * The device_id
     */
    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    /**
     *
     * @return
     * The deviceToken
     */
    public String getDeviceToken() {
        return deviceToken;
    }

    /**
     *
     * @param deviceToken
     * The device_token
     */
    public void setDeviceToken(String deviceToken) {
        this.deviceToken = deviceToken;
    }

    /**
     *
     * @return
     * The platform
     */
    public String getPlatform() {
        return platform;
    }

    /**
     *
     * @param platform
     * The platform
     */
    public void setPlatform(String platform) {
        this.platform = platform;
    }


    public Boolean getReferForDeviceEnable() {
        return isReferForDeviceEnable;
    }

    public void setReferForDeviceEnable(Boolean referForDeviceEnable) {
        isReferForDeviceEnable = referForDeviceEnable;
    }

    public Boolean getReferFnEnable() {
        return isReferFnEnable;
    }

    public void setReferFnEnable(Boolean referFnEnable) {
        isReferFnEnable = referFnEnable;
    }

}

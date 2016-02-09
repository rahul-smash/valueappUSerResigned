package com.signity.bonbon.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by rajesh on 13/10/15.
 */
public class MobResponse {

    @SerializedName("success")
    @Expose
    private Boolean success;

    @SerializedName("user_exists")
    @Expose
    private Integer userExistStatus;

    @SerializedName("message")
    @Expose
    private String message;

    @SerializedName("data")
    @Expose
    private MobData data;

    /**
     * @return The success
     */
    public Boolean getSuccess() {
        return success;
    }

    /**
     * @param success The success
     */
    public void setSuccess(Boolean success) {
        this.success = success;
    }

    /**
     * @return The success
     */
    public MobData getData() {
        return data;
    }

    /**
     * @param data The success
     */
    public void setData(MobData data) {
        this.data = data;
    }

    public Integer getUserExistStatus() {
        return userExistStatus;
    }

    public void setUserExistStatus(Integer userExistStatus) {
        this.userExistStatus = userExistStatus;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}

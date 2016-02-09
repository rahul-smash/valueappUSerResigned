package com.signity.bonbon.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by root on 14/10/15.
 */
public class EmailResponse {

    @SerializedName("success")
    @Expose
    private Boolean success;
    @SerializedName("message")
    @Expose
    private String message;

    /**
     *
     * @return
     * The success
     */
    public Boolean getSuccess() {
        return success;
    }

    /**
     *
     * @param success
     * The success
     */
    public void setSuccess(Boolean success) {
        this.success = success;
    }

    /**
     *
     * @return
     * The message
     */
    public String getMessage() {
        return message;
    }

    /**
     *
     * @param message
     * The message
     */
    public void setMessage(String message) {
        this.message = message;
    }
}
